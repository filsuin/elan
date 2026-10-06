import fr.elan.app.domain.*
import fr.elan.app.content.*
import java.time.LocalDate

private var checks = 0
private fun test(name: String, block: () -> Boolean) {
    check(block()) { "FAIL: $name" }
    checks++
    println("PASS: $name")
}
private fun rejected(block: () -> Unit): Boolean = try { block(); false } catch (_: IllegalArgumentException) { true }

fun main() {
    val day = LocalDate.of(2026, 10, 2)
    val base = AppSnapshot()
    test("four default routines") { base.routines.size == 4 }
    test("unique routine identifiers") { base.routines.map { it.id }.distinct().size == 4 }
    test("empty initial journal") { base.days.isEmpty() }
    test("missing date is not persisted on read") { base.entry(day).mood == null && base.days.isEmpty() }
    var state = JournalActions.toggleRoutine(base, day, "pause")
    test("routine checked") { "pause" in state.entry(day).completedTaskIds }
    test("new day resets checks") { state.entry(day.plusDays(1)).completedTaskIds.isEmpty() }
    state = JournalActions.saveVictory(state, day, "  I asked for help  ")
    test("victory trimmed") { state.entry(day).victory == "I asked for help" }
    state = JournalActions.saveMood(state, day, 2, "  A mixed day  ", setOf("Repos"))
    test("mood saved") { state.entry(day).mood == 2 }
    test("note trimmed") { state.entry(day).note == "A mixed day" }
    test("save mood preserves routine") { "pause" in state.entry(day).completedTaskIds }
    test("save mood preserves victory") { state.entry(day).victory == "I asked for help" }
    state = JournalActions.saveMood(state, day, 4, "Better", emptySet())
    test("upsert has no duplicate dates") { state.days.size == 1 && state.entry(day).mood == 4 }
    state = JournalActions.removeMood(state, day)
    test("remove mood clears note and tags") { state.entry(day).mood == null && state.entry(day).note.isEmpty() && state.entry(day).tags.isEmpty() }
    test("remove mood preserves victory") { state.entry(day).victory.isNotEmpty() }
    test("remove mood preserves routine") { "pause" in state.entry(day).completedTaskIds }
    state = JournalActions.toggleRoutine(state, day, "pause")
    test("routine can be unchecked") { state.entry(day).completedTaskIds.isEmpty() }
    state = JournalActions.addRoutine(state, "custom", "  A walk  ")
    test("custom routine trimmed") { state.routines.last().title == "A walk" }
    state = JournalActions.renameRoutine(state, "custom", "A short walk")
    test("rename keeps stable identifier") { state.routines.last() == RoutineTask("custom", "A short walk") }
    state = JournalActions.toggleRoutine(state, day, "custom")
    state = JournalActions.removeRoutine(state, "custom")
    test("delete routine cleans references") { state.routines.none { it.id == "custom" } && "custom" !in state.entry(day).completedTaskIds }
    test("blank routine rejected") { rejected { JournalActions.addRoutine(base, "x", "  ") } }
    test("oversized routine rejected") { rejected { JournalActions.addRoutine(base, "x", "x".repeat(81)) } }
    test("duplicate identifier rejected") { rejected { JournalActions.addRoutine(base, "pause", "Other") } }
    test("unknown toggle rejected") { rejected { JournalActions.toggleRoutine(base, day, "missing") } }
    test("zero mood rejected") { rejected { JournalActions.saveMood(base, day, 0, "", emptySet()) } }
    test("mood above five rejected") { rejected { JournalActions.saveMood(base, day, 6, "", emptySet()) } }
    test("unknown tag rejected") { rejected { JournalActions.saveMood(base, day, 2, "", setOf("unknown")) } }
    test("oversized note rejected") { rejected { JournalActions.saveMood(base, day, 3, "x".repeat(2001), emptySet()) } }
    test("maximum note accepted") { JournalActions.saveMood(base, day, 3, "x".repeat(2000), emptySet()).entry(day).note.length == 2000 }
    test("oversized victory rejected") { rejected { JournalActions.saveVictory(base, day, "x".repeat(2001)) } }
    var history = base
    for (offset in 0..40) history = JournalActions.saveMood(history, day.minusDays(offset.toLong()), 3, "", emptySet())
    history = JournalActions.saveMood(history, day.plusDays(1), 5, "", emptySet())
    test("seven-day history exact boundary") { JournalActions.moodHistory(history, day, 7).size == 7 }
    test("thirty-day history exact boundary") { JournalActions.moodHistory(history, day, 30).size == 30 }
    test("history excludes future dates") { JournalActions.moodHistory(history, day).size == 41 }
    test("history descending") { JournalActions.moodHistory(history, day).first().date == day.toString() }
    test("nonpositive filter rejected") { rejected { JournalActions.moodHistory(base, day, 0) } }
    test("daily tip deterministic") { DailyContent.tip(day) == DailyContent.tip(day) }
    test("daily tip rotates") { DailyContent.tip(day) != DailyContent.tip(day.plusDays(1)) }
    test("negative epoch still valid") { DailyContent.index(LocalDate.of(1960, 1, 1), 14) in 0..13 }
    test("leap day works") { DailyContent.tip(LocalDate.of(2028, 2, 29)).title.isNotBlank() }
    test("all yoga identifiers unique") { YogaCatalog.poses.map { it.id }.distinct().size == YogaCatalog.poses.size }
    test("six yoga guides") { YogaCatalog.poses.size == 6 }
    test("all yoga guides have precautions and four steps") { YogaCatalog.poses.all { it.steps.size == 4 && it.caution.isNotBlank() && it.seconds > 0 } }
    var timer = Countdown(60_000).start(1000)
    test("timer starts") { timer.running && timer.deadlineMillis == 61_000L }
    timer = timer.tick(1500)
    test("seconds round up") { timer.secondsLeft == 60L }
    timer = timer.tick(8500)
    test("delayed tick has no cumulative drift") { timer.remainingMillis == 52_500L }
    timer = timer.pause(11_000)
    test("pause accounts for elapsed time") { !timer.running && timer.remainingMillis == 50_000L }
    test("paused tick changes nothing") { timer.tick(99_000) == timer }
    timer = timer.start(100_000)
    test("resume preserves remaining duration") { timer.deadlineMillis == 150_000L }
    timer = timer.tick(999_000)
    test("timer reaches zero without going negative") { timer.finished && !timer.running && timer.remainingMillis == 0L }
    test("finished timer requires reset") { timer.start(1_000_000) == timer }
    timer = timer.reset()
    test("reset restores full duration") { timer.remainingMillis == 60_000L && !timer.running }
    test("zero duration rejected") { rejected { Countdown(0) } }
    println("\n$checks checks passed.")
}
