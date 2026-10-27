package com.kult.app.ui.dialogs

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kult.app.AppModal
import com.kult.app.SyncoViewModel
import com.kult.app.data.Room
import com.kult.app.data.SocialData
import com.kult.app.ui.components.Brand
import com.kult.app.ui.components.BoxedTextField
import com.kult.app.ui.components.ButtonStyle
import com.kult.app.ui.components.Faq
import com.kult.app.ui.components.FieldLabel
import com.kult.app.ui.components.ModalText
import com.kult.app.ui.components.ModalTitle
import com.kult.app.ui.components.PreferenceSwitch
import com.kult.app.ui.components.RemoteImage
import com.kult.app.ui.components.SyncoButton
import com.kult.app.ui.components.SyncoModal
import com.kult.app.ui.components.SyncoSelect
import com.kult.app.ui.components.bottomBorder
import com.kult.app.ui.components.syncoCard
import com.kult.app.ui.components.topBorder
import com.kult.app.ui.icons.Glyph
import com.kult.app.ui.icons.SOCIAL_STROKE
import com.kult.app.ui.icons.SyncoIcon
import com.kult.app.ui.screens.NotesDialog
import com.kult.app.ui.theme.Accent
import com.kult.app.ui.theme.DarkSyncoColors
import com.kult.app.ui.theme.LightSyncoColors
import com.kult.app.ui.theme.RewardChevron
import com.kult.app.ui.theme.SyncoColors
import com.kult.app.ui.theme.SyncoTheme
import com.kult.app.ui.theme.syncoText
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val ptBR: Locale = Locale.forLanguageTag("pt-BR")

private fun formatPoints(points: Int): String = NumberFormat.getIntegerInstance(ptBR).format(points)

/** Shows the dialog the view model asks for, over the current screen. [room] is the open live room, if any. */
@Composable
fun AppModalHost(modal: AppModal, viewModel: SyncoViewModel, room: Room?) {
    if (modal == AppModal.EditNote || modal is AppModal.FriendNote) {
        NotesDialog(modal, viewModel)
        return
    }
    val close = { viewModel.modal = null }
    SyncoModal(onDismiss = close, closeLabel = if (modal == AppModal.Rewards) "Fechar recompensas" else "Fechar") {
        Brand(withWordmark = true)
        when (modal) {
            AppModal.Settings -> SettingsPanel(viewModel)
            AppModal.Rewards -> RewardsDialog(viewModel)
            AppModal.EditProfile -> EditProfileDialog(viewModel)
            AppModal.Schedule -> ScheduleDialog(viewModel)
            AppModal.Playlist -> PlaylistDialog(viewModel)
            is AppModal.Reward -> RewardDialog(viewModel, modal)
            AppModal.Members -> if (room != null) MembersDialog(room)
            AppModal.Video -> if (room != null) VideoDialog(viewModel, room)
            AppModal.EditNote, is AppModal.FriendNote -> Unit
        }
    }
}

@Composable
private fun RewardsDialog(viewModel: SyncoViewModel) {
    val colors = SyncoTheme.colors
    ModalTitle("Recompensas Synco")
    Row(
        Modifier.padding(top = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        SyncoIcon(Glyph.Medal, 32.dp, tint = Accent)
        Text(
            buildAnnotatedString {
                append(formatPoints(viewModel.points))
                withStyle(SpanStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal, color = colors.muted, letterSpacing = 0.sp)) {
                    append(" pontos")
                }
            },
            style = syncoText(32f, 700, lineHeight = 48f, letterSpacing = -1f),
            color = colors.heading,
        )
    }
    ModalText("Transforme suas conexões em novas descobertas.", Modifier.padding(top = 15.dp))
    listOf(
        Triple(Glyph.Film, "Ver Novos Filmes", "Explore novas histórias") to 500,
        Triple(Glyph.Users, "Novos Contactos", "Amplie seu círculo cultural") to 750,
        Triple(Glyph.Star, "Chefia Elite", "Seu próximo capítulo") to 1500,
    ).forEach { (reward, cost) ->
        val (glyph, title, subtitle) = reward
        Row(
            Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .syncoCard(colors.card, colors.cardBorder, radius = 18.dp)
                .clickable(role = Role.Button) { viewModel.modal = AppModal.Reward(title, cost) }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.chip),
                contentAlignment = Alignment.Center,
            ) {
                SyncoIcon(glyph, 18.dp, tint = Accent)
            }
            Column(Modifier.weight(1f)) {
                Text(title, style = syncoText(11f, 500), color = colors.heading)
                Text(subtitle, Modifier.padding(top = 5.dp), style = syncoText(10f), color = colors.muted)
            }
            Text("$cost pts", style = syncoText(10f), color = Accent, maxLines = 1)
            SyncoIcon(Glyph.Arrow, 15.dp, tint = RewardChevron)
        }
    }
}

@Composable
private fun RewardDialog(viewModel: SyncoViewModel, reward: AppModal.Reward) {
    val enough = viewModel.points >= reward.cost
    ModalTitle(reward.title)
    ModalText("Desbloqueie esta recompensa por ${reward.cost} pontos. Você tem ${formatPoints(viewModel.points)} pontos.")
    SyncoButton(
        if (enough) "Resgatar recompensa" else "Pontos insuficientes",
        onClick = {
            viewModel.points -= reward.cost
            viewModel.modal = null
            viewModel.notify("Recompensa desbloqueada!")
        },
        enabled = enough,
    )
}

@Composable
private fun EditProfileDialog(viewModel: SyncoViewModel) {
    var draft by rememberSaveable { mutableStateOf(viewModel.name) }
    ModalTitle("Seu universo, do seu jeito.", bottomSpacing = 20.dp)
    FieldLabel("Nome")
    BoxedTextField(draft, onValueChange = { draft = it }, modifier = Modifier.padding(top = 10.dp, bottom = 20.dp))
    ModalText("São Paulo, Brasil · Cinéfilo, Estudioso, Melómano")
    SyncoButton(
        "Salvar perfil",
        onClick = {
            if (draft.isNotBlank()) {
                viewModel.name = draft.trim()
                viewModel.modal = null
                viewModel.notify("Perfil atualizado.")
            }
        },
    )
}

@Composable
private fun ScheduleDialog(viewModel: SyncoViewModel) {
    val context = LocalContext.current
    var dateTime by rememberSaveable {
        mutableLongStateOf(Calendar.getInstance().apply { set(2026, Calendar.OCTOBER, 5, 20, 0, 0) }.timeInMillis)
    }
    val shape = RoundedCornerShape(14.dp)
    ModalTitle("Uma sessão. Duas histórias.")
    ModalText("Cidade de Deus com Irene F.")
    FieldLabel("Dia e horário")
    Box(
        Modifier
            .padding(top = 10.dp, bottom = 20.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(SyncoTheme.colors.card)
            .border(1.dp, SyncoTheme.colors.border, shape)
            .clickable(role = Role.Button) {
                val calendar = Calendar.getInstance().apply { timeInMillis = dateTime }
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                dateTime = Calendar.getInstance().apply { set(year, month, day, hour, minute, 0) }.timeInMillis
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true,
                        ).show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                ).show()
            }
            .padding(12.dp),
    ) {
        Text(
            SimpleDateFormat("dd/MM/yyyy, HH:mm", ptBR).format(dateTime),
            style = syncoText(12f),
            color = SyncoTheme.colors.ink,
        )
    }
    SyncoButton(
        "Confirmar sessão",
        onClick = {
            viewModel.scheduled = true
            viewModel.modal = null
            viewModel.notify("Sessão agendada. Irene receberá seu convite.")
        },
    )
}

@Composable
private fun PlaylistDialog(viewModel: SyncoViewModel) {
    val colors = SyncoTheme.colors
    ModalTitle("A trilha de vocês.")
    ModalText("Uma playlist compartilhada com Irene.")
    SocialData.playlist.forEach { song ->
        Row(
            Modifier
                .fillMaxWidth()
                .bottomBorder(colors.border)
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SyncoIcon(Glyph.Music, 20.dp, tint = Accent)
            Text(song, style = syncoText(11f), color = colors.ink)
        }
    }
    SyncoButton(
        "Criar e convidar Irene",
        onClick = {
            viewModel.modal = null
            viewModel.notify("Playlist criada. Convite enviado para Irene!")
        },
        modifier = Modifier.padding(top = 15.dp),
    )
}

@Composable
private fun MembersDialog(room: Room) {
    ModalTitle("Na mesma sintonia.")
    ModalText("${room.count} pessoas na sala ${room.title}.")
    val photos = (SocialData.members + SocialData.ireneAvatar + SocialData.myAvatar).take(room.count)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        photos.chunked(5).forEachIndexed { rowIndex, row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEachIndexed { index, photo ->
                    RemoteImage(
                        photo,
                        contentDescription = "Participante ${rowIndex * 5 + index + 1}",
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(CircleShape),
                    )
                }
                repeat(5 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun VideoDialog(viewModel: SyncoViewModel, room: Room) {
    ModalTitle(room.title)
    RemoteImage(
        room.scene,
        contentDescription = "Sessão de ${room.title}",
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.5f)
            .clip(RoundedCornerShape(10.dp)),
    )
    SyncoButton(
        if (viewModel.playing) "Pausar sessão" else "Retomar sessão",
        onClick = { viewModel.playing = !viewModel.playing },
        modifier = Modifier.padding(top = 15.dp),
        leading = if (viewModel.playing) Glyph.Pause else Glyph.Play,
    )
}

private val settingsSections = listOf(
    "Conta e Perfil",
    "Notificações",
    "Modo claro/escuro",
    "Privacidade e Segurança",
    "Idioma e Região",
    "Ajuda e Suporte",
    "Sobre a Aplicação",
)

@Composable
private fun SettingsPanel(viewModel: SyncoViewModel) {
    val colors = SyncoTheme.colors
    var expanded by rememberSaveable { mutableStateOf<String?>(null) }
    ModalTitle("Definições", bottomSpacing = 10.dp)
    Text(
        "Seu Synco, do seu jeito.",
        Modifier.padding(bottom = 24.dp),
        style = syncoText(13f, lineHeight = 23.4f),
        color = colors.secondary,
    )
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        settingsSections.forEachIndexed { index, section ->
            val open = expanded == section
            Column(
                Modifier
                    .fillMaxWidth()
                    .syncoCard(colors.card, colors.cardBorder, radius = 18.dp)
                    .padding(horizontal = 16.dp),
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { expanded = if (open) null else section }
                        .padding(vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(section, Modifier.weight(1f), style = syncoText(14f, 500), color = colors.ink)
                    if (index == 2) {
                        Text(if (viewModel.darkTheme) "Escuro" else "Claro", style = syncoText(10f), color = colors.muted)
                    }
                    SyncoIcon(
                        Glyph.ChevronRight,
                        16.dp,
                        Modifier.rotate(if (open) 90f else 0f),
                        tint = colors.muted,
                        strokeWidth = SOCIAL_STROKE,
                    )
                }
                if (open) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .topBorder(colors.cardBorder)
                            .padding(top = 16.dp, bottom = 20.dp),
                    ) {
                        SettingsSectionContent(index, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionContent(index: Int, viewModel: SyncoViewModel) {
    val colors = SyncoTheme.colors
    val preferences = viewModel.preferences
    when (index) {
        0 -> {
            Column(Modifier.padding(top = 7.dp, bottom = 4.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text("Seu perfil", style = syncoText(10f), color = colors.muted)
                Text(viewModel.name, style = syncoText(17f, 550), color = colors.ink)
                Text("Interesses, nome e conexões culturais.", style = syncoText(10f), color = colors.muted)
            }
            SyncoButton(
                "Editar perfil",
                onClick = { viewModel.modal = AppModal.EditProfile },
                modifier = Modifier.padding(top = 12.dp),
                style = ButtonStyle.Secondary,
                trailing = Glyph.ChevronRight,
                iconSize = 15.dp,
                iconStroke = SOCIAL_STROKE,
            )
            SettingsNote("Este protótipo usa um perfil local. Não há uma conta ou credenciais vinculadas.")
        }
        1 -> {
            PreferenceSwitch("Novos matches", "Quando surgir uma nova sintonia cultural.", preferences.matches) {
                viewModel.preferences = preferences.copy(matches = it)
            }
            PreferenceSwitch("Mensagens", "Conversas privadas e mensagens nos grupos.", preferences.messages) {
                viewModel.preferences = preferences.copy(messages = it)
            }
            PreferenceSwitch("Lembretes de sessões", "Antes de começar seu encontro cultural.", preferences.sessions) {
                viewModel.preferences = preferences.copy(sessions = it)
            }
            SettingsNote("Preferências salvas neste dispositivo. Notificações reais ainda não estão conectadas.")
        }
        2 -> {
            Text(
                "Escolha a aparência da aplicação.",
                Modifier.padding(top = 3.dp, bottom = 13.dp),
                style = syncoText(13f, lineHeight = 23.4f),
                color = colors.secondary,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ThemeOption("Claro", LightSyncoColors, selected = !viewModel.darkTheme, Modifier.weight(1f)) {
                    viewModel.darkTheme = false
                }
                ThemeOption("Escuro", DarkSyncoColors, selected = viewModel.darkTheme, Modifier.weight(1f)) {
                    viewModel.darkTheme = true
                }
            }
        }
        3 -> {
            PreferenceSwitch("Mostrar meu status", "Disponível, assistindo ou offline.", preferences.presence) {
                viewModel.preferences = preferences.copy(presence = it)
            }
            PreferenceSwitch("Confirmações de leitura", "Permitir que saibam quando você leu.", preferences.readReceipts) {
                viewModel.preferences = preferences.copy(readReceipts = it)
            }
            SettingsNote("Essas preferências são locais. Os status exibidos na aplicação são de demonstração.")
            Faq(
                "Dicas de segurança",
                "Não compartilhe senhas ou dados sensíveis. Para encontros presenciais, prefira locais públicos " +
                    "e avise alguém de confiança.",
                Modifier.padding(top = 14.dp),
            )
        }
        4 -> {
            SettingsSelect(
                "Idioma",
                preferences.language,
                listOf("pt-BR" to "Português (Brasil)", "pt-PT" to "Português (Portugal)"),
            ) { viewModel.preferences = preferences.copy(language = it) }
            SettingsSelect(
                "Região",
                preferences.region,
                listOf("Brasil" to "Brasil", "Portugal" to "Portugal"),
            ) { viewModel.preferences = preferences.copy(region = it) }
            SettingsNote(
                "Preferência salva. Traduções regionais e recomendações por localização ainda não estão conectadas.",
            )
        }
        5 -> {
            Faq(
                "Como encontrar pessoas?",
                "Explore o Feed e curta perfis com interesses em comum. Use as categorias para descobrir pessoas " +
                    "por filmes, música ou livros.",
            )
            Faq(
                "Como entrar em uma sala?",
                "Em Grupos, escolha uma sala pública e toque em Entrar. Você também pode acompanhar amigos que " +
                    "estão assistindo.",
            )
            Faq(
                "Como enviar mensagens e áudio?",
                "Abra Chats ou o chat de uma sala. Escreva uma mensagem ou toque no microfone para gravar.",
            )
            SettingsNote("Central de ajuda do protótipo. Atendimento humano ainda não disponível.")
        }
        else -> {
            Text(
                "Synco",
                Modifier.padding(top = 5.dp, bottom = 7.dp),
                style = syncoText(23f, letterSpacing = -0.5f),
                color = colors.ink,
            )
            Text("Versão 1.0.0 · Protótipo", style = syncoText(10f, lineHeight = 17f), color = colors.muted)
            Text(
                "Filmes, música e livros aproximam. O Synco transforma afinidades em novas conexões.",
                Modifier.padding(top = 15.dp, bottom = 20.dp),
                style = syncoText(12f, lineHeight = 21.6f),
                color = colors.secondary,
            )
            Text(
                "Conversas, salas e perfis de demonstração. Texto e preferências são armazenados localmente; " +
                    "fotos e áudios enviados são temporários.",
                style = syncoText(10f, lineHeight = 17f),
                color = colors.muted,
            )
        }
    }
}

@Composable
private fun SettingsNote(text: String) {
    Text(
        text,
        Modifier.padding(top = 14.dp),
        style = syncoText(11f, lineHeight = 18.7f),
        color = SyncoTheme.colors.secondary,
    )
}

@Composable
private fun SettingsSelect(label: String, value: String, options: List<Pair<String, String>>, onSelect: (String) -> Unit) {
    Column(Modifier.padding(top = 10.dp, bottom = 15.dp)) {
        Text(label, style = syncoText(11f), color = SyncoTheme.colors.ink)
        SyncoSelect(value, options, onSelect, Modifier.padding(top = 9.dp))
    }
}

/** Light or dark preview card of the appearance setting; [swatch] is the palette it previews. */
@Composable
private fun ThemeOption(label: String, swatch: SyncoColors, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val colors = SyncoTheme.colors
    val shape = RoundedCornerShape(9.dp)
    val swatchShape = RoundedCornerShape(6.dp)
    Column(
        modifier
            .clip(shape)
            .border(if (selected) 2.dp else 1.dp, if (selected) Accent else colors.border, shape)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .height(58.dp)
                .clip(swatchShape)
                .background(swatch.swatchBackground)
                .border(1.dp, swatch.swatchBorder, swatchShape)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            listOf(27.dp to Accent, 53.dp to swatch.swatchBar, 38.dp to swatch.swatchBar).forEach { (width, color) ->
                Box(
                    Modifier
                        .width(width)
                        .height(4.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(color),
                )
            }
        }
        Text(label, style = syncoText(11f), color = colors.ink)
    }
}
