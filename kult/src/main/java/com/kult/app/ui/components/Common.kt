package com.kult.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.kult.app.R
import com.kult.app.ui.icons.Glyph
import com.kult.app.ui.icons.SOCIAL_STROKE
import com.kult.app.ui.icons.SyncoIcon
import com.kult.app.ui.theme.Accent
import com.kult.app.ui.theme.BackLink
import com.kult.app.ui.theme.EmptyButtonBorder
import com.kult.app.ui.theme.SyncoTheme
import com.kult.app.ui.theme.syncoText

/** Space left under scrolling content for the floating bottom navigation (118) and the page padding (24). */
val NavClearance = 142.dp

/**
 * Vertically scrolling page with the prototype's side padding, clear of the system bars and bottom navigation.
 * The page shrinks above the keyboard so a focused field scrolls into view.
 */
@Composable
fun SyncoScreen(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = horizontalPadding)
            .navigationBarsPadding()
            .padding(bottom = NavClearance),
        content = content,
    )
}

@Composable
fun Brand(modifier: Modifier = Modifier, withWordmark: Boolean = false) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.logo_clean),
            contentDescription = "Synco",
            modifier = Modifier.size(width = 40.dp, height = 37.dp),
            contentScale = ContentScale.Fit,
        )
        if (withWordmark) {
            Image(
                painter = painterResource(R.drawable.wordmark_clean),
                contentDescription = null,
                modifier = Modifier.size(width = 107.dp, height = 34.dp),
                contentScale = ContentScale.Fit,
                colorFilter = if (SyncoTheme.colors.isDark) ColorFilter.tint(Color.White) else null,
            )
        }
    }
}

@Composable
fun AppHeader(
    modifier: Modifier = Modifier,
    height: Dp = 80.dp,
    start: @Composable () -> Unit = { Brand() },
    end: @Composable () -> Unit = {},
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(height),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        start()
        end()
    }
}

/** "‹ Salas", "‹ Voltar à conversa" links at the top of inner screens. */
@Composable
fun BackLink(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        SyncoIcon(Glyph.ChevronLeft, 20.dp, tint = BackLink, strokeWidth = SOCIAL_STROKE)
        Text(label, style = syncoText(13f, lineHeight = 19.5f), color = BackLink)
    }
}

/** Remote (URL) or bundled (drawable id) photo, cropped to fill its bounds. */
@Composable
fun RemoteImage(
    model: Any,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    AsyncImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier.background(SyncoTheme.colors.chip),
        contentScale = ContentScale.Crop,
        colorFilter = colorFilter,
    )
}

/** White card with the thin border used by most sections. */
fun Modifier.syncoCard(color: Color, borderColor: Color, radius: Dp = 26.dp): Modifier {
    val shape = RoundedCornerShape(radius)
    return clip(shape)
        .background(color)
        .border(1.dp, borderColor, shape)
}

fun Modifier.topBorder(color: Color, width: Dp = 1.dp): Modifier = drawBehind {
    drawLine(color, Offset(0f, 0f), Offset(size.width, 0f), width.toPx())
}

fun Modifier.bottomBorder(color: Color, width: Dp = 1.dp): Modifier = drawBehind {
    val y = size.height - width.toPx() / 2
    drawLine(color, Offset(0f, y), Offset(size.width, y), width.toPx())
}

/** Round button holding a single icon. */
@Composable
fun CircleButton(
    size: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    background: Color = Color.Transparent,
    enabled: Boolean = true,
    shape: Shape = CircleShape,
    content: @Composable () -> Unit,
) {
    Box(
        modifier
            .size(size)
            .alpha(if (enabled) 1f else 0.5f)
            .clip(shape)
            .background(background)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

enum class ButtonStyle { Primary, Secondary }

/** The prototype's `.button.primary` / `.button.secondary`: full width, 52 high, fully rounded. */
@Composable
fun SyncoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ButtonStyle = ButtonStyle.Primary,
    enabled: Boolean = true,
    leading: Glyph? = null,
    trailing: Glyph? = null,
    iconSize: Dp = 17.dp,
    iconStroke: Float = 1.25f,
    spread: Boolean = false,
) {
    val colors = SyncoTheme.colors
    val shape = RoundedCornerShape(28.dp)
    val content = if (style == ButtonStyle.Primary) Color.White else colors.ink
    Row(
        modifier
            .fillMaxWidth()
            .height(52.dp)
            .alpha(if (enabled) 1f else 0.5f)
            .clip(shape)
            .background(if (style == ButtonStyle.Primary) Accent else colors.card)
            .then(if (style == ButtonStyle.Secondary) Modifier.border(1.dp, colors.border, shape) else Modifier)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = if (spread) 22.dp else 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (spread) {
            Arrangement.SpaceBetween
        } else {
            Arrangement.spacedBy(9.dp, Alignment.CenterHorizontally)
        },
    ) {
        if (leading != null) SyncoIcon(leading, iconSize, tint = content, strokeWidth = iconStroke)
        Text(text, style = syncoText(14f, 550, lineHeight = 21f), color = content)
        if (trailing != null) SyncoIcon(trailing, iconSize, tint = content, strokeWidth = iconStroke)
    }
}

/** Rounded label with an optional icon (interests, categories). */
@Composable
fun Pill(
    text: String,
    modifier: Modifier = Modifier,
    glyph: Glyph? = null,
    radius: Dp = 18.dp,
    padding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 9.dp),
    textStyle: TextStyle = syncoText(11f),
) {
    Row(
        modifier
            .clip(RoundedCornerShape(radius))
            .background(SyncoTheme.colors.chip)
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (glyph != null) SyncoIcon(glyph, 14.dp)
        Text(text, style = textStyle, color = SyncoTheme.colors.ink)
    }
}

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    background: Color,
    borderColor: Color? = null,
    radius: Dp = 12.dp,
    padding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
) {
    val colors = SyncoTheme.colors
    val shape = RoundedCornerShape(radius)
    Row(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(background)
            .then(if (borderColor != null) Modifier.border(1.dp, borderColor, shape) else Modifier)
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SyncoIcon(Glyph.Search, 18.dp, tint = colors.searchIcon, strokeWidth = SOCIAL_STROKE)
        PlainTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            modifier = Modifier.weight(1f),
            textStyle = syncoText(14f, lineHeight = 21f),
        )
    }
}

/** Unstyled single-line text input with a placeholder, for fields drawn by their container. */
@Composable
fun PlainTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = syncoText(14f, lineHeight = 21f),
    placeholderColor: Color = SyncoTheme.colors.ink.copy(alpha = 0.5f),
    maxLength: Int = Int.MAX_VALUE,
) {
    val colors = SyncoTheme.colors
    BasicTextField(
        value = value,
        onValueChange = { onValueChange(it.take(maxLength)) },
        modifier = modifier,
        textStyle = textStyle.copy(color = colors.ink),
        singleLine = true,
        cursorBrush = SolidColor(Accent),
        decorationBox = { field ->
            Box(contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) Text(placeholder, style = textStyle, color = placeholderColor, maxLines = 1)
                field()
            }
        },
    )
}

/** Bordered input used in dialogs (name, note, date). */
@Composable
fun BoxedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    textStyle: TextStyle = syncoText(12f, lineHeight = 18f),
    padding: Dp = 12.dp,
    borderColor: Color = SyncoTheme.colors.border,
    maxLength: Int = Int.MAX_VALUE,
) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SyncoTheme.colors.card)
            .border(1.dp, borderColor, shape)
            .padding(padding),
    ) {
        PlainTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            modifier = Modifier.fillMaxWidth(),
            textStyle = textStyle,
            maxLength = maxLength,
        )
    }
}

@Composable
fun DemoNote(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 5.dp),
        style = syncoText(9f, lineHeight = 15.3f),
        color = SyncoTheme.colors.muted,
        textAlign = TextAlign.Center,
    )
}

/** Centered empty or error state (`.social-empty`). */
@Composable
fun SocialEmpty(
    title: String,
    modifier: Modifier = Modifier,
    glyph: Glyph? = null,
    message: String? = null,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
) {
    val colors = SyncoTheme.colors
    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (glyph != null) SyncoIcon(glyph, 28.dp, tint = colors.muted, strokeWidth = SOCIAL_STROKE)
        Text(title, style = syncoText(16f, 550), color = colors.emptyTitle, textAlign = TextAlign.Center)
        if (message != null) {
            Text(
                message,
                style = syncoText(12f, lineHeight = 20.4f),
                color = colors.muted,
                textAlign = TextAlign.Center,
            )
        }
        if (actionLabel != null) {
            val shape = RoundedCornerShape(20.dp)
            Text(
                actionLabel,
                Modifier
                    .clip(shape)
                    .border(1.dp, EmptyButtonBorder, shape)
                    .clickable(role = Role.Button, onClick = onAction)
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                style = syncoText(12f),
                color = Accent,
            )
        }
    }
}
