package com.kult.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.kult.app.ui.icons.Glyph
import com.kult.app.ui.icons.SOCIAL_STROKE
import com.kult.app.ui.icons.SyncoIcon
import com.kult.app.ui.theme.Accent
import com.kult.app.ui.theme.SwitchOff
import com.kult.app.ui.theme.SyncoTheme
import com.kult.app.ui.theme.syncoText

/** The prototype's compact switch: 34×19 track, 15 knob. */
@Composable
fun SyncoSwitch(checked: Boolean, modifier: Modifier = Modifier) {
    val knobOffset by animateDpAsState(if (checked) 15.dp else 0.dp, label = "knob")
    Box(
        modifier
            .size(width = 34.dp, height = 19.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (checked) Accent else SwitchOff)
            .padding(2.dp),
    ) {
        Box(
            Modifier
                .offset(x = knobOffset)
                .size(15.dp)
                .clip(CircleShape)
                .background(Color.White),
        )
    }
}

/** Setting with a title, a hint and a switch (`.preference-switch`). */
@Composable
fun PreferenceSwitch(label: String, hint: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(label, style = syncoText(13f, 500, lineHeight = 19.5f), color = SyncoTheme.colors.ink)
            Text(
                hint,
                Modifier.padding(top = 5.dp),
                style = syncoText(11f, lineHeight = 17.6f),
                color = SyncoTheme.colors.muted,
            )
        }
        SyncoSwitch(checked)
    }
}

/** Drop-down replacement for the prototype's `<select>` fields. */
@Composable
fun SyncoSelect(
    value: String,
    options: List<Pair<String, String>>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = syncoText(11f),
    background: Color = SyncoTheme.colors.background,
    borderColor: Color = SyncoTheme.colors.border,
    verticalPadding: Int = 10,
    horizontalPadding: Int = 10,
) {
    var expanded by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(14.dp)
    Box(modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(background)
                .border(1.dp, borderColor, shape)
                .clickable(role = Role.DropdownList) { expanded = true }
                .padding(horizontal = horizontalPadding.dp, vertical = verticalPadding.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                options.firstOrNull { it.first == value }?.second ?: value,
                Modifier.weight(1f),
                style = textStyle,
                color = SyncoTheme.colors.ink,
            )
            SyncoIcon(
                Glyph.ChevronRight,
                14.dp,
                Modifier.rotate(90f),
                tint = SyncoTheme.colors.muted,
                strokeWidth = SOCIAL_STROKE,
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (key, label) ->
                DropdownMenuItem(
                    text = { Text(label, style = syncoText(13f)) },
                    onClick = {
                        onSelect(key)
                        expanded = false
                    },
                )
            }
        }
    }
}

/** Question that expands to show its answer (`<details>` in the prototype). */
@Composable
fun Faq(question: String, answer: String, modifier: Modifier = Modifier) {
    var open by rememberSaveable(question) { mutableStateOf(false) }
    val colors = SyncoTheme.colors
    Column(
        modifier
            .fillMaxWidth()
            .topBorder(colors.border)
            .clickable { open = !open }
            .padding(vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            SyncoIcon(
                Glyph.ChevronRight,
                12.dp,
                Modifier.rotate(if (open) 90f else 0f),
                tint = colors.muted,
                strokeWidth = SOCIAL_STROKE,
            )
            Text(question, style = syncoText(13f, lineHeight = 20.8f), color = colors.ink)
        }
        if (open) {
            Text(
                answer,
                Modifier.padding(top = 10.dp),
                style = syncoText(12f, lineHeight = 21.6f),
                color = colors.secondary,
            )
        }
    }
}
