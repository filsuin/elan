package fr.elan.app

import fr.elan.app.content.*
import fr.elan.app.domain.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class PracticeTest {
    @Test fun everyPoseHasAUniqueSchematicAndInstructions() {
        assertEquals(48, YogaCatalog.poses.size)
        assertEquals(YogaCatalog.poses.map { it.id }.toSet(), PoseSketches.all.keys)
        assertEquals(48, PoseSketches.all.values.toSet().size)
        assertTrue(YogaCatalog.poses.all { it.steps.size == 4 && it.caution.isNotBlank() })
    }
    @Test fun builtInPlansHaveValidDurationsAndReferences() {
        assertEquals(10, PracticeCatalog.sessions.size)
        PracticeCatalog.sessions.forEach { session ->
            val plan = PracticeCatalog.yogaPlan(session)
            assertEquals(session.practiceSeconds, plan.plannedSeconds)
            assertEquals(session.practiceSeconds + session.steps.size * 10, plan.totalSeconds)
        }
    }
    @Test fun delayedTicksAndSkipsNeverCreditSkippedTime() {
        val plan = PracticePlan("Test", PracticeKind.YOGA, listOf(
            TimedStep("Preparation", "", 5, StepKind.PREPARATION),
            TimedStep("One", "", 10, StepKind.POSE), TimedStep("Two", "", 10, StepKind.POSE)))
        val player = PracticePlayer(plan, "r", "2026-10-09").start(0).tick(6000).skip(6000).tick(50000)
        assertEquals(11000L, player.actualMillis)
        assertTrue(player.finished)
        assertFalse(player.record(1)!!.completed)
    }
    @Test fun retriesDoNotDuplicateRecords() {
        val row = PracticeRecord("test", "2026-10-09", 1, PracticeKind.YOGA, "Test", 10, 10, true)
        val saved = PracticeActions.saveRecord(AppSnapshot(), row)
        assertEquals(saved, PracticeActions.saveRecord(saved, row))
    }
    @Test fun favoritesAndCustomSessionsDoNotTouchJournal() {
        val journal = JournalActions.saveVictory(AppSnapshot(), LocalDate.of(2026, 10, 9), "My note")
        val s = PracticeActions.saveSession(PracticeActions.toggleFavorite(journal, "mountain"),
            YogaSession("custom-example", "Test", "", PracticeCatalog.stepsFor("tree_supported")))
        assertEquals(journal.days, s.days)
        assertEquals(listOf(PoseSide.LEFT, PoseSide.RIGHT), s.customSessions.single().steps.map { it.side })
    }
}

