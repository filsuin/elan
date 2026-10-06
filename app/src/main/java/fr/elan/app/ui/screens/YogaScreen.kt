package fr.elan.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import fr.elan.app.content.YogaPose
import fr.elan.app.domain.Countdown
import fr.elan.app.ui.components.*
import java.util.Locale

@Composable
fun YogaScreen(onPose: (String) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Une pause pour toi.", style = MaterialTheme.typography.headlineLarge)
                Text("Yoga doux et positions de repos, sans objectif de performance.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            ElanCard(color = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer) {
                Eyebrow(Icons.Outlined.Spa, "Avant de commencer")
                Text(YogaCatalog.safety, style = MaterialTheme.typography.bodyMedium)
            }
        }
        items(YogaCatalog.poses, key = { it.id }) { pose ->
            Card(onClick = { onPose(pose.id) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    PoseIllustration(pose.id, pose.title, Modifier.fillMaxWidth().height(140.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(pose.title, style = MaterialTheme.typography.titleLarge)
                            Text(pose.subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Outlined.ChevronRight, null)
                    }
                    Eyebrow(Icons.Outlined.Timer, "${pose.seconds} secondes · durée indicative")
                }
            }
        }
    }
}

@Composable
fun YogaDetailScreen(pose: YogaPose, timer: Countdown, onStart: () -> Unit, onPause: () -> Unit, onReset: () -> Unit) {
    val view = LocalView.current
    DisposableEffect(timer.running, view) {
        val before = view.keepScreenOn
        if (timer.running) view.keepScreenOn = true
        onDispose { view.keepScreenOn = before }
    }
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(pose.title, style = MaterialTheme.typography.headlineLarge)
                Text(pose.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            ElanCard(color = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer) {
                PoseIllustration(pose.id, pose.title, Modifier.fillMaxWidth().height(180.dp))
                Text("Schéma simplifié · adapte les supports à ton confort", style = MaterialTheme.typography.labelSmall)
            }
        }
        item {
            ElanCard {
                Eyebrow(Icons.Outlined.Chair, pose.equipment)
                SectionTitle("Pour t'installer")
                pose.steps.forEachIndexed { index, step ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("${index + 1}.", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
                        Text(step, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
        item {
            ElanCard(color = MaterialTheme.colorScheme.tertiaryContainer, contentColor = MaterialTheme.colorScheme.onTertiaryContainer) {
                Eyebrow(Icons.Outlined.Info, "À adapter à ton corps")
                Text(pose.caution)
                Text("Arrête en cas de douleur, vertige ou gêne. Tu peux quitter la posture avant la fin.", style = MaterialTheme.typography.bodyMedium)
            }
        }
        item {
            ElanCard {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Ton temps de pause", style = MaterialTheme.typography.titleLarge)
                    Box(Modifier.size(164.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(progress = { timer.progress }, modifier = Modifier.fillMaxSize(), strokeWidth = 6.dp)
                        Text(String.format(Locale.ROOT, "%02d:%02d", timer.secondsLeft / 60, timer.secondsLeft % 60),
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.semantics { contentDescription = "${timer.secondsLeft} secondes restantes" })
                    }
                    Text(when { timer.finished -> "La pause est terminée. Reviens doucement."
                        timer.running -> "Respire naturellement, sans forcer."
                        timer.remainingMillis < timer.totalMillis -> "En pause. Reprends seulement quand tu es prêt·e."
                        else -> "Installe-toi, puis démarre quand tu es prêt·e." },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = if (timer.running) onPause else onStart, enabled = !timer.finished) {
                            Icon(if (timer.running) Icons.Outlined.Pause else Icons.Outlined.PlayArrow, null)
                            Spacer(Modifier.width(8.dp)); Text(if (timer.running) "Pause" else "Démarrer")
                        }
                        OutlinedButton(onClick = onReset) { Text("Recommencer") }
                    }
                    Text("Le minuteur se met en pause quand l'application passe en arrière-plan. Aucun son ni notification.",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
