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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
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
    NavHost(navController = navController, startDestination = AppRoutes.HOME) {
        composable(AppRoutes.HOME) {
            HomePage(
                onArabic = { navController.navigate(AppRoutes.ARABIC_LEVELS) },
                onEnglish = { navController.navigate(AppRoutes.ENGLISH_LEVELS) },
                onPlay = { navController.navigate(AppRoutes.PLAY) }
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
                onSelectSection = { /* Section content will be connected later. */ }
            )
        }
        composable(AppRoutes.ENGLISH_LEVEL_PAGE) { entry ->
            val level = entry.arguments?.getString("level")?.toIntOrNull() ?: 1
            LevelSectionsPage(
                language = "en",
                level = level,
                onBack = { navController.popBackStack() },
                onSelectSection = { /* Section content will be connected later. */ }
            )
        }
        composable(AppRoutes.NUMBERS) { ContentPage("الأرقام والعدّ", LearningCatalog.digits.joinToString("  ")) }
        composable(AppRoutes.PLAY) { ContentPage("وقت اللعب", "منطقة الألعاب قيد التجهيز") }
    }
}

@Composable
private fun HomePage(
    onArabic: () -> Unit,
    onEnglish: () -> Unit,
    onPlay: () -> Unit
) {
    var darkMode by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showShop by remember { mutableStateOf(false) }

    val background = if (darkMode) {
        Brush.verticalGradient(listOf(Color(0xFF172033), Color(0xFF253552)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFFF7FBFF), Color(0xFFE8F3FF)))
    }
    val textColor = if (darkMode) Color.White else Color(0xFF24324A)
    val cardColor = if (darkMode) Color(0xFF2E3E5C) else Color.White.copy(alpha = 0.95f)

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(modifier = Modifier.fillMaxSize().background(background)) {
                RealCharacterHero(Modifier.fillMaxSize().shiftDownByFraction(CHARACTER_BOX_SHIFT_DOWN))

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
                        TopAction("⚙️", "الإعدادات", textColor) { showSettings = true }
                        TopAction(
                            icon = if (darkMode) "☀️" else "🌙",
                            label = if (darkMode) "نهاري" else "ليلي",
                            textColor = textColor
                        ) { darkMode = !darkMode }
                    }

                    Spacer(Modifier.height(4.dp))
                    ChildProfileCard(cardColor, textColor)
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
                            onClick = { showShop = true }
                        )
                    }

                    Spacer(Modifier.height(4.dp))
                }

                if (showSettings) {
                    InfoDialog("الإعدادات", "ستتوسع الإعدادات هنا لاحقًا.") { showSettings = false }
                }
                if (showShop) {
                    InfoDialog("المتجر", "المتجر قيد التجهيز، وسيتمكن الطفل من استخدام نجومه لشراء المقتنيات.") { showShop = false }
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
private fun ChildProfileCard(cardColor: Color, textColor: Color) {
    val shape = RoundedCornerShape(22.dp)
    Card(
        modifier = Modifier.fillMaxWidth().shadow(6.dp, shape),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = cardColor, contentColor = textColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("👦", fontSize = 38.sp)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("صديقي الصغير", fontWeight = FontWeight.Bold, color = textColor)
                Text("ملف الطفل", fontSize = 12.sp, color = textColor)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⭐ 0", fontWeight = FontWeight.ExtraBold, color = textColor)
                Text("نجومي", fontSize = 11.sp, color = textColor)
            }
        }
    }
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
private fun RealCharacterHero(modifier: Modifier = Modifier) {
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
    var greetingActive by remember { mutableStateOf(false) }
    // Greeting is intentionally a single animation: Waving. It remains looping for the
    // complete TTS utterance and is never replaced by another clip.
    LaunchedEffect(characterNode, taps, greetingActive) {
        val node = characterNode ?: return@LaunchedEffect

        // Stop only the clips we actually use. Repeatedly stopping/starting every
        // animation on every speech tick was unnecessary and could destabilize startup.
        runCatching { node.stopAnimation(CLIP_IDLE) }
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

    DisposableEffect(context) {
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
            if (released) return@OnInitListener
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
            speaker.setSpeechRate(0.88f)
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
            val result = speaker.speak(GREETING_TEXT, TextToSpeech.QUEUE_FLUSH, params, GREETING_UTTERANCE_ID)
            if (result == TextToSpeech.ERROR) finishGreeting()
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
