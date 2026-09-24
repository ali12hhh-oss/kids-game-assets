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
private const val GOLD_STAR = 2

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
    var combo by remember { mutableIntStateOf(0) }
    var dodged by remember { mutableIntStateOf(0) }
    var perfects by remember { mutableIntStateOf(0) }
    var goldCollected by remember { mutableIntStateOf(0) }
    var countdown by remember { mutableIntStateOf(3) }
    var roundId by remember { mutableIntStateOf(0) }
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
        combo = 0
        dodged = 0
        perfects = 0
        goldCollected = 0
        countdown = 3
        roundId++
        tick++
        finished = false
        running = true
    }

    LaunchedEffect(roundId) {
        for (value in 3 downTo 1) {
            countdown = value
            delay(700)
        }
        countdown = 0
    }

    LaunchedEffect(running, finished, countdown, roundId) {
        if (!running || finished || countdown > 0) return@LaunchedEffect
        var elapsedMs = 0L
        var spawnMs = 0L
        while (running && !finished && remaining > 0) {
            delay(50)
            tick++
            elapsedMs += 50
            spawnMs += 50
            val difficulty = 1f + ((GAME_SECONDS - remaining).toFloat() / GAME_SECONDS) * 0.65f
            val speed = (if (fastMode) 0.00155f else 0.00105f) * difficulty
            items.forEach { it.progress += speed * 50f }

            val removeIds = mutableListOf<Int>()
            items.forEach { item ->
                if (item.progress >= 0.83f) {
                    if (item.lane == playerLane) {
                        if (item.type == STAR || item.type == GOLD_STAR) {
                            collected += 1
                            combo += 1
                            val base = if (item.type == GOLD_STAR) 25 else 10
                            score += base + (combo.coerceAtMost(8) - 1) * 2
                            if (item.type == GOLD_STAR) {
                                perfects += 1
                                goldCollected += 1
                            }
                        } else if (!jumping) {
                            misses += 1
                            combo = 0
                            score = (score - 4).coerceAtLeast(0)
                        } else {
                            dodged += 1
                            combo += 1
                            score += 8 + combo.coerceAtMost(5)
                        }
                    }
                    removeIds += item.id
                }
            }
            items.removeAll { it.id in removeIds }

            if (spawnMs >= (if (fastMode) 650L else 850L) / difficulty) {
                spawnMs = 0L
                spawnCounter += 1
                val lane = Random.nextInt(0, 3)
                val type = when {
                    spawnCounter % 7 == 0 -> GOLD_STAR
                    spawnCounter % 4 == 0 -> BARRIER
                    else -> STAR
                }
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

    LaunchedEffect(jumping) {
        if (jumping) {
            delay(620)
            jumping = false
        }
    }

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val model = remember { runCatching { modelLoader.createModelInstance("Mannequin_Medium.glb") }.getOrNull() }
    val cameraNode = rememberCameraNode(engine) {
        position = Position(x = 0f, y = 0.35f, z = 5.8f)
    }
    val animationFrame = tick
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
                .background(Brush.verticalGradient(listOf(Color(0xFF081525), Color(0xFF163B52), Color(0xFF3C7A72), Color(0xFFB6D68A))))
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
                if (animationFrame < 0) return@Canvas
                val laneWidth = size.width / 3f
                val top = size.height * 0.02f
                val bottom = size.height * 0.98f
                val horizonY = size.height * 0.34f
                val roadTop = size.width * 0.22f
                val roadBottom = size.width * 0.96f
                drawRect(Color(0xFF1B3A46).copy(alpha = 0.42f), Offset(0f, horizonY), androidx.compose.ui.geometry.Size(size.width, size.height - horizonY))
                drawLine(Color.White.copy(alpha = 0.16f), Offset(size.width * 0.08f, horizonY), Offset(size.width * 0.02f, size.height), strokeWidth = 6f)
                drawLine(Color.White.copy(alpha = 0.16f), Offset(size.width * 0.92f, horizonY), Offset(size.width * 0.98f, size.height), strokeWidth = 6f)
                for (i in 1..2) {
                    val x = laneWidth * i
                    drawLine(Color.White.copy(alpha = 0.12f), Offset(x, top), Offset(x, bottom), strokeWidth = 2f)
                }
                drawLine(
                    Color(0xFFFFD54F).copy(alpha = 0.22f),
                    Offset(0f, size.height * 0.83f),
                    Offset(size.width, size.height * 0.83f),
                    strokeWidth = 5f
                )
                items.forEach { item ->
                    val x = laneWidth * (item.lane + 0.5f)
                    val y = top + (bottom - top) * item.progress.coerceIn(0f, 1f)
                    if (item.type == STAR) {
                        drawCircle(Color(0xFFFFD54F), radius = 28f, center = Offset(x, y))
                        drawCircle(Color.White.copy(alpha = 0.55f), radius = 9f, center = Offset(x - 7f, y - 8f))
                    } else if (item.type == GOLD_STAR) {
                        drawCircle(Color(0xFFFFA000), radius = 34f, center = Offset(x, y))
                        drawCircle(Color(0xFFFFE082), radius = 26f, center = Offset(x, y))
                        drawCircle(Color.White.copy(alpha = 0.72f), radius = 9f, center = Offset(x - 8f, y - 9f))
                        drawLine(Color.White.copy(alpha = 0.82f), Offset(x - 16f, y), Offset(x + 16f, y), strokeWidth = 4f)
                        drawLine(Color.White.copy(alpha = 0.82f), Offset(x, y - 16f), Offset(x, y + 16f), strokeWidth = 4f)
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
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.38f))
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("⭐ $score", color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
                            Text("⏱ $remaining", color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("مغامرة ريبو", modifier = Modifier.fillMaxWidth(), color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                Text("انطلق، اجمع النجوم وتجاوز الحواجز", modifier = Modifier.fillMaxWidth(), color = Color.White.copy(alpha = 0.88f), fontSize = 13.sp, textAlign = TextAlign.Center)
                if (combo > 1) Text("🔥 سلسلة $combo", modifier = Modifier.fillMaxWidth(), color = Color(0xFFFFD54F), fontSize = 13.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                Spacer(Modifier.height(5.dp))
                Box(Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.18f))) {
                    Box(Modifier.fillMaxWidth((remaining.toFloat() / GAME_SECONDS).coerceIn(0f, 1f)).fillMaxSize().clip(RoundedCornerShape(8.dp)).background(Color(0xFFFFD54F)))
                }
            }

            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.30f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LaneJoystick(
                        lane = playerLane,
                        onLaneChange = { delta ->
                            if (running && !finished && countdown == 0) playerLane = (playerLane + delta).coerceIn(0, 2)
                        }
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        ControlButton(
                            label = if (fastMode) "⚡" else "🏃",
                            caption = if (fastMode) "اندفاع" else "جري",
                            active = fastMode,
                            onClick = { if (running && !finished && countdown == 0) fastMode = !fastMode }
                        )
                        ControlButton(
                            label = "↑",
                            caption = "قفز",
                            active = jumping,
                            onClick = { if (running && !finished && countdown == 0 && !jumping) jumping = true }
                        )
                    }
                }
            }

            if (countdown > 0 && !finished) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.28f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(shape = CircleShape, colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f))) {
                        Box(Modifier.size(118.dp), contentAlignment = Alignment.Center) {
                            Text(countdown.toString(), fontSize = 54.sp, fontWeight = FontWeight.Black, color = Color(0xFF315CFF))
                        }
                    }
                }
            }

            if (!running && !finished) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.32f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 28.dp, vertical = 22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("اللعبة متوقفة", fontSize = 22.sp, fontWeight = FontWeight.Black)
                            Text("جاهز للعودة إلى المغامرة؟", fontSize = 14.sp, color = Color.DarkGray)
                            Button(onClick = { running = true }) { Text("متابعة") }
                        }
                    }
                }
            }

            if (finished) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.58f)),
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
                            Text("نهاية الجولة 🎉", fontSize = 30.sp, fontWeight = FontWeight.Black)
                            Text("جمعت $collected نجمة", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("النقاط  $score", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("تجاوزت $dodged حاجزًا بنجاح", fontSize = 14.sp, color = Color.Gray)
                            Text("اصطدمت بـ $misses حاجز", fontSize = 14.sp, color = Color.Gray)
                            Text("⭐ نجوم ذهبية: $goldCollected   🔥 أفضل سلسلة: $combo", fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
            .size(112.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.10f))
            .pointerInput(Unit) {
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
        Box(
            modifier = Modifier.size(82.dp).clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.28f))
        )
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.90f)),
            contentAlignment = Alignment.Center
        ) {
            Text("↔", color = Color(0xFF183047), fontSize = 25.sp, fontWeight = FontWeight.Black)
        }
        Text(
            text = when (lane) { 0 -> "يسار"; 2 -> "يمين"; else -> "وسط" },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp),
            color = Color.White.copy(alpha = 0.82f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ControlButton(label: String, caption: String, active: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(if (active) Color(0xFFFFD54F) else Color.Black.copy(alpha = 0.42f))
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier.fillMaxSize().clip(CircleShape)
                    .background(if (active) Color(0xFFFFE27A) else Color.White.copy(alpha = 0.08f))
            ) {
                Text(
                    label,
                    fontSize = 29.sp,
                    color = if (active) Color(0xFF24324A) else Color.White,
                    fontWeight = FontWeight.Black
                )
            }
        }
        Spacer(Modifier.height(3.dp))
        Text(caption, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
    }
}
