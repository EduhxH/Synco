package com.kult.app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kult.app.SyncoViewModel
import com.kult.app.data.ChatMessage
import com.kult.app.data.MessageKind
import com.kult.app.data.SeedMessage
import com.kult.app.data.SocialData
import com.kult.app.ui.components.CircleButton
import com.kult.app.ui.components.Dot
import com.kult.app.ui.components.PlainTextField
import com.kult.app.ui.components.RemoteImage
import com.kult.app.ui.components.bottomBorder
import com.kult.app.ui.components.syncoCard
import com.kult.app.ui.components.topBorder
import com.kult.app.ui.icons.Glyph
import com.kult.app.ui.icons.SyncoIcon
import com.kult.app.ui.theme.Accent
import com.kult.app.ui.theme.AttachmentText
import com.kult.app.ui.theme.ChatTime
import com.kult.app.ui.theme.SyncoTheme
import com.kult.app.ui.theme.syncoText
import kotlinx.coroutines.delay

/** Sticker colors of the prototype (`.chat-sticker`, `.sticker-1` … `.sticker-3`). */
private class StickerPalette(val background: Color, val border: Color, val text: Color, val label: Color)

private val stickerPalettes = listOf(
    StickerPalette(Color(0xFFE0DED8), Color(0xFFC4C2BD), Color(0xFF242427), Color(0xFF55555B)),
    StickerPalette(Color(0xFFCF606A), Color(0xFFCF606A), Color(0xFF171719), Color(0xFF55555B)),
    StickerPalette(Color(0xFF303135), Color(0xFF66666D), Color(0xFFEEEEEE), Color(0xFFC3C3CA)),
    StickerPalette(Color(0xFFB6B7BE), Color(0xFFB6B7BE), Color(0xFF232428), Color(0xFF55555B)),
)

/**
 * Chat used both in private conversations ([isPrivate]: fills the height, with photo and sticker buttons
 * in the composer) and in live rooms (a card inside the page, with an attachments row).
 * Audio is simulated: recording shows the timer and sends a voice message placeholder.
 */
@Composable
fun SessionChat(
    viewModel: SyncoViewModel,
    conversationId: String,
    title: String,
    subtitle: String,
    seed: List<SeedMessage>,
    isPrivate: Boolean,
    modifier: Modifier = Modifier,
    intro: (@Composable () -> Unit)? = null,
) {
    val colors = SyncoTheme.colors
    val messages = remember(conversationId) { viewModel.messages(conversationId, seed) }
    var text by rememberSaveable(conversationId) { mutableStateOf("") }
    var stickersOpen by remember { mutableStateOf(false) }
    var recording by remember { mutableStateOf(false) }
    var seconds by remember { mutableIntStateOf(0) }
    var micMuted by remember { mutableStateOf(false) }
    var audioMuted by remember { mutableStateOf(false) }

    val pickPhoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) viewModel.send(conversationId, MessageKind.Photo, uri.toString())
    }
    LaunchedEffect(recording) {
        if (recording) {
            seconds = 0
            while (true) {
                delay(1000)
                seconds += 1
            }
        }
    }

    val hasText = text.isNotBlank()
    val sendText = {
        if (hasText && !recording) {
            viewModel.send(conversationId, MessageKind.Text, text.trim())
            text = ""
        }
    }
    val startRecording = {
        stickersOpen = false
        recording = true
    }
    val stopRecording = { discard: Boolean ->
        if (!discard) viewModel.send(conversationId, MessageKind.Audio, formatDuration(seconds))
        recording = false
    }
    val choosePhoto = {
        pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    val sendSticker = { sticker: String ->
        viewModel.send(conversationId, MessageKind.Sticker, sticker)
        stickersOpen = false
    }

    if (isPrivate) {
        Column(modifier.fillMaxWidth()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(subtitle, style = syncoText(10f, lineHeight = 16f), color = colors.muted)
                AudioControls(
                    micMuted = micMuted,
                    audioMuted = audioMuted,
                    onToggleMic = { micMuted = !micMuted },
                    onToggleAudio = { audioMuted = !audioMuted },
                    size = 30.dp,
                    background = Color.Transparent,
                )
            }
            val listState = rememberLazyListState()
            LaunchedEffect(messages.size) {
                listState.animateScrollToItem(messages.size)
            }
            LazyColumn(
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(top = 12.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item(key = "intro") { intro?.invoke() }
                items(messages, key = { it.id }) { message ->
                    MessageRow(message, isPrivate = true, audioMuted = audioMuted)
                }
            }
            if (stickersOpen) {
                StickerPicker(
                    onSelect = sendSticker,
                    onClose = { stickersOpen = false },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .heightIn(max = 180.dp),
                    scrollable = true,
                )
            }
            val shape = RoundedCornerShape(28.dp)
            Row(
                Modifier
                    .padding(top = 8.dp, bottom = 12.dp)
                    .fillMaxWidth()
                    .clip(shape)
                    .background(colors.card)
                    .border(1.dp, colors.cardBorder, shape)
                    .padding(7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (recording) {
                    RecordingBar(micMuted, seconds, onDiscard = { stopRecording(true) }, onSend = { stopRecording(false) }, sendShape = CircleShape)
                } else {
                    ComposerButton(Glyph.Photo, "Enviar foto", onClick = choosePhoto)
                    PlainTextField(
                        value = text,
                        onValueChange = { text = it },
                        placeholder = "Mensagem…",
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp, top = 8.dp, bottom = 8.dp),
                        placeholderColor = colors.muted,
                        maxLength = 2000,
                    )
                    ComposerButton(Glyph.Sticker, "Escolher sticker", onClick = { stickersOpen = !stickersOpen })
                    if (hasText) ComposerButton(Glyph.Mic, "Gravar áudio", onClick = startRecording)
                    SendButton(
                        glyph = if (hasText) Glyph.Send else Glyph.Mic,
                        contentDescription = if (hasText) "Enviar mensagem" else "Gravar mensagem de áudio",
                        shape = CircleShape,
                        onClick = { if (hasText) sendText() else startRecording() },
                    )
                }
            }
        }
    } else {
        Column(
            modifier
                .fillMaxWidth()
                .syncoCard(colors.card, colors.cardBorder)
                .padding(horizontal = 16.dp, vertical = 22.dp),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .bottomBorder(colors.cardBorder)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(title, style = syncoText(17f, 500, lineHeight = 25.5f, letterSpacing = -0.25f), color = colors.ink)
                    Text(subtitle, Modifier.padding(top = 6.dp), style = syncoText(9f, lineHeight = 14.4f), color = colors.muted)
                }
                AudioControls(
                    micMuted = micMuted,
                    audioMuted = audioMuted,
                    onToggleMic = { micMuted = !micMuted },
                    onToggleAudio = { audioMuted = !audioMuted },
                    size = 34.dp,
                    background = colors.chip,
                )
            }
            Column(
                Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .heightIn(min = 190.dp)
                    .padding(start = 2.dp, end = 2.dp, top = 4.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                messages.forEach { message -> MessageRow(message, isPrivate = false, audioMuted = audioMuted) }
            }
            if (stickersOpen) {
                StickerPicker(onSelect = sendSticker, onClose = { stickersOpen = false }, modifier = Modifier.padding(vertical = 10.dp))
            }
            val shape = RoundedCornerShape(20.dp)
            Row(
                Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .clip(shape)
                    .background(colors.background)
                    .border(1.dp, colors.cardBorder, shape)
                    .padding(start = 14.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                if (recording) {
                    RecordingBar(micMuted, seconds, onDiscard = { stopRecording(true) }, onSend = { stopRecording(false) }, sendShape = RoundedCornerShape(9.dp))
                } else {
                    PlainTextField(
                        value = text,
                        onValueChange = { text = it },
                        placeholder = "Comente com a sala…",
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        placeholderColor = colors.muted,
                        maxLength = 2000,
                    )
                    SendButton(
                        glyph = if (hasText) Glyph.Send else Glyph.Mic,
                        contentDescription = if (hasText) "Enviar mensagem" else "Gravar mensagem de áudio",
                        shape = RoundedCornerShape(9.dp),
                        onClick = { if (hasText) sendText() else startRecording() },
                    )
                }
            }
            Row(
                Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .topBorder(colors.cardBorder)
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                AttachmentButton(Glyph.Photo, "Foto", enabled = !recording, onClick = choosePhoto)
                AttachmentButton(Glyph.Sticker, "Stickers", enabled = !recording, onClick = { stickersOpen = !stickersOpen })
                if (!recording) AttachmentButton(Glyph.Mic, "Áudio", enabled = true, onClick = startRecording)
                Spacer(Modifier.weight(1f))
                Text(
                    when {
                        recording -> "Pare para enviar"
                        micMuted -> "Microfone mutado"
                        else -> "Somente ao gravar"
                    },
                    style = syncoText(7f),
                    color = colors.muted,
                )
            }
        }
    }
}

@Composable
private fun AudioControls(
    micMuted: Boolean,
    audioMuted: Boolean,
    onToggleMic: () -> Unit,
    onToggleAudio: () -> Unit,
    size: Dp,
    background: Color,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        AudioToggle(
            glyph = if (micMuted) Glyph.MicMuted else Glyph.Mic,
            muted = micMuted,
            contentDescription = if (micMuted) "Ativar microfone para gravação" else "Mutar microfone para gravação",
            size = size,
            background = background,
            onClick = onToggleMic,
        )
        AudioToggle(
            glyph = if (audioMuted) Glyph.Silent else Glyph.Volume,
            muted = audioMuted,
            contentDescription = if (audioMuted) "Ativar som das mensagens" else "Mutar som das mensagens",
            size = size,
            background = background,
            onClick = onToggleAudio,
        )
    }
}

@Composable
private fun AudioToggle(glyph: Glyph, muted: Boolean, contentDescription: String, size: Dp, background: Color, onClick: () -> Unit) {
    val colors = SyncoTheme.colors
    val alert = if (colors.isDark) Color(0xFFF0A9B6) else Color(0xFFA43946)
    val alertBackground = if (colors.isDark) Color(0xFF35232A) else Color(0xFFFFF2F3)
    CircleButton(size, onClick, background = if (muted) alertBackground else background) {
        SyncoIcon(glyph, 19.dp, tint = if (muted) alert else colors.ink, contentDescription = contentDescription)
    }
}

/** One message, aligned to the end when it is yours. */
@Composable
private fun MessageRow(message: ChatMessage, isPrivate: Boolean, audioMuted: Boolean) {
    val widthFraction = if (isPrivate) 0.8f else 0.9f
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.own) Arrangement.End else Arrangement.Start,
    ) {
        if (message.own) Spacer(Modifier.weight(1f - widthFraction))
        MessageBubble(message, isPrivate, audioMuted, Modifier.weight(widthFraction, fill = false))
        if (!message.own) Spacer(Modifier.weight(1f - widthFraction))
    }
}

@Composable
private fun MessageBubble(message: ChatMessage, isPrivate: Boolean, audioMuted: Boolean, modifier: Modifier) {
    val colors = SyncoTheme.colors
    val shape: Shape = when {
        isPrivate -> RoundedCornerShape(20.dp)
        message.own -> RoundedCornerShape(topStart = 14.dp, topEnd = 3.dp, bottomEnd = 14.dp, bottomStart = 14.dp)
        else -> RoundedCornerShape(topStart = 3.dp, topEnd = 14.dp, bottomEnd = 14.dp, bottomStart = 14.dp)
    }
    val background = when {
        isPrivate && message.own -> Accent
        isPrivate -> colors.chip
        message.own -> colors.ownBubble
        else -> colors.roomBubble
    }
    val textColor = when {
        isPrivate && message.own -> Color.White
        isPrivate -> colors.ink
        else -> colors.roomText
    }
    Column(
        modifier
            .width(IntrinsicSize.Max)
            .clip(shape)
            .background(background)
            .padding(
                if (isPrivate) PaddingValues(horizontal = 14.dp, vertical = 10.dp) else PaddingValues(horizontal = 13.dp, vertical = 11.dp),
            ),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = if (isPrivate) 4.dp else 7.dp),
            horizontalArrangement = if (isPrivate) Arrangement.End else Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!isPrivate) {
                Text(
                    if (message.own) "Você" else message.author,
                    Modifier.padding(end = 18.dp),
                    style = syncoText(10f, 550),
                    color = if (message.own) colors.ownAuthor else colors.roomAuthor,
                )
            }
            Text(
                message.time,
                style = syncoText(8f),
                color = if (isPrivate && message.own) Color.White else ChatTime,
            )
        }
        when (message.kind) {
            MessageKind.Text -> Text(message.content, style = syncoText(14f, lineHeight = 22.4f), color = textColor)
            MessageKind.Sticker -> StickerCard(message.content, Modifier.width(145.dp))
            MessageKind.Photo -> RemoteImage(
                message.content,
                contentDescription = "Foto enviada por ${if (message.own) "você" else message.author}",
                modifier = Modifier
                    .width(220.dp)
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(12.dp)),
            )
            MessageKind.Audio -> Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SyncoIcon(if (audioMuted) Glyph.Silent else Glyph.Play, 18.dp, tint = textColor)
                Text("Mensagem de voz · ${message.content}", style = syncoText(13f), color = textColor)
            }
        }
    }
}

@Composable
private fun StickerCard(text: String, modifier: Modifier = Modifier) {
    val palette = stickerPalettes[SocialData.stickers.indexOf(text).coerceAtLeast(0)]
    val shape = RoundedCornerShape(5.dp)
    Column(
        modifier
            .heightIn(min = 95.dp)
            .clip(shape)
            .background(palette.background)
            .border(1.dp, palette.border, shape)
            .padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
    ) {
        Text("SYNCO", style = syncoText(7f, 650, letterSpacing = 2f), color = palette.label)
        Text(text, style = syncoText(21f, 750, lineHeight = 22.05f, letterSpacing = -0.7f), color = palette.text)
    }
}

@Composable
private fun StickerPicker(onSelect: (String) -> Unit, onClose: () -> Unit, modifier: Modifier = Modifier, scrollable: Boolean = false) {
    val colors = SyncoTheme.colors
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.stickerPickerBackground)
            .border(1.dp, colors.stickerPickerBorder, shape)
            .then(if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier)
            .padding(12.dp),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Stickers Synco", style = syncoText(11f), color = colors.stickerPickerText)
            Box(Modifier.clickable(role = Role.Button, onClick = onClose)) {
                SyncoIcon(Glyph.Close, 19.dp, tint = colors.stickerPickerText, contentDescription = "Fechar stickers")
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SocialData.stickers.chunked(2).forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    pair.forEach { sticker ->
                        StickerCard(
                            sticker,
                            Modifier
                                .weight(1f)
                                .heightIn(min = 100.dp)
                                .clickable(role = Role.Button) { onSelect(sticker) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ComposerButton(glyph: Glyph, contentDescription: String, onClick: () -> Unit) {
    CircleButton(35.dp, onClick, shape = RoundedCornerShape(9.dp)) {
        SyncoIcon(glyph, 19.dp, tint = SyncoTheme.colors.ink, contentDescription = contentDescription)
    }
}

@Composable
private fun SendButton(glyph: Glyph, contentDescription: String, shape: Shape, onClick: () -> Unit) {
    CircleButton(35.dp, onClick, background = Accent, shape = shape) {
        SyncoIcon(glyph, 19.dp, tint = Color.White, contentDescription = contentDescription)
    }
}

@Composable
private fun RecordingBar(micMuted: Boolean, seconds: Int, onDiscard: () -> Unit, onSend: () -> Unit, sendShape: Shape) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 35.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CircleButton(35.dp, onDiscard, shape = RoundedCornerShape(9.dp)) {
            SyncoIcon(Glyph.Close, 19.dp, tint = SyncoTheme.colors.muted, contentDescription = "Descartar gravação")
        }
        Row(
            Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Dot(Accent, 6.dp)
            Text(if (micMuted) "Microfone mutado" else "Gravando áudio", style = syncoText(10f), color = Accent)
            Text(
                formatDuration(seconds),
                style = syncoText(10f).merge(TextStyle(fontFeatureSettings = "tnum")),
                color = SyncoTheme.colors.muted,
            )
        }
        SendButton(Glyph.Send, "Enviar gravação", sendShape, onSend)
    }
}

@Composable
private fun AttachmentButton(glyph: Glyph, label: String, enabled: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .heightIn(min = 32.dp)
            .alpha(if (enabled) 1f else 0.5f)
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        SyncoIcon(glyph, 16.dp, tint = AttachmentText)
        Text(label, style = syncoText(11f), color = AttachmentText)
    }
}

private fun formatDuration(seconds: Int): String = "${seconds / 60}:${(seconds % 60).toString().padStart(2, '0')}"
