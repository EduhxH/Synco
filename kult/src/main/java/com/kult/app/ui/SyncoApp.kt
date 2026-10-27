package com.kult.app.ui

import android.content.ClipData
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import com.kult.app.SyncoViewModel
import com.kult.app.data.SocialData
import com.kult.app.ui.components.BottomNav
import com.kult.app.ui.components.ToastBanner
import com.kult.app.ui.dialogs.AppModalHost
import com.kult.app.ui.navigation.Route
import com.kult.app.ui.screens.ChatsScreen
import com.kult.app.ui.screens.ContactProfileScreen
import com.kult.app.ui.screens.FeedScreen
import com.kult.app.ui.screens.GroupsScreen
import com.kult.app.ui.screens.MatchScreen
import com.kult.app.ui.screens.ProfileScreen
import com.kult.app.ui.screens.RoomScreen
import com.kult.app.ui.theme.SyncoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Root of the app: the current screen, the bottom navigation, dialogs and toasts. */
@Composable
fun SyncoApp(viewModel: SyncoViewModel) {
    val colors = SyncoTheme.colors
    val route = viewModel.route
    // The Chats screens sit on the card color; every other screen on the page background.
    val pageBackground = if (route is Route.Chats) colors.card else colors.background
    val room = (route as? Route.Groups)?.roomId?.let(SocialData::room)
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val copyLink = { link: String, message: String ->
        scope.launch {
            clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("Synco", link)))
            viewModel.notify(message)
        }
        Unit
    }

    BackHandler(enabled = viewModel.canGoBack) { viewModel.back() }

    Box(
        Modifier
            .fillMaxSize()
            .background(pageBackground),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(Modifier.widthIn(max = 560.dp)) {
            when (route) {
                Route.Feed -> FeedScreen(
                    viewModel,
                    onShare = { copyLink("synco://feed", "Link do Synco copiado para compartilhar.") },
                )
                Route.Match -> MatchScreen(viewModel)
                is Route.Chats -> ChatsScreen(viewModel, route.contactId)
                is Route.Groups -> if (room != null) {
                    RoomScreen(
                        viewModel,
                        room,
                        onInvite = { copyLink("synco://grupos/${room.id}", "Link da sala copiado.") },
                    )
                } else {
                    GroupsScreen(viewModel, roomNotFound = route.roomId != null)
                }
                is Route.Profile -> if (route.contactId == null) {
                    ProfileScreen(viewModel)
                } else {
                    ContactProfileScreen(viewModel, route.contactId)
                }
            }
        }

        val inConversation = route is Route.Chats && route.contactId != null
        if (!inConversation) {
            BottomNav(
                active = route.tab,
                ringColor = pageBackground,
                onSelect = viewModel::selectTab,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        viewModel.modal?.let { modal -> AppModalHost(modal, viewModel, room) }

        viewModel.toast?.let { toast ->
            LaunchedEffect(toast) {
                delay(3500)
                viewModel.dismissToast(toast)
            }
            ToastBanner(toast.message, Modifier.align(Alignment.BottomCenter))
        }
    }

    BackHandler(enabled = viewModel.modal != null) { viewModel.modal = null }
}
