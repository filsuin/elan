package fr.elan.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import fr.elan.app.domain.Mood
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun frenchDate(date: LocalDate): String = date.format(DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.FRENCH))
    .replaceFirstChar { it.titlecase(Locale.FRENCH) }
fun fullDate(date: LocalDate): String = date.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH))

@Composable
fun screenContentPadding(): PaddingValues {
    val compact = LocalConfiguration.current.screenWidthDp < 360
    return PaddingValues(horizontal = if (compact) 16.dp else 20.dp, vertical = 20.dp)
}

@Composable
fun dialogContentMaxHeight(preferred: Dp): Dp {
    val available = (LocalConfiguration.current.screenHeightDp - 180).coerceAtLeast(120).dp
    return minOf(preferred, available)
}

@Composable
fun ElanCard(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable ColumnScope.() -> Unit,
) {
    val compact = LocalConfiguration.current.screenWidthDp < 360
    Card(modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = color, contentColor = contentColor)) {
        Column(Modifier.padding(horizontal = if (compact) 16.dp else 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
fun SectionTitle(title: String, subtitle: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
        if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun Eyebrow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, null, Modifier.size(18.dp))
        Text(text, Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
    }
}

fun moodIcon(score: Int): ImageVector = when (score) {
    1 -> Icons.Outlined.SentimentVeryDissatisfied
    2 -> Icons.Outlined.SentimentDissatisfied
    3 -> Icons.Outlined.SentimentNeutral
    4 -> Icons.Outlined.SentimentSatisfiedAlt
    else -> Icons.Outlined.SentimentVerySatisfied
}

@Composable
fun MoodSelector(selected: Int?, onSelect: (Int) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val columns = if (maxWidth < 264.dp) 3 else 5
        val itemWidth = (maxWidth - 6.dp * (columns - 1)) / columns
        FlowRow(Modifier.fillMaxWidth().selectableGroup(), horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Mood.entries.forEach { mood ->
            val active = selected == mood.score
            Surface(
                selected = active, onClick = { onSelect(mood.score) },
                modifier = Modifier.width(itemWidth).heightIn(min = 80.dp).semantics {
                    contentDescription = "${mood.score} sur 5 : ${mood.label}"
                    role = Role.RadioButton
                },
                shape = RoundedCornerShape(20.dp),
                color = if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = if (active) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                border = if (active) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
            ) {
                Column(Modifier.padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(moodIcon(mood.score), null, Modifier.size(28.dp))
                    Text(mood.score.toString(), style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
    }
    if (selected != null) {
        val mood = Mood.fromScore(selected)
        Text(mood.label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Text(mood.response, style = MaterialTheme.typography.bodyMedium)
    }
}
