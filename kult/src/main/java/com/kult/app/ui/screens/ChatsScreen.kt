package com.kult.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.kult.app.SyncoViewModel
import com.kult.app.data.Friend
import com.kult.app.data.SocialData
import com.kult.app.ui.components.AppHeader
import com.kult.app.ui.components.AvatarWithPresence
import com.kult.app.ui.components.DemoNote
import com.kult.app.ui.components.Dot
import com.kult.app.ui.components.RemoteImage
import com.kult.app.ui.components.SearchField
import com.kult.app.ui.components.SocialEmpty
import com.kult.app.ui.components.SyncoScreen
import com.kult.app.ui.components.bottomBorder
import com.kult.app.ui.components.presenceColor
import com.kult.app.ui.icons.Glyph
import com.kult.app.ui.icons.SOCIAL_STROKE
import com.kult.app.ui.icons.SyncoIcon
import com.kult.app.ui.navigation.Route
import com.kult.app.ui.theme.Accent
import com.kult.app.ui.theme.Chevron
import com.kult.app.ui.theme.SyncoTheme
import com.kult.app.ui.theme.syncoText
import java.util.Locale

private val ptBR: Locale = Locale.forLanguageTag("pt-BR")

@Composable
fun ChatsScreen(viewModel: SyncoViewModel, contactId: String?) {
    val contact = SocialData.friend(contactId)
    when {
        contactId == null -> Inbox(viewModel)
        contact != null -> PrivateConversation(viewModel, contact)
        else -> SyncoScreen {
            AppHeader(height = 64.dp)
            SocialEmpty(
                "Conversa não encontrada",
                actionLabel = "Voltar para as conversas",
                onAction = { viewModel.backTo(Route.Chats()) },
            )
        }
    }
}

@Composable
private fun Inbox(viewModel: SyncoViewModel) {
    val colors = SyncoTheme.colors
    var search by rememberSaveable { mutableStateOf("") }
    var onlyUnread by rememberSaveable { mutableStateOf(false) }
    val visibleFriends = SocialData.friends.filter { friend ->
        friend.name.lowercase(ptBR).contains(search.lowercase(ptBR)) &&
            (!onlyUnread || viewModel.isUnread(friend))
    }
    SyncoScreen {
        AppHeader(height = 64.dp)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 20.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "Conversas",
                Modifier.weight(1f),
                style = syncoText(26f, 600, lineHeight = 39f, letterSpacing = -0.8f),
                color = colors.ink,
            )
            Text(
                viewModel.name,
                Modifier.padding(bottom = 7.dp),
                style = syncoText(12f),
                color = colors.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        SearchField(
            value = search,
            onValueChange = { search = it },
            placeholder = "Pesquisar",
            background = colors.background,
        )
        NotesStrip(
            viewModel,
            ringColor = colors.card,
            modifier = Modifier.padding(top = 20.dp, bottom = 24.dp),
            sidePadding = 0.dp,
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Mensagens", style = syncoText(17f, 600, lineHeight = 25.5f, letterSpacing = -0.3f), color = colors.ink)
            Text(
                if (onlyUnread) "Ver todas" else "Não lidas",
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(role = Role.Button) { onlyUnread = !onlyUnread }
                    .padding(vertical = 8.dp, horizontal = 2.dp),
                style = syncoText(12f),
                color = colors.secondary,
            )
        }
        Column(Modifier.padding(top = 3.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            visibleFriends.forEach { friend ->
                ConversationRow(friend, unread = viewModel.isUnread(friend), onClick = { viewModel.openChat(friend.id) })
            }
        }
        if (visibleFriends.isEmpty()) {
            SocialEmpty(
                if (onlyUnread) "Tudo em dia por aqui." else "Nenhuma conversa encontrada.",
                glyph = Glyph.Message,
                message = if (onlyUnread) "Não há mensagens não lidas nesta seleção." else "Tente buscar por outro nome.",
            )
        }
        DemoNote("Conversas locais de demonstração, sem envio para outros usuários.")
    }
}

private fun SyncoViewModel.isUnread(friend: Friend): Boolean = friend.unread > 0 && friend.id !in readIds

@Composable
private fun ConversationRow(friend: Friend, unread: Boolean, onClick: () -> Unit) {
    val colors = SyncoTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        AvatarWithPresence(
            model = friend.avatar,
            size = 58.dp,
            dotColor = colors.presenceColor(friend.status, watchingAsOnline = true),
            ringColor = colors.card,
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                friend.name,
                style = syncoText(14f, if (unread) 600 else 500),
                color = if (unread) colors.ink else colors.readName,
            )
            Text(
                buildAnnotatedString {
                    append(friend.preview)
                    append(" ")
                    withStyle(SpanStyle(color = colors.secondary, fontWeight = FontWeight.Normal)) {
                        append("· ${friend.time}")
                    }
                },
                style = syncoText(12f, if (unread) 600 else 400),
                color = if (unread) colors.ink else colors.muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (unread) Dot(Accent, 7.dp)
            SyncoIcon(Glyph.ChevronRight, 17.dp, tint = Chevron, strokeWidth = SOCIAL_STROKE)
        }
    }
}

@Composable
private fun PrivateConversation(viewModel: SyncoViewModel, contact: Friend) {
    val colors = SyncoTheme.colors
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(colors.card)
                .bottomBorder(colors.cardBorder)
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HeaderIconButton(Glyph.ChevronLeft, "Voltar para conversas") { viewModel.backTo(Route.Chats()) }
            AvatarWithPresence(
                model = contact.avatar,
                size = 36.dp,
                dotColor = colors.presenceColor(contact.status, watchingAsOnline = true),
                ringColor = colors.card,
            )
            Column(Modifier.weight(1f)) {
                Text(contact.name, Modifier.padding(bottom = 5.dp), style = syncoText(16f, 600), color = colors.ink)
                Text(contact.statusLabel, style = syncoText(10f), color = colors.muted)
            }
            HeaderIconButton(Glyph.User, "Ver perfil da pessoa") { viewModel.openContactProfile(contact.id) }
        }
        SessionChat(
            viewModel = viewModel,
            conversationId = "private-${contact.id}",
            title = "Conversa privada",
            subtitle = "Conversa local",
            seed = contact.initialMessages,
            isPrivate = true,
            modifier = Modifier.weight(1f),
            intro = { ConversationIntro(viewModel, contact) },
        )
    }
}

@Composable
private fun HeaderIconButton(glyph: Glyph, contentDescription: String, onClick: () -> Unit) {
    Box(
        Modifier
            .size(width = 28.dp, height = 36.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        SyncoIcon(glyph, 20.dp, tint = SyncoTheme.colors.muted, strokeWidth = SOCIAL_STROKE, contentDescription = contentDescription)
    }
}

@Composable
private fun ConversationIntro(viewModel: SyncoViewModel, contact: Friend) {
    val colors = SyncoTheme.colors
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        RemoteImage(contact.avatar, contentDescription = null, modifier = Modifier.size(78.dp).clip(CircleShape))
        Text(
            contact.name,
            Modifier.padding(top = 14.dp),
            style = syncoText(21f, 600, letterSpacing = -0.5f),
            color = colors.ink,
        )
        Text(
            "Uma boa conversa começa com uma sintonia.",
            Modifier
                .padding(top = 10.dp, bottom = 16.dp)
                .widthIn(max = 260.dp),
            style = syncoText(12f, lineHeight = 19.2f),
            color = colors.secondary,
            textAlign = TextAlign.Center,
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
            IntroButton("Ver informações", glyph = null) { viewModel.openContactProfile(contact.id) }
            contact.roomId?.let { roomId ->
                IntroButton("Entrar na sala", glyph = Glyph.Film) { viewModel.joinRoom(roomId) }
            }
        }
        Text(
            "Mensagens de demonstração",
            Modifier.padding(top = 24.dp),
            style = syncoText(10f),
            color = colors.secondary,
        )
    }
}

@Composable
private fun IntroButton(label: String, glyph: Glyph?, onClick: () -> Unit) {
    Row(
        Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SyncoTheme.colors.chip)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (glyph != null) SyncoIcon(glyph, 14.dp, strokeWidth = SOCIAL_STROKE)
        Text(label, style = syncoText(12f), color = SyncoTheme.colors.ink)
    }
}
