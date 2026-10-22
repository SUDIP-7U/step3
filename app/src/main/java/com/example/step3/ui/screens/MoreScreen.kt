package com.example.step3.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.step3.data.DetailRoute
import com.example.step3.data.Dosha
import com.example.step3.data.ReflectionData
import com.example.step3.ui.components.BulletList
import com.example.step3.ui.components.DetailHeader
import com.example.step3.ui.components.DisclaimerCard
import com.example.step3.ui.components.EmojiBadge
import com.example.step3.ui.components.ScreenList
import com.example.step3.ui.components.ScreenTitle
import com.example.step3.ui.components.SectionLabel
import com.example.step3.ui.components.TridoshaCard
import com.example.step3.ui.components.softAccent
import com.example.step3.ui.theme.AppTheme

// =====================================================================================
// More Screen
// =====================================================================================

@Composable
fun MoreScreen(onOpenRoute: (DetailRoute) -> Unit) {
    ScreenList {
        item { ScreenTitle("More", "Explore your dosha, learn about the app, and tune your experience.") }
        item {
            MoreLinkRow("🧘", "My dosha", "Explore the three doshas and reflect on your own", onClick = { onOpenRoute(DetailRoute.MyDosha) })
        }
        item {
            MoreLinkRow("🌱", "About", "Why Tridosha Balance exists", onClick = { onOpenRoute(DetailRoute.About) })
        }
        item {
            MoreLinkRow("⚙️", "Settings", "Reminders and animations", onClick = { onOpenRoute(DetailRoute.Settings) })
        }
        item { DisclaimerCard() }
    }
}

@Composable
private fun MoreLinkRow(emoji: String, title: String, subtitle: String, onClick: () -> Unit) {
    TridoshaCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            EmojiBadge(emoji, AppTheme.colors.sageSoft, size = 48)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = AppTheme.colors.ink)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = AppTheme.colors.muted)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = AppTheme.colors.muted,
            )
        }
    }
}

// =====================================================================================
// My Dosha Screen
// =====================================================================================

@Composable
fun MyDoshaScreen(initialDosha: Dosha, onBack: () -> Unit) {
    val c = AppTheme.colors
    var selected by rememberSaveable { mutableStateOf(initialDosha) }
    val answers = rememberSaveable(
        saver = listSaver(
            save = { it.toList() },
            restore = { it.toMutableStateList() },
        ),
    ) { List(ReflectionData.questions.size) { -1 }.toMutableStateList() }

    ScreenList {
        item { DetailHeader(title = "My dosha", onBack = onBack) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Dosha.entries.forEach { dosha ->
                    DoshaTab(
                        dosha = dosha,
                        selected = dosha == selected,
                        modifier = Modifier.weight(1f),
                        onClick = { selected = dosha },
                    )
                }
            }
        }
        item {
            TridoshaCard(shape = RoundedCornerShape(26.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EmojiBadge(selected.emoji, selected.softAccent(), size = 56)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(selected.title, style = MaterialTheme.typography.headlineSmall, color = c.ink)
                        Text(
                            "${selected.element} · ${selected.tagline}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = c.muted,
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    selected.qualities.forEach { quality ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(selected.softAccent())
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        ) {
                            Text(quality, style = MaterialTheme.typography.labelMedium, color = c.ink)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("When in balance", style = MaterialTheme.typography.titleMedium, color = c.deepSage)
                Text(selected.balanced, style = MaterialTheme.typography.bodyMedium, color = c.ink)
                Spacer(Modifier.height(12.dp))
                Text("When out of balance", style = MaterialTheme.typography.titleMedium, color = c.deepSage)
                Text(selected.imbalanced, style = MaterialTheme.typography.bodyMedium, color = c.ink)
                Spacer(Modifier.height(16.dp))
                Text("Gentle ways to support balance", style = MaterialTheme.typography.titleMedium, color = c.deepSage)
                Spacer(Modifier.height(8.dp))
                BulletList(selected.tips)
            }
        }
        item { SectionLabel("A short reflection") }
        items(ReflectionData.questions.size) { qIndex ->
            val question = ReflectionData.questions[qIndex]
            TridoshaCard {
                Text(
                    "${qIndex + 1}. ${question.prompt}",
                    style = MaterialTheme.typography.titleMedium,
                    color = c.ink,
                )
                Spacer(Modifier.height(8.dp))
                question.options.forEachIndexed { optionIndex, option ->
                    val isSelected = answers[qIndex] == optionIndex
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = isSelected,
                                role = Role.RadioButton,
                                onClick = { answers[qIndex] = optionIndex },
                            )
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = c.deepSage,
                                unselectedColor = c.muted,
                            ),
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(option, style = MaterialTheme.typography.bodyMedium, color = c.ink)
                    }
                }
            }
        }
        item {
            ReflectionResultCard(
                answers = answers,
                onReset = { for (i in answers.indices) answers[i] = -1 },
            )
        }
        item { DisclaimerCard() }
    }
}

@Composable
private fun DoshaTab(dosha: Dosha, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val c = AppTheme.colors
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(if (selected) dosha.softAccent() else c.cardBackground)
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(dosha.emoji, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(4.dp))
        Text(
            dosha.title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) c.ink else c.muted,
        )
    }
}

@Composable
private fun ReflectionResultCard(answers: List<Int>, onReset: () -> Unit) {
    val c = AppTheme.colors
    val answered = answers.count { it >= 0 }
    val total = answers.size

    TridoshaCard(containerColor = c.sageSoft, shape = RoundedCornerShape(26.dp)) {
        if (answered < total) {
            Text("Your reflection", style = MaterialTheme.typography.titleMedium, color = c.deepSage)
            Spacer(Modifier.height(4.dp))
            Text(
                "Answer all $total questions to see which dosha your responses lean toward. ($answered of $total answered)",
                style = MaterialTheme.typography.bodyMedium,
                color = c.ink,
            )
        } else {
            val counts = Dosha.entries.map { dosha -> answers.count { it == dosha.ordinal } }
            val topCount = counts.max()
            val leaning = Dosha.entries.filter { counts[it.ordinal] == topCount }

            Text("Your reflection leans toward", style = MaterialTheme.typography.titleMedium, color = c.deepSage)
            Spacer(Modifier.height(6.dp))
            Text(
                text = leaning.joinToString(" & ") { it.title },
                style = MaterialTheme.typography.headlineSmall,
                color = c.ink,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (leaning.size == 1) {
                    "${leaning.first().tagline}. Explore the guidance above for ${leaning.first().title}."
                } else {
                    "Your answers are evenly mixed, which is common. Explore each dosha above and notice what feels familiar."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = c.ink,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "This is a light self-reflection, not a diagnosis.",
                style = MaterialTheme.typography.labelMedium,
                color = c.muted,
            )
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onReset,
            enabled = answered > 0,
            colors = ButtonDefaults.buttonColors(
                containerColor = c.deepSage,
                contentColor = c.onDeepSage,
                disabledContainerColor = c.outline,
                disabledContentColor = c.muted,
            ),
            shape = RoundedCornerShape(50),
        ) {
            Text("Start over", style = MaterialTheme.typography.labelLarge)
        }
    }
}

// =====================================================================================
// About Screen
// =====================================================================================

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val c = AppTheme.colors
    val context = LocalContext.current
    val versionName = remember {
        runCatching {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "1.0"
    }

    ScreenList {
        item { DetailHeader(title = "About", onBack = onBack) }
        item {
            TridoshaCard(shape = RoundedCornerShape(26.dp), contentPadding = PaddingValues(22.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EmojiBadge("🪷", c.sageSoft, size = 60)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Tridosha Balance", style = MaterialTheme.typography.headlineSmall, color = c.ink)
                        Text("Version $versionName", style = MaterialTheme.typography.bodyMedium, color = c.muted)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    "Tridosha Balance is a calm companion for learning the basics of Ayurveda. " +
                        "It introduces the three doshas, Vata, Pitta and Kapha, and offers simple " +
                        "ideas for daily rhythm and mindful eating.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = c.ink,
                )
            }
        }
        item {
            TridoshaCard {
                Text("What you will find here", style = MaterialTheme.typography.titleMedium, color = c.deepSage)
                Spacer(Modifier.height(10.dp))
                BulletList(
                    listOf(
                        "A daily pause to slow down and breathe.",
                        "Gentle routines for morning, daytime, evening and night.",
                        "A food guide inspired by traditional Ayurvedic ideas.",
                        "A light reflection to explore your own tendencies.",
                    ),
                )
            }
        }
        item { DisclaimerCard() }
    }
}

// =====================================================================================
// Settings Screen
// =====================================================================================

@Composable
fun SettingsScreen(
    remindersEnabled: Boolean,
    animationsEnabled: Boolean,
    onRemindersChange: (Boolean) -> Unit,
    onAnimationsChange: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    ScreenList {
        item { DetailHeader(title = "Settings", onBack = onBack) }
        item {
            SettingToggleRow(
                emoji = "🔔",
                title = "Daily reminders",
                subtitle = "A soft nudge to take your daily pause",
                checked = remindersEnabled,
                onCheckedChange = onRemindersChange,
            )
        }
        item {
            SettingToggleRow(
                emoji = "✨",
                title = "Gentle animations",
                subtitle = "Breathing circle and smooth transitions",
                checked = animationsEnabled,
                onCheckedChange = onAnimationsChange,
            )
        }
        item {
            TridoshaCard {
                Text("Appearance", style = MaterialTheme.typography.titleMedium, color = AppTheme.colors.ink)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Tridosha Balance follows your device's light or dark mode automatically.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.muted,
                )
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    emoji: String,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val c = AppTheme.colors
    TridoshaCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EmojiBadge(emoji, c.sageSoft, size = 48)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = c.ink)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = c.muted)
            }
            Spacer(Modifier.width(12.dp))
            Switch(
                checked = checked,
                onCheckedChange = null,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = c.onDeepSage,
                    checkedTrackColor = c.deepSage,
                    checkedBorderColor = c.deepSage,
                    uncheckedThumbColor = c.muted,
                    uncheckedTrackColor = c.sageSoft,
                    uncheckedBorderColor = c.outline,
                ),
            )
        }
    }
}
