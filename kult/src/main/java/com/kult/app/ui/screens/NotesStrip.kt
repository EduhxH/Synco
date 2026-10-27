package com.kult.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kult.app.AppModal
import com.kult.app.SyncoViewModel
import com.kult.app.data.Friend
import com.kult.app.data.Note
import com.kult.app.data.NoteKind
import com.kult.app.data.NotePresence
import com.kult.app.data.SocialData
import com.kult.app.ui.components.AvatarWithPresence
import com.kult.app.ui.components.BoxedTextField
import com.kult.app.ui.components.ButtonStyle
import com.kult.app.ui.components.Dot
import com.kult.app.ui.components.RemoteImage
import com.kult.app.ui.components.SyncoButton
import com.kult.app.ui.components.SyncoModal
import com.kult.app.ui.components.SyncoSelect
import com.kult.app.ui.components.notePresenceColor
import com.kult.app.ui.icons.Glyph
import com.kult.app.ui.icons.SOCIAL_STROKE
import com.kult.app.ui.icons.SyncoIcon
import com.kult.app.ui.theme.Accent
import com.kult.app.ui.theme.NotesBackdrop
import com.kult.app.ui.theme.SyncoTheme
import com.kult.app.ui.theme.syncoText

/**
 * Row of notes and presence: your note first, then each friend's. Tapping your note edits it;
 * tapping a friend's shows it with a shortcut to the conversation (see [NotesDialog]).
 */
@Composable
fun NotesStrip(
    viewModel: SyncoViewModel,
    ringColor: Color,
    modifier: Modifier = Modifier,
    sidePadding: Dp = 4.dp,
) {
    Column(modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(start = sidePadding, end = sidePadding, top = 8.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            NotePerson(
                note = viewModel.myNote,
                avatar = SocialData.myAvatar,
                name = "Sua nota",
                ringColor = ringColor,
                onClick = { viewModel.modal = AppModal.EditNote },
            )
            SocialData.friends.forEachIndexed { index, friend ->
                NotePerson(
                    note = SocialData.demoNotes[index],
                    avatar = friend.avatar,
                    name = friend.firstName,
                    ringColor = ringColor,
                    onClick = { viewModel.modal = AppModal.FriendNote(index) },
                )
            }
        }
        if (viewModel.noteFeedback.isNotEmpty()) {
            Text(
                viewModel.noteFeedback,
                Modifier.padding(top = 8.dp),
                style = syncoText(11f),
                color = SyncoTheme.colors.secondary,
            )
        }
    }
}

/** Dialog to edit your note ([AppModal.EditNote]) or read a friend's ([AppModal.FriendNote]). */
@Composable
fun NotesDialog(modal: AppModal, viewModel: SyncoViewModel) {
    val close = { viewModel.modal = null }
    SyncoModal(
        onDismiss = close,
        backdrop = NotesBackdrop,
        padding = 28.dp,
        borderColor = SyncoTheme.colors.cardBorder,
        closeColor = SyncoTheme.colors.secondary,
        closeLabel = "Fechar nota",
    ) {
        if (modal is AppModal.FriendNote) {
            val friend = SocialData.friends[modal.index]
            FriendNote(
                friend = friend,
                note = SocialData.demoNotes[modal.index],
                onChat = {
                    close()
                    viewModel.openChat(friend.id)
                },
            )
        } else {
            NoteEditor(
                initial = viewModel.myNote,
                onSave = { note ->
                    viewModel.myNote = note
                    viewModel.noteFeedback =
                        if (note.text.isNotEmpty()) "Nota atualizada neste dispositivo." else "Nota removida. Status atualizado."
                    close()
                },
            )
        }
    }
}

@Composable
private fun NotePerson(note: Note, avatar: String, name: String, ringColor: Color, onClick: () -> Unit) {
    val colors = SyncoTheme.colors
    val placeholder = note.text.isEmpty()
    val bubbleShape = RoundedCornerShape(16.dp)
    Column(
        Modifier
            .width(88.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(role = Role.Button, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 44.dp)
                .drawWithContent {
                    drawContent()
                    // Speech tail pointing at the avatar.
                    val half = 4.95.dp.toPx()
                    val x = 32.5.dp.toPx()
                    val y = size.height - 0.5.dp.toPx()
                    val tail = Path().apply {
                        moveTo(x - half, y)
                        lineTo(x, y + half)
                        lineTo(x + half, y)
                        close()
                    }
                    drawPath(tail, colors.card)
                    val stroke = 1.dp.toPx()
                    drawLine(colors.cardBorder, Offset(x - half, y), Offset(x, y + half), stroke)
                    drawLine(colors.cardBorder, Offset(x, y + half), Offset(x + half, y), stroke)
                }
                .clip(bubbleShape)
                .background(colors.card)
                .border(1.dp, colors.cardBorder, bubbleShape)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        ) {
            if (note.kind == NoteKind.Music && !placeholder) {
                SyncoIcon(Glyph.Music, 13.dp, tint = Accent, strokeWidth = SOCIAL_STROKE)
            }
            Text(
                if (placeholder) "Deixe uma nota…" else note.text,
                style = syncoText(10f, lineHeight = 13.5f),
                color = if (placeholder) colors.secondary else colors.ink,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
        AvatarWithPresence(
            model = avatar,
            size = 54.dp,
            dotColor = colors.notePresenceColor(note.presence),
            ringColor = ringColor,
            modifier = Modifier.padding(top = 6.dp),
            ringWidth = 3.dp,
            dotInset = 0.dp,
        )
        Text(
            name,
            Modifier.padding(top = 8.dp),
            style = syncoText(11f, lineHeight = 15.4f),
            color = colors.ink,
            maxLines = 1,
        )
    }
}

@Composable
private fun NoteEditor(initial: Note, onSave: (Note) -> Unit) {
    val colors = SyncoTheme.colors
    var draft by remember { mutableStateOf(initial) }
    val music = draft.kind == NoteKind.Music
    Text(
        "Sua nota",
        Modifier.padding(top = 8.dp, bottom = 12.dp),
        style = syncoText(30f, lineHeight = 45f, letterSpacing = -1f),
        color = colors.ink,
    )
    Text(
        "Uma música ou um pensamento para compartilhar.",
        Modifier.padding(bottom = 20.dp),
        style = syncoText(13f, lineHeight = 20.8f),
        color = colors.secondary,
    )
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        NoteKindOption("Mensagem", Glyph.Message, selected = !music, Modifier.weight(1f)) {
            draft = draft.copy(kind = NoteKind.Message)
        }
        NoteKindOption("Música", Glyph.Music, selected = music, Modifier.weight(1f)) {
            draft = draft.copy(kind = NoteKind.Music)
        }
    }
    NoteFieldLabel(if (music) "Música favorita" else "Mensagem")
    BoxedTextField(
        value = draft.text,
        onValueChange = { draft = draft.copy(text = it) },
        modifier = Modifier.padding(top = 8.dp),
        placeholder = if (music) "Nome da música" else "O que está na sua cabeça?",
        textStyle = syncoText(13f, lineHeight = 19.5f),
        padding = 14.dp,
        borderColor = colors.cardBorder,
        maxLength = 60,
    )
    Text(
        "${draft.text.length}/60",
        Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        style = syncoText(10f),
        color = colors.secondary,
        textAlign = TextAlign.End,
    )
    if (music) {
        NoteFieldLabel("Artista")
        BoxedTextField(
            value = draft.artist,
            onValueChange = { draft = draft.copy(artist = it) },
            modifier = Modifier.padding(top = 8.dp),
            placeholder = "Nome do artista",
            textStyle = syncoText(13f, lineHeight = 19.5f),
            padding = 14.dp,
            borderColor = colors.cardBorder,
            maxLength = 60,
        )
    }
    NoteFieldLabel("Meu status")
    SyncoSelect(
        value = draft.presence.name,
        options = NotePresence.entries.map { it.name to it.label },
        onSelect = { draft = draft.copy(presence = NotePresence.valueOf(it)) },
        modifier = Modifier.padding(top = 8.dp),
        textStyle = syncoText(13f, lineHeight = 19.5f),
        background = colors.card,
        borderColor = colors.cardBorder,
        verticalPadding = 14,
        horizontalPadding = 14,
    )
    Text(
        "Nota e status locais. Músicas são exibidas como texto, sem reprodução ou envio a outros usuários.",
        Modifier.padding(vertical = 20.dp),
        style = syncoText(11f, lineHeight = 17.6f),
        color = colors.secondary,
    )
    SyncoButton(
        "Salvar nota",
        onClick = {
            onSave(draft.copy(text = draft.text.trim(), artist = if (music) draft.artist.trim() else ""))
        },
    )
    if (initial.text.isNotEmpty()) {
        SyncoButton(
            "Remover nota",
            onClick = { onSave(initial.copy(text = "", artist = "")) },
            modifier = Modifier.padding(top = 12.dp),
            style = ButtonStyle.Secondary,
        )
    }
}

@Composable
private fun NoteKindOption(label: String, glyph: Glyph, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val colors = SyncoTheme.colors
    val shape = RoundedCornerShape(24.dp)
    Row(
        modifier
            .clip(shape)
            .background(colors.card)
            .border(1.dp, if (selected) Accent else colors.cardBorder, shape)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        val content = if (selected) colors.ink else colors.secondary
        SyncoIcon(glyph, 16.dp, tint = content, strokeWidth = SOCIAL_STROKE)
        Text(label, style = syncoText(12f), color = content)
    }
}

@Composable
private fun NoteFieldLabel(text: String) {
    Text(text, Modifier.padding(top = 20.dp), style = syncoText(13f, lineHeight = 19.5f), color = SyncoTheme.colors.ink)
}

@Composable
private fun FriendNote(friend: Friend, note: Note, onChat: () -> Unit) {
    val colors = SyncoTheme.colors
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        RemoteImage(friend.avatar, contentDescription = null, modifier = Modifier.size(64.dp).clip(CircleShape))
        Text(
            friend.name,
            Modifier.padding(top = 16.dp, bottom = 12.dp),
            style = syncoText(30f, lineHeight = 45f, letterSpacing = -1f),
            color = colors.ink,
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Dot(colors.notePresenceColor(note.presence), 8.dp)
            Text(note.presence.label, style = syncoText(12f), color = colors.secondary)
        }
        val shape = RoundedCornerShape(20.dp)
        Column(
            Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
                .clip(shape)
                .background(colors.card)
                .border(1.dp, colors.cardBorder, shape)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (note.kind == NoteKind.Music) SyncoIcon(Glyph.Music, 24.dp, tint = Accent, strokeWidth = SOCIAL_STROKE)
            Text(note.text, style = syncoText(20f, 500, lineHeight = 30f), color = colors.ink, textAlign = TextAlign.Center)
            if (note.artist.isNotEmpty()) Text(note.artist, style = syncoText(14f), color = colors.secondary)
        }
        Text(
            "Nota e presença de demonstração.",
            Modifier.padding(vertical = 20.dp),
            style = syncoText(11f, lineHeight = 17.6f),
            color = colors.secondary,
            textAlign = TextAlign.Center,
        )
        SyncoButton("Abrir conversa", onClick = onChat)
    }
}
