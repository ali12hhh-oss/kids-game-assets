package com.ali12hhh.kidslearning.navigation

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

private const val CLIP_IDLE = 0
private val REACTION_CLIPS = (1 until 11).toList()
private const val ANIMATION_COUNT = 11

// The 3D model's visual center sits above the center of its Scene view. Instead of moving
// the character inside the Scene (which can clip it), the whole Scene box (with its tap
// layer) is shifted down by this fraction of its own height. Tune this one value:
// larger = character lower on the page, smaller = higher.
private const val CHARACTER_BOX_SHIFT_DOWN = 0.19f

/** Moves the composable down by [fraction] of its own height, without changing its size. */
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
                onArabic = { navController.navigate(AppRoutes.ARABIC_LETTERS) },
                onEnglish = { navController.navigate(AppRoutes.ENGLISH_LETTERS) },
                onPlay = { navController.navigate(AppRoutes.PLAY) }
            )
        }
        composable(AppRoutes.ARABIC_LETTERS) { ContentPage("الحروف العربية", LearningCatalog.arabicLetters.joinToString("  ")) }
        composable(AppRoutes.ENGLISH_LETTERS) { ContentPage("English Letters", LearningCatalog.englishLetters.joinToString("  ")) }
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
                // Layer 1: the character fills the whole page, exactly as before, so its
                // position and size are unchanged. Taps on it trigger the animations.
                RealCharacterHero(Modifier.fillMaxSize().shiftDownByFraction(CHARACTER_BOX_SHIFT_DOWN))

                // Layer 2: the page sections drawn over the character layer.
                // Right-to-left layout: the first item of every Row is on the RIGHT.
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Top bar: settings on the right, day/night mode on the left.
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

                    // Child identity card.
                    ChildProfileCard(cardColor, textColor)

                    // The character lives in this free space in the middle.
                    Spacer(Modifier.weight(1f))

                    // Row 1: Arabic (right) and English (left).
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

                    // Row 2: Break (right) and Shop (left).
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
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val model = remember(modelLoader) {
        runCatching { modelLoader.createModelInstance("Mannequin_Medium_Anim.glb") }.getOrNull()
    }
    // Keep the model at its existing large scale; move the camera back to fit the entire figure.
    val cameraNode = rememberCameraNode(engine) { position = Position(x = 0f, y = 0f, z = 5.5f) }
    val characterNode = remember(model) {
        model?.let { instance -> ModelNode(modelInstance = instance, autoAnimate = false, scaleToUnits = 2.2f, centerOrigin = Position(x = 0f, y = 0f, z = 0f)).also { it.position = Position(x = 0f, y = 0f, z = 0f) } }
    }
    var taps by remember { mutableStateOf(0) }
    LaunchedEffect(characterNode, taps) {
        val node = characterNode ?: return@LaunchedEffect
        for (index in 0 until ANIMATION_COUNT) runCatching { node.stopAnimation(index) }
        if (taps == 0) runCatching { node.playAnimation(CLIP_IDLE) }
        else {
            val clip = REACTION_CLIPS[(taps - 1) % REACTION_CLIPS.size]
            runCatching { node.playAnimation(clip, 1f, false) }
            delay(3000)
            for (index in 0 until ANIMATION_COUNT) runCatching { node.stopAnimation(index) }
            runCatching { node.playAnimation(CLIP_IDLE) }
        }
    }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Scene(modifier = Modifier.fillMaxSize(), engine = engine, modelLoader = modelLoader, cameraNode = cameraNode, cameraManipulator = null, isOpaque = false, childNodes = listOfNotNull(characterNode))
        Box(Modifier.fillMaxSize().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { taps += 1 })
    }
}

@Composable
private fun ContentPage(title: String, content: String) {
    Scaffold { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.headlineMedium)
            Text(content, textAlign = TextAlign.Center)
            Text("المحتوى التعليمي الأولي — ستتم إضافة الصوت والتفاعل في المرحلة التالية.")
        }
    }
}
