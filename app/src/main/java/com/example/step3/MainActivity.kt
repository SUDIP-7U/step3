package com.example.step3

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.example.step3.ui.theme.AppTheme
import com.example.step3.ui.theme.TridoshaTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// =====================================================================================
// Activity
// =====================================================================================

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

// =====================================================================================
// Persistence
// =====================================================================================

class SettingsStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var remindersEnabled: Boolean
        get() = prefs.getBoolean(KEY_REMINDERS, true)
        set(value) = prefs.edit(commit = false) { putBoolean(KEY_REMINDERS, value) }

    var animationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_ANIMATIONS, true)
        set(value) = prefs.edit(commit = false) { putBoolean(KEY_ANIMATIONS, value) }

    private companion object {
        const val PREFS_NAME = "tridosha_settings"
        const val KEY_REMINDERS = "reminders_enabled"
        const val KEY_ANIMATIONS = "animations_enabled"
    }
}

// =====================================================================================
// Data models
// =====================================================================================

enum class Dosha(
    val title: String,
    val emoji: String,
    val element: String,
    val tagline: String,
    val qualities: List<String>,
    val balanced: String,
    val imbalanced: String,
    val tips: List<String>,
) {
    Vata(
        title = "Vata",
        emoji = "🍃",
        element = "Air & Space",
        tagline = "The energy of movement",
        qualities = listOf("Light", "Dry", "Cool", "Mobile"),
        balanced = "Creative, lively, adaptable and quick to learn.",
        imbalanced = "Restlessness, worry, dry skin and irregular sleep or appetite.",
        tips = listOf(
            "Favor warm, cooked, gently spiced meals.",
            "Keep a steady daily rhythm for meals and sleep.",
            "Try a slow self-massage with warm oil before bathing.",
            "Choose calming movement such as walking or gentle stretching.",
        ),
    ),
    Pitta(
        title = "Pitta",
        emoji = "🔥",
        element = "Fire & Water",
        tagline = "The energy of transformation",
        qualities = listOf("Warm", "Sharp", "Oily", "Intense"),
        balanced = "Focused, confident, warm-hearted and a natural leader.",
        imbalanced = "Irritability, overheating, impatience and sharp hunger.",
        tips = listOf(
            "Favor cooling, fresh foods and sweet, bitter flavors.",
            "Avoid long exposure to midday heat.",
            "Leave room for rest and play, not only achievement.",
            "Spend quiet time near water, trees or soft moonlight.",
        ),
    ),
    Kapha(
        title = "Kapha",
        emoji = "🌿",
        element = "Earth & Water",
        tagline = "The energy of structure",
        qualities = listOf("Heavy", "Steady", "Soft", "Cool"),
        balanced = "Calm, loyal, nurturing and grounded with great stamina.",
        imbalanced = "Sluggishness, heaviness, clinging to routine and low motivation.",
        tips = listOf(
            "Start the day with brisk movement and fresh air.",
            "Favor light, warm, well-spiced meals.",
            "Invite variety: try something new each week.",
            "Rise early and avoid daytime napping.",
        ),
    ),
}

data class RoutinePeriod(
    val title: String,
    val emoji: String,
    val timeRange: String,
    val summary: String,
    val tips: List<String>,
)

data class FoodItem(
    val name: String,
    val note: String,
    val soothes: List<Dosha>,
)

data class FoodCategory(
    val id: String,
    val title: String,
    val emoji: String,
    val blurb: String,
    val items: List<FoodItem>,
)

data class PausePrompt(val title: String, val body: String)

data class ReflectionQuestion(
    val prompt: String,
    /** Answers are ordered to match Dosha.entries: Vata, Pitta, Kapha. */
    val options: List<String>,
)

enum class MainTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Home("Home", Icons.Filled.Home),
    Routine("Routine", Icons.Filled.DateRange),
    Food("Food", Icons.Filled.Favorite),
    More("More", Icons.Filled.Menu),
}

enum class DetailRoute { Food, MyDosha, About, Settings }

private data class ScreenState(val tab: MainTab, val route: DetailRoute?)

// =====================================================================================
// Static content
// =====================================================================================

private object PauseData {
    val prompts = listOf(
        PausePrompt("Breathe in for four", "Let the exhale be a little longer than the inhale. Repeat five times."),
        PausePrompt("Soften your shoulders", "Notice where you are holding tension and let it melt away on the next breath."),
        PausePrompt("Feel your feet", "Press them gently into the floor. You are here, and that is enough."),
        PausePrompt("Sip something warm", "Hold the cup with both hands and taste the first sip slowly."),
        PausePrompt("Look far away", "Rest your eyes on something distant for a full minute. Let your gaze be wide."),
    )
}

private object RoutineData {
    val periods = listOf(
        RoutinePeriod(
            title = "Morning",
            emoji = "🌅",
            timeRange = "5:30 – 9:00",
            summary = "Wake gently and greet the day before the world asks anything of you.",
            tips = listOf(
                "Rise around sunrise, ideally before 7:00.",
                "Drink a glass of warm water.",
                "Stretch or walk slowly for 10 minutes.",
                "Eat a warm, light breakfast without screens.",
            ),
        ),
        RoutinePeriod(
            title = "Daytime",
            emoji = "☀️",
            timeRange = "11:00 – 15:00",
            summary = "Digestion is traditionally strongest at midday, so make this your main meal.",
            tips = listOf(
                "Eat your largest meal between noon and 1:00.",
                "Sit down and chew slowly.",
                "Take a 10-minute walk after eating.",
                "Sip warm water through the afternoon.",
            ),
        ),
        RoutinePeriod(
            title = "Evening",
            emoji = "🌇",
            timeRange = "17:00 – 20:00",
            summary = "Let the pace slow. Wind down your effort as the light fades.",
            tips = listOf(
                "Enjoy a lighter dinner before 7:30.",
                "Take a calm stroll outdoors.",
                "Dim the lights and set screens aside.",
                "Share a quiet moment with someone you care about.",
            ),
        ),
        RoutinePeriod(
            title = "Night",
            emoji = "🌙",
            timeRange = "21:00 – 22:30",
            summary = "Prepare body and mind for deep, restorative sleep.",
            tips = listOf(
                "Aim to be in bed by 10:00.",
                "Try warm spiced milk or a herbal infusion.",
                "Write down three things you are grateful for.",
                "Rub a little warm oil on your feet before sleeping.",
            ),
        ),
    )
}

private object FoodData {
    private val all = Dosha.entries

    val categories = listOf(
        FoodCategory(
            id = "grains",
            title = "Grains",
            emoji = "🌾",
            blurb = "Grains are the grounding base of most traditional meals.",
            items = listOf(
                FoodItem("Basmati rice", "Light, easy to digest and soothing in warm dishes.", listOf(Dosha.Vata, Dosha.Pitta)),
                FoodItem("Oats", "Warm, grounding porridge that suits cooler mornings.", listOf(Dosha.Vata, Dosha.Pitta)),
                FoodItem("Barley", "Light and slightly drying, useful when you feel heavy.", listOf(Dosha.Pitta, Dosha.Kapha)),
                FoodItem("Millet", "A warming, fluffy grain with a gentle, earthy taste.", listOf(Dosha.Kapha)),
            ),
        ),
        FoodCategory(
            id = "fruits",
            title = "Fruits",
            emoji = "🍎",
            blurb = "Enjoy fruit in season, ripe, and apart from heavy meals.",
            items = listOf(
                FoodItem("Ripe mango", "Sweet and nourishing, a classic summer fruit.", listOf(Dosha.Vata, Dosha.Pitta)),
                FoodItem("Pomegranate", "Astringent-sweet and traditionally balancing for everyone.", all),
                FoodItem("Stewed apple", "Cooked with cinnamon, it becomes soft and comforting.", listOf(Dosha.Vata, Dosha.Pitta)),
                FoodItem("Berries", "Tart and light, they bring brightness to the plate.", listOf(Dosha.Pitta, Dosha.Kapha)),
            ),
        ),
        FoodCategory(
            id = "vegetables",
            title = "Vegetables",
            emoji = "🥕",
            blurb = "Lightly cooked vegetables are gentler on digestion than raw ones.",
            items = listOf(
                FoodItem("Sweet potato", "Naturally sweet and grounding when roasted or steamed.", listOf(Dosha.Vata, Dosha.Pitta)),
                FoodItem("Leafy greens", "Bitter and astringent, lovely sautéed with a little ghee.", listOf(Dosha.Pitta, Dosha.Kapha)),
                FoodItem("Carrots", "Mildly sweet and warming when cooked.", listOf(Dosha.Vata, Dosha.Kapha)),
                FoodItem("Zucchini", "Soft, hydrating and very easy to digest.", listOf(Dosha.Vata, Dosha.Pitta)),
            ),
        ),
        FoodCategory(
            id = "spices",
            title = "Spices",
            emoji = "🌶️",
            blurb = "Spices awaken digestion and make food easier to enjoy.",
            items = listOf(
                FoodItem("Ginger", "Warming and stimulating, welcome in cool, damp weather.", listOf(Dosha.Vata, Dosha.Kapha)),
                FoodItem("Cumin", "A gentle everyday digestive that suits most meals.", all),
                FoodItem("Fennel", "Cooling and sweet, often chewed after meals.", all),
                FoodItem("Cinnamon", "Sweet and warming, perfect in porridge and teas.", listOf(Dosha.Vata, Dosha.Kapha)),
                FoodItem("Coriander", "Fresh and cooling, a friend to sensitive digestion.", all),
            ),
        ),
        FoodCategory(
            id = "drinks",
            title = "Drinks",
            emoji = "🍵",
            blurb = "Warm drinks, sipped slowly, are a quiet daily ritual.",
            items = listOf(
                FoodItem("Warm water", "Simple and soothing, sipped through the day.", all),
                FoodItem("Cumin-coriander-fennel tea", "A gentle tri-spice infusion after meals.", all),
                FoodItem("Ginger tea", "Warming and clearing on a grey morning.", listOf(Dosha.Vata, Dosha.Kapha)),
                FoodItem("Warm spiced milk", "A comforting evening cup with cardamom or nutmeg.", listOf(Dosha.Vata, Dosha.Pitta)),
                FoodItem("Fresh mint infusion", "Cooling and refreshing in warm weather.", listOf(Dosha.Pitta)),
            ),
        ),
    )

    fun byId(id: String?): FoodCategory = categories.firstOrNull { it.id == id } ?: categories.first()
}

private object ReflectionData {
    val questions = listOf(
        ReflectionQuestion(
            prompt = "When you wake up, you usually feel…",
            options = listOf(
                "Light and quick to get going",
                "Alert, warm and focused",
                "Steady, but slow to start",
            ),
        ),
        ReflectionQuestion(
            prompt = "When you are under pressure, you tend to…",
            options = listOf(
                "Feel anxious or scattered",
                "Become irritable or critical",
                "Withdraw or hold on tightly",
            ),
        ),
        ReflectionQuestion(
            prompt = "Your appetite is usually…",
            options = listOf(
                "Variable. Sometimes you forget to eat",
                "Strong. You get hungry right on time",
                "Steady, and you can easily skip a meal",
            ),
        ),
        ReflectionQuestion(
            prompt = "You feel your best in weather that is…",
            options = listOf(
                "Warm and gentle",
                "Cool and breezy",
                "Dry and light",
            ),
        ),
    )
}

// =====================================================================================
// Theme helpers
// =====================================================================================

private val LocalAnimationsEnabled = compositionLocalOf { true }

@Composable
@ReadOnlyComposable
private fun Dosha.accent(): Color = when (this) {
    Dosha.Vata -> AppTheme.colors.vata
    Dosha.Pitta -> AppTheme.colors.pitta
    Dosha.Kapha -> AppTheme.colors.kapha
}

@Composable
@ReadOnlyComposable
private fun Dosha.softAccent(): Color = when (this) {
    Dosha.Vata -> AppTheme.colors.vataSoft
    Dosha.Pitta -> AppTheme.colors.pittaSoft
    Dosha.Kapha -> AppTheme.colors.kaphaSoft
}

// =====================================================================================
// App shell + navigation
// =====================================================================================

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

@Composable
private fun TridoshaBottomBar(selected: MainTab, onSelect: (MainTab) -> Unit) {
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

// =====================================================================================
// Shared building blocks
// =====================================================================================

@Composable
private fun ScreenList(content: LazyListScope.() -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content,
    )
}

@Composable
private fun TridoshaCard(
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
private fun EmojiBadge(
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
private fun DetailHeader(title: String, onBack: () -> Unit) {
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
private fun ScreenTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = AppTheme.colors.ink)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = AppTheme.colors.muted)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(Locale.getDefault()),
        style = MaterialTheme.typography.labelMedium,
        color = AppTheme.colors.muted,
    )
}

@Composable
private fun DoshaChip(dosha: Dosha) {
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
private fun BulletList(items: List<String>) {
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
private fun DisclaimerCard() {
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

// =====================================================================================
// Home
// =====================================================================================

@Composable
private fun HomeScreen(
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

// =====================================================================================
// Daily Routine
// =====================================================================================

@Composable
private fun RoutineScreen() {
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

// =====================================================================================
// Food Guide
// =====================================================================================

@Composable
private fun FoodGuideScreen(onCategoryClick: (FoodCategory) -> Unit) {
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
private fun FoodDetailScreen(category: FoodCategory, onBack: () -> Unit) {
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

// =====================================================================================
// More
// =====================================================================================

@Composable
private fun MoreScreen(onOpenRoute: (DetailRoute) -> Unit) {
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
// My Dosha
// =====================================================================================

@Composable
private fun MyDoshaScreen(initialDosha: Dosha, onBack: () -> Unit) {
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
// About
// =====================================================================================

@Composable
private fun AboutScreen(onBack: () -> Unit) {
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
// Settings
// =====================================================================================

@Composable
private fun SettingsScreen(
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
