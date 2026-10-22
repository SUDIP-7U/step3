package com.example.step3.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.step3.data.RoutineData
import com.example.step3.data.RoutinePeriod
import com.example.step3.ui.components.BulletList
import com.example.step3.ui.components.EmojiBadge
import com.example.step3.ui.components.LocalAnimationsEnabled
import com.example.step3.ui.components.ScreenList
import com.example.step3.ui.components.ScreenTitle
import com.example.step3.ui.components.TridoshaCard
import com.example.step3.ui.theme.AppTheme

@Composable
fun RoutineScreen() {
    ScreenList {
        item {
            ScreenTitle(
                title = "Daily routine",
                subtitle = "Four gentle rhythms for the day. Tap a card for tips.",
            )
        }
        items(RoutineData.periods.size) { index ->
            RoutineCard(RoutineData.periods[index])
        }
    }
}

@Composable
private fun RoutineCard(period: RoutinePeriod) {
    val c = AppTheme.colors
    val animations = LocalAnimationsEnabled.current
    var expanded by rememberSaveable(period.title) { mutableStateOf(false) }

    TridoshaCard(
        modifier = if (animations) Modifier.animateContentSize() else Modifier,
        onClick = { expanded = !expanded },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            EmojiBadge(period.emoji, c.sageSoft, size = 52)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(period.title, style = MaterialTheme.typography.titleLarge, color = c.ink)
                Text(period.timeRange, style = MaterialTheme.typography.labelLarge, color = c.deepSage)
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(period.summary, style = MaterialTheme.typography.bodyMedium, color = c.muted)
        if (expanded) {
            Spacer(Modifier.height(14.dp))
            BulletList(period.tips)
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = if (expanded) "Tap to collapse" else "Tap for tips",
            style = MaterialTheme.typography.labelMedium,
            color = c.deepSage,
        )
    }
}
