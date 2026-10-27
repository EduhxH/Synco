package com.kult.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Color tokens of the Synco prototype (Figma Make), for the light and dark themes.
 * Names follow the CSS variables of the prototype where there is one (`--feed-*`, `--muted`, …).
 */
@Immutable
data class SyncoColors(
    val isDark: Boolean,
    val background: Color,
    val card: Color,
    val ink: Color,
    val secondary: Color,
    val muted: Color,
    val chip: Color,
    val border: Color,
    val cardBorder: Color,
    val navBackground: Color,
    val navBorder: Color,
    val presenceOnline: Color,
    val heading: Color,
    val emphasis: Color,
    val readName: Color,
    val iconBox: Color,
    val searchIcon: Color,
    val discoveryMuted: Color,
    val roomBubble: Color,
    val roomAuthor: Color,
    val roomText: Color,
    val ownBubble: Color,
    val ownAuthor: Color,
    val modalLabel: Color,
    val toastBackground: Color,
    val toastBorder: Color,
    val stickerPickerBackground: Color,
    val stickerPickerBorder: Color,
    val stickerPickerText: Color,
    val emptyTitle: Color,
    val swatchBackground: Color,
    val swatchBorder: Color,
    val swatchBar: Color,
)

val Accent = Color(0xFFD94B52)
val PresenceOffline = Color(0xFF8A8E98)
val DotOffline = Color(0xFFADB0B8)
val SmallTag = Color(0xFFB6B6BD)
val BackLink = Color(0xFF5B5E68)
val Chevron = Color(0xFFA0A2AA)
val RewardChevron = Color(0xFF85858C)
val AttachmentText = Color(0xFF686B75)
val MoreMembers = Color(0xFF454750)
val InviteBorder = Color(0xFFC9CAD3)
val AvatarBorder = Color(0xFFE2E2E8)
val SwitchOff = Color(0xFFC7C8CF)
val ChatTime = Color(0xFF757781)
val EmptyButtonBorder = Color(0xFFE9C6CD)
val OverlayDark = Color(0xFF161719)
val CaptionDark = Color(0xFF141517)
val ProfilePhotoBackground = Color(0xFF272729)
val VideoBackground = Color(0xFF242527)
val PosterBackground = Color(0xFFE7E7EC)
val VideoMeta = Color(0xFFD2D2D4)
val ModalBackdrop = Color(0x6624252A)
val NotesBackdrop = Color(0x99000000)

val LightSyncoColors = SyncoColors(
    isDark = false,
    background = Color(0xFFF0F1F3),
    card = Color(0xFFFFFFFF),
    ink = Color(0xFF202123),
    secondary = Color(0xFF686A70),
    muted = Color(0xFF73757E),
    chip = Color(0xFFEDEDEE),
    border = Color(0xFFE7E7EC),
    cardBorder = Color(0xFFDCDDE3),
    navBackground = Color(0xFFE6E6E9),
    navBorder = Color(0xFFC6C7CE),
    presenceOnline = Color(0xFF39846A),
    heading = Color(0xFF24252A),
    emphasis = Color(0xFF383A43),
    readName = Color(0xFF2E3038),
    iconBox = Color(0xFF5E6069),
    searchIcon = Color(0xFF8A8C95),
    discoveryMuted = Color(0xFF686A70),
    roomBubble = Color(0xFFF0F0F3),
    roomAuthor = Color(0xFF61636E),
    roomText = Color(0xFF383A43),
    ownBubble = Color(0xFFFCEBEE),
    ownAuthor = Color(0xFFAD3C49),
    modalLabel = Color(0xFF565963),
    toastBackground = Color(0xFFFFFFFF),
    toastBorder = Color(0xFFDEDEE4),
    stickerPickerBackground = Color(0xFFF7F7F9),
    stickerPickerBorder = Color(0xFFE2E2E8),
    stickerPickerText = Color(0xFF62656E),
    emptyTitle = Color(0xFF454750),
    swatchBackground = Color(0xFFFFFFFF),
    swatchBorder = Color(0xFFDEDEE4),
    swatchBar = Color(0xFFDEDEE4),
)

val DarkSyncoColors = SyncoColors(
    isDark = true,
    background = Color(0xFF17181B),
    card = Color(0xFF232429),
    ink = Color(0xFFF2F2F3),
    secondary = Color(0xFFB4B5BC),
    muted = Color(0xFFAAAAB4),
    chip = Color(0xFF34353B),
    border = Color(0xFF35363B),
    cardBorder = Color(0xFF41434C),
    navBackground = Color(0xFF303138),
    navBorder = Color(0xFF595B65),
    presenceOnline = Color(0xFF67B995),
    heading = Color(0xFFEEEEEE),
    emphasis = Color(0xFFEEEEEE),
    readName = Color(0xFFEEEEEE),
    iconBox = Color(0xFFDDDDDD),
    searchIcon = Color(0xFFDDDDDD),
    discoveryMuted = Color(0xFFC9C9D2),
    roomBubble = Color(0xFF2B2C31),
    roomAuthor = Color(0xFFBDBDC6),
    roomText = Color(0xFFE0E0E6),
    ownBubble = Color(0xFF3B292F),
    ownAuthor = Color(0xFFE6A4AE),
    modalLabel = Color(0xFFDDDDDD),
    toastBackground = Color(0xFF202125),
    toastBorder = Color(0xFF3C3D43),
    stickerPickerBackground = Color(0xFF27282D),
    stickerPickerBorder = Color(0xFF3B3C43),
    stickerPickerText = Color(0xFFDDDDDD),
    emptyTitle = Color(0xFFEEEEEE),
    swatchBackground = Color(0xFF1B1C1E),
    swatchBorder = Color(0xFF3B3C40),
    swatchBar = Color(0xFF45464B),
)
