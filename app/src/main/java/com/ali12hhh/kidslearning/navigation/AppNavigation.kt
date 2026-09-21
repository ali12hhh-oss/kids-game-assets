package com.ali12hhh.kidslearning.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalLayoutDirection
import io.github.sceneview.Scene
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.node.ModelNode
import io.github.sceneview.math.Position
import kotlinx.coroutines.delay
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ali12hhh.kidslearning.core.LearningCatalog

private const val CLIP_IDLE = 0
private val REACTION_CLIPS = (1 until 11).toList()
private const val ANIMATION_COUNT = 11

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppRoutes.HOME) {
        composable(AppRoutes.HOME) { HomePage() }
        composable(AppRoutes.ARABIC_LETTERS) { ContentPage("الحروف العربية", LearningCatalog.arabicLetters.joinToString("  ")) }
        composable(AppRoutes.ENGLISH_LETTERS) { ContentPage("English Letters", LearningCatalog.englishLetters.joinToString("  ")) }
        composable(AppRoutes.NUMBERS) { ContentPage("الأرقام والعدّ", LearningCatalog.digits.joinToString("  ")) }
        composable(AppRoutes.PLAY) { ContentPage("وقت اللعب", "منطقة الألعاب قيد التجهيز") }
    }
}

@Composable
private fun HomePage() {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(
                modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFF7FBFF), Color(0xFFE8F3FF)))),
                contentAlignment = Alignment.Center
            ) {
                RealCharacterHero(Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun RealCharacterHero(modifier: Modifier = Modifier) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val model = remember(modelLoader) {
        runCatching { modelLoader.createModelInstance("Mannequin_Medium_Anim.glb") }.getOrNull()
    }
    val cameraNode = rememberCameraNode(engine) { position = Position(x = 0f, y = 0f, z = 3.2f) }
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
