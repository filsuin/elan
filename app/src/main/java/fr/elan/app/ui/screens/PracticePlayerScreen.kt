package fr.elan.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import fr.elan.app.content.YogaCatalog
import fr.elan.app.domain.*
import fr.elan.app.ui.components.*

@Composable
fun PracticePlayerScreen(
    player: PracticePlayer, settings: UserSettings, voiceAvailable: Boolean, saving: Boolean,
    onPlay: () -> Unit, onPause: () -> Unit, onSkip: () -> Unit, onRestart: () -> Unit,
    onExit: () -> Unit, onSave: () -> Unit, onSettings: (UserSettings) -> Unit,
) {
    val view = LocalView.current
    DisposableEffect(player.running, view) {
        val before = view.keepScreenOn
        if (player.running) view.keepScreenOn = true
        onDispose { view.keepScreenOn = before }
    }
    val step = player.step
    val pose = step.poseId?.let(YogaCatalog::find)
    val nextPose = player.plan.steps.drop(player.index + 1).firstOrNull { it.isPractice }
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item {
            Text(player.plan.title, style = MaterialTheme.typography.headlineMedium)
            Text(player.plan.kind.label, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(progress = { player.progress }, modifier = Modifier.fillMaxWidth())
        }
        if (player.finished) {
            item { ElanCard(color = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer) {
                Text("Un moment pour toi, terminé.", style = MaterialTheme.typography.headlineMedium)
                Text("${formatDuration(player.actualMillis / 1000)} de pratique effective sur ${formatDuration(player.plan.plannedSeconds.toLong())} prévues.")
                Text(if (player.skippedPractice) "Parcours adapté : certaines étapes ont été passées." else "Parcours complet.")
                Text("L'installation, les transitions et les pauses ne sont pas comptées dans le temps de pratique.", style = MaterialTheme.typography.bodySmall)
                Button(onClick = onSave, enabled = !saving && player.actualMillis >= 1000) { Text(if (saving) "Enregistrement..." else "Enregistrer dans mon suivi") }
                OutlinedButton(onClick = onRestart, enabled = !saving) { Text("Recommencer sans enregistrer") }
                TextButton(onClick = onExit, enabled = !saving) { Text("Quitter") }
            } }
        } else {
            item { ElanCard {
                Text(step.title, style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                Text(step.instruction)
                if (pose != null) PoseIllustration(pose.id, pose.title, Modifier.fillMaxWidth().height(155.dp))
                Box(Modifier.fillMaxWidth().height(190.dp), contentAlignment = Alignment.Center) {
                    if (step.kind == StepKind.INHALE || step.kind == StepKind.EXHALE) {
                        val color = MaterialTheme.colorScheme.primaryContainer
                        val fraction = if (step.kind == StepKind.INHALE) stepProgressRadius(player.stepProgress) else stepProgressRadius(1f - player.stepProgress)
                        Canvas(Modifier.size(180.dp).clearAndSetSemantics {}) { drawCircle(color, size.minDimension / 2f * fraction) }
                    }
                    CircularProgressIndicator(progress = { 1f - player.stepProgress }, modifier = Modifier.size(178.dp), strokeWidth = 5.dp)
                    Text(formatDuration(player.secondsLeft), style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.semantics { contentDescription = "${player.secondsLeft} secondes restantes" })
                }
                Text(if (player.running) "En cours" else "En pause · reprends quand tu es prêt·e", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = if (player.running) onPause else onPlay, enabled = !saving) {
                        Icon(if (player.running) Icons.Outlined.Pause else Icons.Outlined.PlayArrow, null)
                        Spacer(Modifier.width(8.dp)); Text(if (player.running) "Pause" else "Démarrer / reprendre")
                    }
                    OutlinedButton(onClick = onSkip, enabled = !saving) { Text("Passer l'étape") }
                }
                TextButton(onClick = onExit, enabled = !saving) { Text("Arrêter la pratique") }
                Text("${formatDuration(player.actualMillis / 1000)} pratiquées · étape ${player.index + 1}/${player.plan.steps.size}", style = MaterialTheme.typography.bodySmall)
            } }
            if (pose != null) item { ElanCard {
                Text(if (step.kind == StepKind.TRANSITION) "Préparer la prochaine posture" else "Repères de posture", style = MaterialTheme.typography.titleLarge)
                if (step.side != PoseSide.NONE) Text(step.side.label, style = MaterialTheme.typography.titleMedium)
                Text("Matériel : ${pose.equipment}", style = MaterialTheme.typography.labelLarge)
                pose.steps.forEachIndexed { i, text -> Text("${i + 1}. $text") }
                Text(pose.caution, color = MaterialTheme.colorScheme.error)
                Text("Plus doux : ${pose.adaptation}")
            } }
            if (nextPose != null) item { Text("À suivre : ${nextPose.title}", style = MaterialTheme.typography.titleMedium) }
        }
        item { ElanCard {
            Text("Accompagnement facultatif", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Signal sonore", Modifier.weight(1f)); Switch(settings.soundEnabled, { onSettings(settings.copy(soundEnabled = it)) })
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Annonces vocales", Modifier.weight(1f)); Switch(settings.voiceEnabled, { onSettings(settings.copy(voiceEnabled = it)) })
            }
            if (settings.voiceEnabled && !voiceAvailable) Text("Aucune voix française hors ligne disponible ou moteur en cours d'initialisation. Le guidage visuel reste actif.", style = MaterialTheme.typography.bodySmall)
            Text("Le lecteur se met en pause en arrière-plan ou au verrouillage. Aucun service audio de fond. Une rotation conserve le parcours mais le met en pause.", style = MaterialTheme.typography.bodySmall)
            Text("Une pratique non enregistrée ne résiste pas à un arrêt du processus. Enregistre ton temps avant de quitter.", style = MaterialTheme.typography.bodySmall)
        } }
    }
}
private fun stepProgressRadius(value: Float): Float = 0.58f + value.coerceIn(0f, 1f) * 0.37f

