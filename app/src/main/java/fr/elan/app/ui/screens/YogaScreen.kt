package fr.elan.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import fr.elan.app.content.*
import fr.elan.app.domain.*
import fr.elan.app.ui.components.*

@Composable
fun YogaScreen(
    snapshot: AppSnapshot = AppSnapshot(),
    onFavorite: (String) -> Unit = {}, onSession: (String) -> Unit = {}, onCreate: () -> Unit = {},
    onEdit: (String) -> Unit = {}, onDelete: (String) -> Unit = {},
    onPose: (String) -> Unit,
) {
    var page by rememberSaveable { mutableStateOf(0) }
    var query by rememberSaveable { mutableStateOf("") }
    var level by rememberSaveable { mutableStateOf<String?>(null) }
    var category by rememberSaveable { mutableStateOf<String?>(null) }
    var onlyFavorites by rememberSaveable { mutableStateOf(false) }
    var filtersExpanded by rememberSaveable { mutableStateOf(false) }
    var deleting by rememberSaveable { mutableStateOf<String?>(null) }
    val poses = remember(query, level, category, onlyFavorites, snapshot.favoritePoseIds) {
        YogaCatalog.search(query, level?.let(YogaLevel::valueOf), category?.let(YogaCategory::valueOf),
            if (onlyFavorites) snapshot.favoritePoseIds else null)
    }
    LazyColumn(Modifier.testTag("yoga_list"), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Bouger à ton rythme.", style = MaterialTheme.typography.headlineLarge)
            Text("48 fiches · 10 parcours · tes séances", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Positions", "Séances", "Mes séances").forEachIndexed { i, label ->
                    FilterChip(selected = page == i, onClick = { page = i }, label = { Text(label) })
                }
            }
        }
        if (page == 0) {
            item {
                OutlinedTextField(query, { query = it.take(100) }, Modifier.fillMaxWidth().testTag("yoga_search"),
                    label = { Text("Nom, zone du corps ou matériel") }, singleLine = true,
                    leadingIcon = { Icon(Icons.Outlined.Search, null) },
                    trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { query = "" }) { Icon(Icons.Outlined.Close, "Effacer la recherche") } })
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(onlyFavorites, { onlyFavorites = !onlyFavorites }, label = { Text("Favoris (${snapshot.favoritePoseIds.size})") },
                        leadingIcon = { Icon(Icons.Outlined.FavoriteBorder, null, Modifier.size(18.dp)) })
                    FilterChip(filtersExpanded, { filtersExpanded = !filtersExpanded }, label = { Text("Filtres") },
                        leadingIcon = { Icon(Icons.Outlined.Tune, null, Modifier.size(18.dp)) })
                }
                if (filtersExpanded) {
                    Text("Niveau", style = MaterialTheme.typography.labelLarge)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(level == null, { level = null }, label = { Text("Tous") })
                        YogaLevel.entries.forEach { option -> FilterChip(level == option.name, { level = option.name }, label = { Text(option.label) }) }
                    }
                    Text("Position", style = MaterialTheme.typography.labelLarge)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(category == null, { category = null }, label = { Text("Toutes") })
                        YogaCategory.entries.forEach { option -> FilterChip(category == option.name, { category = option.name }, label = { Text(option.label) }) }
                    }
                }
                if (level != null || category != null) TextButton(onClick = { level = null; category = null }) { Text("Réinitialiser les filtres") }
                Text("${poses.size} fiche(s)", style = MaterialTheme.typography.labelLarge)
            }
            if (poses.isEmpty()) item { ElanCard { Text("Aucune posture pour ces critères. Essaie un autre mot ou retire un filtre.") } }
            items(poses, key = { it.id }) { pose ->
                Card(onClick = { onPose(pose.id) }, Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PoseIllustration(pose.id, pose.title, Modifier.width(108.dp).height(84.dp))
                            Column(Modifier.weight(1f)) {
                                Text(pose.title, style = MaterialTheme.typography.titleMedium)
                                Text("${pose.level.label} · ${pose.category.label}", style = MaterialTheme.typography.bodySmall)
                                Text("${pose.seconds} s" + if (pose.bilateral) " par côté" else "", style = MaterialTheme.typography.labelMedium)
                            }
                            IconButton(onClick = { onFavorite(pose.id) }) {
                                Icon(if (pose.id in snapshot.favoritePoseIds) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                                    if (pose.id in snapshot.favoritePoseIds) "Retirer ${pose.title} des favoris" else "Ajouter ${pose.title} aux favoris")
                            }
                        }
                        Text(pose.focus.joinToString(" · "), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        } else {
            if (page == 2) item {
                FilledTonalButton(onClick = onCreate, enabled = snapshot.customSessions.size < MAX_CUSTOM_SESSIONS, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Outlined.Add, null); Spacer(Modifier.width(8.dp)); Text("Créer une séance")
                }
                if (snapshot.customSessions.isEmpty()) Text("Assemble les positions que tu connais, règle leurs durées et retrouve-les ici.", Modifier.padding(top = 12.dp))
            }
            val sessions = if (page == 1) PracticeCatalog.sessions else snapshot.customSessions
            items(sessions, key = { it.id }) { session ->
                ElanCard {
                    Text(session.title, style = MaterialTheme.typography.titleLarge)
                    Text(session.description)
                    Text("${formatDuration(session.practiceSeconds.toLong())} de pratique · ${session.steps.size} étapes · ${YogaLevel.entries[session.level].label}",
                        style = MaterialTheme.typography.labelLarge)
                    Text("Hors installation et transitions", style = MaterialTheme.typography.bodySmall)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { onSession(session.id) }) { Text("Voir le parcours") }
                        if (page == 2) {
                            TextButton(onClick = { onEdit(session.id) }) { Text("Modifier") }
                            IconButton(onClick = { deleting = session.id }) { Icon(Icons.Outlined.DeleteOutline, "Supprimer ${session.title}") }
                        }
                    }
                }
            }
        }
        item { ElanCard(color = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer) {
            Eyebrow(Icons.Outlined.Info, "Avant de pratiquer")
            Text(YogaCatalog.safety, style = MaterialTheme.typography.bodySmall)
        } }
    }
    deleting?.let { id -> AlertDialog(onDismissRequest = { deleting = null }, title = { Text("Supprimer cette séance ?") },
        text = { Text("L'historique des pratiques déjà enregistrées sera conservé.") },
        confirmButton = { TextButton(onClick = { onDelete(id); deleting = null }) { Text("Supprimer") } },
        dismissButton = { TextButton(onClick = { deleting = null }) { Text("Annuler") } }) }
}

@Composable
fun YogaDetailScreen(pose: YogaPose, favorite: Boolean, onFavorite: () -> Unit, onPractice: (Int) -> Unit) {
    var duration by rememberSaveable(pose.id) { mutableStateOf(pose.seconds) }
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item { Text(pose.title, style = MaterialTheme.typography.headlineLarge)
            Text(pose.sanskrit, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${pose.level.label} · ${pose.category.label}", style = MaterialTheme.typography.labelLarge) }
        item { PoseIllustration(pose.id, pose.title, Modifier.fillMaxWidth().height(185.dp))
            Text("Schéma de repérage uniquement. Lis les étapes, adapte les supports et respecte le côté indiqué.", style = MaterialTheme.typography.bodySmall) }
        item { OutlinedButton(onClick = onFavorite) { Icon(if (favorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder, null)
            Spacer(Modifier.width(8.dp)); Text(if (favorite) "Retirer des favoris" else "Ajouter aux favoris") } }
        item { ElanCard {
            Eyebrow(Icons.Outlined.Chair, pose.equipment)
            SectionTitle("Pour t'installer")
            pose.steps.forEachIndexed { i, text -> Text("${i + 1}. $text") }
        } }
        item { ElanCard(color = MaterialTheme.colorScheme.tertiaryContainer, contentColor = MaterialTheme.colorScheme.onTertiaryContainer) {
            Eyebrow(Icons.Outlined.Info, "Précautions")
            Text(pose.caution)
            Text("Variante plus douce", style = MaterialTheme.typography.titleMedium)
            Text(pose.adaptation)
        } }
        item { ElanCard {
            Text(if (pose.bilateral) "Durée par côté" else "Durée indicative", style = MaterialTheme.typography.titleLarge)
            DurationSelector(duration, { duration = it }, listOf(pose.seconds, 30, 45, 60, 90, 120, 180).distinct().sorted())
            if (pose.bilateral) Text("Le lecteur prévoit un côté gauche puis un côté droit, avec une transition.")
            Button(onClick = { onPractice(duration) }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.PlayArrow, null); Spacer(Modifier.width(8.dp)); Text("Démarrer la pratique")
            }
            Text("Installation de 10 s. Tu peux mettre en pause ou quitter une posture à tout moment. Enregistre la pratique à la fin pour l'ajouter au suivi.", style = MaterialTheme.typography.bodySmall)
        } }
        item { Text(YogaCatalog.safety, style = MaterialTheme.typography.bodySmall) }
    }
}

@Composable
fun DurationSelector(value: Int, onValue: (Int) -> Unit, options: List<Int>) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { seconds -> FilterChip(value == seconds, { onValue(seconds) }, label = { Text("${seconds} s") }) }
    }
}

@Composable
fun YogaSessionScreen(session: YogaSession, onStart: (Int, Int) -> Unit, onDuplicate: () -> Unit, onPose: (String) -> Unit) {
    var percent by rememberSaveable(session.id) { mutableStateOf(100) }
    var transition by rememberSaveable(session.id) { mutableStateOf(10) }
    val plan = remember(session, percent, transition) { PracticeCatalog.yogaPlan(session, transition, percent) }
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text(session.title, style = MaterialTheme.typography.headlineLarge); Text(session.description) }
        item { ElanCard {
            Text("${formatDuration(plan.plannedSeconds.toLong())} de pratique", style = MaterialTheme.typography.titleLarge)
            Text("${formatDuration(plan.totalSeconds.toLong())} au total avec installation et transitions")
            Text("Durée des postures", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(50, 100, 150, 200).forEach { v -> FilterChip(percent == v, { percent = v }, label = { Text("$v %") }) }
            }
            Text("Transition entre les postures", style = MaterialTheme.typography.labelLarge)
            DurationSelector(transition, { transition = it }, listOf(5, 10, 15, 20, 30))
            Text("La transition n'est jamais une obligation : mets en pause pour t'installer en sécurité.", style = MaterialTheme.typography.bodySmall)
            Text("Arrête en cas de douleur, vertige ou gêne. En cas de blessure, de problème de santé ou de grossesse, demande un avis adapté avant de pratiquer.", style = MaterialTheme.typography.bodySmall)
            Button(onClick = { onStart(transition, percent) }, modifier = Modifier.fillMaxWidth()) { Text("Démarrer cette séance") }
            OutlinedButton(onClick = onDuplicate) { Text("Personnaliser une copie") }
        } }
        item { Text("Le parcours", style = MaterialTheme.typography.titleLarge) }
        items(session.steps.indices.toList(), key = { it }) { i ->
            val step = session.steps[i]
            val pose = requireNotNull(YogaCatalog.find(step.poseId))
            val actual = plan.steps.filter { it.kind == StepKind.POSE }[i].seconds
            OutlinedCard(onClick = { onPose(step.poseId) }, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("${i + 1}.", Modifier.padding(end = 14.dp), style = MaterialTheme.typography.titleLarge)
                    Column(Modifier.weight(1f)) { Text(pose.title, style = MaterialTheme.typography.titleMedium)
                        Text("$actual s" + if (step.side != PoseSide.NONE) " · ${step.side.label}" else "") }
                    Icon(Icons.Outlined.ChevronRight, "Lire la fiche")
                }
            }
        }
        item { Text(YogaCatalog.safety, style = MaterialTheme.typography.bodySmall) }
    }
}
