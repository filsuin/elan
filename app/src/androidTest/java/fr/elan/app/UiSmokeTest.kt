package fr.elan.app

import androidx.compose.material3.Surface
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import fr.elan.app.domain.*
import fr.elan.app.ui.screens.*
import fr.elan.app.ui.theme.ElanTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

/** Self-contained UI tests: do not read or erase a user's real journal. */
class UiSmokeTest {
    @get:Rule val compose = createComposeRule()
    private val today = LocalDate.of(2026, 10, 2)

    @Test fun moodCanBeSelectedAndSaved() {
        var score: Int? = null
        compose.setContent {
            ElanTheme(UserSettings(dynamicColors = false)) {
                Surface { MoodScreen(AppSnapshot(), today, { _, value, _, _ -> score = value }, {}) }
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
