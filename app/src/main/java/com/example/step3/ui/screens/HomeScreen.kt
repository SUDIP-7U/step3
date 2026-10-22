package com.example.step3.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.step3.data.DetailRoute
import com.example.step3.data.Dosha
import com.example.step3.data.MainTab
import com.example.step3.data.PauseData
import com.example.step3.ui.components.EmojiBadge
import com.example.step3.ui.components.LocalAnimationsEnabled
import com.example.step3.ui.components.ScreenList
import com.example.step3.ui.components.SectionLabel
import com.example.step3.ui.components.TridoshaCard
import com.example.step3.ui.components.accent
import com.example.step3.ui.components.softAccent
import com.example.step3.ui.theme.AppTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    onDoshaClick: (Dosha) -> Unit,
    onOpenTab: (MainTab) -> Unit,
    onOpenRoute: (DetailRoute) -> Unit,
) {
    ScreenList {
        item { GreetingBanner() }
        item { DailyPauseCard() }
        item { SectionLabel("The three doshas") }
        items(Dosha.entries.size) { index ->
            val dosha = Dosha.entries[index]
            DoshaOverviewCard(dosha = dosha, onClick = { onDoshaClick(dosha) })
        }
        item { SectionLabel("Quick actions") }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionButton("🌅", "Daily routine", Modifier.weight(1f)) { onOpenTab(MainTab.Routine) }
                    QuickActionButton("🥣", "Food guide", Modifier.weight(1f)) { onOpenTab(MainTab.Food) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionButton("🧘", "My dosha", Modifier.weight(1f)) { onOpenRoute(DetailRoute.MyDosha) }
                    QuickActionButton("⚙️", "Settings", Modifier.weight(1f)) { onOpenRoute(DetailRoute.Settings) }
                }
            }
        }
    }
}

@Composable
private fun GreetingBanner() {
    val greeting = remember { greetingFor(LocalTime.now().hour) }
    val dateText = remember {
        LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.getDefault()))
    }
    TridoshaCard(
        shape = RoundedCornerShape(26.dp),
        containerColor = AppTheme.colors.sageSoft,
        contentPadding = PaddingValues(22.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(dateText, style = MaterialTheme.typography.labelLarge, color = AppTheme.colors.muted)
                Spacer(Modifier.height(4.dp))
                Text(greeting, style = MaterialTheme.typography.headlineMedium, color = AppTheme.colors.deepSage)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Namaste. Take a slow breath. There is nothing to fix today, only something to notice.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.ink,
                )
            }
            Spacer(Modifier.width(12.dp))
            EmojiBadge("🪷", AppTheme.colors.cardBackground, size = 56)
        }
    }
}

private fun greetingFor(hour: Int): String = when (hour) {
    in 5..11 -> "Good morning"
    in 12..16 -> "Good afternoon"
    in 17..20 -> "Good evening"
    else -> "Good night"
}

@Composable
private fun DailyPauseCard() {
    val c = AppTheme.colors
    val animations = LocalAnimationsEnabled.current
    var index by rememberSaveable { mutableIntStateOf(0) }
    val prompt = PauseData.prompts[index]

    val transition = rememberInfiniteTransition(label = "breath")
    val breathing by transition.animateFloat(
        initialValue = 0.82f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breathScale",
    )
    val scale = if (animations) breathing else 1f

    TridoshaCard(shape = RoundedCornerShape(26.dp)) {
        SectionLabel("Your daily pause")
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(88.dp), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clip(CircleShape)
                        .background(c.sage.copy(alpha = 0.35f)),
                )
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(c.sage),
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(prompt.title, style = MaterialTheme.typography.titleLarge, color = c.ink)
                Spacer(Modifier.height(4.dp))
                Text(prompt.body, style = MaterialTheme.typography.bodyMedium, color = c.muted)
            }
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { index = (index + 1) % PauseData.prompts.size }) {
            Text("Another pause", color = c.deepSage, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun DoshaOverviewCard(dosha: Dosha, onClick: () -> Unit) {
    TridoshaCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            EmojiBadge(dosha.emoji, dosha.softAccent(), size = 52)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(dosha.title, style = MaterialTheme.typography.titleLarge, color = AppTheme.colors.ink)
                Text(
                    "${dosha.element} · ${dosha.tagline}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.muted,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    dosha.qualities.joinToString("  ·  "),
                    style = MaterialTheme.typography.labelMedium,
                    color = dosha.accent(),
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = AppTheme.colors.muted,
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    emoji: String,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    TridoshaCard(
        modifier = modifier,
        onClick = onClick,
        containerColor = AppTheme.colors.sageSoft,
        contentPadding = PaddingValues(16.dp),
    ) {
        Text(emoji, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.titleMedium, color = AppTheme.colors.deepSage)
    }
}
