package com.kult.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.kult.app.AppModal
import com.kult.app.R
import com.kult.app.SyncoViewModel
import com.kult.app.data.SocialData
import com.kult.app.ui.components.AppHeader
import com.kult.app.ui.components.ButtonStyle
import com.kult.app.ui.components.Dot
import com.kult.app.ui.components.RemoteImage
import com.kult.app.ui.components.SyncoButton
import com.kult.app.ui.components.SyncoScreen
import com.kult.app.ui.components.syncoCard
import com.kult.app.ui.icons.Glyph
import com.kult.app.ui.icons.SyncoIcon
import com.kult.app.ui.theme.Accent
import com.kult.app.ui.theme.CaptionDark
import com.kult.app.ui.theme.OverlayDark
import com.kult.app.ui.theme.PosterBackground
import com.kult.app.ui.theme.SmallTag
import com.kult.app.ui.theme.SyncoTheme
import com.kult.app.ui.theme.syncoText

@Composable
fun MatchScreen(viewModel: SyncoViewModel) {
    val colors = SyncoTheme.colors
    SyncoScreen {
        AppHeader(
            Modifier.padding(bottom = 20.dp),
            end = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    SyncoIcon(Glyph.Star, 12.dp, tint = Accent)
                    Text("PARA VOCÊ", style = syncoText(10f, letterSpacing = 0.8f), color = SmallTag)
                }
            },
        )
        MatchPoster()
        Row(
            Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
                .syncoCard(colors.card, colors.cardBorder, radius = 22.dp)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            RemoteImage(SocialData.ireneAvatar, "Irene F", Modifier.size(64.dp).clip(CircleShape))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        buildAnnotatedString {
                            append("Irene F")
                            withStyle(SpanStyle(color = colors.muted, fontWeight = FontWeight.Normal)) { append(", 27") }
                        },
                        style = syncoText(19f, 600, lineHeight = 28.5f),
                        color = colors.ink,
                    )
                    Dot(Accent, 5.dp, Modifier.padding(start = 7.dp))
                }
                Text(
                    "Rio de Janeiro, Brasil",
                    Modifier.padding(top = 6.dp),
                    style = syncoText(12f),
                    color = colors.muted,
                )
            }
            SyncoIcon(Glyph.Film, 18.dp, tint = Accent)
        }
        Column(
            Modifier
                .padding(vertical = 24.dp)
                .fillMaxWidth()
                .syncoCard(colors.card, colors.cardBorder)
                .padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "MESMO FILME. NOVA HISTÓRIA.",
                style = syncoText(10f, 600, letterSpacing = 1.2f),
                color = Accent,
                textAlign = TextAlign.Center,
            )
            Text(
                buildAnnotatedString {
                    append("Temos um\nMatch Cultural")
                    withStyle(SpanStyle(color = Accent)) { append("!") }
                },
                Modifier.padding(vertical = 13.dp),
                style = syncoText(38f, lineHeight = 42.94f, letterSpacing = -1.3f),
                color = colors.heading,
                textAlign = TextAlign.Center,
            )
            Text(
                buildAnnotatedString {
                    append("Deseja ver ")
                    withStyle(SpanStyle(color = colors.emphasis, fontWeight = FontWeight(500))) {
                        append("Cidade de Deus")
                    }
                    append("\ncom esta pessoa?")
                },
                style = syncoText(15f, lineHeight = 25.5f),
                color = colors.secondary,
                textAlign = TextAlign.Center,
            )
        }
        SyncoButton(
            if (viewModel.scheduled) "Sessão agendada!" else "Aceitar e agendar sessão",
            onClick = { viewModel.modal = AppModal.Schedule },
            leading = if (viewModel.scheduled) Glyph.Check else Glyph.Play,
        )
        SyncoButton(
            "Criar Playlist Juntos",
            onClick = { viewModel.modal = AppModal.Playlist },
            modifier = Modifier.padding(top = 16.dp),
            style = ButtonStyle.Secondary,
            leading = Glyph.Music,
        )
    }
}

@Composable
private fun MatchPoster() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(330.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(PosterBackground),
    ) {
        Image(
            painter = painterResource(R.drawable.cidade_de_deus),
            contentDescription = "Pôster do filme Cidade de Deus",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Row(
            Modifier
                .padding(14.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(OverlayDark)
                .padding(horizontal = 13.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            SyncoIcon(Glyph.Heart, 13.dp, tint = Accent)
            Text("98% de sintonia", style = syncoText(11f), color = Color.White)
        }
        Text(
            "UM FILME DE FERNANDO MEIRELLES",
            Modifier
                .align(Alignment.BottomStart)
                .padding(14.dp)
                .background(CaptionDark)
                .padding(horizontal = 8.dp, vertical = 7.dp),
            style = syncoText(8f, letterSpacing = 1f),
            color = Color.White,
        )
    }
}
