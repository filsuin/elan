package fr.elan.app

import fr.elan.app.domain.*
import fr.elan.app.content.DailyContent
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class DomainTest {
    private val day = LocalDate.of(2026, 10, 2)

    @Test fun updatesPreserveOtherFieldsAndAvoidDuplicates() {
        var data = JournalActions.toggleRoutine(AppSnapshot(), day, "pause")
        data = JournalActions.saveVictory(data, day, "Tried something")
        data = JournalActions.saveMood(data, day, 2, "First", emptySet())
        data = JournalActions.saveMood(data, day, 4, "Second", setOf("Repos"))
        assertEquals(1, data.days.size)
        assertEquals(4, data.entry(day).mood)
        assertEquals("Tried something", data.entry(day).victory)
        assertTrue("pause" in data.entry(day).completedTaskIds)
    }
    @Test fun aNewDayHasNoCompletedTasks() {
        val data = JournalActions.toggleRoutine(AppSnapshot(), day, "pause")
        assertTrue(data.entry(day.plusDays(1)).completedTaskIds.isEmpty())
    }
    @Test fun deletingMoodDoesNotDeleteVictory() {
        val data = JournalActions.saveVictory(AppSnapshot(), day, "Rested")
        assertEquals("Rested", JournalActions.removeMood(data, day).entry(day).victory)
    }
    @Test fun calendarDaySelectionIsDeterministic() {
        assertEquals(DailyContent.tip(day), DailyContent.tip(day))
        assertNotEquals(DailyContent.tip(day), DailyContent.tip(day.plusDays(1)))
    }
    @Test fun timerTracksRealElapsedTimeAndPause() {
        val timer = Countdown(60_000).start(1000).tick(5000).pause(11_000)
        assertEquals(50_000L, timer.remainingMillis)
        assertFalse(timer.running)
        assertEquals(50_000L, timer.tick(999_000).remainingMillis)
        assertTrue(timer.start(1_000_000).tick(1_100_000).finished)
    }
    @Test(expected = IllegalArgumentException::class) fun invalidMoodIsRejected() {
        JournalActions.saveMood(AppSnapshot(), day, 6, "", emptySet())
    }
}
