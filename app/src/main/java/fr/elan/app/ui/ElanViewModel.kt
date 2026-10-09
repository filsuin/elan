package fr.elan.app.ui

import android.app.Application
import android.net.Uri
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.elan.app.ElanApplication
import fr.elan.app.content.YogaCatalog
import fr.elan.app.content.PracticeCatalog
import fr.elan.app.platform.PracticeAudio
import fr.elan.app.platform.ReminderScheduler
import fr.elan.app.data.SnapshotJson
import fr.elan.app.domain.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class ElanViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as ElanApplication).repository
    private val _snapshot = MutableStateFlow<AppSnapshot?>(null)
    val snapshot = _snapshot.asStateFlow()
    private val _today = MutableStateFlow(LocalDate.now())
    val today = _today.asStateFlow()
    private val _loadError = MutableStateFlow<String?>(null)
    val loadError = _loadError.asStateFlow()
    private val messages = Channel<String>(Channel.BUFFERED)
    val events = messages.receiveAsFlow()
    private val _dataGeneration = MutableStateFlow(0)
    val dataGeneration = _dataGeneration.asStateFlow()
    private val _pendingImport = MutableStateFlow<AppSnapshot?>(null)
    val pendingImport = _pendingImport.asStateFlow()
    private val _busy = MutableStateFlow(false)
    val busy = _busy.asStateFlow()
    private var loadJob: Job? = null
    private var timerJob: Job? = null
    private var activePose: String? = null
    private val _timer = MutableStateFlow(Countdown(60_000))
    val timer = _timer.asStateFlow()

    private val audio = PracticeAudio(application)
    val voiceAvailable = audio.voiceAvailable
    private val _practice = MutableStateFlow<PracticePlayer?>(null)
    val practice = _practice.asStateFlow()
    private val _draft = MutableStateFlow<YogaSession?>(null)
    val draft = _draft.asStateFlow()
    private val _practiceSaving = MutableStateFlow(false)
    val practiceSaving = _practiceSaving.asStateFlow()
    private val _draftSaving = MutableStateFlow(false)
    val draftSaving = _draftSaving.asStateFlow()
    private var practiceJob: Job? = null

    init {
        reload()
        viewModelScope.launch {
            try {
                var restoreAlarm = true
                repository.data.map { Triple(it.settings.reminderEnabled, it.settings.reminderHour, it.settings.reminderMinute) }
                    .distinctUntilChanged().collect { (enabled, hour, minute) ->
                        runCatching { ReminderScheduler.schedule(application, UserSettings(reminderEnabled = enabled, reminderHour = hour, reminderMinute = minute), restore = restoreAlarm) }
                        restoreAlarm = false
                    }
            } catch (cancel: CancellationException) { throw cancel }
            catch (_: Exception) { /* Reading errors are already surfaced by reload(). */ }
        }
        viewModelScope.launch {
            while (isActive) { delay(30_000); refreshDay() }
        }
    }

    fun refreshDay() {
        _today.value = LocalDate.now()
        _snapshot.value?.settings?.let { settings ->
            runCatching { ReminderScheduler.schedule(getApplication(), settings) }
        }
    }
    fun reload() {
        loadJob?.cancel()
        _loadError.value = null
        loadJob = viewModelScope.launch {
            try { repository.data.collect {
                _snapshot.value = it; _loadError.value = null
            } }
            catch (cancel: CancellationException) { throw cancel }
            catch (_: Exception) {
                _loadError.value = "Impossible de lire le journal local. Aucune donnée n'a été réinitialisée. Vérifie l'espace libre puis réessaie."
            }
        }
    }

    private fun perform(
        success: String? = null,
        failureMessage: (Throwable) -> String = ::defaultFailureMessage,
        action: suspend () -> Unit,
    ) {
        viewModelScope.launch {
            try { action(); if (success != null) messages.send(success) }
            catch (cancel: CancellationException) { throw cancel }
            catch (error: Exception) { messages.send(failureMessage(error)) }
        }
    }

    private fun defaultFailureMessage(error: Throwable): String = when (error) {
        is IOException -> "Enregistrement impossible. Vérifie l'espace disponible et réessaie."
        is IllegalArgumentException -> error.message ?: "Les informations saisies ne sont pas valides."
        else -> "L'opération n'a pas abouti. Réessaie."
    }

    private fun importFailureMessage(error: Throwable): String = when (error) {
        is SecurityException -> "Accès au fichier refusé. Choisis de nouveau la sauvegarde."
        is IOException -> "Impossible de lire ce fichier. Vérifie qu'il est encore accessible."
        is IllegalArgumentException -> "Sauvegarde refusée : ${error.message ?: "contenu invalide"}."
        else -> "Sauvegarde invalide ou incomplète. Le journal actuel n'a pas été modifié."
    }

    private fun exportFailureMessage(error: Throwable): String = when (error) {
        is SecurityException -> "Accès à l'emplacement refusé. Choisis un autre dossier."
        is IOException -> "Export impossible. Vérifie l'espace disponible et l'emplacement choisi."
        else -> "La sauvegarde n'a pas pu être exportée. Réessaie."
    }

    fun toggleTask(date: LocalDate, id: String) = perform {
        repository.update { JournalActions.toggleRoutine(it, date, id) }
    }
    fun saveRoutine(id: String?, title: String) = perform("Routine enregistrée") {
        repository.update {
            if (id == null) JournalActions.addRoutine(it, UUID.randomUUID().toString(), title)
            else JournalActions.renameRoutine(it, id, title)
        }
    }
    fun deleteRoutine(id: String) = perform("Routine supprimée") {
        repository.update { JournalActions.removeRoutine(it, id) }
    }
    fun saveVictory(date: LocalDate, text: String) = perform(
        if (text.isBlank()) "Petite victoire effacée" else "Petite victoire enregistrée",
    ) {
        require(date <= LocalDate.now()) { "Impossible de modifier une date future" }
        repository.update { JournalActions.saveVictory(it, date, text) }
    }
    fun saveMood(date: LocalDate, mood: Int, note: String, tags: Set<String>) = perform("Humeur enregistrée") {
        require(date <= LocalDate.now())
        repository.update { JournalActions.saveMood(it, date, mood, note, tags) }
    }
    fun deleteMood(date: LocalDate) = perform("Humeur supprimée") {
        repository.update { JournalActions.removeMood(it, date) }
        _dataGeneration.update { it + 1 }
    }
    fun updateSettings(settings: UserSettings) = perform {
        require(settings.firstName.length <= 30)
        require(settings.weeklyGoalMinutes in 5..300 && settings.reminderHour in 0..23 && settings.reminderMinute in 0..59)
        repository.update { it.copy(settings = settings.copy(firstName = settings.firstName.trim())) }
    }
    fun eraseAll() = perform("Données locales effacées") {
        require(!_practiceSaving.value && !_draftSaving.value) { "Attends la fin de l'enregistrement" }
        discardPractice(); _draft.value = null
        repository.replace(AppSnapshot())
        _dataGeneration.update { it + 1 }
        pauseTimer()
    }

    fun exportTo(uri: Uri) = perform("Sauvegarde exportée", ::exportFailureMessage) {
        _busy.value = true
        try {
            val current = repository.data.first()
            withContext(Dispatchers.IO) {
                getApplication<Application>().contentResolver.openOutputStream(uri, "wt")?.use {
                    it.write(SnapshotJson.encode(current).toByteArray(Charsets.UTF_8))
                } ?: throw IOException("Fichier inaccessible")
            }
        } finally { _busy.value = false }
    }

    fun prepareImport(uri: Uri) = perform(failureMessage = ::importFailureMessage) {
        _busy.value = true
        try {
            val imported = withContext(Dispatchers.IO) {
                val text = getApplication<Application>().contentResolver.openInputStream(uri)?.use { input ->
                    val output = ByteArrayOutputStream()
                    val buffer = ByteArray(8192)
                    while (true) {
                        val count = input.read(buffer)
                        if (count < 0) break
                        require(output.size().toLong() + count <= SnapshotJson.MAX_IMPORT_BYTES) { "Fichier trop volumineux" }
                        output.write(buffer, 0, count)
                    }
                    output.toString("UTF-8")
                } ?: throw IOException("Fichier inaccessible")
                SnapshotJson.decode(text)
            }
            _pendingImport.value = imported
        } finally { _busy.value = false }
    }
    fun cancelImport() { _pendingImport.value = null }
    fun confirmImport() {
        val imported = _pendingImport.value ?: return
        _pendingImport.value = null
        perform("Sauvegarde restaurée") {
            require(!_practiceSaving.value && !_draftSaving.value) { "Attends la fin de l'enregistrement" }
            discardPractice(); _draft.value = null
            // A backup must not silently opt this device into notifications.
            repository.replace(imported.copy(settings = imported.settings.copy(reminderEnabled = false)))
            _dataGeneration.update { it + 1 }
        }
    }

    fun selectPose(id: String) {
        if (activePose == id) return
        pauseTimer()
        activePose = id
        _timer.value = Countdown((YogaCatalog.find(id)?.seconds ?: 60) * 1000L)
    }
    fun startTimer() {
        if (_timer.value.running) return
        _timer.update { it.start(SystemClock.elapsedRealtime()) }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timer.value.running) {
                delay(100)
                _timer.update { it.tick(SystemClock.elapsedRealtime()) }
            }
        }
    }
    fun pauseTimer() {
        timerJob?.cancel()
        _timer.update { it.pause(SystemClock.elapsedRealtime()) }
    }
    fun resetTimer() { pauseTimer(); _timer.update { it.reset() } }

    fun toggleFavorite(id: String) = perform { repository.update { PracticeActions.toggleFavorite(it, id) } }
    fun deleteSession(id: String) = perform("Séance supprimée") { repository.update { PracticeActions.deleteSession(it, id) } }
    fun deletePractice(id: String) = perform("Pratique supprimée") { repository.update { PracticeActions.deleteRecord(it, id) } }
    fun setWeeklyGoal(minutes: Int) = perform {
        require(minutes in 5..300)
        repository.update { it.copy(settings = it.settings.copy(weeklyGoalMinutes = minutes)) }
    }
    fun setReminder(enabled: Boolean, hour: Int? = null, minute: Int? = null) = perform {
        require(hour == null || hour in 0..23)
        require(minute == null || minute in 0..59)
        require(!enabled || ReminderScheduler.allowed(getApplication())) { "Autorise les notifications dans les réglages Android avant d'activer le rappel." }
        repository.update { s -> s.copy(settings = s.settings.copy(reminderEnabled = enabled,
            reminderHour = hour ?: s.settings.reminderHour, reminderMinute = minute ?: s.settings.reminderMinute)) }
    }
    fun notifyPermissionDenied() = perform { messages.send("Notifications non autorisées. Le rappel reste désactivé.") }

    fun beginEditing(session: YogaSession? = null, copy: Boolean = false) {
        if (_draftSaving.value) return
        if ((session == null || copy) && (_snapshot.value?.customSessions?.size ?: 0) >= MAX_CUSTOM_SESSIONS) {
            perform { messages.send("30 séances personnelles au maximum. Supprime une séance avant d'en créer une autre.") }
            return
        }
        _draft.value = if (session == null) YogaSession("custom-${UUID.randomUUID()}", "", "", emptyList())
        else if (copy) session.copy(id = "custom-${UUID.randomUUID()}", title = "${session.title} (copie)".take(80)) else session
    }
    fun changeDraft(session: YogaSession) { if (!_draftSaving.value) _draft.value = session }
    fun discardDraft() { if (!_draftSaving.value) _draft.value = null }
    fun saveDraft() {
        if (_draftSaving.value) return
        val session = _draft.value ?: return
        _draftSaving.value = true
        perform("Séance enregistrée") {
            try { repository.update { PracticeActions.saveSession(it, session) }; _draft.value = null }
            finally { _draftSaving.value = false }
        }
    }
    fun launchPractice(plan: PracticePlan) {
        // UI asks before leaving an active session; never overwrite an unsaved practice.
        if (_practice.value != null) return
        pauseTimer()
        _practice.value = PracticePlayer(plan, UUID.randomUUID().toString(), LocalDate.now().toString())
    }
    fun launchPose(id: String, seconds: Int) {
        val pose = YogaCatalog.find(id) ?: return
        launchPractice(PracticeCatalog.yogaPlan(YogaSession("single-$id", pose.title, "", PracticeCatalog.stepsFor(id, seconds))))
    }
    fun playPractice() {
        if (_practiceSaving.value) return
        val current = _practice.value ?: return
        if (current.running || current.finished) return
        _practice.value = current.start(SystemClock.elapsedRealtime())
        audio.cue(_practice.value!!, _snapshot.value?.settings ?: UserSettings())
        practiceJob?.cancel()
        practiceJob = viewModelScope.launch {
            while (isActive && _practice.value?.running == true) {
                delay(100)
                val before = _practice.value ?: break
                val after = before.tick(SystemClock.elapsedRealtime())
                _practice.value = after
                if (before.index != after.index || (!before.finished && after.finished))
                    audio.cue(after, _snapshot.value?.settings ?: UserSettings())
            }
        }
    }
    fun pausePractice() {
        practiceJob?.cancel(); practiceJob = null; audio.stop()
        _practice.update { it?.pause(SystemClock.elapsedRealtime()) }
    }
    fun skipPractice() {
        if (_practiceSaving.value) return
        _practice.update { it?.skip(SystemClock.elapsedRealtime()) }
        _practice.value?.let { if (it.running || it.finished) audio.cue(it, _snapshot.value?.settings ?: UserSettings()) }
    }
    fun restartPractice() {
        if (_practiceSaving.value) return
        pausePractice(); _practice.update { it?.restart() }
    }
    fun discardPractice() {
        if (_practiceSaving.value) return
        pausePractice(); _practice.value = null
    }
    fun savePractice() {
        if (_practiceSaving.value) return
        pausePractice()
        val player = _practice.value ?: return
        val record = player.record(System.currentTimeMillis()) ?: run {
            perform { messages.send("Aucune seconde de pratique à enregistrer.") }; return
        }
        _practiceSaving.value = true
        perform("Pratique enregistrée dans ton suivi") {
            try {
                repository.update { PracticeActions.saveRecord(it, record) }
                _practice.value = null
            } finally { _practiceSaving.value = false }
        }
    }
    fun pauseAll() { pauseTimer(); pausePractice() }
    override fun onCleared() { audio.close(); super.onCleared() }
}
