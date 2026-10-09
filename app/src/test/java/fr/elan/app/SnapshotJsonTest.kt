package fr.elan.app

import fr.elan.app.data.SnapshotJson
import fr.elan.app.domain.*
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class SnapshotJsonTest {
    @Test fun fullSnapshotRoundTrip() {
        val date = LocalDate.of(2026, 10, 2)
        var state = AppSnapshot(settings = UserSettings("Éloïse", ThemeMode.DARK, false))
        state = JournalActions.toggleRoutine(state, date, "pause")
        state = JournalActions.saveMood(state, date, 4, "Une note avec é, des guillemets \" et\nun retour", setOf("Repos"))
        state = JournalActions.saveVictory(state, date, "J'ai osé")
        assertEquals(state, SnapshotJson.decode(SnapshotJson.encode(state)))
    }
    @Test fun emptyRoutineListStaysEmpty() {
        val empty = AppSnapshot(routines = emptyList())
        assertEquals(empty, SnapshotJson.decode(SnapshotJson.encode(empty)))
    }
    @Test(expected = IllegalArgumentException::class) fun futureSchemaIsRejected() {
        val obj = JSONObject(SnapshotJson.encode(AppSnapshot())).put("schemaVersion", 999)
        SnapshotJson.decode(obj.toString())
    }
    @Test(expected = IllegalArgumentException::class) fun duplicateDatesAreRejected() {
        val state = JournalActions.saveMood(AppSnapshot(), LocalDate.of(2026, 10, 2), 3, "", emptySet())
        val obj = JSONObject(SnapshotJson.encode(state))
        val days = obj.getJSONArray("days")
        days.put(days.getJSONObject(0))
        SnapshotJson.decode(obj.toString())
    }
    @Test(expected = IllegalArgumentException::class) fun unknownFormatIsRejected() {
        val obj = JSONObject(SnapshotJson.encode(AppSnapshot())).put("format", "not-elan")
        SnapshotJson.decode(obj.toString())
    }
    @Test(expected = IllegalArgumentException::class) fun duplicateTagsAreRejected() {
        val date = LocalDate.of(2026, 10, 2)
        val state = JournalActions.saveMood(AppSnapshot(), date, 3, "", setOf("Repos"))
        val obj = JSONObject(SnapshotJson.encode(state))
        obj.getJSONArray("days").getJSONObject(0).put("tags", org.json.JSONArray(listOf("Repos", "Repos")))
        SnapshotJson.decode(obj.toString())
    }
    @Test(expected = IllegalArgumentException::class) fun oversizedFirstNameIsRejected() {
        val obj = JSONObject(SnapshotJson.encode(AppSnapshot()))
        obj.getJSONObject("settings").put("firstName", "x".repeat(31))
        SnapshotJson.decode(obj.toString())
    }

    @Test fun legacyV1GetsDefaultsWithoutLosingJournal() {
        val original = JournalActions.saveVictory(AppSnapshot(), LocalDate.of(2026, 10, 2), "Conservé")
        val old = JSONObject(SnapshotJson.encode(original)).put("schemaVersion", 1)
        old.remove("favoritePoseIds"); old.remove("customSessions"); old.remove("practiceHistory")
        val migrated = SnapshotJson.decode(old.toString())
        assertEquals(2, migrated.schemaVersion)
        assertEquals(original.days, migrated.days)
        assertFalse(migrated.settings.reminderEnabled)
        assertTrue(migrated.practiceHistory.isEmpty())
    }
    @Test fun v2RoundTripIncludesAllNewData() {
        var s = AppSnapshot(settings = UserSettings(weeklyGoalMinutes = 45, soundEnabled = true,
            voiceEnabled = true, reminderEnabled = true, reminderHour = 8, reminderMinute = 15))
        s = PracticeActions.toggleFavorite(s, "tree_supported")
        s = PracticeActions.saveSession(s, YogaSession("custom-test", "Mon parcours", "Note",
            fr.elan.app.content.PracticeCatalog.stepsFor("tree_supported", 45)))
        s = PracticeActions.saveRecord(s, PracticeRecord("r", "2026-10-09", 1, PracticeKind.BREATHING, "Pause", 60, 60, true))
        assertEquals(s, SnapshotJson.decode(SnapshotJson.encode(s)))
    }
    @Test(expected = IllegalArgumentException::class) fun decimalTimeIsRejectedInsteadOfTruncated() {
        val obj = JSONObject(SnapshotJson.encode(AppSnapshot()))
        obj.getJSONObject("settings").put("reminderHour", 8.5)
        SnapshotJson.decode(obj.toString())
    }
    @Test(expected = IllegalArgumentException::class) fun unknownFavoriteRejected() {
        val obj = JSONObject(SnapshotJson.encode(AppSnapshot())).put("favoritePoseIds", org.json.JSONArray(listOf("missing")))
        SnapshotJson.decode(obj.toString())
    }
    @Test(expected = IllegalArgumentException::class) fun incompleteV2Rejected() {
        val obj = JSONObject(SnapshotJson.encode(AppSnapshot())); obj.remove("practiceHistory")
        try { SnapshotJson.decode(obj.toString()) } catch (e: org.json.JSONException) { throw IllegalArgumentException(e) }
    }
    @Test(expected = IllegalArgumentException::class) fun trailingPayloadRejected() {
        SnapshotJson.decode(SnapshotJson.encode(AppSnapshot()) + " {}")
    }
}
