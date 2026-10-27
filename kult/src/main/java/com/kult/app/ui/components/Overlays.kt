package com.kult.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kult.app.ui.icons.Glyph
import com.kult.app.ui.icons.SyncoIcon
import com.kult.app.ui.navigation.Tab
import com.kult.app.ui.theme.Accent
import com.kult.app.ui.theme.ModalBackdrop
import com.kult.app.ui.theme.SyncoTheme
import com.kult.app.ui.theme.syncoText

/** Floating bottom navigation with its ring of page background around it. */
@Composable
fun BottomNav(active: Tab, ringColor: Color, onSelect: (Tab) -> Unit, modifier: Modifier = Modifier) {
    val colors = SyncoTheme.colors
    val shape = RoundedCornerShape(48.dp)
    Box(
        modifier
            .navigationBarsPadding()
            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
            .widthIn(max = 520.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(52.dp))
            .background(ringColor)
            .padding(4.dp),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.navBackground)
                .border(1.dp, colors.navBorder, shape)
                .padding(9.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Tab.entries.forEach { tab ->
                val selected = tab == active
                Column(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(36.dp))
                        .background(if (selected) colors.card else Color.Transparent)
                        .clickable(role = Role.Tab) { onSelect(tab) }
                        .padding(vertical = 11.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    SyncoIcon(tab.glyph, 23.dp, tint = if (selected) Accent else colors.ink)
                    Text(
                        tab.label,
                        style = syncoText(10f, if (selected) 600 else 400, lineHeight = 15f),
                        color = colors.ink,
                    )
                }
            }
        }
    }
}

/** Confirmation that slides up above the bottom navigation (`.toast`). */
@Composable
fun ToastBanner(message: String, modifier: Modifier = Modifier) {
    val colors = SyncoTheme.colors
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier
            .navigationBarsPadding()
            .padding(start = 19.dp, end = 19.dp, bottom = 95.dp)
            .widthIn(max = 338.dp)
            .shadow(10.dp, shape, ambientColor = Color.Black.copy(alpha = 0.2f), spotColor = Color.Black.copy(alpha = 0.2f))
            .clip(shape)
            .background(colors.toastBackground)
            .border(1.dp, colors.toastBorder, shape)
            .padding(horizontal = 22.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SyncoIcon(Glyph.Check, 18.dp, tint = Accent)
        Text(message, style = syncoText(12f, lineHeight = 18f), color = colors.heading)
    }
}

/**
 * Centered dialog over a dimmed backdrop (`.modal`). Tapping the backdrop or the × closes it;
 * content taller than the screen scrolls.
 */
@Composable
fun SyncoModal(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    backdrop: Color = ModalBackdrop,
    padding: Dp = 25.dp,
    background: Color = SyncoTheme.colors.background,
    borderColor: Color = SyncoTheme.colors.border,
    closeColor: Color = SyncoTheme.colors.muted,
    closeLabel: String = "Fechar",
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(26.dp)
    Box(
        Modifier
            .fillMaxSize()
            .background(backdrop)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDismiss)
            .systemBarsPadding()
            .imePadding()
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier
                .widthIn(max = 520.dp)
                .fillMaxWidth()
                .clip(shape)
                .background(background)
                .border(1.dp, borderColor, shape)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
                .verticalScroll(rememberScrollState()),
        ) {
            Column(Modifier.padding(padding), content = content)
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 6.dp, end = 6.dp)
                    .size(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .clickable(role = Role.Button, onClick = onDismiss)
                    .semantics { contentDescription = closeLabel },
                contentAlignment = Alignment.Center,
            ) {
                Text("×", style = syncoText(25f, lineHeight = 37.5f), color = closeColor)
            }
        }
    }
}

/** Title of a dialog (`.modal h2`). */
@Composable
fun ModalTitle(text: String, modifier: Modifier = Modifier, bottomSpacing: Dp = 15.dp) {
    Text(
        text,
        modifier.padding(top = 25.dp, bottom = bottomSpacing),
        style = syncoText(32f, lineHeight = 38.4f, letterSpacing = -1f),
        color = SyncoTheme.colors.ink,
    )
}

/** Body text of a dialog (`.modal p`). */
@Composable
fun ModalText(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        modifier.padding(bottom = 20.dp),
        style = syncoText(13f, lineHeight = 23.4f),
        color = SyncoTheme.colors.secondary,
    )
}

/** Label above a dialog field (`.modal label`). */
@Composable
fun FieldLabel(text: String, modifier: Modifier = Modifier) {
    Text(text, modifier, style = syncoText(12f, lineHeight = 18f), color = SyncoTheme.colors.modalLabel)
}
