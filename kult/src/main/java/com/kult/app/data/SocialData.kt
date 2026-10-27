package com.kult.app.data

import com.kult.app.R
import com.kult.app.ui.icons.Glyph

/** Square crop of an Unsplash photo, as the prototype requests them. */
fun unsplash(photo: String, size: Int = 160): String =
    "https://images.unsplash.com/$photo?auto=format&fit=crop&w=$size&h=$size&q=85"

enum class Presence { Available, Watching, Offline }

data class SeedMessage(val author: String, val content: String)

data class Friend(
    val id: String,
    val name: String,
    val age: Int,
    val avatar: String,
    val status: Presence,
    val statusLabel: String,
    val preview: String,
    val time: String,
    val unread: Int,
    val initialMessages: List<SeedMessage>,
    val roomId: String? = null,
) {
    val firstName: String get() = name.substringBefore(' ')
    val largeAvatar: String get() = avatar.replace("w=160&h=160", "w=900&h=900")
}

data class Room(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val glyph: Glyph,
    /** Thumbnail: a drawable resource id or an image URL. */
    val image: Any,
    val scene: String,
    val count: Int,
    val capacity: Int,
    val host: String,
    val tag: String,
    val progress: String,
)

data class DiscoveryProfile(
    val name: String,
    val age: Int,
    val city: String,
    val category: String,
    val photo: String,
    /** Second photo of the card: a drawable resource id or an image URL. */
    val culture: Any,
    val affinity: Int,
    val bio: String,
    val interests: List<Pair<Glyph, String>>,
)

enum class NoteKind { Message, Music }

enum class NotePresence(val label: String) {
    Online("Online"),
    Offline("Offline"),
    Busy("Não perturbe"),
}

data class Note(
    val kind: NoteKind = NoteKind.Message,
    val text: String = "",
    val artist: String = "",
    val presence: NotePresence = NotePresence.Online,
)

enum class MessageKind { Text, Sticker, Photo, Audio }

data class ChatMessage(
    val id: String,
    val author: String,
    val time: String,
    val kind: MessageKind,
    val content: String,
    val own: Boolean,
)

/** Demo content of the prototype: people, rooms and profiles shown across the screens. */
object SocialData {
    val myAvatar = unsplash("photo-1500648767791-00dcc994a43e", 600)
    val ireneAvatar = unsplash("photo-1534528741775-53994a69daeb", 300)

    val friends = listOf(
        Friend(
            id = "irene",
            name = "Irene F",
            age = 27,
            avatar = unsplash("photo-1534528741775-53994a69daeb"),
            status = Presence.Available,
            statusLabel = "Disponível",
            preview = "Vamos combinar um filme para hoje?",
            time = "14:32",
            unread = 2,
            initialMessages = listOf(
                SeedMessage("Irene F", "Oi! Vi que você também gosta de Cidade de Deus."),
                SeedMessage("Irene F", "Vamos combinar um filme para hoje?"),
            ),
        ),
        Friend(
            id = "lucas",
            name = "Lucas A",
            age = 29,
            avatar = unsplash("photo-1506794778202-cad84cf45f1d"),
            status = Presence.Watching,
            statusLabel = "Assistindo a Cidade de Deus",
            roomId = "cidade-de-deus",
            preview = "Estou na sala de Cidade de Deus. Vem!",
            time = "13:48",
            unread = 1,
            initialMessages = listOf(SeedMessage("Lucas A", "Estou na sala de Cidade de Deus. Vem!")),
        ),
        Friend(
            id = "ana",
            name = "Ana P",
            age = 26,
            avatar = unsplash("photo-1524504388940-b1c1722653e1"),
            status = Presence.Available,
            statusLabel = "Disponível",
            preview = "Qual vai ser nossa próxima leitura?",
            time = "12:15",
            unread = 0,
            initialMessages = listOf(
                SeedMessage("Ana P", "Terminei Dom Casmurro! Qual vai ser nossa próxima leitura?"),
            ),
        ),
        Friend(
            id = "bia",
            name = "Bia S",
            age = 25,
            avatar = unsplash("photo-1517841905240-472988babdf9"),
            status = Presence.Offline,
            statusLabel = "Offline",
            preview = "Adorei as músicas que você indicou.",
            time = "Ontem",
            unread = 0,
            initialMessages = listOf(SeedMessage("Bia S", "Adorei as músicas que você indicou. Obrigada!")),
        ),
        Friend(
            id = "rafa",
            name = "Rafa M",
            age = 30,
            avatar = unsplash("photo-1535713875002-d1d0cf377fde"),
            status = Presence.Watching,
            statusLabel = "Assistindo a Parasita",
            roomId = "parasita",
            preview = "Esse final rende muita conversa.",
            time = "Ontem",
            unread = 0,
            initialMessages = listOf(
                SeedMessage("Rafa M", "Esse final rende muita conversa. Você já viu Parasita?"),
            ),
        ),
    )

    val rooms = listOf(
        Room(
            id = "cidade-de-deus",
            title = "Cidade de Deus",
            subtitle = "Cinema brasileiro, conversa boa.",
            category = "Filmes",
            glyph = Glyph.Film,
            image = R.drawable.cidade_de_deus,
            scene = "https://images.unsplash.com/photo-1483729558449-99ef09a8c325?auto=format&fit=crop&w=900&q=85",
            count = 10,
            capacity = 20,
            host = "Lucas A",
            tag = "2002 · Drama · 2h 10min",
            progress = "00:42:18 / 02:10:00",
        ),
        Room(
            id = "parasita",
            title = "Parasita",
            subtitle = "Para quem gosta de um bom suspense.",
            category = "Filmes",
            glyph = Glyph.Film,
            image = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=200&q=85",
            scene = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=900&q=85",
            count = 8,
            capacity = 16,
            host = "Rafa M",
            tag = "2019 · Suspense · 2h 12min",
            progress = "00:18:04 / 02:12:00",
        ),
        Room(
            id = "brasilidade",
            title = "Um pouco de brasilidade",
            subtitle = "Seu Jorge, Gilberto Gil e suas indicações.",
            category = "Música",
            glyph = Glyph.Music,
            image = "https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&w=200&q=85",
            scene = "https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&w=900&q=85",
            count = 6,
            capacity = 15,
            host = "Bia S",
            tag = "Música brasileira · Playlist compartilhada",
            progress = "Faixa 4 de 12",
        ),
        Room(
            id = "dom-casmurro",
            title = "Clube: Dom Casmurro",
            subtitle = "Um capítulo, muitas interpretações.",
            category = "Livros",
            glyph = Glyph.Book,
            image = "https://images.unsplash.com/photo-1507842217343-583bb7270b66?auto=format&fit=crop&w=200&q=85",
            scene = "https://images.unsplash.com/photo-1507842217343-583bb7270b66?auto=format&fit=crop&w=900&q=85",
            count = 4,
            capacity = 12,
            host = "Ana P",
            tag = "Machado de Assis · Clube de leitura",
            progress = "Capítulo 32",
        ),
    )

    val discoveryProfiles = listOf(
        DiscoveryProfile(
            name = "Irene F",
            age = 27,
            city = "Rio de Janeiro",
            category = "Filmes",
            photo = unsplash("photo-1534528741775-53994a69daeb", 900),
            culture = R.drawable.cidade_de_deus,
            affinity = 98,
            bio = "Um bom filme, uma conversa que rende e uma playlist para o caminho de volta.",
            interests = listOf(
                Glyph.Film to "Cidade de Deus",
                Glyph.Music to "Seu Jorge",
                Glyph.Book to "Dom Casmurro",
            ),
        ),
        DiscoveryProfile(
            name = "Lucas A",
            age = 29,
            city = "São Paulo",
            category = "Música",
            photo = unsplash("photo-1506794778202-cad84cf45f1d", 900),
            culture = unsplash("photo-1516280440614-37939bbacd81", 900),
            affinity = 94,
            bio = "Coleciono discos e histórias. Sempre à procura de companhia para o próximo show.",
            interests = listOf(
                Glyph.Music to "Gilberto Gil",
                Glyph.Film to "Parasita",
                Glyph.Music to "Seu Jorge",
            ),
        ),
        DiscoveryProfile(
            name = "Ana P",
            age = 26,
            city = "São Paulo",
            category = "Livros",
            photo = unsplash("photo-1524504388940-b1c1722653e1", 900),
            culture = unsplash("photo-1507842217343-583bb7270b66", 900),
            affinity = 96,
            bio = "Mais um capítulo antes de dormir. Vamos trocar livros e descobrir novas histórias?",
            interests = listOf(
                Glyph.Book to "Dom Casmurro",
                Glyph.Film to "Cidade de Deus",
                Glyph.Music to "Tim Maia",
            ),
        ),
    )

    /** People shown in a live room, with "Você" first. */
    val members = listOf(
        myAvatar,
        ireneAvatar,
        unsplash("photo-1506794778202-cad84cf45f1d"),
        unsplash("photo-1524504388940-b1c1722653e1"),
        unsplash("photo-1506794778202-cad84cf45f1d"),
        unsplash("photo-1517841905240-472988babdf9"),
        unsplash("photo-1535713875002-d1d0cf377fde"),
        unsplash("photo-1521119989659-a83eee488004"),
    )
    val memberNames = listOf("Você", "Irene", "Lucas", "Ana", "Pedro", "Bia", "Rafa", "Julia")

    /** Notes of [friends], in the same order. */
    val demoNotes = listOf(
        Note(NoteKind.Music, "Aquele Abraço", "Gilberto Gil", NotePresence.Online),
        Note(NoteKind.Message, "Um filme bom para hoje?", "", NotePresence.Online),
        Note(NoteKind.Message, "Só mais um capítulo.", "", NotePresence.Online),
        Note(NoteKind.Music, "Amiga da Minha Mulher", "Seu Jorge", NotePresence.Offline),
        Note(NoteKind.Message, "No meu tempo, sem pressa.", "", NotePresence.Busy),
    )

    val stickers = listOf("Boa cena!", "Na mesma sintonia", "Só mais um capítulo", "Dá o replay")

    val playlist = listOf(
        "Seu Jorge — Amiga da Minha Mulher",
        "Gilberto Gil — Aquele Abraço",
        "Tim Maia — Não Quero Dinheiro",
    )

    fun friend(id: String?): Friend? = friends.find { it.id == id }

    fun room(id: String?): Room? = rooms.find { it.id == id }
}
