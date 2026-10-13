package fr.elan.app.data

import fr.elan.app.content.YogaCatalog
import fr.elan.app.domain.*
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener

/** Explicit schema, bounded input and transactional v1 -> v2 migration. No silent reset. */
object SnapshotJson {
    const val FORMAT = "elan-personal-journal"
    const val MAX_IMPORT_BYTES = 10 * 1024 * 1024
    const val MAX_DAYS = 20_000

    fun encode(snapshot: AppSnapshot): String = JSONObject().apply {
        put("format", FORMAT)
        put("schemaVersion", 2)
        put("settings", JSONObject().apply {
            with(snapshot.settings) {
                put("firstName", firstName); put("theme", theme.name); put("dynamicColors", dynamicColors)
                put("weeklyGoalMinutes", weeklyGoalMinutes); put("soundEnabled", soundEnabled); put("voiceEnabled", voiceEnabled)
                put("reminderEnabled", reminderEnabled); put("reminderHour", reminderHour); put("reminderMinute", reminderMinute)
            }
        })
        put("routines", JSONArray().apply {
            snapshot.routines.forEach { put(JSONObject().put("id", it.id).put("title", it.title)) }
        })
        put("days", JSONArray().apply {
            snapshot.days.values.sortedBy { it.date }.forEach { day ->
                put(JSONObject().apply {
                    put("date", day.date); put("mood", day.mood ?: JSONObject.NULL)
                    put("note", day.note); put("tags", JSONArray(day.tags.sorted()))
                    put("victory", day.victory); put("completedTaskIds", JSONArray(day.completedTaskIds.sorted()))
                })
            }
        })
        put("favoritePoseIds", JSONArray(snapshot.favoritePoseIds.sorted()))
        put("customSessions", JSONArray().apply {
            snapshot.customSessions.forEach { session -> put(JSONObject().apply {
                put("id", session.id); put("title", session.title); put("description", session.description)
                put("steps", JSONArray().apply {
                    session.steps.forEach { put(JSONObject().put("poseId", it.poseId).put("seconds", it.seconds).put("side", it.side.name)) }
                })
            }) }
        })
        put("practiceHistory", JSONArray().apply {
            snapshot.practiceHistory.forEach { record -> put(JSONObject().apply {
                put("id", record.id); put("date", record.date); put("endedAtEpochMillis", record.endedAtEpochMillis)
                put("kind", record.kind.name); put("title", record.title)
                put("actualSeconds", record.actualSeconds); put("plannedSeconds", record.plannedSeconds); put("completed", record.completed)
            }) }
        })
    }.toString(2)

    fun decode(text: String): AppSnapshot {
        require(text.toByteArray(Charsets.UTF_8).size <= MAX_IMPORT_BYTES) { "Fichier trop volumineux (10 Mo maximum)" }
        val tokener = JSONTokener(text)
        val obj = tokener.nextValue() as? JSONObject ?: throw IllegalArgumentException("Objet JSON attendu")
        require(tokener.nextClean() == 0.toChar()) { "Contenu ajouté après la sauvegarde" }
        require(string(obj, "format", 100) == FORMAT) { "Ce fichier n'est pas une sauvegarde Élan" }
        val version = int(obj, "schemaVersion")
        require(version == 1 || version == 2) { "Version de sauvegarde non prise en charge" }
        val so = obj.getJSONObject("settings")
        var settings = UserSettings(string(so, "firstName", 30), ThemeMode.valueOf(string(so, "theme", 20)), bool(so, "dynamicColors"))
        if (version == 2) settings = settings.copy(
            weeklyGoalMinutes = int(so, "weeklyGoalMinutes").also { require(it in 5..300) },
            soundEnabled = bool(so, "soundEnabled"), voiceEnabled = bool(so, "voiceEnabled"),
            reminderEnabled = bool(so, "reminderEnabled"),
            reminderHour = int(so, "reminderHour").also { require(it in 0..23) },
            reminderMinute = int(so, "reminderMinute").also { require(it in 0..59) },
        )
        val routines = objects(obj, "routines", MAX_ROUTINES).map {
            RoutineTask(string(it, "id", 100, true), string(it, "title", MAX_TASK_LENGTH, true))
        }
        unique(routines.map { it.id })
        val ids = routines.map { it.id }.toSet()
        val days = LinkedHashMap<String, DayEntry>()
        objects(obj, "days", MAX_DAYS).forEach { day ->
            val tags = strings(day.getJSONArray("tags"), moodTags.size)
            val completed = strings(day.getJSONArray("completedTaskIds"), MAX_ROUTINES)
            require(tags.all { it in moodTags }) { "Thème d'humeur inconnu" }
            require(completed.all { it in ids }) { "Routine référencée introuvable" }
            require(day.has("mood")) { "Champ humeur absent" }
            val mood = if (day.isNull("mood")) null else int(day, "mood")
            val note = string(day, "note", MAX_NOTE_LENGTH)
            require(mood != null || (note.isEmpty() && tags.isEmpty()))
            val entry = DayEntry(string(day, "date", 10), mood, note, tags,
                string(day, "victory", MAX_NOTE_LENGTH), completed)
            require(!days.containsKey(entry.date)) { "Date dupliquée dans la sauvegarde" }
            days[entry.date] = entry
        }
        var result = AppSnapshot(settings = settings, routines = routines, days = days)
        if (version == 2) {
            val favorites = strings(obj.getJSONArray("favoritePoseIds"), YogaCatalog.poses.size)
            require(favorites.all { YogaCatalog.find(it) != null }) { "Favori inconnu" }
            val sessions = objects(obj, "customSessions", MAX_CUSTOM_SESSIONS).map { s ->
                val steps = objects(s, "steps", MAX_SESSION_STEPS).map {
                    YogaStep(string(it, "poseId", 100, true), int(it, "seconds"), PoseSide.valueOf(string(it, "side", 20)))
                }
                YogaSession(string(s, "id", 100, true), string(s, "title", 80, true),
                    string(s, "description", 300), steps).also(PracticeActions::validateSession)
            }
            unique(sessions.map { it.id })
            val history = objects(obj, "practiceHistory", MAX_PRACTICE_HISTORY).map {
                PracticeRecord(string(it, "id", 100, true), string(it, "date", 10), long(it, "endedAtEpochMillis"),
                    PracticeKind.valueOf(string(it, "kind", 30)), string(it, "title", 100, true),
                    int(it, "actualSeconds"), int(it, "plannedSeconds"), bool(it, "completed"))
            }
            unique(history.map { it.id })
            result = result.copy(favoritePoseIds = favorites, customSessions = sessions, practiceHistory = history)
        }
        return result
    }
    private fun objects(obj: JSONObject, key: String, limit: Int): List<JSONObject> {
        val array = obj.getJSONArray(key)
        require(array.length() <= limit) { "Trop d'éléments : $key" }
        return (0 until array.length()).map { array.getJSONObject(it) }
    }
    private fun unique(values: List<String>) { require(values.distinct().size == values.size) { "Identifiants ou valeurs dupliqués" } }
    private fun strings(array: JSONArray, limit: Int): Set<String> {
        require(array.length() <= limit) { "Liste trop longue" }
        val values = (0 until array.length()).map {
            (array.get(it) as? String ?: throw IllegalArgumentException("Texte attendu")).also { value -> require(value.length <= 100) }
        }
        unique(values)
        return values.toSet()
    }
    private fun string(obj: JSONObject, key: String, limit: Int, nonblank: Boolean = false): String =
        (obj.get(key) as? String ?: throw IllegalArgumentException("Texte attendu : $key")).also {
            require(it.length <= limit && (!nonblank || it.isNotBlank())) { "Texte invalide : $key" }
        }
    private fun bool(obj: JSONObject, key: String): Boolean =
        obj.get(key) as? Boolean ?: throw IllegalArgumentException("Booléen attendu : $key")
    private fun long(obj: JSONObject, key: String): Long {
        val value = obj.get(key)
        require(value is Int || value is Long) { "Entier attendu : $key" }
        return (value as Number).toLong()
    }
    private fun int(obj: JSONObject, key: String): Int = long(obj, key).also {
        require(it in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()) { "Entier hors limites" }
    }.toInt()
}
