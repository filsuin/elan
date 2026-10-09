package fr.elan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import fr.elan.app.domain.*
import fr.elan.app.ui.components.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MoodScreen(
    snapshot: AppSnapshot, today: LocalDate,
    onSave: (LocalDate, Int, String, Set<String>) -> Unit,
    onDelete: (LocalDate) -> Unit,
    onSaveVictory: (LocalDate, String) -> Unit,
) {
    var window by rememberSaveable { mutableIntStateOf(30) }
    var editDate by rememberSaveable { mutableStateOf<String?>(null) }
    var deleteDate by rememberSaveable { mutableStateOf<String?>(null) }
    val entry = snapshot.entry(today)
    val history = remember(snapshot.days, today, window) {
        snapshot.days.values.filter { e ->
            val day = LocalDate.parse(e.date)
            (e.mood != null || e.victory.isNotBlank()) && day <= today &&
                (window == 0 || day >= today.minusDays(window.toLong() - 1))
        }.sortedByDescending { it.date }
    }
    LazyColumn(
        modifier = Modifier.testTag("mood_list"),
        contentPadding = screenContentPadding(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Mon humeur", style = MaterialTheme.typography.headlineLarge)
                Text("Tout ce que tu ressens a sa place ici.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            ElanCard {
                Eyebrow(Icons.Outlined.FavoriteBorder, "Aujourd'hui · ${fullDate(today)}")
                Text("Comment te sens-tu ?", style = MaterialTheme.typography.titleLarge)
                Text("Un ressenti, pas une note de performance.", style = MaterialTheme.typography.bodyMedium)
                key(entry.date, entry.mood, entry.note, entry.tags) {
                    MoodEditor(entry) { score, note, tags -> onSave(today, score, note, tags) }
                }
            }
        }
        item {
            ElanCard {
                SectionTitle("Ces 7 derniers jours", "Les jours sans saisie restent vides.")
                MoodWeek(snapshot, today)
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionTitle("Mon historique", "Mes humeurs et mes petites victoires.")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(7 to "7 jours", 30 to "30 jours", 0 to "Tout").forEach { (value, label) ->
                        FilterChip(selected = window == value, onClick = { window = value }, label = { Text(label) })
                    }
                }
            }
        }
        if (history.isEmpty()) {
            item { ElanCard { Text("Ton histoire commence ici.", style = MaterialTheme.typography.titleMedium)
                Text("Les humeurs et victoires enregistrées apparaîtront dans cet espace.") } }
        }
        items(history, key = { it.date }) { day ->
            Card(onClick = { editDate = day.date }, modifier = Modifier.fillMaxWidth().testTag("history_${day.date}"), shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(if (day.mood != null) moodIcon(day.mood) else Icons.Outlined.AutoAwesome,
                            null, Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
                        Column(Modifier.weight(1f)) {
                            Text(fullDate(LocalDate.parse(day.date)), style = MaterialTheme.typography.labelLarge)
                            Text(day.mood?.let { Mood.fromScore(it).label } ?: "Une petite victoire", style = MaterialTheme.typography.titleMedium)
                        }
                        Icon(Icons.Outlined.Edit, "Ouvrir l'entrée du ${day.date}", Modifier.size(20.dp))
                    }
                    if (day.note.isNotBlank()) Text(day.note, style = MaterialTheme.typography.bodyMedium)
                    if (day.tags.isNotEmpty()) Text(day.tags.joinToString(" · "), style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (day.victory.isNotBlank()) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Text("Ma petite victoire", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary)
                        Text(day.victory, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
        item {
            Text("Ce journal ne pose aucun diagnostic. Si une difficulté persiste ou devient lourde à porter, parle-en à une personne de confiance ou à un professionnel.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    editDate?.let { date ->
        val editingDate = LocalDate.parse(date)
        val editing = snapshot.entry(editingDate)
        var victory by rememberSaveable(date) { mutableStateOf(editing.victory) }
        AlertDialog(onDismissRequest = { editDate = null },
            title = { Text(fullDate(editingDate)) },
            text = {
                Column(Modifier.heightIn(max = dialogContentMaxHeight(520.dp)).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    MoodEditor(editing) { score, note, tags ->
                        onSave(editingDate, score, note, tags)
                        editDate = null
                    }
                    HorizontalDivider()
                    Text("Petite victoire", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = victory,
                        onValueChange = { victory = it.take(MAX_NOTE_LENGTH) },
                        modifier = Modifier.fillMaxWidth().testTag("historical_victory_field"),
                        label = { Text("Une chose faite, tentée ou traversée") },
                        minLines = 2,
                        maxLines = 6,
                        supportingText = { Text("${victory.length} / $MAX_NOTE_LENGTH") },
                    )
                    Button(
                        onClick = { onSaveVictory(editingDate, victory); editDate = null },
                        modifier = Modifier.fillMaxWidth().testTag("save_historical_victory"),
                        enabled = victory.trim() != editing.victory,
                    ) {
                        Text(if (victory.isBlank()) "Effacer ma petite victoire" else "Enregistrer ma petite victoire")
                    }
                    if (editing.mood != null) TextButton(onClick = { deleteDate = date; editDate = null }) {
                        Text("Supprimer cette humeur", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = { TextButton(onClick = { editDate = null }) { Text("Fermer") } },
        )
    }
    deleteDate?.let { date ->
        AlertDialog(onDismissRequest = { deleteDate = null }, title = { Text("Supprimer cette humeur ?") },
            text = { Text("Le ressenti, sa note et ses thèmes seront effacés. La petite victoire et la routine de ce jour seront conservées.") },
            confirmButton = { TextButton(onClick = { onDelete(LocalDate.parse(date)); deleteDate = null }) { Text("Supprimer") } },
            dismissButton = { TextButton(onClick = { deleteDate = null }) { Text("Annuler") } })
    }
}

@Composable
private fun MoodEditor(initial: DayEntry, onSave: (Int, String, Set<String>) -> Unit) {
    var selected by rememberSaveable(initial.date) { mutableStateOf(initial.mood) }
    var note by rememberSaveable(initial.date) { mutableStateOf(initial.note) }
    var tags: List<String> by rememberSaveable(initial.date) { mutableStateOf(initial.tags.toList()) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MoodSelector(selected) { selected = it }
        Text("Qu'est-ce qui a compté ?", style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            moodTags.forEach { tag ->
                FilterChip(selected = tag in tags,
                    onClick = { tags = if (tag in tags) tags - tag else tags + tag },
                    label = { Text(tag) })
            }
        }
        OutlinedTextField(value = note, onValueChange = { note = it.take(MAX_NOTE_LENGTH) },
            modifier = Modifier.fillMaxWidth(), label = { Text("Quelques mots, si tu le souhaites") },
            minLines = 2, maxLines = 6, supportingText = { Text("${note.length} / $MAX_NOTE_LENGTH") })
        Button(onClick = { selected?.let { onSave(it, note, tags.toSet()) } }, modifier = Modifier.fillMaxWidth(),
            enabled = selected != null && (selected != initial.mood || note.trim() != initial.note || tags.toSet() != initial.tags)) {
            Icon(Icons.Outlined.Check, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp))
            Text(if (initial.mood == null) "Enregistrer mon humeur" else "Mettre à jour mon humeur")
        }
        if (initial.mood != null) Text("Une seule humeur par jour, modifiable à tout moment.", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun MoodWeek(snapshot: AppSnapshot, today: LocalDate) {
    val weekday = remember { DateTimeFormatter.ofPattern("EE", Locale.FRENCH) }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        (6 downTo 0).forEach { offset ->
            val date = today.minusDays(offset.toLong())
            val score = snapshot.days[date.toString()]?.mood
            Column(Modifier.weight(1f).semantics(mergeDescendants = true) {
                contentDescription = "${fullDate(date)} : ${score?.let { Mood.fromScore(it).label } ?: "non renseigné"}"
            }, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Box(Modifier.height(88.dp).fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
                    if (score != null) {
                        Box(Modifier.widthIn(max = 34.dp).fillMaxWidth().height((score * 15).dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                            Text("$score", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    } else Text("—", color = MaterialTheme.colorScheme.outline)
                }
                Text(date.format(weekday).removeSuffix(".").take(2), style = MaterialTheme.typography.labelSmall, maxLines = 1)
                Text("${date.dayOfMonth}", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
