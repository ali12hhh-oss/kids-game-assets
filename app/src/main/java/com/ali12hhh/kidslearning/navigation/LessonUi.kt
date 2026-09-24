package com.ali12hhh.kidslearning.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import kotlinx.coroutines.delay

@Composable
fun ProLessonButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(18.dp),
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: androidx.compose.material3.ButtonElevation? = ButtonDefaults.buttonElevation(defaultElevation = 7.dp),
    content: @Composable () -> Unit
) {
    val base = if (enabled) Color(0xFF1976D2) else Color(0xFF90A4AE)
    val contentColor = if (enabled) Color.White else Color(0xFFE0E0E0)
    val top = base
    val bottom = base.copy(alpha = .78f)

    Surface(
        modifier = modifier
            .shadow(7.dp, shape)
            .clip(shape)
            .clickable(enabled = enabled, onClick = onClick),
        shape = shape,
        color = Color.Transparent,
        contentColor = contentColor
    ) {
        Box(
            Modifier
                .background(Brush.verticalGradient(listOf(top, bottom)))
                .padding(horizontal = 14.dp, vertical = 11.dp)
        ) {
            Row { content() }
        }
    }
}

@Composable
fun LessonCharacter3D(
    modifier: Modifier = Modifier,
    dancing: Boolean = true,
    verticalOffset: Float = -0.05f
) {
    val engine = rememberEngine()
    val loader = rememberModelLoader(engine)
    val model = remember {
        runCatching { loader.createModelInstance("Mannequin_Medium_Anim.glb") }.getOrNull()
    }
    val camera = rememberCameraNode(engine) {
        position = Position(x = 0f, y = 0f, z = 3.7f)
    }
    val node = remember(model) {
        model?.let {
            ModelNode(
                modelInstance = it,
                autoAnimate = false,
                scaleToUnits = 2.2f
            ).also { n -> n.position = Position(x = 0f, y = verticalOffset, z = 0f) }
        }
    }

    LaunchedEffect(node, dancing) {
        val n = node ?: return@LaunchedEffect
        if (dancing) {
            runCatching { n.playAnimation(7, 1f, true) }
        } else {
            runCatching { n.playAnimation(0, 1f, true) }
        }
        delay(Long.MAX_VALUE)
    }

    Scene(
        modifier = modifier,
        engine = engine,
        modelLoader = loader,
        cameraNode = camera,
        cameraManipulator = null,
        isOpaque = false,
        childNodes = listOfNotNull(node)
    )
}
