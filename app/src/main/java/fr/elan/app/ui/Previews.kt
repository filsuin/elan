package fr.elan.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import fr.elan.app.domain.*
import fr.elan.app.ui.screens.*
import fr.elan.app.ui.theme.ElanTheme
import java.time.LocalDate

private val previewDay = LocalDate.of(2026, 10, 2)

@Preview(name = "Accueil clair", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun HomePreview() {
    ElanTheme(UserSettings(dynamicColors = false)) {
        HomeScreen(AppSnapshot(), previewDay, {}, { _, _ -> }, {}, {}, {}, {})
    }
}

@Preview(name = "Accueil sombre", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun DarkHomePreview() {
    ElanTheme(UserSettings(theme = ThemeMode.DARK, dynamicColors = false)) {
        HomeScreen(AppSnapshot(), previewDay, {}, { _, _ -> }, {}, {}, {}, {})
    }
}

@Preview(name = "Humeur", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun MoodPreview() {
    ElanTheme(UserSettings(dynamicColors = false)) { MoodScreen(AppSnapshot(), previewDay, { _, _, _, _ -> }, {}) }
}

@Preview(name = "Yoga", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun YogaPreview() { ElanTheme(UserSettings(dynamicColors = false)) { YogaScreen {} } }
