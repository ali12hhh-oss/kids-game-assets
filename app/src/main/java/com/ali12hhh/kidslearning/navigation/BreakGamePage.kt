package com.ali12hhh.kidslearning.navigation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

private data class BreakItem(val id: Int, val lane: Int, val type: Int, var progress: Float)
private const val GAME_SECONDS = 45
private const val STAR = 0
private const val BARRIER = 1

@Composable
fun BreakGamePage(onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var running by remember { mutableStateOf(true) }
    var finished by remember { mutableStateOf(false) }
    var playerLane by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var collected by remember { mutableIntStateOf(0) }
    var misses by remember { mutableIntStateOf(0) }
    var remaining by remember { mutableIntStateOf(GAME_SECONDS) }
    var jumping by remember { mutableStateOf(false) }
    var fastMode by remember { mutableStateOf(false) }
    var tick by remember { mutableIntStateOf(0) }
    var spawnCounter by remember { mutableIntStateOf(0) }
    val items = remember { mutableStateListOf<BreakItem>() }

    fun resetGame() {
        items.clear()
        playerLane = 1
        score = 0
        collected = 0
        misses = 0
        remaining = GAME_SECONDS
        jumping = false
        fastMode = false
        spawnCounter = 0
        tick++
        finished = false
        running = true
    }

    LaunchedEffect(running, finished, tick) {
        if (!running || finished) return@LaunchedEffect
        var elapsedMs = 0L
        var spawnMs = 0L
        while (running && !finished && remaining > 0) {
            delay(50)
            elapsedMs += 50
            spawnMs += 50
            val speed = if (fastMode) 0.00155f else 0.00105f
            items.forEach { it.progress += speed * 50f }

            val removeIds = mutableListOf<Int>()
            items.forEach { item ->
                if (item.progress >= 0.83f) {
                    if (item.lane == playerLane) {
                        if (item.type == STAR) {
                            collected += 1
                            score += 10
                        } else if (!jumping) {
                            misses += 1
                            score = (score - 4).coerceAtLeast(0)
                        } else {
                            score += 6
                        }
                    }
                    removeIds += item.id
                }
            }
            items.removeAll { it.id in removeIds }

            if (spawnMs >= if (fastMode) 650L else 850L) {
                spawnMs = 0L
                spawnCounter += 1
                val lane = Random.nextInt(0, 3)
                val type = if (spawnCounter % 4 == 0) BARRIER else STAR
                items += BreakItem(spawnCounter, lane, type, -0.08f)
            }

            if (elapsedMs >= 1000L) {
                elapsedMs -= 1000L
                remaining -= 1
            }
        }

        if (remaining <= 0) {
            running = false
            finished = true
            val reward = (collected / 2).coerceIn(1, 12)
            if (reward > 0) AppSettings.addStars(context, reward)
        }
    }

    LaunchedEffect(jumping, tick) {
        if (jumping) {
            delay(620)
            jumping = false
        }
    }

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val model = remember { runCatching { modelLoader.createModelInstance("Mannequin_Medium_Anim.glb") }.getOrNull() }
    val cameraNode = rememberCameraNode(engine) {
        position = Position(x = 0f, y = 0.35f, z = 5.8f)
    }
    val playerX = (playerLane - 1) * 0.78f
    val playerY = if (jumping) 0.65f else 0f
    val characterNode = remember(model) {
        model?.let { instance ->
            ModelNode(
                modelInstance = instance,
                autoAnimate = false,
                scaleToUnits = 2.15f,
                centerOrigin = Position(x = 0f, y = -1f, z = 0f)
            )
        }
    }

    LaunchedEffect(characterNode, running, jumping, fastMode, playerLane) {
        val node = characterNode ?: return@LaunchedEffect
        runCatching { node.stopAnimation(0) }
        runCatching { node.stopAnimation(6) }
        runCatching { node.stopAnimation(9) }
        runCatching { node.stopAnimation(10) }
        if (jumping) {
            runCatching { node.playAnimation(9, 1f, false) }
        } else if (running) {
            runCatching { node.playAnimation(10, if (fastMode) 1.35f else 1.05f, true) }
        } else {
            runCatching { node.playAnimation(0, 1f, true) }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF10233A), Color(0xFF1E4C5F), Color(0xFF8AC6A8))))
        ) {
            Scene(
                modifier = Modifier.fillMaxSize(),
                engine = engine,
                modelLoader = modelLoader,
                cameraNode = cameraNode,
                cameraManipulator = null,
                isOpaque = false,
                childNodes = listOfNotNull(characterNode?.also {
                    it.position = Position(x = playerX, y = playerY, z = 0f)
                })
            )

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 105.dp, bottom = 190.dp)
            ) {
                val laneWidth = size.width / 3f
                val top = size.height * 0.02f
                val bottom = size.height * 0.98f
                for (i in 1..2) {
                    val x = laneWidth * i
                    drawLine(Color.White.copy(alpha = 0.20f), Offset(x, top), Offset(x, bottom), strokeWidth = 3f)
                }
                drawLine(
                    Color.White.copy(alpha = 0.18f),
                    Offset(0f, size.height * 0.83f),
                    Offset(size.width, size.height * 0.83f),
                    strokeWidth = 5f
                )
                items.forEach { item ->
                    val x = laneWidth * (item.lane + 0.5f)
                    val y = top + (bottom - top) * item.progress.coerceIn(0f, 1f)
                    if (item.type == STAR) {
                        drawCircle(Color(0xFFFFD54F), radius = 24f, center = Offset(x, y))
                        drawCircle(Color.White.copy(alpha = 0.55f), radius = 9f, center = Offset(x - 7f, y - 8f))
                    } else {
                        val half = 25f
                        drawRoundRect(
                            Color(0xFFE85D5D),
                            topLeft = Offset(x - half, y - half),
                            size = androidx.compose.ui.geometry.Size(half * 2f, half * 2f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                        )
                        drawLine(Color.White.copy(alpha = 0.8f), Offset(x - 12f, y - 12f), Offset(x + 12f, y + 12f), strokeWidth = 5f)
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (finished) onBack() else running = !running },
                        modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.30f))
                    ) {
                        Text(if (finished) "‹" else if (running) "Ⅱ" else "▶", color = Color.White, fontSize = 22.sp)
                    }
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.28f))
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("⭐ $score", color = Color.White, fontWeight = FontWeight.Black)
                            Text("⏱ $remaining", color = Color.White, fontWeight = FontWeight.Black)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("مغامرة دبدوب", modifier = Modifier.fillMaxWidth(), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                Text("اجمع النجوم وتجاوز الحواجز", modifier = Modifier.fillMaxWidth(), color = Color.White.copy(alpha = 0.88f), fontSize = 13.sp, textAlign = TextAlign.Center)
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                LaneJoystick(
                    lane = playerLane,
                    onLaneChange = { delta ->
                        if (running && !finished) playerLane = (playerLane + delta).coerceIn(0, 2)
                    }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ControlButton(
                        label = if (fastMode) "⚡" else "🏃",
                        caption = if (fastMode) "سريع" else "جري",
                        active = fastMode,
                        onClick = { if (running && !finished) fastMode = !fastMode }
                    )
                    ControlButton(
                        label = "↥",
                        caption = "قفز",
                        active = jumping,
                        onClick = { if (running && !finished && !jumping) jumping = true }
                    )
                }
            }

            if (finished) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.50f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(0.86f).padding(12.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("أحسنت! 🎉", fontSize = 30.sp, fontWeight = FontWeight.Black)
                            Text("جمعت $collected نجمة", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("النقاط: $score", fontSize = 16.sp)
                            Text("أخطاء: $misses", fontSize = 14.sp, color = Color.Gray)
                            Text("حصلت على ⭐ " + (collected / 2).coerceIn(1, 12) + " من نجوم التطبيق", textAlign = TextAlign.Center)
                            Spacer(Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(onClick = { resetGame() }) { Text("العب مرة أخرى") }
                                OutlinedButton(onClick = onBack) { Text("خروج") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LaneJoystick(lane: Int, onLaneChange: (Int) -> Unit) {
    var dragStartX by remember { mutableStateOf<Float?>(null) }
    Box(
        modifier = Modifier
            .size(116.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.30f))
            .pointerInput(lane) {
                detectDragGestures(
                    onDragStart = { offset -> dragStartX = offset.x },
                    onDrag = { change, _ ->
                        change.consume()
                        val start = dragStartX ?: change.position.x
                        val delta = change.position.x - start
                        if (abs(delta) > 24f) {
                            onLaneChange(if (delta > 0) 1 else -1)
                            dragStartX = change.position.x
                        }
                    },
                    onDragEnd = { dragStartX = null },
                    onDragCancel = { dragStartX = null }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("↔", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Text("حرّك", color = Color.White, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ControlButton(label: String, caption: String, active: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (active) Color(0xFFFFD54F) else Color.Black.copy(alpha = 0.34f))
        ) {
            Text(label, fontSize = 27.sp, color = if (active) Color(0xFF24324A) else Color.White)
        }
        Text(caption, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
