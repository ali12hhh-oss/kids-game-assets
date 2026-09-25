package com.ali12hhh.kidslearning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import kotlinx.coroutines.delay
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader

private const val SPLASH_DURATION_MS = 4_500L
private const val SPLASH_JUMP_CLIP = 9
private const val SPLASH_DANCE_CLIP = 7

@Composable
fun ReboSplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(SPLASH_DURATION_MS)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF29B6F6),
                        Color(0xFF1565C0),
                        Color(0xFF0D2B52)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        SplashCharacter(
            modifier = Modifier
                .align(Alignment.Center)
                .size(440.dp)
        )

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "تعلّم مع ريبو",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "نتعلّم • نلعب • نكتشف",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(34.dp))
        }
    }
}

@Composable
private fun SplashCharacter(modifier: Modifier) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val model = remember {
        runCatching {
            modelLoader.createModelInstance("Mannequin_Medium_Anim.glb")
        }.getOrNull()
    }
    val cameraNode = rememberCameraNode(engine) {
        position = Position(x = 0f, y = 0.02f, z = 5.25f)
    }
    val characterNode = remember(model) {
        model?.let { instance ->
            ModelNode(
                modelInstance = instance,
                autoAnimate = false,
                // Reduced model size while keeping the 440dp viewport unchanged.
                scaleToUnits = 3.0f,
                centerOrigin = Position(x = 0f, y = -0.02f, z = 0f)
            ).also {
                it.position = Position(x = 0f, y = -1.14f, z = 0f)
            }
        }
    }

    var animationReady by remember { mutableStateOf(false) }

    LaunchedEffect(characterNode) {
        val node = characterNode ?: return@LaunchedEffect
        // Never render the bind/T-pose. Start with the jump, then switch to cheering
        // as the second movement for the rest of the splash.
        runCatching { node.stopAnimation(SPLASH_JUMP_CLIP) }
        runCatching { node.stopAnimation(SPLASH_DANCE_CLIP) }
        runCatching { node.playAnimation(SPLASH_JUMP_CLIP, 1f, false) }
        delay(900)
        runCatching { node.stopAnimation(SPLASH_JUMP_CLIP) }
        runCatching { node.playAnimation(SPLASH_DANCE_CLIP, 1f, true) }
        animationReady = true
    }

    if (animationReady && characterNode != null) {
        Scene(
            modifier = modifier,
            engine = engine,
            modelLoader = modelLoader,
            cameraNode = cameraNode,
            cameraManipulator = null,
            isOpaque = false,
            childNodes = listOf(characterNode)
        )
    }
}
