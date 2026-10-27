package com.kult.app.ui.icons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Stroke width of the prototype's `Icon` and `ChatIcon` components. */
const val APP_STROKE = 1.25f

/** Stroke width of the prototype's `SocialIcon` component. */
const val SOCIAL_STROKE = 1.4f

/**
 * Line icons of the prototype, drawn on a 24×24 grid with round caps and joins.
 * The path data is the same as in the prototype's `Icon`, `SocialIcon` and `ChatIcon` components.
 */
enum class Glyph(internal val draw: GlyphBuilder.() -> Unit) {
    Film({ rect(3f, 8f, 18f, 13f, 2f); path("m3 8 17-5 1 5M7 7l2-4m4 3 2-4M3 12h18") }),
    Book({ path("M12 5v16M12 5C9 3 6 3 3 4v15c3-1 6-1 9 2 3-3 6-3 9-2V4c-3-1-6-1-9 1Z") }),
    Music({ path("M4 14v-2a8 8 0 0 1 16 0v2"); rect(3f, 12f, 4f, 8f, 2f); rect(17f, 12f, 4f, 8f, 2f) }),
    Star({ path("m12 3 2.8 5.7 6.2.9-4.5 4.4 1.1 6.2-5.6-3-5.6 3 1.1-6.2L3 9.6l6.2-.9Z") }),
    Heart({ path("M20.8 5.5a5.5 5.5 0 0 0-7.8 0L12 6.6l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8L12 22l8.8-8.7a5.5 5.5 0 0 0 0-7.8Z") }),
    Users({ circle(9f, 8f, 3f); path("M3 21v-3a6 6 0 0 1 12 0v3m2-16a3 3 0 0 1 0 6m1 4a5 5 0 0 1 3 5") }),
    User({ circle(12f, 7f, 4f); path("M4 22v-3a8 8 0 0 1 16 0v3") }),
    Gear({ path("m9 3-1 3-3 1-2 3 2 2-1 3 2 3h3l2 3h3l1-3 3-1 2-3-2-2 1-3-2-3h-3l-2-3Z"); circle(12f, 12f, 3f) }),
    Play({ path("m8 4 12 8-12 8Z") }),
    Pause({ path("M8 5v14M16 5v14") }),
    Medal({ circle(12f, 9f, 6f); path("m8 14-2 8 6-3 6 3-2-8m-6-5 2-2 2 2-2 2Z") }),
    Arrow({ path("M5 12h14m-5-5 5 5-5 5") }),
    Check({ path("m5 12 4 4L19 6") }),
    Pin({ path("M19 10c0 5-7 11-7 11S5 15 5 10a7 7 0 0 1 14 0Z"); circle(12f, 10f, 2f) }),
    Expand({ path("M8 3H3v5m13-5h5v5M3 16v5h5m13-5v5h-5") }),
    Volume({ path("m11 4-6 5H2v6h3l6 5ZM15 8a6 6 0 0 1 0 8m3-11a10 10 0 0 1 0 14") }),
    Silent({ path("m11 4-6 5H2v6h3l6 5ZM16 9l6 6m0-6-6 6") }),
    Message({ path("M21 11a9 9 0 0 1-9 9 10 10 0 0 1-4-1l-5 2 2-5a10 10 0 0 1-1-4 9 9 0 0 1 17-1Z") }),
    Close({ path("m6 6 12 12M18 6 6 18") }),
    Rewind({ path("M4 10a8 8 0 1 1 1 9M4 4v6h6") }),
    Search({ circle(10.5f, 10.5f, 6.5f); path("m16 16 5 5") }),
    ChevronRight({ path("m9 5 7 7-7 7") }),
    ChevronLeft({ path("m15 5-7 7 7 7") }),
    Mic({ rect(9f, 2f, 6f, 12f, 3f); path("M5 10v2a7 7 0 0 0 14 0v-2m-7 9v3m-4 0h8") }),
    MicMuted({ path("M9 5V4a3 3 0 0 1 6 0v7M9 9v3a3 3 0 0 0 5 2M5 10v2a7 7 0 0 0 12 5m2-7v2m-7 7v3m-4 0h8M3 3l18 18") }),
    Photo({ rect(3f, 3f, 18f, 18f, 3f); circle(8f, 8f, 1.5f); path("m3 17 5-5 4 4 4-6 5 7") }),
    Sticker({ path("M15 21H6a3 3 0 0 1-3-3V6a3 3 0 0 1 3-3h12a3 3 0 0 1 3 3v9Zm0 0v-4a2 2 0 0 1 2-2h4"); path("M8 11h8m-8-3h5") }),
    Send({ path("m21 3-7 18-4-7-7-4Zm0 0L10 14") }),
}

/** Collects the SVG elements of a glyph as path data. */
internal class GlyphBuilder {
    val paths = mutableListOf<String>()

    fun path(data: String) {
        paths += data
    }

    fun circle(cx: Float, cy: Float, r: Float) {
        paths += "M${cx - r} ${cy}a$r $r 0 1 0 ${2 * r} 0a$r $r 0 1 0 ${-2 * r} 0Z"
    }

    fun rect(x: Float, y: Float, width: Float, height: Float, radius: Float) {
        val w = width - 2 * radius
        val h = height - 2 * radius
        val r = radius
        paths += "M${x + r} ${y}h${w}a$r $r 0 0 1 $r ${r}v${h}a$r $r 0 0 1 ${-r} ${r}" +
            "h${-w}a$r $r 0 0 1 ${-r} ${-r}v${-h}a$r $r 0 0 1 $r ${-r}Z"
    }
}

private val vectors = HashMap<Pair<Glyph, Float>, ImageVector>()

fun Glyph.vector(strokeWidth: Float = APP_STROKE): ImageVector = vectors.getOrPut(this to strokeWidth) {
    val builder = GlyphBuilder().apply(draw)
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        builder.paths.forEach { data ->
            addPath(
                pathData = addPathNodes(data),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = strokeWidth,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }
    }.build()
}

@Composable
fun SyncoIcon(
    glyph: Glyph,
    size: Dp,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    strokeWidth: Float = APP_STROKE,
    contentDescription: String? = null,
) {
    Icon(
        imageVector = glyph.vector(strokeWidth),
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        tint = tint,
    )
}
