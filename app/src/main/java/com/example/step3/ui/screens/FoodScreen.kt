package com.example.step3.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.step3.data.FoodCategory
import com.example.step3.data.FoodData
import com.example.step3.ui.components.DetailHeader
import com.example.step3.ui.components.DisclaimerCard
import com.example.step3.ui.components.DoshaChip
import com.example.step3.ui.components.EmojiBadge
import com.example.step3.ui.components.ScreenList
import com.example.step3.ui.components.ScreenTitle
import com.example.step3.ui.components.SectionLabel
import com.example.step3.ui.components.TridoshaCard
import com.example.step3.ui.theme.AppTheme

@Composable
fun FoodGuideScreen(onCategoryClick: (FoodCategory) -> Unit) {
    ScreenList {
        item { ScreenTitle("Food guide", "Eat with attention and let your meals be simple.") }
        item {
            TridoshaCard(containerColor = AppTheme.colors.sageSoft) {
                Text("How to read this guide", style = MaterialTheme.typography.titleMedium, color = AppTheme.colors.deepSage)
                Spacer(Modifier.height(6.dp))
                Text(
                    "These notes follow traditional Ayurvedic ideas about which foods feel soothing to each dosha. " +
                        "Treat them as gentle inspiration, not rules. Your own experience matters most.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.ink,
                )
            }
        }
        item { SectionLabel("Categories") }
        val rows = FoodData.categories.chunked(2)
        items(rows.size) { rowIndex ->
            val row = rows[rowIndex]
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { category ->
                    FoodCategoryCard(
                        category = category,
                        modifier = Modifier.weight(1f),
                        onClick = { onCategoryClick(category) },
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun FoodCategoryCard(category: FoodCategory, modifier: Modifier, onClick: () -> Unit) {
    TridoshaCard(modifier = modifier, onClick = onClick, shape = RoundedCornerShape(26.dp)) {
        EmojiBadge(category.emoji, AppTheme.colors.sageSoft, size = 52)
        Spacer(Modifier.height(12.dp))
        Text(category.title, style = MaterialTheme.typography.titleLarge, color = AppTheme.colors.ink)
        Text(
            "${category.items.size} foods",
            style = MaterialTheme.typography.bodyMedium,
            color = AppTheme.colors.muted,
        )
    }
}

@Composable
fun FoodDetailScreen(category: FoodCategory, onBack: () -> Unit) {
    val c = AppTheme.colors
    ScreenList {
        item { DetailHeader(title = category.title, onBack = onBack) }
        item {
            TridoshaCard(containerColor = c.sageSoft) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EmojiBadge(category.emoji, c.cardBackground, size = 52)
                    Spacer(Modifier.width(14.dp))
                    Text(
                        category.blurb,
                        style = MaterialTheme.typography.bodyLarge,
                        color = c.ink,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        items(category.items.size) { index ->
            val item = category.items[index]
            TridoshaCard {
                Text(item.name, style = MaterialTheme.typography.titleLarge, color = c.ink)
                Spacer(Modifier.height(4.dp))
                Text(item.note, style = MaterialTheme.typography.bodyMedium, color = c.muted)
                Spacer(Modifier.height(12.dp))
                SectionLabel("Traditionally soothing for")
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item.soothes.forEach { DoshaChip(it) }
                }
            }
        }
        item { DisclaimerCard() }
    }
}
