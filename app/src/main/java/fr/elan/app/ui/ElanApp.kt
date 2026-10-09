package fr.elan.app.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.elan.app.content.*
import fr.elan.app.domain.*
import fr.elan.app.platform.ReminderScheduler
import fr.elan.app.platform.UpdateChecker
import fr.elan.app.ui.screens.*
import fr.elan.app.ui.theme.ElanTheme

private enum class Tab(val title: String, val icon: ImageVector) {
    TODAY("Accueil", Icons.Outlined.WbSunny), YOGA("Yoga", Icons.Outlined.SelfImprovement),
    BREATHE("Respirer", Icons.Outlined.Air), MOOD("Humeur", Icons.Outlined.FavoriteBorder),
    PROGRESS("Suivi", Icons.Outlined.BarChart),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElanApp(vm: ElanViewModel = viewModel()) {
    val snapshot by vm.snapshot.collectAsStateWithLifecycle()
    val today by vm.today.collectAsStateWithLifecycle()
    val loadError by vm.loadError.collectAsStateWithLifecycle()
    val generation by vm.dataGeneration.collectAsStateWithLifecycle()
    val pendingImport by vm.pendingImport.collectAsStateWithLifecycle()
    val busy by vm.busy.collectAsStateWithLifecycle()
    val practice by vm.practice.collectAsStateWithLifecycle()
    val draft by vm.draft.collectAsStateWithLifecycle()
    val practiceSaving by vm.practiceSaving.collectAsStateWithLifecycle()
    val draftSaving by vm.draftSaving.collectAsStateWithLifecycle()
    val voiceAvailable by vm.voiceAvailable.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableStateOf(Tab.TODAY.name) }
    var selectedPose by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedSession by rememberSaveable { mutableStateOf<String?>(null) }
    var settingsOpen by rememberSaveable { mutableStateOf(false) }
    var confirmExit by rememberSaveable { mutableStateOf(false) }
    var confirmDraftExit by rememberSaveable { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val stateHolder = key(generation, today) { rememberSaveableStateHolder() }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current
    val updateScope = rememberCoroutineScope()
    var notificationsAllowed by remember { mutableStateOf(ReminderScheduler.allowed(context)) }
    val export = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri -> if (uri != null) vm.exportTo(uri) }
    val importer = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> if (uri != null) vm.prepareImport(uri) }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        notificationsAllowed = ReminderScheduler.allowed(context)
        if (granted && notificationsAllowed) vm.setReminder(true) else vm.notifyPermissionDenied()
    }
    LaunchedEffect(vm) { vm.events.collect { snackbar.showSnackbar(it) } }
    LaunchedEffect(generation) { selectedPose = null; selectedSession = null; confirmExit = false; confirmDraftExit = false }
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    vm.refreshDay()
                    notificationsAllowed = ReminderScheduler.allowed(context)
                    updateScope.launch { UpdateChecker.checkAndNotify(context) }
                }
                Lifecycle.Event.ON_STOP -> vm.pauseAll()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer); vm.pauseAll() }
    }
    fun back() {
        if (practiceSaving || draftSaving) return
        when {
            practice != null -> { vm.pausePractice(); confirmExit = true }
            draft != null -> confirmDraftExit = true
            selectedPose != null -> selectedPose = null
            else -> selectedSession = null
        }
    }
    val nested = practice != null || draft != null || selectedPose != null || selectedSession != null
    BackHandler(enabled = nested) { back() }
    ElanTheme(snapshot?.settings ?: UserSettings()) {
        Scaffold(
            topBar = { TopAppBar(
                title = { Text(when { practice != null -> "Ma pratique"; draft != null -> "Ma séance"; nested -> "Mon yoga"; else -> "Élan" }, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = { if (nested) IconButton(onClick = { back() }, enabled = !practiceSaving && !draftSaving) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Retour") } },
                actions = { if (snapshot != null && practice == null && draft == null) IconButton(onClick = { settingsOpen = true }) { Icon(Icons.Outlined.Tune, "Ouvrir mon espace et les réglages") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
            ) },
            bottomBar = { if (!nested && snapshot != null && loadError == null) NavigationBar {
                Tab.entries.forEach { tab -> NavigationBarItem(selected = selectedTab == tab.name, onClick = { selectedTab = tab.name },
                    icon = { Icon(tab.icon, null) }, label = { Text(tab.title, maxLines = 1) }) }
            } },
            snackbarHost = { SnackbarHost(snackbar) },
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding).imePadding(), contentAlignment = Alignment.TopCenter) {
                Box(Modifier.widthIn(max = 760.dp).fillMaxWidth().fillMaxHeight()) {
                    val current = snapshot
                    val playing = practice
                    val editing = draft
                    when {
                        loadError != null -> Column(Modifier.align(Alignment.Center).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("Le journal n'a pas pu s'ouvrir", style = MaterialTheme.typography.titleLarge)
                            Text(loadError.orEmpty()); Button(onClick = vm::reload) { Text("Réessayer") }
                        }
                        current == null -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                        playing != null -> PracticePlayerScreen(playing, current.settings, voiceAvailable, practiceSaving,
                            vm::playPractice, vm::pausePractice, vm::skipPractice, vm::restartPractice,
                            { vm.pausePractice(); confirmExit = true }, vm::savePractice, vm::updateSettings)
                        editing != null -> SessionEditorScreen(editing, draftSaving, vm::changeDraft, vm::saveDraft)
                        else -> {
                            val pose = selectedPose?.let(YogaCatalog::find)
                            val session = (PracticeCatalog.sessions + current.customSessions).firstOrNull { it.id == selectedSession }
                            when {
                                pose != null -> YogaDetailScreen(pose, pose.id in current.favoritePoseIds,
                                    { vm.toggleFavorite(pose.id) }, { vm.launchPose(pose.id, it) })
                                session != null -> YogaSessionScreen(session,
                                    { transition, percent -> vm.launchPractice(PracticeCatalog.yogaPlan(session, transition, percent)) },
                                    { vm.beginEditing(session, copy = true) }, { selectedPose = it })
                                else -> key(generation) { stateHolder.SaveableStateProvider(selectedTab) {
                                    when (Tab.valueOf(selectedTab)) {
                                        Tab.TODAY -> HomeScreen(current, today, { vm.toggleTask(today, it) }, vm::saveRoutine,
                                            vm::deleteRoutine, { vm.saveVictory(today, it) }, { selectedTab = Tab.MOOD.name },
                                            { selectedTab = Tab.YOGA.name }, { selectedTab = Tab.BREATHE.name }, { selectedTab = Tab.PROGRESS.name })
                                        Tab.YOGA -> YogaScreen(current, vm::toggleFavorite, { selectedSession = it }, { vm.beginEditing() },
                                            { id -> current.customSessions.firstOrNull { it.id == id }?.let { vm.beginEditing(it) } },
                                            vm::deleteSession, { selectedPose = it })
                                        Tab.BREATHE -> BreathingScreen(vm::launchPractice)
                                        Tab.MOOD -> MoodScreen(current, today, vm::saveMood, vm::deleteMood, vm::saveVictory)
                                        Tab.PROGRESS -> ProgressScreen(current, today, vm::setWeeklyGoal, vm::deletePractice)
                                    }
                                } }
                            }
                        }
                    }
                }
            }
        }
        if (settingsOpen && snapshot != null) SettingsDialog(
            settings = snapshot!!.settings, busy = busy, onUpdate = vm::updateSettings, onDismiss = { settingsOpen = false },
            onExport = { export.launch("elan-sauvegarde-$today.json") },
            onImport = { importer.launch(arrayOf("application/json", "text/plain", "application/octet-stream")) },
            onErase = vm::eraseAll, notificationsAllowed = notificationsAllowed,
            onReminder = { enabled ->
                if (!enabled) vm.setReminder(false)
                else if (Build.VERSION.SDK_INT >= 33 && !ReminderScheduler.allowed(context)) permission.launch(Manifest.permission.POST_NOTIFICATIONS)
                else vm.setReminder(true)
            },
            onReminderTime = { hour, minute -> vm.setReminder(snapshot!!.settings.reminderEnabled, hour, minute) },
            onNotificationSettings = {
                context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName))
            },
        )
        pendingImport?.let { backup -> AlertDialog(onDismissRequest = vm::cancelImport, title = { Text("Remplacer les données actuelles ?") },
            text = { Text("${backup.days.size} journées, ${backup.routines.size} routines, ${backup.favoritePoseIds.size} favoris, ${backup.customSessions.size} séances et ${backup.practiceHistory.size} pratiques. Tout le contenu local sera remplacé. Exporte d'abord pour le conserver. Le rappel sera désactivé après restauration.") },
            confirmButton = { TextButton(onClick = { settingsOpen = false; vm.confirmImport() }) { Text("Remplacer et restaurer") } },
            dismissButton = { TextButton(onClick = vm::cancelImport) { Text("Annuler") } }) }
        if (confirmExit && practice != null) AlertDialog(onDismissRequest = { confirmExit = false }, title = { Text("Quitter la pratique ?") },
            text = { Text("${formatDuration((practice?.actualMillis ?: 0) / 1000)} de pratique effective. Tu peux enregistrer ce temps même sans terminer le parcours, ou l'abandonner.") },
            confirmButton = { TextButton(onClick = { confirmExit = false; vm.savePractice() }, enabled = (practice?.actualMillis ?: 0) >= 1000 && !practiceSaving) { Text("Enregistrer et quitter") } },
            dismissButton = { FlowRow {
                TextButton(onClick = { confirmExit = false; vm.discardPractice() }, enabled = !practiceSaving) { Text("Abandonner") }
                TextButton(onClick = { confirmExit = false }) { Text("Rester") }
            } })
        if (confirmDraftExit && draft != null) AlertDialog(onDismissRequest = { confirmDraftExit = false }, title = { Text("Quitter le brouillon ?") },
            text = { Text("Les modifications non enregistrées seront perdues. La séance enregistrée auparavant ne sera pas modifiée.") },
            confirmButton = { TextButton(onClick = { confirmDraftExit = false; vm.discardDraft() }) { Text("Abandonner le brouillon") } },
            dismissButton = { TextButton(onClick = { confirmDraftExit = false }) { Text("Continuer la modification") } })
    }
}
