package com.kult.app.ui.navigation

import com.kult.app.ui.icons.Glyph

/** Tabs of the bottom navigation, in display order. */
enum class Tab(val label: String, val glyph: Glyph) {
    Feed("Feed", Glyph.Film),
    Match("Match", Glyph.Heart),
    Chats("Chats", Glyph.Message),
    Groups("Grupos", Glyph.Users),
    Profile("Perfil", Glyph.User),
}

/** Screens of the app; mirrors the prototype's paths (`/chats/:contactId`, `/grupos/:roomId`, …). */
sealed interface Route {
    val tab: Tab

    data object Feed : Route {
        override val tab = Tab.Feed
    }

    data object Match : Route {
        override val tab = Tab.Match
    }

    data class Chats(val contactId: String? = null) : Route {
        override val tab = Tab.Chats
    }

    data class Groups(val roomId: String? = null) : Route {
        override val tab = Tab.Groups
    }

    data class Profile(val contactId: String? = null) : Route {
        override val tab = Tab.Profile
    }
}

val Tab.root: Route
    get() = when (this) {
        Tab.Feed -> Route.Feed
        Tab.Match -> Route.Match
        Tab.Chats -> Route.Chats()
        Tab.Groups -> Route.Groups()
        Tab.Profile -> Route.Profile()
    }
