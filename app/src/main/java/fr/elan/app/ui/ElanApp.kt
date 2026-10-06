package fr.elan.app.ui

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.elan.app.content.YogaCatalog
import fr.elan.app.domain.UserSettings
import fr.elan.app.ui.screens.*
import fr.elan.app.ui.theme.ElanTheme

private enum class Tab(val title: String, val icon: ImageVector) {
    TODAY("Aujourd'hui", Icons.Outlined.WbSunny),
    YOGA("Yoga", Icons.Outlined.SelfImprovement),
    MOOD("Humeur", Icons.Outlined.FavoriteBorder),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElanApp(vm: ElanViewModel = viewModel()) {
    val snapshot by vm.snapshot.collectAsStateWithLifecycle()
    val today by vm.today.collectAsStateWithLifecycle()
    val loadError by vm.loadError.collectAsStateWithLifecycle()
    val timer by vm.timer.collectAsStateWithLifecycle()
    val generation by vm.dataGeneration.collectAsStateWithLifecycle()
    val pendingImport by vm.pendingImport.collectAsStateWithLifecycle()
    val busy by vm.busy.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableStateOf(Tab.TODAY.name) }
    var selectedPose by rememberSaveable { mutableStateOf<String?>(null) }
    var settingsOpen by rememberSaveable { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val stateHolder = key(generation, today) { rememberSaveableStateHolder() }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val export = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) vm.exportTo(uri)
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) vm.prepareImport(uri)
    }
    LaunchedEffect(vm) { vm.events.collect { snackbar.showSnackbar(it) } }
    LaunchedEffect(selectedPose) { selectedPose?.let(vm::selectPose) }
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> vm.refreshDay()
                Lifecycle.Event.ON_STOP -> vm.pauseTimer()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer); vm.pauseTimer() }
    }
    BackHandler(enabled = selectedPose != null) { vm.pauseTimer(); selectedPose = null }
    ElanTheme(snapshot?.settings ?: UserSettings()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (selectedPose == null) "Élan" else "Ma pause yoga", style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        if (selectedPose != null) IconButton(onClick = { vm.pauseTimer(); selectedPose = null }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Retour aux positions")
                        }
                    },
                    actions = {
                        if (snapshot != null) IconButton(onClick = { settingsOpen = true }) {
                            Icon(Icons.Outlined.Tune, "Ouvrir mon espace et les réglages")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                )
            },
            bottomBar = {
                if (selectedPose == null && snapshot != null && loadError == null) NavigationBar {
                    Tab.entries.forEach { tab ->
                        NavigationBarItem(selected = selectedTab == tab.name, onClick = { selectedTab = tab.name },
                            icon = { Icon(tab.icon, null) }, label = { Text(tab.title) })
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbar) },
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding).imePadding(), contentAlignment = Alignment.TopCenter) {
                Box(Modifier.widthIn(max = 760.dp).fillMaxWidth().fillMaxHeight()) {
                    val current = snapshot
                    when {
                        loadError != null -> Column(Modifier.align(Alignment.Center).padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("Le journal n'a pas pu s'ouvrir", style = MaterialTheme.typography.titleLarge)
                            Text(loadError.orEmpty())
                            Button(onClick = vm::reload) { Text("Réessayer") }
                        }
                        current == null -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                        else -> {
                            val pose = selectedPose?.let(YogaCatalog::find)
                            if (pose != null) YogaDetailScreen(pose, timer, vm::startTimer, vm::pauseTimer, vm::resetTimer)
                            else key(generation) {
                                stateHolder.SaveableStateProvider(selectedTab) {
                                    when (Tab.valueOf(selectedTab)) {
                                        Tab.TODAY -> HomeScreen(
                                            snapshot = current, today = today,
                                            onToggle = { vm.toggleTask(today, it) },
                                            onSaveRoutine = vm::saveRoutine, onDeleteRoutine = vm::deleteRoutine,
                                            onVictory = { vm.saveVictory(today, it) },
                                            onMood = { selectedTab = Tab.MOOD.name }, onYoga = { selectedTab = Tab.YOGA.name },
                                        )
                                        Tab.YOGA -> YogaScreen { id -> vm.selectPose(id); selectedPose = id }
                                        Tab.MOOD -> MoodScreen(current, today, vm::saveMood, vm::deleteMood)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (settingsOpen && snapshot != null) SettingsDialog(
            settings = snapshot!!.settings, busy = busy,
            onUpdate = vm::updateSettings, onDismiss = { settingsOpen = false },
            onExport = { export.launch("elan-sauvegarde-$today.json") },
            onImport = { importLauncher.launch(arrayOf("application/json", "text/plain", "application/octet-stream")) },
            onErase = vm::eraseAll,
        )
        pendingImport?.let { backup ->
            AlertDialog(onDismissRequest = vm::cancelImport, title = { Text("Remplacer le journal actuel ?") },
                text = { Text("Cette sauvegarde contient ${backup.days.size} journées et ${backup.routines.size} routines. Elle remplacera toutes les données locales. Exporte d'abord le journal actuel pour le conserver.") },
                confirmButton = { TextButton(onClick = { settingsOpen = false; vm.confirmImport() }) { Text("Remplacer et restaurer") } },
                dismissButton = { TextButton(onClick = vm::cancelImport) { Text("Annuler") } })
        }
    }
}
