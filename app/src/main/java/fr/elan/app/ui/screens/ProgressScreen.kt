package fr.elan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import fr.elan.app.domain.*
import fr.elan.app.ui.components.*
import java.time.LocalDate

@Composable
fun ProgressScreen(snapshot: AppSnapshot, today: LocalDate, onGoal: (Int) -> Unit, onDelete: (String) -> Unit) {
    var window by rememberSaveable { mutableStateOf(30) }
    var kind by rememberSaveable { mutableStateOf<String?>(null) }
    var deleting by rememberSaveable { mutableStateOf<String?>(null) }
    val records = remember(snapshot.practiceHistory, today, window, kind) {
        PracticeAnalytics.history(snapshot, today, window.takeIf { it > 0 }, kind?.let(PracticeKind::valueOf))
    }
    val stats = PracticeAnalytics.stats(records)
    val weekStart = PracticeAnalytics.weekStart(today)
    val weekSeconds = PracticeAnalytics.weekSeconds(snapshot, today)
    val goal = snapshot.settings.weeklyGoalMinutes
    LazyColumn(contentPadding = screenContentPadding(), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item { Text("Mes petits pas.", style = MaterialTheme.typography.headlineLarge)
            Text("Des repères de pratique, pas un score de santé. Les jours sans pratique ont aussi leur place.") }
        item { ElanCard(color = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer) {
            Text("Cette semaine", style = MaterialTheme.typography.titleLarge)
            Text("${formatDuration(weekSeconds)} / $goal min", style = MaterialTheme.typography.headlineMedium)
            LinearProgressIndicator(progress = { (weekSeconds.toFloat() / (goal * 60)).coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth())
            Text("Du lundi au dimanche · yoga, respiration et pauses enregistrées", style = MaterialTheme.typography.bodySmall)
            val totals = PracticeAnalytics.history(snapshot, today).groupBy { it.date }.mapValues { (_, rows) -> rows.sumOf { it.actualSeconds.toLong() } }
            val maximum = (0..6).maxOf { totals[weekStart.plusDays(it.toLong()).toString()] ?: 0L }.coerceAtLeast(60L)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
                val labels = listOf("L", "M", "M", "J", "V", "S", "D")
                repeat(7) { i ->
                    val date = weekStart.plusDays(i.toLong())
                    val seconds = totals[date.toString()] ?: 0L
                    Column(Modifier.weight(1f).semantics(mergeDescendants = true) {
                        contentDescription = "${frenchDate(date)} : ${formatDuration(seconds)} de pratique"
                    }, horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.height(70.dp).fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
                            Box(Modifier.fillMaxWidth(0.7f).height(if (seconds == 0L) 3.dp else (8 + 62 * seconds.toFloat() / maximum).dp)
                                .background(if (date > today) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp)))
                        }
                        Text(labels[i], style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            Text("Mon objectif hebdomadaire, ajustable", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(10, 20, 30, 45, 60, 90, 120).forEach { v -> FilterChip(goal == v, { onGoal(v) }, label = { Text("$v min") }) }
            }
            Text("${PracticeAnalytics.streak(snapshot, today)} jour(s) consécutif(s) avec une pratique enregistrée, jusqu'à aujourd'hui ou hier.", style = MaterialTheme.typography.bodySmall)
        } }
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(7 to "7 jours", 30 to "30 jours", 90 to "90 jours", 0 to "Tout").forEach { (days, label) -> FilterChip(window == days, { window = days }, label = { Text(label) }) }
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(kind == null, { kind = null }, label = { Text("Toutes les pratiques") })
                PracticeKind.entries.forEach { k -> FilterChip(kind == k.name, { kind = k.name }, label = { Text(k.label) }) }
            }
        }
        item { ElanCard {
            Text("Sur la période sélectionnée", style = MaterialTheme.typography.titleMedium)
            Text("${formatDuration(stats.seconds)} pratiquées", style = MaterialTheme.typography.headlineMedium)
            Text("${stats.count} pratique(s) · ${stats.activeDays} jour(s) actifs")
            Text("${stats.completedCount} parcours complets · ${stats.count - stats.completedCount} parcours adaptés ou arrêtés plus tôt", style = MaterialTheme.typography.bodySmall)
            Text("Le temps effectif exclut préparation, transitions, pauses et portions sautées. Il est arrondi à la seconde inférieure lors de l'enregistrement.", style = MaterialTheme.typography.bodySmall)
        } }
        item { Text("Historique", style = MaterialTheme.typography.titleLarge) }
        if (records.isEmpty()) item { Text("Aucune pratique enregistrée pour ces critères. Le bouton d'enregistrement apparaît à la fin du lecteur.") }
        items(records, key = { it.id }) { record -> ElanCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(record.title, style = MaterialTheme.typography.titleMedium)
                    Text(frenchDate(LocalDate.parse(record.date)), style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = { deleting = record.id }) { Icon(Icons.Outlined.DeleteOutline, "Supprimer cette pratique de l'historique") }
            }
            Text("${record.kind.label} · ${formatDuration(record.actualSeconds.toLong())} / ${formatDuration(record.plannedSeconds.toLong())}")
            Text(if (record.completed) "Parcours complet" else "Parcours adapté / partiel", style = MaterialTheme.typography.labelMedium)
        } }
    }
    deleting?.let { id -> AlertDialog(onDismissRequest = { deleting = null }, title = { Text("Supprimer cette pratique ?") },
        text = { Text("Elle sera retirée de l'historique et des statistiques. Les séances et le journal ne changent pas.") },
        confirmButton = { TextButton(onClick = { onDelete(id); deleting = null }) { Text("Supprimer") } },
        dismissButton = { TextButton(onClick = { deleting = null }) { Text("Annuler") } }) }
}

