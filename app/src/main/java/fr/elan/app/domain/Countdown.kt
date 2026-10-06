package fr.elan.app.domain

/** Monotonic time is injected by the caller; a delayed tick never extends a session. */
data class Countdown(
    val totalMillis: Long,
    val remainingMillis: Long = totalMillis,
    val deadlineMillis: Long? = null,
) {
    init { require(totalMillis > 0 && remainingMillis in 0..totalMillis) }
    val running: Boolean get() = deadlineMillis != null
    val finished: Boolean get() = remainingMillis == 0L
    val secondsLeft: Long get() = (remainingMillis + 999) / 1000
    val progress: Float get() = (remainingMillis.toFloat() / totalMillis).coerceIn(0f, 1f)
    fun start(now: Long): Countdown =
        if (running || finished) this else copy(deadlineMillis = now + remainingMillis)
    fun tick(now: Long): Countdown {
        val deadline = deadlineMillis ?: return this
        val left = (deadline - now).coerceIn(0L, totalMillis)
        return copy(remainingMillis = left, deadlineMillis = if (left == 0L) null else deadline)
    }
    fun pause(now: Long): Countdown = tick(now).copy(deadlineMillis = null)
    fun reset(): Countdown = Countdown(totalMillis)
}
