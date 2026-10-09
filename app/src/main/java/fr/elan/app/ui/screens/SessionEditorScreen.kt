package fr.elan.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.elan.app.content.*
import fr.elan.app.domain.*
import fr.elan.app.ui.components.ElanCard
import fr.elan.app.ui.components.dialogContentMaxHeight
import fr.elan.app.ui.components.screenContentPadding

/** Draft lives in the ViewModel, survives rotation; only Save persists it. */
@Composable
fun SessionEditorScreen(draft: YogaSession, saving: Boolean, onChange: (YogaSession) -> Unit, onSave: () -> Unit) {
    var adding by rememberSaveable(draft.id) { mutableStateOf(false) }
    var query by rememberSaveable(draft.id) { mutableStateOf("") }
    fun updateSteps(steps: List<YogaStep>) { onChange(draft.copy(steps = steps)) }
    LazyColumn(contentPadding = screenContentPadding(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("Ma séance sur mesure", style = MaterialTheme.typography.headlineLarge)
            Text("Ajoute les postures que tu connais. Un brouillon n'est pas une séance enregistrée.") }
        item {
            OutlinedTextField(draft.title, { onChange(draft.copy(title = it.take(80))) }, label = { Text("Nom de la séance") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(draft.description, { onChange(draft.copy(description = it.take(300))) }, label = { Text("Intention ou note (facultatif)") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
        }
        item { Text("${draft.steps.size}/40 étapes · ${formatDuration(draft.practiceSeconds.toLong())} hors transitions", style = MaterialTheme.typography.titleMedium)
            Text("Les positions asymétriques ajoutent deux étapes. Tu peux ensuite les réordonner ou les retirer.", style = MaterialTheme.typography.bodySmall) }
        items(draft.steps.indices.toList(), key = { it }) { i ->
            val step = draft.steps[i]
            val pose = requireNotNull(YogaCatalog.find(step.poseId))
            ElanCard {
                Text("${i + 1}. ${pose.title}" + if (step.side != PoseSide.NONE) " · ${step.side.label}" else "", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { updateSteps(draft.steps.mapIndexed { j, s -> if (i == j) s.copy(seconds = (s.seconds - 15).coerceAtLeast(15)) else s }) }, enabled = step.seconds > 15 && !saving) { Icon(Icons.Outlined.Remove, "Réduire la durée de l'étape ${i + 1}") }
                    Text("${step.seconds} s", Modifier.weight(1f))
                    IconButton(onClick = { updateSteps(draft.steps.mapIndexed { j, s -> if (i == j) s.copy(seconds = (s.seconds + 15).coerceAtMost(300)) else s }) }, enabled = step.seconds < 300 && !saving) { Icon(Icons.Outlined.Add, "Augmenter la durée de l'étape ${i + 1}") }
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { val list = draft.steps.toMutableList(); java.util.Collections.swap(list, i, i - 1); updateSteps(list) }, enabled = i > 0 && !saving) { Text("Monter") }
                    OutlinedButton(onClick = { val list = draft.steps.toMutableList(); java.util.Collections.swap(list, i, i + 1); updateSteps(list) }, enabled = i < draft.steps.lastIndex && !saving) { Text("Descendre") }
                    IconButton(onClick = { updateSteps(draft.steps.filterIndexed { j, _ -> j != i }) }, enabled = !saving) { Icon(Icons.Outlined.DeleteOutline, "Retirer l'étape ${i + 1}") }
                }
            }
        }
        item {
            OutlinedButton(onClick = { adding = true }, enabled = draft.steps.size < MAX_SESSION_STEPS && !saving, modifier = Modifier.fillMaxWidth()) { Text("Ajouter une posture") }
            Spacer(Modifier.height(10.dp))
            Button(onClick = onSave, enabled = draft.title.isNotBlank() && draft.steps.isNotEmpty() && !saving, modifier = Modifier.fillMaxWidth()) { Text(if (saving) "Enregistrement..." else "Enregistrer la séance") }
            Text(YogaCatalog.safety, Modifier.padding(top = 12.dp), style = MaterialTheme.typography.bodySmall)
        }
    }
    if (adding) AlertDialog(onDismissRequest = { adding = false }, title = { Text("Ajouter une posture") },
        text = { Column {
            OutlinedTextField(query, { query = it.take(100) }, label = { Text("Rechercher") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            LazyColumn(Modifier.heightIn(max = dialogContentMaxHeight(340.dp))) {
                val results = YogaCatalog.search(query)
                if (results.isEmpty()) item { Text("Aucun résultat", Modifier.padding(16.dp)) }
                items(results, key = { it.id }) { pose ->
                    val added = PracticeCatalog.stepsFor(pose.id)
                    TextButton(onClick = { updateSteps(draft.steps + added); adding = false }, modifier = Modifier.fillMaxWidth(),
                        enabled = draft.steps.size + added.size <= MAX_SESSION_STEPS) {
                        Column(Modifier.fillMaxWidth()) { Text(pose.title); Text("${pose.level.label} · ${added.size} étape(s)", style = MaterialTheme.typography.bodySmall) }
                    }
                }
            }
        } }, confirmButton = { TextButton(onClick = { adding = false }) { Text("Fermer") } })
}

