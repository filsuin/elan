package fr.elan.app.domain

import java.time.LocalDate

const val MAX_NOTE_LENGTH = 2000
const val MAX_TASK_LENGTH = 80
const val MAX_ROUTINES = 30

enum class ThemeMode { SYSTEM, LIGHT, DARK }

data class UserSettings(
    val firstName: String = "",
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColors: Boolean = true,
)

data class RoutineTask(val id: String, val title: String)

enum class Mood(val score: Int, val label: String, val response: String) {
    DIFFICULT(1, "Difficile", "Tu n'as pas à tout réussir aujourd'hui. Quel petit soutien te ferait du bien ?"),
    LOW(2, "Pas terrible", "Tu peux ralentir et choisir une seule chose à la fois."),
    NEUTRAL(3, "Neutre", "Une journée ordinaire a aussi sa place ici."),
    GOOD(4, "Bien", "Prends un instant pour remarquer ce qui te fait du bien."),
    GREAT(5, "Très bien", "Garde une trace de ce moment, sans obligation de le reproduire demain.");

    companion object {
        fun fromScore(score: Int): Mood = entries.firstOrNull { it.score == score }
            ?: throw IllegalArgumentException("Humeur invalide : $score")
    }
}

data class DayEntry(
    val date: String,
    val mood: Int? = null,
    val note: String = "",
    val tags: Set<String> = emptySet(),
    val victory: String = "",
    val completedTaskIds: Set<String> = emptySet(),
) {
    init {
        require(LocalDate.parse(date).toString() == date) { "Date ISO invalide" }
        require(mood == null || mood in 1..5) { "Humeur invalide" }
        require(note.length <= MAX_NOTE_LENGTH && victory.length <= MAX_NOTE_LENGTH)
    }

    fun isEmpty(): Boolean =
        mood == null && note.isBlank() && tags.isEmpty() && victory.isBlank() && completedTaskIds.isEmpty()
}

data class AppSnapshot(
    val schemaVersion: Int = 1,
    val settings: UserSettings = UserSettings(),
    val routines: List<RoutineTask> = defaultRoutines(),
    val days: Map<String, DayEntry> = emptyMap(),
) {
    fun entry(date: LocalDate): DayEntry = days[date.toString()] ?: DayEntry(date.toString())
}

fun defaultRoutines() = listOf(
    RoutineTask("pause", "Prendre une minute pour respirer naturellement"),
    RoutineTask("intention", "Choisir une intention réaliste"),
    RoutineTask("movement", "Faire une pause et bouger à mon rythme"),
    RoutineTask("kindness", "M'accorder un petit moment agréable"),
)

val moodTags = listOf("Sommeil", "Travail", "Relations", "Mouvement", "Repos", "Stress", "Dehors", "Temps pour moi")
