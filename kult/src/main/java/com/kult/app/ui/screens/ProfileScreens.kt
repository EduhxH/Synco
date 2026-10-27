package com.kult.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.kult.app.AppModal
import com.kult.app.SyncoViewModel
import com.kult.app.data.SocialData
import com.kult.app.ui.components.AppHeader
import com.kult.app.ui.components.BackLink
import com.kult.app.ui.components.ButtonStyle
import com.kult.app.ui.components.CircleButton
import com.kult.app.ui.components.DemoNote
import com.kult.app.ui.components.Dot
import com.kult.app.ui.components.Pill
import com.kult.app.ui.components.RemoteImage
import com.kult.app.ui.components.SocialEmpty
import com.kult.app.ui.components.SyncoButton
import com.kult.app.ui.components.SyncoScreen
import com.kult.app.ui.components.bottomBorder
import com.kult.app.ui.components.presenceColor
import com.kult.app.ui.components.syncoCard
import com.kult.app.ui.icons.APP_STROKE
import com.kult.app.ui.icons.Glyph
import com.kult.app.ui.icons.SOCIAL_STROKE
import com.kult.app.ui.icons.SyncoIcon
import com.kult.app.ui.navigation.Route
import com.kult.app.ui.navigation.Tab
import com.kult.app.ui.theme.Accent
import com.kult.app.ui.theme.OverlayDark
import com.kult.app.ui.theme.ProfilePhotoBackground
import com.kult.app.ui.theme.SyncoTheme
import com.kult.app.ui.theme.syncoText

private val HalfSaturation = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.5f) })

private val myInterests = listOf(
    Triple(Glyph.Film, "Filmes", "Cidade de Deus, Parasita"),
    Triple(Glyph.Book, "Livros", "Dom Casmurro"),
    Triple(Glyph.Music, "Artistas", "Seu Jorge, Gilberto Gil"),
)

@Composable
fun ProfileScreen(viewModel: SyncoViewModel) {
    val colors = SyncoTheme.colors
    SyncoScreen {
        AppHeader(
            Modifier.padding(bottom = 20.dp),
            end = {
                CircleButton(30.dp, onClick = { viewModel.modal = AppModal.Settings }) {
                    SyncoIcon(Glyph.Gear, 20.dp, tint = colors.muted, contentDescription = "Configurações")
                }
            },
        )
        ProfilePhoto(SocialData.myAvatar, viewModel.name, showTag = true)
        ProfileTitle(viewModel.name, 28, "São Paulo, Brasil") {
            SyncoIcon(Glyph.Pin, 13.dp, tint = colors.muted)
        }
        SectionCard {
            SectionTitle("Minhas categorias")
            FlowRow(
                Modifier.padding(top = 15.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf(Glyph.Film to "Cinéfilo", Glyph.Book to "Estudioso", Glyph.Music to "Melómano").forEach { (glyph, label) ->
                    Pill(
                        label,
                        glyph = glyph,
                        radius = 20.dp,
                        padding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                        textStyle = syncoText(11f, 500),
                    )
                }
            }
        }
        SectionCard(Modifier.padding(top = 24.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SectionTitle("Interesses")
                Text("O QUE ME MOVE", style = syncoText(9f, letterSpacing = 1f), color = colors.muted)
            }
            myInterests.forEachIndexed { index, (glyph, label, text) ->
                InterestRow(glyph, label, text, showDivider = index < myInterests.lastIndex)
            }
        }
        SyncoButton(
            "Editar Perfil",
            onClick = { viewModel.modal = AppModal.EditProfile },
            modifier = Modifier.padding(top = 32.dp),
            trailing = Glyph.Arrow,
            spread = true,
        )
    }
}

@Composable
fun ContactProfileScreen(viewModel: SyncoViewModel, contactId: String) {
    val colors = SyncoTheme.colors
    val contact = SocialData.friend(contactId)
    val room = SocialData.room(contact?.roomId)
    SyncoScreen {
        AppHeader(
            Modifier.padding(bottom = 20.dp),
            start = { BackLink("Voltar à conversa", onClick = { viewModel.backTo(Route.Chats(contactId)) }) },
            end = { Text("PERFIL", style = syncoText(10f, letterSpacing = 1.2f), color = colors.muted) },
        )
        if (contact == null) {
            SocialEmpty(
                "Perfil não encontrado",
                actionLabel = "Voltar para conversas",
                onAction = { viewModel.selectTab(Tab.Chats) },
            )
            return@SyncoScreen
        }
        ProfilePhoto(contact.largeAvatar, contact.name, showTag = false)
        ProfileTitle(contact.name, contact.age, contact.statusLabel) {
            Dot(colors.presenceColor(contact.status), 8.dp)
        }
        SectionCard {
            SectionTitle("Uma conversa para começar")
            Text(
                contact.initialMessages.first().content,
                Modifier.padding(top = 16.dp),
                style = syncoText(15f, lineHeight = 25.5f),
                color = colors.secondary,
            )
        }
        if (room != null) {
            SectionCard(Modifier.padding(top = 24.dp)) {
                SectionTitle("Compartilhando agora")
                InterestRow(room.glyph, room.category, room.title, showDivider = true, iconStroke = SOCIAL_STROKE)
                SyncoButton(
                    "Entrar na sala",
                    onClick = { viewModel.joinRoom(room.id) },
                    modifier = Modifier.padding(top = 20.dp),
                    style = ButtonStyle.Secondary,
                    trailing = Glyph.ChevronRight,
                    iconSize = 16.dp,
                    iconStroke = SOCIAL_STROKE,
                )
            }
        }
        SyncoButton(
            "Conversar com ${contact.firstName}",
            onClick = { viewModel.backTo(Route.Chats(contactId)) },
            modifier = Modifier.padding(top = 32.dp),
            trailing = Glyph.Message,
            iconSize = 18.dp,
            iconStroke = SOCIAL_STROKE,
            spread = true,
        )
        DemoNote("Perfil de demonstração. Apenas informações disponíveis nesta aplicação.")
    }
}

@Composable
private fun ProfilePhoto(model: Any, contentDescription: String, showTag: Boolean) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(ProfilePhotoBackground),
    ) {
        RemoteImage(model, contentDescription, Modifier.fillMaxSize(), colorFilter = HalfSaturation)
        if (showTag) {
            Row(
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(OverlayDark)
                    .padding(horizontal = 11.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Dot(Accent, 4.dp)
                Text("ABERTO A CONEXÕES", style = syncoText(9f, letterSpacing = 1f), color = Color.White)
            }
        }
    }
}

@Composable
private fun ProfileTitle(name: String, age: Int, subtitle: String, leading: @Composable () -> Unit) {
    val colors = SyncoTheme.colors
    Column(Modifier.padding(top = 24.dp, bottom = 32.dp)) {
        Text(
            buildAnnotatedString {
                append(name)
                withStyle(SpanStyle(color = colors.muted)) { append(", $age") }
            },
            style = syncoText(36f, lineHeight = 54f, letterSpacing = -1.2f),
            color = colors.ink,
        )
        Row(
            Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            leading()
            Text(subtitle, style = syncoText(13f), color = colors.muted)
        }
    }
}

@Composable
private fun SectionCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val colors = SyncoTheme.colors
    Column(
        modifier
            .fillMaxWidth()
            .syncoCard(colors.card, colors.cardBorder)
            .padding(18.dp),
    ) {
        content()
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = syncoText(18f, 500), color = SyncoTheme.colors.ink)
}

@Composable
private fun InterestRow(glyph: Glyph, label: String, text: String, showDivider: Boolean, iconStroke: Float = APP_STROKE) {
    val colors = SyncoTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .then(if (showDivider) Modifier.bottomBorder(colors.cardBorder) else Modifier)
            .padding(top = 20.dp, bottom = if (showDivider) 20.dp else 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        Box(
            Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(colors.chip),
            contentAlignment = Alignment.Center,
        ) {
            SyncoIcon(glyph, 20.dp, tint = colors.iconBox, strokeWidth = iconStroke)
        }
        Column {
            Text(label, style = syncoText(11f), color = colors.muted)
            Text(text, Modifier.padding(top = 5.dp), style = syncoText(15f, lineHeight = 24f), color = colors.emphasis)
        }
    }
}
