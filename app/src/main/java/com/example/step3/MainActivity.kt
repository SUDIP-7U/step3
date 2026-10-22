package com.example.step3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.step3.data.DetailRoute
import com.example.step3.data.Dosha
import com.example.step3.data.FoodData
import com.example.step3.data.MainTab
import com.example.step3.data.ScreenState
import com.example.step3.data.SettingsStore
import com.example.step3.ui.components.LocalAnimationsEnabled
import com.example.step3.ui.components.TridoshaBottomBar
import com.example.step3.ui.screens.AboutScreen
import com.example.step3.ui.screens.FoodDetailScreen
import com.example.step3.ui.screens.FoodGuideScreen
import com.example.step3.ui.screens.HomeScreen
import com.example.step3.ui.screens.MoreScreen
import com.example.step3.ui.screens.MyDoshaScreen
import com.example.step3.ui.screens.RoutineScreen
import com.example.step3.ui.screens.SettingsScreen
import com.example.step3.ui.theme.AppTheme
import com.example.step3.ui.theme.TridoshaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val store = SettingsStore(applicationContext)
        setContent {
            TridoshaTheme {
                TridoshaApp(store = store)
            }
        }
    }
}

@Composable
fun TridoshaApp(store: SettingsStore) {
    var remindersEnabled by remember { mutableStateOf(store.remindersEnabled) }
    var animationsEnabled by remember { mutableStateOf(store.animationsEnabled) }

    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Home) }
    var detail by rememberSaveable { mutableStateOf<DetailRoute?>(null) }
    var selectedFoodId by rememberSaveable { mutableStateOf<String?>(null) }
    var focusedDosha by rememberSaveable { mutableStateOf(Dosha.Vata) }

    BackHandler(enabled = detail != null) { detail = null }
    BackHandler(enabled = detail == null && selectedTab != MainTab.Home) { selectedTab = MainTab.Home }

    CompositionLocalProvider(LocalAnimationsEnabled provides animationsEnabled) {
        Scaffold(
            containerColor = AppTheme.colors.cream,
            bottomBar = {
                TridoshaBottomBar(
                    selected = selectedTab,
                    onSelect = { tab ->
                        selectedTab = tab
                        detail = null
                    },
                )
            },
        ) { innerPadding ->
            AnimatedContent(
                targetState = ScreenState(selectedTab, detail),
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                transitionSpec = {
                    if (animationsEnabled) {
                        (fadeIn(tween(280)) + slideInHorizontally(tween(280)) { it / 14 }) togetherWith
                            fadeOut(tween(140))
                    } else {
                        EnterTransition.None togetherWith ExitTransition.None
                    }
                },
                label = "screen",
            ) { state ->
                when (state.route) {
                    DetailRoute.Food -> FoodDetailScreen(
                        category = FoodData.byId(selectedFoodId),
                        onBack = { detail = null },
                    )

                    DetailRoute.MyDosha -> MyDoshaScreen(
                        initialDosha = focusedDosha,
                        onBack = { detail = null },
                    )

                    DetailRoute.About -> AboutScreen(onBack = { detail = null })

                    DetailRoute.Settings -> SettingsScreen(
                        remindersEnabled = remindersEnabled,
                        animationsEnabled = animationsEnabled,
                        onRemindersChange = {
                            remindersEnabled = it
                            store.remindersEnabled = it
                        },
                        onAnimationsChange = {
                            animationsEnabled = it
                            store.animationsEnabled = it
                        },
                        onBack = { detail = null },
                    )

                    null -> when (state.tab) {
                        MainTab.Home -> HomeScreen(
                            onDoshaClick = {
                                focusedDosha = it
                                detail = DetailRoute.MyDosha
                            },
                            onOpenTab = { selectedTab = it },
                            onOpenRoute = { detail = it },
                        )

                        MainTab.Routine -> RoutineScreen()

                        MainTab.Food -> FoodGuideScreen(
                            onCategoryClick = {
                                selectedFoodId = it.id
                                detail = DetailRoute.Food
                            },
                        )

                        MainTab.More -> MoreScreen(onOpenRoute = { detail = it })
                    }
                }
            }
        }
    }
}
