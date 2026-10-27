package com.kult.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.kult.app.AppModal
import com.kult.app.SyncoViewModel
import com.kult.app.data.Room
import com.kult.app.data.SeedMessage
import com.kult.app.data.SocialData
import com.kult.app.ui.components.AppHeader
import com.kult.app.ui.components.BackLink
import com.kult.app.ui.components.CircleButton
import com.kult.app.ui.components.DemoNote
import com.kult.app.ui.components.Dot
import com.kult.app.ui.components.RemoteImage
import com.kult.app.ui.components.RewardsButton
import com.kult.app.ui.components.SearchField
import com.kult.app.ui.components.SocialEmpty
import com.kult.app.ui.components.SyncoScreen
import com.kult.app.ui.components.syncoCard
import com.kult.app.ui.icons.Glyph
import com.kult.app.ui.icons.SOCIAL_STROKE
import com.kult.app.ui.icons.SyncoIcon
import com.kult.app.ui.navigation.Route
import com.kult.app.ui.theme.Accent
import com.kult.app.ui.theme.AvatarBorder
import com.kult.app.ui.theme.CaptionDark
import com.kult.app.ui.theme.InviteBorder
import com.kult.app.ui.theme.MoreMembers
import com.kult.app.ui.theme.SyncoTheme
import com.kult.app.ui.theme.VideoBackground
import com.kult.app.ui.theme.VideoMeta
import com.kult.app.ui.theme.syncoText
import java.util.Locale

private val roomFilters = listOf("Todas", "Filmes", "Música", "Livros")

/** The video still is dimmed like the prototype's `brightness(.65) saturate(.65)`. */
private val DimmedVideo = ColorFilter.colorMatrix(
    ColorMatrix().apply {
        setToSaturation(0.65f)
        timesAssign(ColorMatrix().apply { setToScale(0.65f, 0.65f, 0.65f, 1f) })
    },
)

@Composable
fun GroupsScreen(viewModel: SyncoViewModel, roomNotFound: Boolean) {
    val colors = SyncoTheme.colors
    val locale = Locale.forLanguageTag("pt-BR")
    var filter by rememberSaveable { mutableStateOf("Todas") }
    var search by rememberSaveable { mutableStateOf("") }
    val visibleRooms = SocialData.rooms.filter { room ->
        (filter == "Todas" || room.category == filter) &&
            "${room.title} ${room.subtitle}".lowercase(locale).contains(search.lowercase(locale))
    }
    SyncoScreen {
        AppHeader(
            Modifier.padding(bottom = 20.dp),
            end = { RewardsButton(onClick = { viewModel.modal = AppModal.Rewards }) },
        )
        if (roomNotFound) {
            ErrorNotice("Esta sala não foi encontrada. Escolha uma sala abaixo.")
        }
        Column(Modifier.padding(top = 4.dp, bottom = 32.dp)) {
            Text(
                "BOAS COMPANHIAS, BOAS HISTÓRIAS",
                style = syncoText(9f, 600, letterSpacing = 1f),
                color = Accent,
            )
            Text(
                "Melhor juntos.",
                Modifier.padding(vertical = 14.dp),
                style = syncoText(40f, lineHeight = 46f, letterSpacing = -1.5f),
                color = colors.ink,
            )
            Text(
                "Entre em uma sala e compartilhe o momento.",
                style = syncoText(14f, lineHeight = 23.8f),
                color = colors.secondary,
            )
        }
        NotesStrip(
            viewModel,
            ringColor = colors.background,
            modifier = Modifier.padding(bottom = 32.dp),
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Salas públicas", style = syncoText(20f, 500, letterSpacing = -0.5f), color = colors.ink)
            Text("${SocialData.rooms.size} abertas", style = syncoText(10f), color = colors.muted)
        }
        SearchField(
            value = search,
            onValueChange = { search = it },
            placeholder = "Buscar filme, música ou livro",
            background = colors.card,
            borderColor = colors.border,
            radius = 18.dp,
            padding = PaddingValues(horizontal = 16.dp, vertical = 15.dp),
        )
        FlowRow(
            Modifier.padding(top = 16.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            roomFilters.forEach { category ->
                val selected = category == filter
                Text(
                    category,
                    Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (selected) colors.chip else Color.Transparent)
                        .clickable(role = Role.Tab) { filter = category }
                        .padding(horizontal = 16.dp, vertical = 11.dp),
                    style = syncoText(12f, if (selected) 550 else 400),
                    color = if (selected) colors.ink else colors.secondary,
                )
            }
        }
        Column(Modifier.padding(top = 5.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            visibleRooms.forEach { room -> PublicRoom(room, onJoin = { viewModel.joinRoom(room.id) }) }
        }
        if (visibleRooms.isEmpty()) {
            SocialEmpty(
                "Nenhuma sala encontrada",
                glyph = Glyph.Search,
                message = "Tente outro nome ou explore uma nova categoria.",
                actionLabel = "Ver todas as salas",
                onAction = {
                    search = ""
                    filter = "Todas"
                },
            )
        }
        DemoNote("Salas e presença de demonstração.")
    }
}

@Composable
private fun PublicRoom(room: Room, onJoin: () -> Unit) {
    val colors = SyncoTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .syncoCard(colors.card, colors.cardBorder, radius = 22.dp)
            .padding(horizontal = 10.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        RemoteImage(
            room.image,
            contentDescription = null,
            modifier = Modifier
                .size(width = 47.dp, height = 70.dp)
                .clip(RoundedCornerShape(12.dp)),
        )
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                SyncoIcon(room.glyph, 12.dp, tint = colors.muted, strokeWidth = SOCIAL_STROKE)
                Text(room.category, style = syncoText(8f), color = colors.muted)
                Dot(Accent, 3.dp, Modifier.padding(start = 3.dp))
                Text("Ao vivo", style = syncoText(8f), color = colors.muted)
            }
            Text(
                room.title,
                Modifier.padding(vertical = 6.dp),
                style = syncoText(14f, 500, lineHeight = 18.2f, letterSpacing = -0.2f),
                color = colors.ink,
            )
            Text(room.subtitle, style = syncoText(11f, lineHeight = 17.6f), color = colors.muted)
            Row(
                Modifier.padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                SyncoIcon(Glyph.Users, 12.dp, tint = colors.muted, strokeWidth = SOCIAL_STROKE)
                Text("${room.count}/${room.capacity} pessoas", style = syncoText(8f), color = colors.muted)
            }
        }
        Row(
            Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(colors.chip)
                .clickable(role = Role.Button, onClick = onJoin)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text("Entrar", style = syncoText(10f), color = colors.ink)
            SyncoIcon(Glyph.ChevronRight, 12.dp, strokeWidth = SOCIAL_STROKE)
        }
    }
}

@Composable
private fun ErrorNotice(text: String) {
    val dark = SyncoTheme.colors.isDark
    val shape = RoundedCornerShape(8.dp)
    Text(
        text,
        Modifier
            .padding(vertical = 9.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(if (dark) Color(0xFF35232A) else Color(0xFFFFF2F3))
            .border(1.dp, if (dark) Color(0xFF633B46) else Color(0xFFF1D9DD), shape)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        style = syncoText(11f, lineHeight = 17.6f),
        color = if (dark) Color(0xFFF0A9B6) else Color(0xFFA43946),
    )
}

@Composable
fun RoomScreen(viewModel: SyncoViewModel, room: Room, onInvite: () -> Unit) {
    val colors = SyncoTheme.colors
    SyncoScreen {
        AppHeader(
            Modifier.padding(bottom = 20.dp),
            start = { BackLink("Salas", onClick = { viewModel.backTo(Route.Groups()) }) },
            end = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Dot(Accent, 5.dp)
                        Text("AO VIVO", style = syncoText(10f, letterSpacing = 1f), color = colors.ink)
                    }
                    RewardsButton(onClick = { viewModel.modal = AppModal.Rewards })
                }
            },
        )
        VideoWidget(
            room = room,
            playing = viewModel.playing,
            onTogglePlay = { viewModel.playing = !viewModel.playing },
            onExpand = { viewModel.modal = AppModal.Video },
        )
        Row(
            Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Dot(Accent, 4.dp)
                Text(
                    if (viewModel.playing) "Sessão de demonstração" else "Sessão pausada",
                    style = syncoText(9f, lineHeight = 14.4f),
                    color = colors.muted,
                )
            }
            Text(room.progress, style = syncoText(9f, lineHeight = 14.4f), color = colors.muted)
        }
        Watching(room, onMore = { viewModel.modal = AppModal.Members }, onInvite = onInvite)
        SessionChat(
            viewModel = viewModel,
            conversationId = "room-${room.id}",
            title = "Chat da sessão",
            subtitle = "Conversa local · ${room.count} na sala",
            seed = listOf(SeedMessage(room.host, "Bem-vindo à sala ${room.title}! Fique à vontade para conversar.")),
            isPrivate = false,
            modifier = Modifier.padding(top = 24.dp),
        )
    }
}

@Composable
private fun VideoWidget(room: Room, playing: Boolean, onTogglePlay: () -> Unit, onExpand: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(VideoBackground),
    ) {
        RemoteImage(room.scene, "Sessão de ${room.title}", Modifier.fillMaxSize(), colorFilter = DimmedVideo)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "AO VIVO",
                Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(Accent)
                    .padding(horizontal = 9.dp, vertical = 7.dp),
                style = syncoText(10f, 600, letterSpacing = 1f),
                color = Color.White,
            )
            Row(
                Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(CaptionDark)
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                SyncoIcon(Glyph.Users, 12.dp, tint = Color.White)
                Text("${room.count}", style = syncoText(11f), color = Color.White)
            }
        }
        CircleButton(
            48.dp,
            onClick = onTogglePlay,
            modifier = Modifier
                .align(Alignment.Center)
                .border(1.dp, Color.White.copy(alpha = 0.376f), CircleShape),
            background = CaptionDark,
        ) {
            SyncoIcon(
                if (playing) Glyph.Pause else Glyph.Play,
                24.dp,
                tint = Color.White,
                contentDescription = if (playing) "Pausar sessão" else "Retomar sessão",
            )
        }
        Row(
            Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(start = 16.dp, end = 14.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(room.title, style = syncoText(17f, 600), color = Color.White)
                Text(room.tag, Modifier.padding(top = 6.dp), style = syncoText(10f), color = VideoMeta)
            }
            CircleButton(26.dp, onClick = onExpand) {
                SyncoIcon(Glyph.Expand, 16.dp, tint = Color.White, contentDescription = "Expandir vídeo")
            }
        }
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.33f)
                .height(3.dp)
                .background(Accent),
        )
    }
}

private sealed interface MemberCell {
    data class Member(val photo: String, val name: String, val isYou: Boolean) : MemberCell
    data class More(val count: Int) : MemberCell
    data object Invite : MemberCell
}

@Composable
private fun Watching(room: Room, onMore: () -> Unit, onInvite: () -> Unit) {
    val colors = SyncoTheme.colors
    val cells = buildList {
        SocialData.members.take(minOf(room.count, 8)).forEachIndexed { index, photo ->
            add(MemberCell.Member(photo, SocialData.memberNames[index], isYou = index == 0))
        }
        if (room.count > 8) add(MemberCell.More(room.count - 8))
        add(MemberCell.Invite)
    }
    Column(
        Modifier
            .padding(top = 32.dp)
            .fillMaxWidth()
            .syncoCard(colors.card, colors.cardBorder, radius = 22.dp)
            .padding(horizontal = 16.dp, vertical = 20.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("NA SALA COM VOCÊ", style = syncoText(12f, 600, letterSpacing = 1.1f), color = colors.ink)
            Text("${room.count} pessoas", style = syncoText(10f), color = colors.muted)
        }
        Column(Modifier.padding(top = 21.dp), verticalArrangement = Arrangement.spacedBy(19.dp)) {
            cells.chunked(5).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { cell ->
                        Box(Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
                            when (cell) {
                                is MemberCell.Member -> MemberAvatar(cell)
                                is MemberCell.More -> MemberAction(onMore) {
                                    Text("+${cell.count}", style = syncoText(16f, lineHeight = 46f), color = MoreMembers)
                                    Text("Ver todos", style = syncoText(9f), color = colors.muted)
                                }
                                MemberCell.Invite -> MemberAction(onInvite) {
                                    Box(
                                        Modifier
                                            .size(46.dp)
                                            .border(1.dp, InviteBorder, CircleShape),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text("+", style = syncoText(21f), color = colors.muted)
                                    }
                                    Text("Convidar", style = syncoText(9f), color = colors.muted)
                                }
                            }
                        }
                    }
                    repeat(5 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun MemberAvatar(member: MemberCell.Member) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        RemoteImage(
            member.photo,
            contentDescription = member.name,
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .border(1.dp, if (member.isYou) Accent else AvatarBorder, CircleShape),
        )
        Text(member.name, style = syncoText(9f), color = SyncoTheme.colors.muted)
    }
}

@Composable
private fun MemberAction(onClick: () -> Unit, content: @Composable () -> Unit) {
    Column(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(role = Role.Button, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        content()
    }
}
