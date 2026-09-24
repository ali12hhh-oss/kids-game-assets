package com.ali12hhh.kidslearning.navigation

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import io.github.sceneview.Scene
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.node.ModelNode
import io.github.sceneview.math.Position
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ali12hhh.kidslearning.core.LearningCatalog
import java.util.Locale

private const val CLIP_IDLE = 0
// The merge script defines: 0 Idle_A, 1 Idle_B, 2 Interact, 3 PickUp,
// 4 Use_Item, 5 Spawn_Ground, 6 Waving, 7 Cheering, 8 Sit_Floor_Idle,
// 9 Jump_Full_Short, 10 Walking_A.
private const val CLIP_WAVE = 6
private val REACTION_CLIPS = listOf(1, 2, 3, 4, 5, 7, 8, 9, 10)

private const val CHARACTER_BOX_SHIFT_DOWN = 0.19f
private const val GREETING_UTTERANCE_ID = "home_greeting"
private const val GREETING_TEXT = "مرحبا صديقي. اختر ماذا نتعلم اليوم."

private fun Modifier.shiftDownByFraction(fraction: Float): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(placeable.width, placeable.height) {
        placeable.place(0, (placeable.height * fraction).roundToInt())
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var hasGreetedOnAppLaunch by rememberSaveable { mutableStateOf(false) }
    NavHost(navController = navController, startDestination = AppRoutes.HOME) {
        composable(AppRoutes.SETTINGS) {
            SettingsPage(onBack = { navController.popBackStack() })
        }
        composable(AppRoutes.SHOP) {
            ShopPage(onBack = { navController.popBackStack() })
        }
        composable(AppRoutes.COLLECTION) {
            ShopPage(initialCollection = true, onBack = { navController.popBackStack() })
        }
        composable(AppRoutes.HOME) {
            // Freeze the greeting decision for this Home composition. Without remember,
            // the flag update below recomposes Home immediately and can dispose TTS before
            // the engine finishes initializing, which makes the greeting disappear.
            val greetOnThisHomeEntry = remember { !hasGreetedOnAppLaunch }
            LaunchedEffect(Unit) { hasGreetedOnAppLaunch = true }
            HomePage(
                onArabic = { navController.navigate(AppRoutes.ARABIC_LEVELS) },
                onEnglish = { navController.navigate(AppRoutes.ENGLISH_LEVELS) },
                onPlay = { navController.navigate(AppRoutes.PLAY) },
                onSettings = { navController.navigate(AppRoutes.SETTINGS) },
                onShop = { navController.navigate(AppRoutes.SHOP) },
                onCollection = { navController.navigate(AppRoutes.COLLECTION) },
                greetOnEntry = greetOnThisHomeEntry
            )
        }
        composable(AppRoutes.ARABIC_LETTERS) {
            LanguageLevelsPage(
                language = "ar",
                onBack = { navController.popBackStack() },
                onSelectLevel = { /* Level content will be connected later. */ },
                onSpeak = { _, _ -> /* Speech wiring will be added later. */ }
            )
        }
        composable(AppRoutes.ENGLISH_LETTERS) {
            LanguageLevelsPage(
                language = "en",
                onBack = { navController.popBackStack() },
                onSelectLevel = { /* Level content will be connected later. */ },
                onSpeak = { _, _ -> /* Speech wiring will be added later. */ }
            )
        }
        composable(AppRoutes.ARABIC_LEVELS) {
            LanguageLevelsPage(
                language = "ar",
                onBack = { navController.popBackStack() },
                onSelectLevel = { level -> navController.navigate("${AppRoutes.ARABIC_LEVEL_PAGE}".replace("{level}", level.toString())) },
                onSpeak = { _, _ -> /* Section-page speech will be added later. */ }
            )
        }
        composable(AppRoutes.ENGLISH_LEVELS) {
            LanguageLevelsPage(
                language = "en",
                onBack = { navController.popBackStack() },
                onSelectLevel = { level -> navController.navigate("${AppRoutes.ENGLISH_LEVEL_PAGE}".replace("{level}", level.toString())) },
                onSpeak = { _, _ -> /* Section-page speech will be added later. */ }
            )
        }
        composable(AppRoutes.ARABIC_LEVEL_PAGE) { entry ->
            val level = entry.arguments?.getString("level")?.toIntOrNull() ?: 1
            LevelSectionsPage(
                language = "ar",
                level = level,
                onBack = { navController.popBackStack() },
                onSelectSection = { section ->
                    if (level == 1 && section == "reading") {
                        navController.navigate(AppRoutes.ARABIC_LEVEL_CONTENT.replace("{level}", "1"))
                    } else if (level == 1 && section == "math") {
                        navController.navigate(AppRoutes.ARABIC_LEVEL_ONE_MATH)
                    } else if (level == 2 && section == "reading") {
                        navController.navigate(AppRoutes.ARABIC_LEVEL_TWO_READING)
                    } else if (level == 2 && section == "math") {
                        navController.navigate(AppRoutes.ARABIC_LEVEL_TWO_MATH)
                    } else if (level == 3 && section == "reading") {
                        navController.navigate(AppRoutes.ARABIC_LEVEL_THREE_READING)
                    } else if (level == 3 && section == "math") {
                        navController.navigate(AppRoutes.ARABIC_LEVEL_THREE_MATH)
                    }
                }
            )
        }
        composable(AppRoutes.ARABIC_LEVEL_CONTENT) { entry ->
            val level = entry.arguments?.getString("level")?.toIntOrNull() ?: 1
            if (level == 1) {
                ArabicReadingPage(onBack = { navController.popBackStack() })
            } else {
                ContentPage("العربية", "المحتوى قيد التجهيز")
            }
        }
        composable(AppRoutes.ARABIC_LEVEL_ONE_MATH) {
            ArabicLevelOneMathPage(onBack = { navController.popBackStack() })
        }
        composable(AppRoutes.ARABIC_LEVEL_TWO_MATH) {
            ArabicLevelTwoMathPage(onBack = { navController.popBackStack() })
        }
        composable(AppRoutes.ARABIC_LEVEL_TWO_READING) {
            ArabicLevelTwoReadingPage(onBack = { navController.popBackStack() })
        }
        composable(AppRoutes.ARABIC_LEVEL_THREE_READING) {
            ArabicLevelThreeReadingPage(onBack = { navController.popBackStack() })
        }
        composable(AppRoutes.ARABIC_LEVEL_THREE_MATH) {
            ArabicLevelThreeMathPage(onBack = { navController.popBackStack() })
        }
        composable(AppRoutes.ENGLISH_LEVEL_PAGE) { entry ->
            val level = entry.arguments?.getString("level")?.toIntOrNull() ?: 1
            LevelSectionsPage(
                language = "en",
                level = level,
                onBack = { navController.popBackStack() },
                onSelectSection = { section ->
                    if (level == 1 && (section == "letters" || section == "numbers")) {
                        navController.navigate(if (section == "letters") AppRoutes.ENGLISH_LEVEL_ONE_LETTERS else AppRoutes.ENGLISH_LEVEL_ONE_NUMBERS)
                    } else if (level == 2 && (section == "letters" || section == "numbers")) {
                        navController.navigate(if (section == "letters") AppRoutes.ENGLISH_LEVEL_TWO_LETTERS else AppRoutes.ENGLISH_LEVEL_TWO_NUMBERS)
                    } else if (level == 3 && (section == "colors" || section == "shapes" || section == "words")) {
                        navController.navigate(
                            when (section) {
                                "colors" -> AppRoutes.ENGLISH_LEVEL_THREE_COLORS
                                "shapes" -> AppRoutes.ENGLISH_LEVEL_THREE_SHAPES
                                else -> AppRoutes.ENGLISH_LEVEL_THREE_WORDS
                            }
                        )
                    }
                }
            )
        }
        composable(AppRoutes.ENGLISH_LEVEL_ONE_LETTERS) { EnglishLevelOnePage(onBack = { navController.popBackStack() }, initialSection = 0) }
        composable(AppRoutes.ENGLISH_LEVEL_ONE_NUMBERS) { EnglishLevelOnePage(onBack = { navController.popBackStack() }, initialSection = 1) }
        composable(AppRoutes.ENGLISH_LEVEL_TWO_LETTERS) { EnglishLevelTwoPage(onBack = { navController.popBackStack() }, initialSection = 0) }
        composable(AppRoutes.ENGLISH_LEVEL_TWO_NUMBERS) { EnglishLevelTwoPage(onBack = { navController.popBackStack() }, initialSection = 1) }
        composable(AppRoutes.ENGLISH_LEVEL_THREE_COLORS) { EnglishLevelThreePage(onBack = { navController.popBackStack() }, initialSection = 0) }
        composable(AppRoutes.ENGLISH_LEVEL_THREE_SHAPES) { EnglishLevelThreePage(onBack = { navController.popBackStack() }, initialSection = 1) }
        composable(AppRoutes.ENGLISH_LEVEL_THREE_WORDS) { EnglishLevelThreePage(onBack = { navController.popBackStack() }, initialSection = 2) }
        composable(AppRoutes.NUMBERS) { ContentPage("الأرقام والعدّ", LearningCatalog.digits.joinToString("  ")) }
        composable(AppRoutes.PLAY) { BreakGamePage(onBack = { navController.popBackStack() }) }
    }
}

@Composable
private fun HomePage(
    onArabic: () -> Unit,
    onEnglish: () -> Unit,
    onPlay: () -> Unit,
    onSettings: () -> Unit,
    onShop: () -> Unit,
    onCollection: () -> Unit,
    greetOnEntry: Boolean
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var refreshKey by remember { mutableIntStateOf(0) }
    var darkMode by remember { mutableStateOf(AppSettings.isDarkMode(context)) }
    var showChildProfile by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                darkMode = AppSettings.isDarkMode(context)
                refreshKey++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val background = if (darkMode) {
        Brush.verticalGradient(listOf(Color(0xFF172033), Color(0xFF253552)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFFF7FBFF), Color(0xFFE8F3FF)))
    }
    val textColor = if (darkMode) Color.White else Color(0xFF24324A)
    val cardColor = if (darkMode) Color(0xFF2E3E5C) else Color.White.copy(alpha = 0.95f)

    if (showChildProfile) {
        ChildProfileDialog(onDismiss = { showChildProfile = false }, onSaved = { showChildProfile = false; refreshKey++ })
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(modifier = Modifier.fillMaxSize().background(background)) {
                RealCharacterHero(
                    modifier = Modifier.fillMaxSize().shiftDownByFraction(CHARACTER_BOX_SHIFT_DOWN),
                    greetOnEntry = greetOnEntry
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TopAction("⚙️", "الإعدادات", textColor, onSettings)
                        TopAction(
                            icon = if (darkMode) "☀️" else "🌙",
                            label = if (darkMode) "نهاري" else "ليلي",
                            textColor = textColor
                        ) { darkMode = !darkMode; AppSettings.setDarkMode(context, darkMode) }
                    }

                    Spacer(Modifier.height(4.dp))
                    key(refreshKey) { ChildProfileCard(cardColor, textColor, onCollection) { showChildProfile = true } }
                    Spacer(Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LearningCard(
                            modifier = Modifier.weight(1f),
                            icon = "أ",
                            title = "العربية",
                            subtitle = "تعلّم الحروف والأرقام والكلمات",
                            cardColor = cardColor,
                            textColor = textColor,
                            onClick = onArabic
                        )
                        LearningCard(
                            modifier = Modifier.weight(1f),
                            icon = "a",
                            title = "English",
                            subtitle = "تعلّم الحروف والأرقام",
                            cardColor = cardColor,
                            textColor = textColor,
                            onClick = onEnglish
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BottomCard(
                            modifier = Modifier.weight(1f),
                            icon = "🎮",
                            title = "استراحة",
                            subtitle = "",
                            cardColor = cardColor,
                            textColor = textColor,
                            onClick = onPlay
                        )
                        BottomCard(
                            modifier = Modifier.weight(1f),
                            icon = "🛍️",
                            title = "المتجر",
                            subtitle = "استخدم نجومك",
                            cardColor = cardColor,
                            textColor = textColor,
                            onClick = onShop
                        )
                    }

                    Spacer(Modifier.height(4.dp))
                }

            }
        }
    }
}

@Composable
private fun TopAction(icon: String, label: String, textColor: Color, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable(onClick = onClick).padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 26.sp)
        Text(label, fontSize = 11.sp, color = textColor)
    }
}

@Composable
private fun ChildProfileCard(
    cardColor: Color,
    textColor: Color,
    onCollection: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val context = LocalContext.current
    val shape = RoundedCornerShape(22.dp)
    Card(
        onClick = onOpenProfile,
        modifier = Modifier.fillMaxWidth().shadow(6.dp, shape),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = cardColor, contentColor = textColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageUri = AppSettings.childImageUri(context)
            if (imageUri != null) {
                AsyncImage(model = imageUri, contentDescription = "صورة الطفل", modifier = Modifier.size(58.dp))
            } else {
                Text("👦", fontSize = 38.sp)
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(AppSettings.childName(context), fontWeight = FontWeight.Bold, color = textColor)
                Text("اضغط لفتح بطاقة الطفل", fontSize = 12.sp, color = textColor.copy(alpha = .72f))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⭐ " + AppSettings.childStars(context), fontWeight = FontWeight.ExtraBold, color = textColor)
                Text("نجومي", fontSize = 11.sp, color = textColor)
                TextButton(onClick = onCollection) {
                    Text("مقتنياتي", fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun ChildProfileDialog(
    onDismiss: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(AppSettings.childName(context)) }
    var imageUri by remember { mutableStateOf(AppSettings.childImageUri(context)) }
    var error by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}
            imageUri = uri.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("👤 بطاقة الطفل", fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    if (imageUri != null) {
                        AsyncImage(model = imageUri, contentDescription = "صورة الطفل", modifier = Modifier.size(110.dp))
                    } else {
                        Text("👦", fontSize = 72.sp)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { launcher.launch(arrayOf("image/*")) }, modifier = Modifier.weight(1f)) {
                        Text("تغيير الصورة")
                    }
                    OutlinedButton(onClick = { imageUri = null }, modifier = Modifier.weight(1f)) {
                        Text("إزالة")
                    }
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; error = "" },
                    label = { Text("اسم الطفل") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("⭐ النجوم: " + AppSettings.childStars(context), fontWeight = FontWeight.Bold)
                Text("🛍️ المقتنيات: " + AppSettings.ownedItems(context).size, fontSize = 13.sp)
                if (error.isNotBlank()) Text(error, color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
            }
        },
        confirmButton = {
            Button(onClick = {
                val cleanName = name.trim()
                if (cleanName.isBlank()) {
                    error = "اكتب اسم الطفل أولاً."
                } else {
                    AppSettings.setChildName(context, cleanName)
                    AppSettings.setChildImageUri(context, imageUri)
                    onSaved()
                }
            }) { Text("حفظ") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}

@Composable
private fun LearningCard(
    modifier: Modifier,
    icon: String,
    title: String,
    subtitle: String,
    cardColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    Card(
        onClick = onClick,
        modifier = modifier.height(108.dp).shadow(6.dp, shape),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = cardColor, contentColor = textColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(icon, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
            Text(title, fontWeight = FontWeight.ExtraBold, color = textColor)
            Text(subtitle, fontSize = 11.sp, textAlign = TextAlign.Center, color = textColor)
        }
    }
}

@Composable
private fun BottomCard(
    modifier: Modifier,
    icon: String,
    title: String,
    subtitle: String,
    cardColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(22.dp)
    Card(
        onClick = onClick,
        modifier = modifier.height(80.dp).shadow(6.dp, shape),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = cardColor, contentColor = textColor)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 30.sp)
            Spacer(Modifier.width(8.dp))
            Column {
                Text(title, fontWeight = FontWeight.ExtraBold, color = textColor)
                if (subtitle.isNotBlank()) {
                    Text(subtitle, fontSize = 11.sp, color = textColor)
                }
            }
        }
    }
}

@Composable
private fun InfoDialog(title: String, text: String, onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = { TextButton(onClick = onClose) { Text("إغلاق") } }
    )
}

@Composable
private fun RealCharacterHero(
    modifier: Modifier = Modifier,
    greetOnEntry: Boolean
) {
    val context = LocalContext.current
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    // SceneView 2.3.1 does not expose rememberModelInstance. Keep model creation on the
    // Compose/main thread and reuse the instance across recompositions.
    val model = remember { runCatching { modelLoader.createModelInstance("Mannequin_Medium_Anim.glb") }.getOrNull() }
    val cameraNode = rememberCameraNode(engine) {
        position = Position(x = 0f, y = 0f, z = 5.5f)
    }
    val characterNode = remember(model) {
        model?.let { instance ->
            ModelNode(
                modelInstance = instance,
                autoAnimate = false,
                scaleToUnits = 2.2f,
                centerOrigin = Position(x = 0f, y = 0f, z = 0f)
            ).also { it.position = Position(x = 0f, y = 0f, z = 0f) }
        }
    }

    var taps by remember { mutableStateOf(0) }
    var greetingActive by remember(greetOnEntry) { mutableStateOf(greetOnEntry) }
    // Greeting is intentionally a single animation: Waving. It remains looping for the
    // complete TTS utterance and is never replaced by another clip.
    LaunchedEffect(characterNode, taps, greetingActive) {
        val node = characterNode ?: return@LaunchedEffect

        // Stop only the clips we actually use. Repeatedly stopping/starting every
        // animation on every speech tick was unnecessary and could destabilize startup.
        runCatching { node.stopAnimation(CLIP_IDLE) }
        runCatching { node.stopAnimation(CLIP_WAVE) }
        REACTION_CLIPS.forEach { index -> runCatching { node.stopAnimation(index) } }

        if (greetingActive) {
            runCatching { node.playAnimation(CLIP_WAVE, 1f, true) }
        } else if (taps == 0) {
            runCatching { node.playAnimation(CLIP_IDLE, 1f, true) }
        } else {
            val clip = REACTION_CLIPS[(taps - 1) % REACTION_CLIPS.size]
            runCatching { node.playAnimation(clip, 1f, false) }
            delay(2200)
            runCatching { node.stopAnimation(clip) }
            runCatching { node.playAnimation(CLIP_IDLE, 1f, true) }
        }
    }

    DisposableEffect(context, greetOnEntry) {
        val mainHandler = Handler(Looper.getMainLooper())
        var tts: TextToSpeech? = null
        var released = false

        fun finishGreeting() {
            if (!released) {
                mainHandler.post {
                    if (!released) greetingActive = false
                }
            }
        }

        val initListener = TextToSpeech.OnInitListener { status ->
            if (released || !greetOnEntry) return@OnInitListener
            val speaker = tts ?: return@OnInitListener

            if (status != TextToSpeech.SUCCESS) {
                finishGreeting()
                return@OnInitListener
            }

            // Use Modern Standard Arabic when the engine exposes it, then fall back to
            // Saudi Arabic. The voice selector explicitly prefers male voice identifiers.
            val arabicLocale = Locale.forLanguageTag("ar-XA")
            val localeResult = speaker.setLanguage(arabicLocale)
            if (localeResult == TextToSpeech.LANG_NOT_SUPPORTED ||
                localeResult == TextToSpeech.LANG_MISSING_DATA
            ) {
                speaker.language = Locale("ar", "SA")
            }
            if (!AppSettings.isSpeechEnabled(context)) {
                finishGreeting()
                return@OnInitListener
            }
            speaker.setSpeechRate(AppSettings.speechRate(context))
            speaker.setPitch(0.96f)

            selectArabicMaleVoice(speaker)?.let { speaker.voice = it }

            speaker.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    if (utteranceId == GREETING_UTTERANCE_ID) {
                        mainHandler.post { if (!released) greetingActive = true }
                    }
                }

                override fun onDone(utteranceId: String?) {
                    if (utteranceId == GREETING_UTTERANCE_ID) finishGreeting()
                }

                @Deprecated("Deprecated by Android; kept for API compatibility.")
                override fun onError(utteranceId: String?) {
                    if (utteranceId == GREETING_UTTERANCE_ID) finishGreeting()
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    if (utteranceId == GREETING_UTTERANCE_ID) finishGreeting()
                }
            })

            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, GREETING_UTTERANCE_ID)
            }
            val childName = AppSettings.childName(context).trim()
            val greetingText = if (childName.isBlank() || childName == "صديقي الصغير") {
                GREETING_TEXT
            } else {
                "مرحبا $childName. اختر ماذا نتعلم اليوم."
            }
            val result = speaker.speak(greetingText, TextToSpeech.QUEUE_FLUSH, params, GREETING_UTTERANCE_ID)
            if (result == TextToSpeech.ERROR) finishGreeting()
        }

        if (!greetOnEntry) {
            return@DisposableEffect onDispose { }
        }

        // Prefer Google's Android TTS engine when it is installed, otherwise use
        // whatever Arabic TTS engine is available on the device.
        val googleEngine = speakerEnginePackage(context)
        tts = if (googleEngine != null) {
            TextToSpeech(context, initListener, googleEngine)
        } else {
            TextToSpeech(context, initListener)
        }

        onDispose {
            released = true
            mainHandler.removeCallbacksAndMessages(null)
            tts?.stop()
            tts?.shutdown()
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Scene(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            cameraNode = cameraNode,
            cameraManipulator = null,
            isOpaque = false,
            childNodes = listOfNotNull(characterNode)
        )
        Box(
            Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (!greetingActive) taps += 1
                }
        )
    }
}

private fun speakerEnginePackage(context: Context): String? {
    return runCatching {
        context.packageManager
            .queryIntentServices(android.content.Intent(TextToSpeech.Engine.INTENT_ACTION_TTS_SERVICE), 0)
            .firstOrNull { it.serviceInfo.packageName == "com.google.android.tts" }
            ?.serviceInfo
            ?.packageName
    }.getOrNull()
}

private fun selectArabicMaleVoice(tts: TextToSpeech): android.speech.tts.Voice? {
    val voices = tts.voices.orEmpty()
        .filter { it.locale.language == "ar" }
        .filterNot { it.isNetworkConnectionRequired }

    // Android does not expose a reliable gender property for every installed voice,
    // so only choose identifiers that conventionally denote known male variants.
    val maleMarkers = listOf(
        "male", "man", "maged", "majed", "tarik",
        "standard-b", "standard-c", "wavenet-b", "wavenet-c",
        "chirp3-hd-achird", "chirp3-hd-algenib", "chirp3-hd-algieba",
        "chirp3-hd-alnilam", "chirp3-hd-charon", "chirp3-hd-enceladus",
        "chirp3-hd-fenrir", "chirp3-hd-iapetus", "chirp3-hd-orus",
        "chirp3-hd-puck", "chirp3-hd-rasalgethi", "chirp3-hd-sadachbia",
        "chirp3-hd-sadaltager", "chirp3-hd-schedar", "chirp3-hd-umbriel",
        "chirp3-hd-zubenelgenubi"
    )

    return voices.firstOrNull { voice ->
        val name = voice.name.lowercase(Locale.ROOT)
        maleMarkers.any(name::contains)
    } ?: voices.firstOrNull { it.locale == Locale.forLanguageTag("ar-XA") }
}

@Composable
private fun ContentPage(title: String, content: String) {
    Scaffold { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.headlineMedium)
            Text(content, textAlign = TextAlign.Center)
            Text("المحتوى التعليمي الأولي — ستتم إضافة الصوت والتفاعل في المرحلة التالية.")
        }
    }
}
