package fr.elan.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.elan.app.content.BreathingCatalog
import fr.elan.app.domain.*
import fr.elan.app.ui.components.*

@Composable
fun BreathingScreen(onStart: (PracticePlan) -> Unit) {
    var minutes by rememberSaveable { mutableStateOf(3) }
    var silenceMinutes by rememberSaveable { mutableStateOf(5) }
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item { Text("Retrouver de l'espace.", style = MaterialTheme.typography.headlineLarge)
            Text("Respiration guidée visuellement et pauses silencieuses, sans compte ni connexion.") }
        item { ElanCard(color = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer) {
            Eyebrow(Icons.Outlined.Air, "Avant de commencer")
            Text(BreathingCatalog.safety, style = MaterialTheme.typography.bodyMedium)
        } }
        item { Text("Durée de respiration souhaitée", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(1, 3, 5, 10).forEach { v -> FilterChip(minutes == v, { minutes = v }, label = { Text("$v min") }) }
            }
            Text("Arrondie au cycle complet inférieur ; la durée exacte est affichée ci-dessous.", style = MaterialTheme.typography.bodySmall)
        }
        items(BreathingCatalog.patterns, key = { it.id }) { pattern ->
            val plan = remember(pattern, minutes) { BreathingCatalog.plan(pattern, minutes) }
            ElanCard {
                Text(pattern.title, style = MaterialTheme.typography.titleLarge)
                Text(pattern.description)
                Text("${formatDuration(plan.plannedSeconds.toLong())} · ${(plan.steps.size - 1) / 2} cycles · sans rétention", style = MaterialTheme.typography.labelLarge)
                Button(onClick = { onStart(plan) }) { Text("Commencer") }
            }
        }
        item { ElanCard {
            Text("Pause silencieuse", style = MaterialTheme.typography.titleLarge)
            Text("Un minuteur pour observer les appuis, les sons ou le souffle naturel. Aucun audio de méditation préenregistré.")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(2, 5, 10, 15).forEach { v -> FilterChip(silenceMinutes == v, { silenceMinutes = v }, label = { Text("$v min") }) }
            }
            Button(onClick = { onStart(BreathingCatalog.meditation(silenceMinutes)) }) { Text("M'accorder cette pause") }
            Text("Les sons et annonces facultatifs se règlent dans le lecteur. Tu peux garder les yeux ouverts.", style = MaterialTheme.typography.bodySmall)
        } }
    }
}

