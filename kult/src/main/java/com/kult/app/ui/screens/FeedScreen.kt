package com.kult.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kult.app.DiscoveryAction
import com.kult.app.SyncoViewModel
import com.kult.app.data.DiscoveryProfile
import com.kult.app.data.SocialData
import com.kult.app.ui.components.AppHeader
import com.kult.app.ui.components.ButtonStyle
import com.kult.app.ui.components.CircleButton
import com.kult.app.ui.components.Dot
import com.kult.app.ui.components.Pill
import com.kult.app.ui.components.RemoteImage
import com.kult.app.ui.components.SyncoButton
import com.kult.app.ui.components.SyncoScreen
import com.kult.app.ui.components.syncoCard
import com.kult.app.ui.icons.Glyph
import com.kult.app.ui.icons.SyncoIcon
import com.kult.app.ui.navigation.Tab
import com.kult.app.ui.theme.Accent
import com.kult.app.ui.theme.SyncoTheme
import com.kult.app.ui.theme.syncoText
import kotlin.math.abs

private val feedFilters = listOf(
    "Tudo" to "Para você",
    "Filmes" to "Filmes",
    "Música" to "Música",
    "Livros" to "Livros",
)

@Composable
fun FeedScreen(viewModel: SyncoViewModel, onShare: () -> Unit) {
    val colors = SyncoTheme.colors
    val profile = viewModel.visibleProfiles().getOrNull(viewModel.profileIndex)
    SyncoScreen(horizontalPadding = 12.dp) {
        AppHeader(
            Modifier.padding(horizontal = 12.dp),
            height = 72.dp,
            end = {
                RemoteImage(
                    SocialData.myAvatar,
                    contentDescription = "Ir para meu perfil",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable(role = Role.Button) { viewModel.selectTab(Tab.Profile) },
                )
            },
        )
        DiscoveryFilters(selected = viewModel.feedFilter, onSelect = viewModel::selectFeedFilter)
        if (profile != null) {
            key(viewModel.feedFilter, viewModel.profileIndex) {
                DiscoveryCard(profile, viewModel, onShare)
            }
            Text(
                "Passe para descobrir. Curta para conectar.",
                Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                style = syncoText(11f),
                color = colors.secondary,
                textAlign = TextAlign.Center,
            )
        } else {
            DiscoveryEmpty(viewModel)
        }
    }
}

@Composable
private fun DiscoveryFilters(selected: String, onSelect: (String) -> Unit) {
    val colors = SyncoTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 6.dp, end = 6.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        feedFilters.forEach { (value, label) ->
            val isSelected = value == selected
            Box(
                Modifier
                    .weight(1f)
                    .heightIn(min = 40.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (isSelected) colors.chip else Color.Transparent)
                    .clickable(role = Role.Tab) { onSelect(value) }
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label,
                    style = syncoText(12f, if (isSelected) 500 else 400, lineHeight = 12f),
                    color = if (isSelected) colors.ink else colors.secondary,
                    maxLines = 1,
                    softWrap = false,
                )
            }
        }
    }
}

@Composable
private fun DiscoveryCard(profile: DiscoveryProfile, viewModel: SyncoViewModel, onShare: () -> Unit) {
    val colors = SyncoTheme.colors
    val swipeThreshold = with(LocalDensity.current) { 65.dp.toPx() }
    Column(
        Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                var distance = 0f
                detectHorizontalDragGestures(
                    onDragStart = { distance = 0f },
                    onDragEnd = {
                        if (abs(distance) > swipeThreshold) {
                            viewModel.advanceProfile(if (distance > 0) DiscoveryAction.Like else DiscoveryAction.Skip)
                        }
                    },
                ) { _, dragAmount -> distance += dragAmount }
            },
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(520.dp)
                .clip(RoundedCornerShape(26.dp)),
        ) {
            val showingPortrait = viewModel.photoIndex == 0
            RemoteImage(
                if (showingPortrait) profile.photo else profile.culture,
                contentDescription = if (showingPortrait) profile.name else "Universo cultural de ${profile.name}",
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Black.copy(alpha = 0.133f),
                            0.5f to Color.Transparent,
                            0.76f to Color.Black.copy(alpha = 0.133f),
                            1f to Color.Black.copy(alpha = 0.47f),
                        ),
                    ),
            )
            PhotoTabs(selected = viewModel.photoIndex, onSelect = { viewModel.photoIndex = it })
            Row(
                Modifier
                    .padding(start = 18.dp, top = 40.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(colors.card)
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                SyncoIcon(Glyph.Heart, 13.dp, tint = Accent)
                Text("${profile.affinity}% de sintonia", style = syncoText(11f), color = colors.ink)
            }
            DiscoveryActions(viewModel, onShare, Modifier.align(Alignment.BottomCenter))
        }
        DiscoveryInfo(profile, viewModel)
    }
}

@Composable
private fun PhotoTabs(selected: Int, onSelect: (Int) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, start = 18.dp, end = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        listOf("Retrato", "Universo cultural").forEachIndexed { index, label ->
            Box(
                Modifier
                    .weight(1f)
                    .height(14.dp)
                    .clickable(role = Role.Tab) { onSelect(index) }
                    .semantics { contentDescription = label },
            ) {
                Box(
                    Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth()
                        .height(2.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (index == selected) Color.White else Color.White.copy(alpha = 0.376f)),
                )
            }
        }
    }
}

@Composable
private fun DiscoveryActions(viewModel: SyncoViewModel, onShare: () -> Unit, modifier: Modifier) {
    val colors = SyncoTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, bottom = 18.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircleButton(
            46.dp,
            onClick = viewModel::rewindProfile,
            background = colors.card,
            enabled = viewModel.discoveryHistory.isNotEmpty(),
        ) {
            SyncoIcon(Glyph.Rewind, 22.dp, tint = colors.discoveryMuted, contentDescription = "Voltar ao perfil anterior")
        }
        CircleButton(61.dp, onClick = { viewModel.advanceProfile(DiscoveryAction.Skip) }, background = colors.card) {
            SyncoIcon(Glyph.Close, 30.dp, tint = colors.ink, contentDescription = "Passar perfil")
        }
        CircleButton(46.dp, onClick = { viewModel.advanceProfile(DiscoveryAction.Star) }, background = colors.card) {
            SyncoIcon(Glyph.Star, 23.dp, tint = Accent, contentDescription = "Enviar interesse especial")
        }
        CircleButton(61.dp, onClick = { viewModel.advanceProfile(DiscoveryAction.Like) }, background = Accent) {
            SyncoIcon(Glyph.Heart, 29.dp, tint = Color.White, contentDescription = "Curtir perfil")
        }
        CircleButton(46.dp, onClick = onShare, background = colors.card) {
            SyncoIcon(Glyph.Arrow, 23.dp, tint = colors.discoveryMuted, contentDescription = "Compartilhar perfil")
        }
    }
}

@Composable
private fun DiscoveryInfo(profile: DiscoveryProfile, viewModel: SyncoViewModel) {
    val colors = SyncoTheme.colors
    Column(
        Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .syncoCard(colors.card, colors.cardBorder)
            .padding(24.dp),
    ) {
        Row(
            Modifier.padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Dot(Accent, 5.dp)
            Text("Disponível para conectar", style = syncoText(10f), color = colors.secondary)
        }
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                buildAnnotatedString {
                    append(profile.name)
                    append(" ")
                    withStyle(SpanStyle(fontSize = 24.sp, color = colors.secondary, letterSpacing = 0.sp)) {
                        append(profile.age.toString())
                    }
                },
                Modifier.weight(1f),
                style = syncoText(30f, lineHeight = 36f, letterSpacing = -1f),
                color = colors.ink,
            )
            CircleButton(36.dp, onClick = { viewModel.showDetails = !viewModel.showDetails }, background = colors.chip) {
                SyncoIcon(
                    if (viewModel.showDetails) Glyph.Close else Glyph.Arrow,
                    19.dp,
                    Modifier.rotate(if (viewModel.showDetails) 0f else -90f),
                    tint = colors.ink,
                    contentDescription = if (viewModel.showDetails) "Ocultar detalhes" else "Ver detalhes do perfil",
                )
            }
        }
        Row(
            Modifier.padding(top = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            SyncoIcon(Glyph.Pin, 13.dp, tint = colors.secondary)
            Text("${profile.city}, Brasil", style = syncoText(11f), color = colors.secondary)
        }
        Text(
            profile.bio,
            Modifier
                .padding(top = 20.dp, bottom = 24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topEnd = 14.dp, bottomEnd = 14.dp))
                .background(colors.background)
                .drawBehind { drawRect(Accent, size = Size(3.dp.toPx(), size.height)) }
                .padding(start = 21.dp, end = 18.dp, top = 16.dp, bottom = 16.dp),
            style = syncoText(17f, 500, lineHeight = 27.2f),
            color = colors.ink,
        )
        Text(
            "VOCÊS TÊM EM COMUM",
            Modifier.padding(bottom = 12.dp),
            style = syncoText(9f, letterSpacing = 0.8f),
            color = colors.secondary,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            profile.interests.forEach { (_, label) -> Pill(label) }
        }
        if (viewModel.showDetails) {
            Text(
                "Procuro companhia para compartilhar cultura, trocar recomendações e descobrir novas histórias. " +
                    "Vamos começar pelo que temos em comum?",
                Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.background)
                    .padding(12.dp),
                style = syncoText(12f, lineHeight = 19.2f),
                color = colors.secondary,
            )
        }
    }
}

@Composable
private fun DiscoveryEmpty(viewModel: SyncoViewModel) {
    val colors = SyncoTheme.colors
    Column(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 360.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(colors.card)
            .padding(horizontal = 20.dp, vertical = 35.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        SyncoIcon(Glyph.Users, 42.dp, tint = Accent)
        Spacer(Modifier.height(20.dp))
        Text(
            "Boas conexões levam tempo.",
            style = syncoText(30f, lineHeight = 40f, letterSpacing = -0.7f),
            color = colors.ink,
            textAlign = TextAlign.Center,
        )
        Text(
            "Você viu todos os perfis desta seleção.\nExplore outro interesse ou reveja seus favoritos.",
            Modifier.padding(top = 16.dp, bottom = 25.dp),
            style = syncoText(12f, lineHeight = 21.6f),
            color = colors.muted,
            textAlign = TextAlign.Center,
        )
        SyncoButton(
            "Rever perfis",
            onClick = viewModel::resetDiscovery,
            modifier = Modifier
                .padding(top = 10.dp)
                .widthIn(max = 300.dp),
        )
        if (viewModel.discoveryHistory.isNotEmpty()) {
            SyncoButton(
                "Voltar ao último perfil",
                onClick = viewModel::rewindProfile,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .widthIn(max = 300.dp),
                style = ButtonStyle.Secondary,
            )
        }
    }
}
