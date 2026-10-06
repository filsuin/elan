package fr.elan.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import fr.elan.app.content.DailyContent
import fr.elan.app.domain.*
import fr.elan.app.ui.components.*
import java.time.LocalDate

@Composable
fun HomeScreen(
    snapshot: AppSnapshot, today: LocalDate,
    onToggle: (String) -> Unit,
    onSaveRoutine: (String?, String) -> Unit, onDeleteRoutine: (String) -> Unit,
    onVictory: (String) -> Unit, onMood: () -> Unit, onYoga: () -> Unit,
) {
    val entry = snapshot.entry(today)
    val completed = snapshot.routines.count { it.id in entry.completedTaskIds }
    val tip = DailyContent.tip(today)
    var victory by rememberSaveable(today.toString()) { mutableStateOf(entry.victory) }
    var editTaskId by rememberSaveable { mutableStateOf<String?>(null) }
    var taskDialog by rememberSaveable { mutableStateOf(false) }
    var deleteTaskId by rememberSaveable { mutableStateOf<String?>(null) }

    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(frenchDate(today), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(if (snapshot.settings.firstName.isBlank()) "Un jour à la fois." else "Bonjour ${snapshot.settings.firstName}.", style = MaterialTheme.typography.headlineLarge)
                Text("Un peu d'attention pour toi, à ton rythme.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            ElanCard(color = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer) {
                Eyebrow(Icons.Outlined.WbSunny, "L'idée du jour")
                Text(tip.title, style = MaterialTheme.typography.headlineMedium)
                Text(tip.text, style = MaterialTheme.typography.bodyLarge)
                Text("Une invitation, pas une obligation.", style = MaterialTheme.typography.labelMedium)
            }
        }
        item {
            ElanCard {
                SectionTitle("Ma routine", "Les petits gestes comptent aussi.")
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("$completed / ${snapshot.routines.size} gestes", style = MaterialTheme.typography.labelLarge)
                    Text("Aujourd'hui", style = MaterialTheme.typography.labelMedium)
                }
                LinearProgressIndicator(
                    progress = { if (snapshot.routines.isEmpty()) 0f else completed.toFloat() / snapshot.routines.size },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                )
                if (snapshot.routines.isEmpty()) Text("Commence par ajouter un geste qui te convient.")
                snapshot.routines.forEach { task ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(Modifier.weight(1f).toggleable(
                            value = task.id in entry.completedTaskIds, role = Role.Checkbox,
                            onValueChange = { onToggle(task.id) },
                        ).heightIn(min = 48.dp).padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = task.id in entry.completedTaskIds, onCheckedChange = null,
                                modifier = Modifier.padding(end = 12.dp).size(32.dp))
                            Text(task.title, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                        }
                        IconButton(onClick = { editTaskId = task.id; taskDialog = true }) {
                            Icon(Icons.Outlined.Edit, "Modifier la routine : ${task.title}", Modifier.size(20.dp))
                        }
                    }
                }
                TextButton(onClick = { editTaskId = null; taskDialog = true }, enabled = snapshot.routines.size < MAX_ROUTINES) {
                    Icon(Icons.Outlined.Add, null, Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text("Ajouter un geste")
                }
                Text("Tout ne doit pas être coché. Demain est un nouveau départ.", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            ElanCard(color = MaterialTheme.colorScheme.tertiaryContainer, contentColor = MaterialTheme.colorScheme.onTertiaryContainer) {
                Eyebrow(Icons.Outlined.AutoAwesome, "Un pas pour ma confiance")
                Text(DailyContent.challenge(today), style = MaterialTheme.typography.titleLarge)
                Text("Ma petite victoire", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = victory, onValueChange = { victory = it.take(MAX_NOTE_LENGTH) },
                    modifier = Modifier.fillMaxWidth(), label = { Text("Une chose faite, tentée ou traversée") },
                    minLines = 2, maxLines = 6,
                    supportingText = { Text("${victory.length} / $MAX_NOTE_LENGTH · Enregistre pour conserver ton texte") },
                )
                Button(onClick = { onVictory(victory) }, enabled = victory.trim() != entry.victory) {
                    Icon(Icons.Outlined.Check, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("Enregistrer ma victoire")
                }
            }
        }
        item {
            ElanCard {
                SectionTitle("Et toi, comment ça va ?")
                Text(entry.mood?.let { "Ton ressenti : ${Mood.fromScore(it).label.lowercase()}. Tu peux le modifier." }
                    ?: "Prends un instant pour accueillir ton ressenti, sans le juger.")
                FilledTonalButton(onClick = onMood) { Text("Noter mon humeur") }
            }
        }
        item {
            OutlinedButton(onClick = onYoga, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.SelfImprovement, null); Spacer(Modifier.width(10.dp)); Text("M'accorder une pause yoga")
            }
        }
    }
    if (taskDialog) {
        RoutineDialog(
            task = snapshot.routines.firstOrNull { it.id == editTaskId },
            onDismiss = { taskDialog = false },
            onSave = { onSaveRoutine(editTaskId, it); taskDialog = false },
            onDelete = { deleteTaskId = editTaskId; taskDialog = false },
        )
    }
    deleteTaskId?.let { id ->
        AlertDialog(onDismissRequest = { deleteTaskId = null }, title = { Text("Supprimer ce geste ?") },
            text = { Text("Il sera retiré de ta routine et des cases cochées. Ton journal et tes humeurs seront conservés.") },
            confirmButton = { TextButton(onClick = { onDeleteRoutine(id); deleteTaskId = null }) { Text("Supprimer") } },
            dismissButton = { TextButton(onClick = { deleteTaskId = null }) { Text("Annuler") } })
    }
}

@Composable
private fun RoutineDialog(task: RoutineTask?, onDismiss: () -> Unit, onSave: (String) -> Unit, onDelete: () -> Unit) {
    var title by rememberSaveable(task?.id) { mutableStateOf(task?.title.orEmpty()) }
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text(if (task == null) "Un nouveau petit geste" else "Mon petit geste") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it.take(MAX_TASK_LENGTH) },
                    label = { Text("Nom de la routine") }, supportingText = { Text("${title.length} / $MAX_TASK_LENGTH") },
                    modifier = Modifier.fillMaxWidth(), maxLines = 3)
                if (task != null) TextButton(onClick = onDelete) { Text("Supprimer ce geste", color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = { TextButton(onClick = { onSave(title) }, enabled = title.isNotBlank()) { Text("Enregistrer") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } },
    )
}
