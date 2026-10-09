package fr.elan.app.domain

import fr.elan.app.content.YogaCatalog
import java.time.LocalDate
import java.time.DayOfWeek
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters

const val MAX_CUSTOM_SESSIONS = 30
const val MAX_SESSION_STEPS = 40
const val MAX_PRACTICE_HISTORY = 10000

enum class PracticeKind(val label: String) { YOGA("Yoga"), BREATHING("Respiration"), MEDITATION("Pause silencieuse") }
enum class PoseSide(val label: String) { NONE(""), LEFT("Côté gauche"), RIGHT("Côté droit") }

data class YogaStep(val poseId: String, val seconds: Int, val side: PoseSide = PoseSide.NONE) {
    init { require(seconds in 15..300) { "Une posture doit durer entre 15 et 300 secondes" } }
}
data class YogaSession(val id: String, val title: String, val description: String, val steps: List<YogaStep>) {
    val practiceSeconds: Int get() = steps.sumOf { it.seconds }
    val level: Int get() = steps.maxOfOrNull { YogaCatalog.find(it.poseId)?.level?.ordinal ?: 0 } ?: 0
}

data class PracticeRecord(
    val id: String,
    val date: String,
    val endedAtEpochMillis: Long,
    val kind: PracticeKind,
    val title: String,
    val actualSeconds: Int,
    val plannedSeconds: Int,
    val completed: Boolean,
) {
    init {
        require(id.isNotBlank() && id.length <= 100)
        require(LocalDate.parse(date).toString() == date)
        require(endedAtEpochMillis >= 0)
        require(title.isNotBlank() && title.length <= 100)
        require(plannedSeconds in 1..36000 && actualSeconds in 1..plannedSeconds)
    }
}

enum class StepKind { PREPARATION, TRANSITION, POSE, INHALE, EXHALE, SILENCE }
data class TimedStep(
    val title: String,
    val instruction: String,
    val seconds: Int,
    val kind: StepKind,
    val poseId: String? = null,
    val side: PoseSide = PoseSide.NONE,
) {
    init { require(seconds in 1..3600) }
    val isPractice: Boolean get() = kind != StepKind.PREPARATION && kind != StepKind.TRANSITION
}
data class PracticePlan(val title: String, val kind: PracticeKind, val steps: List<TimedStep>) {
    init {
        require(title.isNotBlank() && title.length <= 100)
        require(steps.isNotEmpty() && steps.size <= 2000)
        require(steps.sumOf { it.seconds } <= 36000)
        require(steps.any { it.isPractice })
    }
    val plannedSeconds: Int get() = steps.filter { it.isPractice }.sumOf { it.seconds }
    val totalSeconds: Int get() = steps.sumOf { it.seconds }
}

/** A foreground-only monotonic player. Pauses never count as practice time.
 * Late ticks consume every crossed boundary; skipping never credits the skipped time. */
data class PracticePlayer(
    val plan: PracticePlan,
    val recordId: String,
    val startedOn: String,
    val index: Int = 0,
    val remainingMillis: Long = plan.steps.first().seconds * 1000L,
    val lastTickMillis: Long? = null,
    val actualMillis: Long = 0,
    val elapsedMillis: Long = 0,
    val skippedPractice: Boolean = false,
    val finished: Boolean = false,
) {
    val step: TimedStep get() = plan.steps[index]
    val running: Boolean get() = lastTickMillis != null && !finished
    val secondsLeft: Long get() = (remainingMillis + 999) / 1000
    val stepProgress: Float get() = 1f - remainingMillis.toFloat() / (step.seconds * 1000L)
    val progress: Float get() = if (finished) 1f else
        ((plan.steps.take(index).sumOf { it.seconds } * 1000L + step.seconds * 1000L - remainingMillis).toFloat() /
            (plan.totalSeconds * 1000L)).coerceIn(0f, 1f)

    fun start(now: Long): PracticePlayer = if (running || finished) this else copy(lastTickMillis = now)
    fun tick(now: Long): PracticePlayer {
        val previous = lastTickMillis ?: return this
        if (now < previous || finished) return this
        var delta = now - previous
        var p = this
        while (delta > 0 && !p.finished) {
            val consumed = minOf(delta, p.remainingMillis)
            p = p.copy(
                remainingMillis = p.remainingMillis - consumed,
                actualMillis = p.actualMillis + if (p.step.isPractice) consumed else 0,
                elapsedMillis = p.elapsedMillis + consumed,
            )
            delta -= consumed
            if (p.remainingMillis == 0L) p = p.advance()
        }
        return p.copy(lastTickMillis = if (p.finished) null else now)
    }
    private fun advance(): PracticePlayer = if (index == plan.steps.lastIndex)
        copy(finished = true, remainingMillis = 0, lastTickMillis = null)
    else copy(index = index + 1, remainingMillis = plan.steps[index + 1].seconds * 1000L)
    fun pause(now: Long): PracticePlayer = tick(now).copy(lastTickMillis = null)
    fun skip(now: Long): PracticePlayer {
        val p = tick(now)
        if (p.finished) return p
        return p.copy(skippedPractice = p.skippedPractice || p.step.isPractice).advance()
    }
    fun restart(): PracticePlayer = PracticePlayer(plan, recordId, startedOn)
    fun record(nowEpoch: Long): PracticeRecord? {
        val seconds = (actualMillis / 1000).toInt()
        if (seconds < 1) return null
        return PracticeRecord(recordId, startedOn, nowEpoch, plan.kind, plan.title, seconds,
            plan.plannedSeconds, finished && !skippedPractice)
    }
}

object PracticeActions {
    fun toggleFavorite(s: AppSnapshot, id: String): AppSnapshot {
        require(YogaCatalog.find(id) != null) { "Posture inconnue" }
        return s.copy(favoritePoseIds = if (id in s.favoritePoseIds) s.favoritePoseIds - id else s.favoritePoseIds + id)
    }
    fun validateSession(session: YogaSession) {
        require(session.id.startsWith("custom-") && session.id.length <= 100) { "Identifiant de séance invalide" }
        require(session.title.isNotBlank() && session.title.length <= 80) { "Nom : 1 à 80 caractères" }
        require(session.description.length <= 300)
        require(session.steps.size in 1..MAX_SESSION_STEPS) { "Choisis entre 1 et 40 étapes" }
        session.steps.forEach { step ->
            val pose = requireNotNull(YogaCatalog.find(step.poseId)) { "Posture inconnue" }
            require(if (pose.bilateral) step.side != PoseSide.NONE else step.side == PoseSide.NONE) { "Côté de posture invalide" }
        }
    }
    fun saveSession(s: AppSnapshot, session: YogaSession): AppSnapshot {
        validateSession(session)
        require(s.customSessions.any { it.id == session.id } || s.customSessions.size < MAX_CUSTOM_SESSIONS) { "30 séances personnelles au maximum" }
        val clean = session.copy(title = session.title.trim(), description = session.description.trim())
        return s.copy(customSessions = if (s.customSessions.any { it.id == clean.id })
            s.customSessions.map { if (it.id == clean.id) clean else it } else s.customSessions + clean)
    }
    fun deleteSession(s: AppSnapshot, id: String) = s.copy(customSessions = s.customSessions.filterNot { it.id == id })
    fun saveRecord(s: AppSnapshot, record: PracticeRecord): AppSnapshot {
        if (s.practiceHistory.any { it.id == record.id }) return s // Idempotent after a retry.
        require(s.practiceHistory.size < MAX_PRACTICE_HISTORY) { "Historique plein. Exporte puis supprime d'anciennes pratiques." }
        return s.copy(practiceHistory = s.practiceHistory + record)
    }
    fun deleteRecord(s: AppSnapshot, id: String) = s.copy(practiceHistory = s.practiceHistory.filterNot { it.id == id })
}

data class PracticeStats(val seconds: Long, val count: Int, val completedCount: Int, val activeDays: Int)
object PracticeAnalytics {
    fun history(s: AppSnapshot, today: LocalDate, window: Int? = null, kind: PracticeKind? = null): List<PracticeRecord> {
        require(window == null || window > 0)
        val start = window?.let { today.minusDays(it.toLong() - 1) }
        return s.practiceHistory.filter {
            val day = LocalDate.parse(it.date)
            day <= today && (start == null || day >= start) && (kind == null || it.kind == kind)
        }.sortedWith(compareByDescending<PracticeRecord> { it.date }.thenByDescending { it.endedAtEpochMillis })
    }
    fun stats(records: List<PracticeRecord>) = PracticeStats(records.sumOf { it.actualSeconds.toLong() },
        records.size, records.count { it.completed }, records.map { it.date }.distinct().size)
    fun weekStart(today: LocalDate): LocalDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    fun weekSeconds(s: AppSnapshot, today: LocalDate): Long = history(s, today).filter {
        LocalDate.parse(it.date) >= weekStart(today)
    }.sumOf { it.actualSeconds.toLong() }
    fun streak(s: AppSnapshot, today: LocalDate): Int {
        val days = history(s, today).map { LocalDate.parse(it.date) }.toSet()
        var day = if (today in days) today else today.minusDays(1)
        var result = 0
        while (day in days) { result++; day = day.minusDays(1) }
        return result
    }
}

/** Calendar time, not +24h: retains the chosen local hour across DST changes. */
fun nextReminder(now: ZonedDateTime, hour: Int, minute: Int): ZonedDateTime {
    require(hour in 0..23 && minute in 0..59)
    val candidate = now.toLocalDate().atTime(hour, minute).atZone(now.zone)
    return if (candidate.isAfter(now)) candidate else now.toLocalDate().plusDays(1).atTime(hour, minute).atZone(now.zone)
}
fun formatDuration(seconds: Long): String = "%d:%02d".format(java.util.Locale.ROOT, seconds / 60, seconds % 60)

