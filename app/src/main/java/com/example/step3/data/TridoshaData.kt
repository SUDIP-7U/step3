package com.example.step3.data

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.edit

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

enum class MainTab(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Filled.Home),
    Routine("Routine", Icons.Filled.DateRange),
    Food("Food", Icons.Filled.Favorite),
    More("More", Icons.Filled.Menu),
}

enum class DetailRoute { Food, MyDosha, About, Settings }

data class ScreenState(val tab: MainTab, val route: DetailRoute?)

// =====================================================================================
// Static content
// =====================================================================================

object PauseData {
    val prompts = listOf(
        PausePrompt("Breathe in for four", "Let the exhale be a little longer than the inhale. Repeat five times."),
        PausePrompt("Soften your shoulders", "Notice where you are holding tension and let it melt away on the next breath."),
        PausePrompt("Feel your feet", "Press them gently into the floor. You are here, and that is enough."),
        PausePrompt("Sip something warm", "Hold the cup with both hands and taste the first sip slowly."),
        PausePrompt("Look far away", "Rest your eyes on something distant for a full minute. Let your gaze be wide."),
    )
}

object RoutineData {
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

object FoodData {
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

object ReflectionData {
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
