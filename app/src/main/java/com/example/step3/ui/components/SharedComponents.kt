package com.example.step3.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.step3.data.Dosha
import com.example.step3.data.MainTab
import com.example.step3.ui.theme.AppTheme
import java.util.Locale

val LocalAnimationsEnabled = compositionLocalOf { true }

@Composable
@ReadOnlyComposable
fun Dosha.accent(): Color = when (this) {
    Dosha.Vata -> AppTheme.colors.vata
    Dosha.Pitta -> AppTheme.colors.pitta
    Dosha.Kapha -> AppTheme.colors.kapha
}

@Composable
@ReadOnlyComposable
fun Dosha.softAccent(): Color = when (this) {
    Dosha.Vata -> AppTheme.colors.vataSoft
    Dosha.Pitta -> AppTheme.colors.pittaSoft
    Dosha.Kapha -> AppTheme.colors.kaphaSoft
}

@Composable
fun TridoshaBottomBar(selected: MainTab, onSelect: (MainTab) -> Unit) {
    val c = AppTheme.colors
    NavigationBar(containerColor = c.cardBackground, tonalElevation = 0.dp) {
        MainTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = tab == selected,
                onClick = { onSelect(tab) },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label, style = MaterialTheme.typography.labelMedium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = c.deepSage,
                    selectedTextColor = c.deepSage,
                    indicatorColor = c.sageSoft,
                    unselectedIconColor = c.muted,
                    unselectedTextColor = c.muted,
                ),
            )
        }
    }
}

@Composable
fun ScreenList(content: LazyListScope.() -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content,
    )
}

@Composable
fun TridoshaCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(20.dp),
    containerColor: Color = AppTheme.colors.cardBackground,
    contentPadding: PaddingValues = PaddingValues(18.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = CardDefaults.cardColors(
        containerColor = containerColor,
        contentColor = AppTheme.colors.ink,
    )
    val elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    val border = BorderStroke(1.dp, AppTheme.colors.outline)
    val cardModifier = modifier.fillMaxWidth()

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = cardModifier,
            shape = shape,
            colors = colors,
            elevation = elevation,
            border = border,
        ) {
            Column(modifier = Modifier.padding(contentPadding), content = content)
        }
    } else {
        Card(
            modifier = cardModifier,
            shape = shape,
            colors = colors,
            elevation = elevation,
            border = border,
        ) {
            Column(modifier = Modifier.padding(contentPadding), content = content)
        }
    }
}

@Composable
fun EmojiBadge(
    emoji: String,
    background: Color,
    modifier: Modifier = Modifier,
    size: Int = 48,
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        Text(emoji, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun DetailHeader(title: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = AppTheme.colors.ink,
            )
        }
        Spacer(Modifier.width(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = AppTheme.colors.ink,
        )
    }
}

@Composable
fun ScreenTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = AppTheme.colors.ink)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = AppTheme.colors.muted)
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(Locale.getDefault()),
        style = MaterialTheme.typography.labelMedium,
        color = AppTheme.colors.muted,
    )
}

@Composable
fun DoshaChip(dosha: Dosha) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(dosha.softAccent())
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dosha.accent()),
        )
        Spacer(Modifier.width(6.dp))
        Text(dosha.title, style = MaterialTheme.typography.labelMedium, color = AppTheme.colors.ink)
    }
}

@Composable
fun BulletList(items: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { item ->
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    Modifier
                        .padding(top = 8.dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(AppTheme.colors.sage),
                )
                Spacer(Modifier.width(10.dp))
                Text(item, style = MaterialTheme.typography.bodyMedium, color = AppTheme.colors.ink)
            }
        }
    }
}

@Composable
fun DisclaimerCard() {
    TridoshaCard(containerColor = AppTheme.colors.sageSoft) {
        Text("A gentle note", style = MaterialTheme.typography.titleMedium, color = AppTheme.colors.deepSage)
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Tridosha Balance is for education and reflection only. It does not diagnose, " +
                "treat or replace advice from a qualified healthcare professional. If you have a " +
                "medical condition, are pregnant, or take medication, please speak with your " +
                "doctor before changing your diet or routine.",
            style = MaterialTheme.typography.bodyMedium,
            color = AppTheme.colors.ink,
        )
    }
}
