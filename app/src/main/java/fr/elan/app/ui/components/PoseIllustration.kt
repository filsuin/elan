package fr.elan.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import fr.elan.app.content.PoseSketches

/** Each catalog id has explicit geometry. Schematic, not an alignment demonstration. */
@Composable
fun PoseIllustration(id: String, title: String, modifier: Modifier = Modifier) {
    val sketch = PoseSketches.all[id] ?: return
    val ink = MaterialTheme.colorScheme.primary
    val support = MaterialTheme.colorScheme.tertiary
    val backdrop = MaterialTheme.colorScheme.primaryContainer
    Canvas(modifier.semantics { contentDescription = "Repère schématique : $title. Les instructions écrites font référence." }) {
        val ratio = minOf(size.width / 300f, size.height / 180f)
        withTransform({
            translate((size.width - 300f * ratio) / 2f, (size.height - 180f * ratio) / 2f)
            scale(ratio, ratio, Offset.Zero)
        }) {
            drawCircle(backdrop, 75f, Offset(150f, 86f))
            drawLine(support.copy(alpha = 0.35f), Offset(25f, 160f), Offset(275f, 160f), 3f, StrokeCap.Round)
            sketch.supports.forEach { path -> path.zipWithNext().forEach { (a, b) ->
                drawLine(support, Offset(a.x, a.y), Offset(b.x, b.y), 3.5f, StrokeCap.Round)
            } }
            sketch.strokes.forEach { path -> path.zipWithNext().forEach { (a, b) ->
                drawLine(ink, Offset(a.x, a.y), Offset(b.x, b.y), 4.5f, StrokeCap.Round)
            } }
            drawCircle(ink, 10f, Offset(sketch.head.x, sketch.head.y), style = Stroke(3.5f))
        }
    }
}
