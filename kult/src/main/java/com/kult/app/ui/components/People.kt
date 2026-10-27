package com.kult.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kult.app.data.NotePresence
import com.kult.app.data.Presence
import com.kult.app.ui.icons.Glyph
import com.kult.app.ui.icons.SyncoIcon
import com.kult.app.ui.theme.Accent
import com.kult.app.ui.theme.DotOffline
import com.kult.app.ui.theme.PresenceOffline
import com.kult.app.ui.theme.SyncoColors
import com.kult.app.ui.theme.SyncoTheme

/** Small filled circle (status dots, "ao vivo" markers). */
@Composable
fun Dot(color: Color, size: Dp, modifier: Modifier = Modifier, ringWidth: Dp = 0.dp, ringColor: Color = Color.Transparent) {
    Box(
        modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
            .then(if (ringWidth > 0.dp) Modifier.border(ringWidth, ringColor, CircleShape) else Modifier),
    )
}

/**
 * Color of a friend's presence dot. The Chats screen shows everyone who is watching as online;
 * elsewhere watching is shown in the accent color.
 */
fun SyncoColors.presenceColor(presence: Presence, watchingAsOnline: Boolean = false): Color = when (presence) {
    Presence.Available -> presenceOnline
    Presence.Watching -> if (watchingAsOnline) presenceOnline else Accent
    Presence.Offline -> DotOffline
}

fun SyncoColors.notePresenceColor(presence: NotePresence): Color = when (presence) {
    NotePresence.Online -> presenceOnline
    NotePresence.Offline -> PresenceOffline
    NotePresence.Busy -> Accent
}

/** Round photo with a presence dot on its lower right edge. */
@Composable
fun AvatarWithPresence(
    model: Any,
    size: Dp,
    dotColor: Color,
    ringColor: Color,
    modifier: Modifier = Modifier,
    dotSize: Dp = 10.dp,
    ringWidth: Dp = 2.dp,
    dotInset: Dp = 1.dp,
) {
    Box(modifier.size(size)) {
        RemoteImage(model, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape))
        Dot(
            color = dotColor,
            size = dotSize,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = -dotInset, y = -dotInset),
            ringWidth = ringWidth,
            ringColor = ringColor,
        )
    }
}

/** Medal button that opens the rewards dialog. */
@Composable
fun RewardsButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    CircleButton(34.dp, onClick, modifier, background = SyncoTheme.colors.chip) {
        SyncoIcon(Glyph.Medal, 19.dp, contentDescription = "Ver recompensas")
    }
}
