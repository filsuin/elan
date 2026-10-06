package fr.elan.app.domain

import java.time.LocalDate

/** Pure transformations: every operation preserves unrelated fields of the same day. */
object JournalActions {
    private fun updateDay(s: AppSnapshot, day: LocalDate, transform: (DayEntry) -> DayEntry): AppSnapshot {
        val entry = transform(s.entry(day))
        return s.copy(days = s.days + (day.toString() to entry))
    }

    fun saveMood(s: AppSnapshot, day: LocalDate, score: Int, note: String, tags: Set<String>): AppSnapshot {
        Mood.fromScore(score)
        require(note.length <= MAX_NOTE_LENGTH) { "Note trop longue" }
        require(tags.all { it in moodTags }) { "Thème inconnu" }
        return updateDay(s, day) { it.copy(mood = score, note = note.trim(), tags = tags.toSet()) }
    }

    fun removeMood(s: AppSnapshot, day: LocalDate): AppSnapshot =
        updateDay(s, day) { it.copy(mood = null, note = "", tags = emptySet()) }

    fun saveVictory(s: AppSnapshot, day: LocalDate, text: String): AppSnapshot {
        require(text.length <= MAX_NOTE_LENGTH) { "Texte trop long" }
        return updateDay(s, day) { it.copy(victory = text.trim()) }
    }

    fun toggleRoutine(s: AppSnapshot, day: LocalDate, id: String): AppSnapshot {
        require(s.routines.any { it.id == id }) { "Routine introuvable" }
        return updateDay(s, day) {
            it.copy(completedTaskIds = if (id in it.completedTaskIds) it.completedTaskIds - id else it.completedTaskIds + id)
        }
    }

    fun addRoutine(s: AppSnapshot, id: String, title: String): AppSnapshot {
        require(s.routines.size < MAX_ROUTINES) { "30 routines au maximum" }
        require(id.isNotBlank() && s.routines.none { it.id == id }) { "Identifiant de routine invalide" }
        val clean = validTitle(title)
        return s.copy(routines = s.routines + RoutineTask(id, clean))
    }

    fun renameRoutine(s: AppSnapshot, id: String, title: String): AppSnapshot {
        require(s.routines.any { it.id == id }) { "Routine introuvable" }
        val clean = validTitle(title)
        return s.copy(routines = s.routines.map { if (it.id == id) it.copy(title = clean) else it })
    }

    fun removeRoutine(s: AppSnapshot, id: String): AppSnapshot = s.copy(
        routines = s.routines.filterNot { it.id == id },
        days = s.days.mapValues { (_, entry) -> entry.copy(completedTaskIds = entry.completedTaskIds - id) },
    )

    private fun validTitle(title: String): String = title.trim().also {
        require(it.isNotEmpty() && it.length <= MAX_TASK_LENGTH) { "Saisis entre 1 et 80 caractères" }
    }

    fun moodHistory(s: AppSnapshot, today: LocalDate, windowDays: Int? = null): List<DayEntry> {
        require(windowDays == null || windowDays > 0)
        val first = windowDays?.let { today.minusDays(it.toLong() - 1) }
        return s.days.values.filter { entry ->
            val day = LocalDate.parse(entry.date)
            entry.mood != null && day <= today && (first == null || day >= first)
        }.sortedByDescending { it.date }
    }
}
