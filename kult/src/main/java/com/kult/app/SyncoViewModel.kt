package com.kult.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.kult.app.data.ChatMessage
import com.kult.app.data.MessageKind
import com.kult.app.data.Note
import com.kult.app.data.SeedMessage
import com.kult.app.data.SocialData
import com.kult.app.ui.navigation.Route
import com.kult.app.ui.navigation.Tab
import com.kult.app.ui.navigation.root
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Dialogs opened over the current screen. */
sealed interface AppModal {
    data object Settings : AppModal
    data object Rewards : AppModal
    data object EditProfile : AppModal
    data object Schedule : AppModal
    data object Playlist : AppModal
    data object Members : AppModal
    data object Video : AppModal
    data class Reward(val title: String, val cost: Int) : AppModal
    data object EditNote : AppModal

    /** Note of the friend at [index] in the demo friends list. */
    data class FriendNote(val index: Int) : AppModal
}

data class Toast(val id: Long, val message: String)

data class Preferences(
    val matches: Boolean = true,
    val messages: Boolean = true,
    val sessions: Boolean = true,
    val presence: Boolean = true,
    val readReceipts: Boolean = true,
    val language: String = "pt-BR",
    val region: String = "Brasil",
)

/** State of the prototype, kept across screens (and configuration changes) for the session. */
class SyncoViewModel : ViewModel() {
    private val backStack = mutableStateListOf<Route>(Route.Feed)

    val route: Route get() = backStack.last()
    val canGoBack: Boolean get() = backStack.size > 1

    var darkTheme by mutableStateOf(false)
    var name by mutableStateOf("Mateus C")
    var modal by mutableStateOf<AppModal?>(null)
    var toast by mutableStateOf<Toast?>(null)
        private set
    var points by mutableIntStateOf(1500)
    var scheduled by mutableStateOf(false)
    var playing by mutableStateOf(true)
    var preferences by mutableStateOf(Preferences())

    // Feed
    var feedFilter by mutableStateOf("Tudo")
    var profileIndex by mutableIntStateOf(0)
    var photoIndex by mutableIntStateOf(0)
    var showDetails by mutableStateOf(false)
    val discoveryHistory = mutableStateListOf<Int>()

    // Notes and chats
    var myNote by mutableStateOf(Note())
    var noteFeedback by mutableStateOf("")
    val readIds = mutableStateListOf<String>()
    private val conversations = HashMap<String, SnapshotStateList<ChatMessage>>()

    fun selectTab(tab: Tab) {
        backStack.clear()
        backStack.add(Route.Feed)
        if (tab != Tab.Feed) backStack.add(tab.root)
    }

    fun open(route: Route) {
        if (backStack.last() != route) backStack.add(route)
    }

    fun back() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }

    /** Returns to [route] if it is in the history, otherwise replaces the current screen with it. */
    fun backTo(route: Route) {
        val index = backStack.lastIndexOf(route)
        if (index in 0 until backStack.lastIndex) {
            while (backStack.lastIndex > index) backStack.removeAt(backStack.lastIndex)
        } else {
            backStack[backStack.lastIndex] = route
        }
    }

    fun openChat(contactId: String) {
        if (contactId !in readIds) readIds.add(contactId)
        open(Route.Chats(contactId))
    }

    fun openContactProfile(contactId: String) = open(Route.Profile(contactId))

    fun joinRoom(roomId: String) {
        playing = true
        open(Route.Groups(roomId))
    }

    fun notify(message: String) {
        toast = Toast(System.nanoTime(), message)
    }

    fun dismissToast(toast: Toast) {
        if (this.toast == toast) this.toast = null
    }

    // Feed

    fun selectFeedFilter(filter: String) {
        feedFilter = filter
        resetDiscovery()
    }

    fun resetDiscovery() {
        profileIndex = 0
        photoIndex = 0
        discoveryHistory.clear()
        showDetails = false
    }

    fun visibleProfiles() =
        SocialData.discoveryProfiles.filter { feedFilter == "Tudo" || it.category == feedFilter }

    fun advanceProfile(action: DiscoveryAction) {
        val profile = visibleProfiles().getOrNull(profileIndex) ?: return
        discoveryHistory.add(profileIndex)
        profileIndex += 1
        photoIndex = 0
        showDetails = false
        when (action) {
            DiscoveryAction.Skip -> Unit
            DiscoveryAction.Star -> notify("Interesse especial enviado para ${profile.name}.")
            DiscoveryAction.Like -> notify("Você curtiu ${profile.name}.")
        }
    }

    fun rewindProfile() {
        val previous = discoveryHistory.lastOrNull() ?: return
        discoveryHistory.removeAt(discoveryHistory.lastIndex)
        profileIndex = previous
        photoIndex = 0
        showDetails = false
    }

    // Chats

    /** Messages of a conversation, seeded the first time it is opened, as the prototype's session storage. */
    fun messages(conversationId: String, seed: List<SeedMessage>): SnapshotStateList<ChatMessage> =
        conversations.getOrPut(conversationId) {
            seed.mapIndexed { index, message ->
                ChatMessage(
                    id = "welcome-$index",
                    author = message.author,
                    time = "14:32",
                    kind = MessageKind.Text,
                    content = message.content,
                    own = false,
                )
            }.toMutableStateList()
        }

    fun send(conversationId: String, kind: MessageKind, content: String) {
        val list = conversations[conversationId] ?: return
        list.add(
            ChatMessage(
                id = "${System.nanoTime()}",
                author = name,
                time = SimpleDateFormat("HH:mm", Locale.forLanguageTag("pt-BR")).format(Date()),
                kind = kind,
                content = content,
                own = true,
            ),
        )
    }
}

enum class DiscoveryAction { Skip, Star, Like }
