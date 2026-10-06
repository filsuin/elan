package fr.elan.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

/** Original, deliberately simplified vector diagrams. No external images or font assets. */
@Composable
fun PoseIllustration(id: String, title: String, modifier: Modifier = Modifier) {
    val ink = MaterialTheme.colorScheme.primary
    val support = MaterialTheme.colorScheme.tertiary
    val backdrop = MaterialTheme.colorScheme.primaryContainer
    Canvas(modifier.semantics { contentDescription = "Schéma simplifié : $title. Suis surtout les étapes et les précautions." }) {
        val ratio = minOf(size.width / 300f, size.height / 180f)
        withTransform({
            translate((size.width - 300f * ratio) / 2f, (size.height - 180f * ratio) / 2f)
            scale(ratio, ratio, Offset.Zero)
        }) {
            drawCircle(backdrop, 76f, Offset(150f, 88f))
            drawLine(support.copy(alpha = 0.45f), Offset(28f, 153f), Offset(272f, 153f), 3f, StrokeCap.Round)
            fun line(x: Float, y: Float, x2: Float, y2: Float) =
                drawLine(ink, Offset(x, y), Offset(x2, y2), 5f, StrokeCap.Round)
            fun head(x: Float, y: Float) = drawCircle(ink, 10f, Offset(x, y), style = Stroke(4f))
            fun furniture(x: Float, y: Float, x2: Float, y2: Float) =
                drawLine(support, Offset(x, y), Offset(x2, y2), 4f, StrokeCap.Round)
            when (id) {
                "seated" -> {
                    furniture(123f, 74f, 123f, 151f); furniture(123f, 110f, 190f, 110f); furniture(186f, 110f, 186f, 151f)
                    head(145f, 43f); line(145f, 56f, 146f, 103f); line(146f, 103f, 204f, 103f)
                    line(204f, 103f, 204f, 148f); line(204f, 148f, 223f, 148f)
                    line(147f, 69f, 164f, 96f); line(164f, 96f, 184f, 101f)
                }
                "child" -> {
                    furniture(76f, 137f, 132f, 137f); furniture(80f, 123f, 128f, 123f)
                    head(112f, 108f)
                    val p = Path().apply { moveTo(126f, 104f); cubicTo(152f, 71f, 184f, 74f, 204f, 111f) }
                    drawPath(p, ink, style = Stroke(5f, cap = StrokeCap.Round))
                    line(204f, 111f, 174f, 138f); line(174f, 138f, 226f, 144f)
                    line(133f, 103f, 129f, 135f); line(129f, 135f, 90f, 135f)
                }
                "cat" -> {
                    head(83f, 90f); line(95f, 92f, 109f, 91f)
                    val p = Path().apply { moveTo(108f, 91f); cubicTo(137f, 64f, 181f, 66f, 210f, 97f) }
                    drawPath(p, ink, style = Stroke(5f, cap = StrokeCap.Round))
                    line(111f, 92f, 106f, 146f); line(106f, 146f, 89f, 146f)
                    line(208f, 96f, 210f, 140f); line(210f, 140f, 244f, 145f)
                }
                "chair" -> {
                    furniture(177f, 96f, 266f, 96f); furniture(183f, 96f, 183f, 151f)
                    furniture(259f, 66f, 259f, 151f)
                    head(56f, 133f); line(70f, 136f, 149f, 139f)
                    line(149f, 139f, 183f, 85f); line(183f, 85f, 243f, 85f)
                    line(82f, 136f, 134f, 146f)
                }
                "side" -> {
                    furniture(38f, 143f, 76f, 143f)
                    head(58f, 129f); line(72f, 130f, 148f, 127f)
                    line(148f, 127f, 193f, 143f); line(193f, 143f, 232f, 130f)
                    line(148f, 133f, 189f, 148f); line(189f, 148f, 239f, 139f)
                    line(88f, 131f, 110f, 145f); line(110f, 145f, 137f, 143f)
                }
                else -> {
                    head(56f, 133f); line(70f, 136f, 151f, 138f)
                    line(151f, 138f, 184f, 93f); line(184f, 93f, 218f, 145f)
                    line(218f, 145f, 239f, 145f); line(86f, 138f, 136f, 146f)
                }
            }
        }
    }
}
