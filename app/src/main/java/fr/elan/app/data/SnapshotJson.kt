package fr.elan.app.data

import fr.elan.app.domain.*
import org.json.JSONArray
import org.json.JSONObject

/** Versioned, explicit format. Invalid or newer data is rejected, never silently reset. */
object SnapshotJson {
    const val FORMAT = "elan-personal-journal"
    const val MAX_IMPORT_BYTES = 10 * 1024 * 1024
    const val MAX_DAYS = 20_000

    fun encode(snapshot: AppSnapshot): String = JSONObject().apply {
        put("format", FORMAT)
        put("schemaVersion", snapshot.schemaVersion)
        put("settings", JSONObject().apply {
            put("firstName", snapshot.settings.firstName)
            put("theme", snapshot.settings.theme.name)
            put("dynamicColors", snapshot.settings.dynamicColors)
        })
        put("routines", JSONArray().apply {
            snapshot.routines.forEach { task -> put(JSONObject().put("id", task.id).put("title", task.title)) }
        })
        put("days", JSONArray().apply {
            snapshot.days.values.sortedBy { it.date }.forEach { day ->
                put(JSONObject().apply {
                    put("date", day.date)
                    put("mood", day.mood ?: JSONObject.NULL)
                    put("note", day.note)
                    put("tags", JSONArray(day.tags.toList()))
                    put("victory", day.victory)
                    put("completedTaskIds", JSONArray(day.completedTaskIds.toList()))
                })
            }
        })
    }.toString(2)

    fun decode(text: String): AppSnapshot {
        val obj = JSONObject(text)
        require(obj.getString("format") == FORMAT) { "Ce fichier n'est pas une sauvegarde Élan" }
        require(obj.getInt("schemaVersion") == 1) { "Version de sauvegarde non prise en charge" }
        val settingsObj = obj.getJSONObject("settings")
        val settings = UserSettings(
            firstName = settingsObj.getString("firstName").also { require(it.length <= 30) },
            theme = ThemeMode.valueOf(settingsObj.getString("theme")),
            dynamicColors = settingsObj.getBoolean("dynamicColors"),
        )
        val routinesJson = obj.getJSONArray("routines")
        require(routinesJson.length() <= MAX_ROUTINES) { "Trop de routines" }
        val routines = (0 until routinesJson.length()).map { i ->
            val task = routinesJson.getJSONObject(i)
            RoutineTask(task.getString("id"), task.getString("title")).also {
                require(it.id.isNotBlank() && it.id.length <= 100)
                require(it.title.isNotBlank() && it.title.length <= MAX_TASK_LENGTH)
            }
        }
        require(routines.map { it.id }.distinct().size == routines.size) { "Identifiants dupliqués" }
        val ids = routines.map { it.id }.toSet()
        val entriesJson = obj.getJSONArray("days")
        require(entriesJson.length() <= MAX_DAYS) { "La sauvegarde contient trop de journées" }
        val days = LinkedHashMap<String, DayEntry>()
        for (i in 0 until entriesJson.length()) {
            val day = entriesJson.getJSONObject(i)
            val tags = strings(day.getJSONArray("tags"), moodTags.size, "Trop de thèmes d'humeur")
            val completed = strings(day.getJSONArray("completedTaskIds"), MAX_ROUTINES, "Trop de routines cochées")
            require(tags.all { it in moodTags }) { "Thème d'humeur inconnu" }
            require(completed.all { it in ids }) { "Routine référencée introuvable" }
            val mood = if (day.isNull("mood")) null else day.getInt("mood")
            require(mood != null || (day.getString("note").isEmpty() && tags.isEmpty()))
            val entry = DayEntry(
                date = day.getString("date"), mood = mood,
                note = day.getString("note"), tags = tags,
                victory = day.getString("victory"), completedTaskIds = completed,
            )
            require(!days.containsKey(entry.date)) { "Date dupliquée dans la sauvegarde" }
            days[entry.date] = entry
        }
        return AppSnapshot(settings = settings, routines = routines, days = days)
    }

    private fun strings(array: JSONArray, maximum: Int, error: String): Set<String> {
        require(array.length() <= maximum) { error }
        val values = (0 until array.length()).map { array.getString(it) }
        require(values.distinct().size == values.size) { "Valeurs dupliquées dans la sauvegarde" }
        return values.toSet()
    }
}
