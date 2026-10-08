package fr.elan.app

import androidx.compose.material3.Surface
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.Espresso.closeSoftKeyboard
import fr.elan.app.domain.*
import fr.elan.app.ui.screens.*
import fr.elan.app.ui.theme.ElanTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicReference

/** Self-contained UI tests: do not read or erase a user's real journal. */
class UiSmokeTest {
    @get:Rule val compose = createComposeRule()
    private val today = LocalDate.of(2026, 10, 2)

    @Test fun moodCanBeSelectedAndSaved() {
        var score: Int? = null
        compose.setContent {
            ElanTheme(UserSettings(dynamicColors = false)) {
                Surface { MoodScreen(AppSnapshot(), today, { _, value, _, _ -> score = value }, {}, { _, _ -> }) }
            }
        }
        compose.onNodeWithContentDescription("4 sur 5 : Bien").performClick()
        compose.onNodeWithText("Enregistrer mon humeur").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(4, score) }
    }
    @Test fun yogaCatalogShowsFirstPose() {
        compose.setContent { ElanTheme(UserSettings(dynamicColors = false)) { Surface { YogaScreen {} } } }
        compose.onNodeWithText("Assise confortable").performScrollTo().assertIsDisplayed()
    }
    @Test fun historicalVictoryCanBeEdited() {
        val past = today.minusDays(1)
        val snapshot = JournalActions.saveVictory(AppSnapshot(), past, "Premier texte")
        val saved = AtomicReference<Pair<LocalDate, String>?>(null)
        compose.setContent {
            ElanTheme(UserSettings(dynamicColors = false)) {
                Surface { MoodScreen(snapshot, today, { _, _, _, _ -> }, {}, { date, text -> saved.set(date to text) }) }
            }
        }
        compose.onNodeWithTag("mood_list").performScrollToNode(hasText("1 octobre 2026"))
        compose.onNodeWithTag("history_2026-10-01").performClick()
        compose.onNodeWithTag("historical_victory_field").performTextReplacement("Texte corrigé")
        closeSoftKeyboard()
        compose.onNodeWithTag("save_historical_victory").performScrollTo().assertIsEnabled().performClick()
        compose.runOnIdle { assertEquals(past to "Texte corrigé", saved.get()) }
    }
    @Test fun homeOffersCustomRoutine() {
        compose.setContent {
            ElanTheme(UserSettings(dynamicColors = false)) {
                Surface { HomeScreen(AppSnapshot(), today, {}, { _, _ -> }, {}, {}, {}, {}) }
            }
        }
        compose.onNodeWithText("Ajouter un geste").performScrollTo().performClick()
        compose.onNodeWithText("Un nouveau petit geste").assertIsDisplayed()
    }
}
