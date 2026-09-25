package com.ali12hhh.kidslearning.navigation

import android.annotation.SuppressLint

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

private data class BreakItem(val id: Int, var lane: Int, val type: Int, var progress: Float)
private const val BREAK_TITLE_OWNED = "break_game_owned_titles"
private const val BREAK_TITLE_EQUIPPED = "break_game_equipped_title"

private fun gameOwnedTitles(context: android.content.Context): Set<String> =
    context.getSharedPreferences("kids_learning_settings", android.content.Context.MODE_PRIVATE)
        .getStringSet(BREAK_TITLE_OWNED, emptySet()) ?: emptySet()

private fun equippedGameTitle(context: android.content.Context): String? =
    context.getSharedPreferences("kids_learning_settings", android.content.Context.MODE_PRIVATE)
        .getString(BREAK_TITLE_EQUIPPED, null)

@SuppressLint("UseKtx")
private fun buyGameTitle(context: android.content.Context, titleId: String, price: Int): Boolean {
    val prefs = context.getSharedPreferences("kids_learning_settings", android.content.Context.MODE_PRIVATE)
    val owned = gameOwnedTitles(context)
    val balance = AppSettings.gameStars(context)
    if (titleId in owned || balance < price) return false
    prefs.edit()
        .putInt("break_game_stars", balance - price)
        .putStringSet(BREAK_TITLE_OWNED, owned + titleId)
        .putString(BREAK_TITLE_EQUIPPED, titleId)
        .apply()
    return true
}

@SuppressLint("UseKtx")
private fun equipGameTitle(context: android.content.Context, titleId: String) {
    if (titleId in gameOwnedTitles(context)) {
        context.getSharedPreferences("kids_learning_settings", android.content.Context.MODE_PRIVATE)
            .edit().putString(BREAK_TITLE_EQUIPPED, titleId).apply()
    }
}

private const val GAME_SECONDS = 60
private const val STAR = 0
private const val BARRIER = 1
private const val GOLD_STAR = 2
private const val MOVING_TRAP = 3
private const val WIDE_TRAP = 4

@Composable
fun BreakGamePage(onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var screen by remember { mutableStateOf(BreakGameScreen.HOME) }
    var storeRefresh by remember { mutableIntStateOf(0) }
    var gameBalance by remember { mutableIntStateOf(AppSettings.gameStars(context)) }

    fun refreshGameBalance() {
        gameBalance = AppSettings.gameStars(context)
    }

    when (screen) {
        BreakGameScreen.HOME -> BreakGameHome(
            context = context,
            gameBalance = gameBalance,
            onStart = { refreshGameBalance(); screen = BreakGameScreen.GAME },
            onStore = { refreshGameBalance(); screen = BreakGameScreen.STORE },
            onInventory = { refreshGameBalance(); screen = BreakGameScreen.INVENTORY },
            onOpenXo = { screen = BreakGameScreen.XO },
            onOpenXoShop = { screen = BreakGameScreen.XO_SHOP },
            onBack = onBack
        )
        BreakGameScreen.STORE -> BreakGameStore(
            context = context,
            refreshKey = storeRefresh,
            gameBalance = gameBalance,
            onPurchased = { refreshGameBalance(); storeRefresh++ },
            onClose = { refreshGameBalance(); screen = BreakGameScreen.HOME }
        )
        BreakGameScreen.INVENTORY -> BreakGameInventory(
            context = context,
            onClose = { screen = BreakGameScreen.HOME }
        )
        BreakGameScreen.GAME -> BreakGamePlayPage(onBack = { refreshGameBalance(); screen = BreakGameScreen.HOME })
        BreakGameScreen.XO -> TicTacToeGamePage(onBack = { screen = BreakGameScreen.HOME })
        BreakGameScreen.XO_SHOP -> TicTacToeGamePage(onBack = { screen = BreakGameScreen.HOME }, startInShop = true)
    }
}

private enum class BreakGameScreen { HOME, GAME, STORE, INVENTORY, XO, XO_SHOP }

@Composable
private fun BreakGameHome(
    context: android.content.Context,
    gameBalance: Int,
    onStart: () -> Unit,
    onStore: () -> Unit,
    onInventory: () -> Unit,
    onOpenXo: () -> Unit,
    onOpenXoShop: () -> Unit,
    onBack: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF071421)) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color(0xFF06111F), Color(0xFF102D43), Color(0xFF17606A), Color(0xFFB7D78D)))
            ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.9f).padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("مغامرة ريبو", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black)
                Text("استراحة ممتعة! اختر ما تريد قبل أن تبدأ.", color = Color(0xFFD7F2F0), fontSize = 15.sp, textAlign = TextAlign.Center)
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color(0xE610211E))) {
                    Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("💰 $gameBalance عملة المغامرة", color = Color(0xFFFFD54F), fontSize = 17.sp, fontWeight = FontWeight.Black)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            BreakMenuCard(Modifier.weight(1f), "⚔️", "مغامرات ريبو", Color(0xFFE85D3F), onStart)
                            BreakMenuCard(Modifier.weight(1f), "🛍️", "متجر المغامرة", Color(0xFFFFA000), onStore)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            BreakMenuCard(Modifier.weight(1f), "×O", "لعبة XO", Color(0xFF36A9E1), onOpenXo)
                            BreakMenuCard(Modifier.weight(1f), "🎨", "متجر XO", Color(0xFF8E5DE7), onOpenXoShop)
                        }
                        BreakMenuWideButton("🎒", "مقتنياتي", Color(0xFF20A77A), onInventory)
                        BreakMenuWideButton("↩", "خروج", Color(0xFF607D8B), onBack)
                    }
                }
                Text("العملات والمقتنيات هنا خاصة بمغامرة ريبو ولا تدخل في رصيد نجوم التعليم.", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun BreakMenuCard(modifier: Modifier, icon: String, title: String, color: Color, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = modifier.height(108.dp), shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color, contentColor = Color.White)) {
        Column(Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(icon, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(4.dp))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun BreakMenuWideButton(icon: String, title: String, color: Color, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = color, contentColor = Color.White)) {
        Text("$icon  $title", fontSize = 15.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun BreakGameInventory(context: android.content.Context, onClose: () -> Unit) {
    val ownedItems = AppSettings.gameOwnedItems(context)
    val ownedOutfits = AppSettings.gameOwnedOutfits(context)
    val ownedEquipment = AppSettings.gameOwnedEquipment(context)
    val ownedTitles = gameOwnedTitles(context)
    val equippedItem = AppSettings.equippedGameItem(context)
    val equippedOutfit = AppSettings.equippedGameOutfit(context)
    val equippedEquipment = AppSettings.equippedGameEquipment(context)
    val equippedTitle = equippedGameTitle(context)
    val itemNames = mapOf("speed_badge" to "شارة الاندفاع", "jump_badge" to "شارة القفز", "gold_badge" to "شارة النجم الذهبي")
    val outfitNames = mapOf("knight_outfit" to "زي الفارس", "rogue_outfit" to "زي المغامر", "mage_outfit" to "زي الساحر")
    val equipmentNames = mapOf("sword_1handed" to "سيف خفيف", "sword_2handed" to "سيف ثقيل", "axe_1handed" to "فأس قتالي", "dagger" to "خنجر", "shield_round" to "درع دائري", "shield_spikes" to "درع الأشواك")
    val titleNames = mapOf("title_reebo_star" to "نجم ريبو", "title_brave_hero" to "البطل الشجاع", "title_smart_explorer" to "المستكشف الذكي", "title_dodge_master" to "سيد المراوغة", "title_speed_champion" to "بطل السرعة", "title_reebo_friend" to "صديق ريبو", "title_challenge_hero" to "بطل التحدي", "title_shining_star" to "النجم اللامع", "title_trap_breaker" to "قاهر الفخاخ", "title_adventure_legend" to "أسطورة المغامرة")
    Box(Modifier.fillMaxSize().background(Color(0xFFF3F8FB)), contentAlignment = Alignment.Center) {
        Card(Modifier.fillMaxWidth(0.94f).fillMaxSize(0.9f), shape = RoundedCornerShape(30.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(20.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("🎒 مقتنياتي", color = Color(0xFF102B3E), fontSize = 25.sp, fontWeight = FontWeight.Black)
                    IconButton(onClick = onClose) { Text("✕", fontSize = 22.sp) }
                }
                Text("💰 ${AppSettings.gameStars(context)} عملة المغامرة", color = Color(0xFFB77900), fontWeight = FontWeight.Bold)
                Text("الترقيات: ${ownedItems.size}", fontWeight = FontWeight.Black, color = Color(0xFF17384D))
                ownedItems.forEach { id -> Text("• ${itemNames[id] ?: id}${if (id == equippedItem) "  ✓ مجهّز" else ""}") }
                Text("الأزياء: ${ownedOutfits.size}", fontWeight = FontWeight.Black, color = Color(0xFF17384D))
                ownedOutfits.forEach { id -> Text("• ${outfitNames[id] ?: id}${if (id == equippedOutfit) "  ✓ مجهّز" else ""}") }
                Text("الأسلحة والدروع: ${ownedEquipment.size}", fontWeight = FontWeight.Black, color = Color(0xFF17384D))
                ownedEquipment.forEach { id -> Text("• ${equipmentNames[id] ?: id}${if (id == equippedEquipment) "  ✓ مجهّز" else ""}") }
                Text("الألقاب: ${ownedTitles.size}", fontWeight = FontWeight.Black, color = Color(0xFF17384D))
                ownedTitles.forEach { id -> Text("• ${titleNames[id] ?: id}${if (id == equippedTitle) "  ✓ مجهّز" else ""}") }
                if (ownedItems.isEmpty() && ownedOutfits.isEmpty() && ownedEquipment.isEmpty() && ownedTitles.isEmpty()) {
                    Text("لا توجد مقتنيات بعد. افتح المتجر واشترِ أول عنصر لك!", color = Color(0xFF547083), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun BreakGamePlayPage(onBack: () -> Unit) {
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
    var bestCombo by remember { mutableIntStateOf(0) }
    var dodged by remember { mutableIntStateOf(0) }
    var goldCollected by remember { mutableIntStateOf(0) }
    var trapHits by remember { mutableIntStateOf(0) }
    var countdown by remember { mutableIntStateOf(3) }
    var roundId by remember { mutableIntStateOf(0) }
    var stage by remember { mutableIntStateOf(1) }
    var roundReward by remember { mutableIntStateOf(0) }
    var missionReward by remember { mutableIntStateOf(0) }
    var feedbackText by remember { mutableStateOf("") }
    var feedbackTick by remember { mutableIntStateOf(0) }
    var feedbackKind by remember { mutableIntStateOf(0) }
    var storeRefresh by remember { mutableIntStateOf(0) }
    var equippedGameItem by remember { mutableStateOf(AppSettings.equippedGameItem(context)) }
    var equippedGameOutfit by remember { mutableStateOf(AppSettings.equippedGameOutfit(context)) }
    var equippedGameEquipment by remember { mutableStateOf(AppSettings.equippedGameEquipment(context)) }
    val items = remember { mutableStateListOf<BreakItem>() }

    fun refreshEquippedItem() {
        equippedGameItem = AppSettings.equippedGameItem(context)
        equippedGameOutfit = AppSettings.equippedGameOutfit(context)
        equippedGameEquipment = AppSettings.equippedGameEquipment(context)
    }

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
        bestCombo = 0
        dodged = 0
        goldCollected = 0
        trapHits = 0
        roundReward = 0
        missionReward = 0
        feedbackText = ""
        feedbackKind = 0
        feedbackTick++
        countdown = 3
        stage = 1
        roundId++
        tick++
        finished = false
        running = true
    }

    LaunchedEffect(roundId) {
        for (value in 3 downTo 1) {
            countdown = value
            delay(650)
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
            stage = when {
                remaining > 30 -> 1
                remaining > 15 -> 2
                else -> 3
            }
            val stageProgress = when (stage) {
                1 -> (GAME_SECONDS - remaining) / 15f
                2 -> (30 - remaining) / 15f
                else -> (15 - remaining).coerceAtLeast(0) / 15f
            }.coerceIn(0f, 1f)
            val difficulty = when (stage) {
                1 -> 1.0f + stageProgress * 0.10f
                2 -> 1.18f + stageProgress * 0.18f
                else -> 1.42f + stageProgress * 0.28f
            }
            val speedBonus = if (equippedGameItem == "speed_badge") 1.12f else 1f
            val speed = (if (fastMode) 0.0018f else 0.0012f) * difficulty * speedBonus
            items.forEach {
                it.progress += speed * 50f
                if (it.type == MOVING_TRAP && it.progress > 0.18f && it.progress < 0.78f && tick % 18 == 0) {
                    it.lane = when (it.lane) {
                        0 -> 1
                        1 -> if (Random.nextBoolean()) 0 else 2
                        else -> 1
                    }
                }
            }

            val removeIds = mutableSetOf<Int>()
            items.forEach { item ->
                if (item.progress >= 0.82f) {
                    if (item.lane == playerLane) {
                        if (item.type == STAR || item.type == GOLD_STAR) {
                            collected++
                            combo++
                            bestCombo = maxOf(bestCombo, combo)
                            score += (if (item.type == GOLD_STAR) 25 else 10) + (combo.coerceAtMost(8) - 1) * 2
                            if (item.type == GOLD_STAR) goldCollected++
                            feedbackText = if (item.type == GOLD_STAR) "نجم ذهبي! +25" else "نجمة! +10"
                            feedbackKind = if (item.type == GOLD_STAR) 2 else 1
                            feedbackTick = tick
                        } else if (!jumping) {
                            misses++
                            trapHits++
                            combo = 0
                            collected = (collected - 2).coerceAtLeast(0)
                            score = (score - 14).coerceAtLeast(0)
                            // Hitting a trap also costs 2 of the game's separate shop currency.
                            AppSettings.addGameStars(context, -2)
                            feedbackText = "فخ! -2 💰"
                            feedbackKind = 3
                            feedbackTick = tick
                        } else {
                            dodged++
                            combo++
                            bestCombo = maxOf(bestCombo, combo)
                            score += 8 + combo.coerceAtMost(5)
                            feedbackText = "مراوغة ممتازة! +8"
                            feedbackKind = 4
                            feedbackTick = tick
                        }
                    }
                    removeIds += item.id
                }
            }
            items.removeAll { it.id in removeIds }

            val spawnBonus = if (equippedGameItem == "speed_badge") 0.90f else 1f
            if (spawnMs >= ((if (fastMode) 610L else 820L) * spawnBonus) / difficulty) {
                spawnMs = 0L
                spawnCounter++
                val lane = Random.nextInt(0, 3)
                val type = when {
                    stage >= 3 && spawnCounter % 11 == 0 -> GOLD_STAR
                    stage >= 3 && spawnCounter % 7 == 0 -> MOVING_TRAP
                    stage >= 2 && spawnCounter % 5 == 0 -> WIDE_TRAP
                    stage >= 2 && spawnCounter % 3 == 0 -> BARRIER
                    else -> STAR
                }
                items += BreakItem(spawnCounter, lane, type, -0.10f)

                // Later stages can create a second lane target without external assets.
                if (stage >= 2 && spawnCounter % 5 == 0) {
                    val secondLane = (lane + if (Random.nextBoolean()) 1 else 2) % 3
                    val secondType = if (stage >= 3 && spawnCounter % 10 == 0) GOLD_STAR else STAR
                    items += BreakItem(spawnCounter + 100000, secondLane, secondType, -0.22f)
                }
            }
            if (elapsedMs >= 1000L) {
                elapsedMs -= 1000L
                remaining--
            }
        }
        if (remaining <= 0) {
            running = false
            finished = true
            val rewardMultiplier = if (equippedGameItem == "gold_badge") 1.5f else 1f
            val baseReward = (collected / 2).coerceIn(0, 12)
            roundReward = (baseReward * rewardMultiplier).toInt().coerceIn(0, 18)
            val missionType = roundId % 3
            val missionDone = when (missionType) {
                0 -> collected >= 12
                1 -> dodged >= 4
                else -> goldCollected >= 2
            }
            missionReward = if (missionDone) 3 else 0
            val totalReward = (roundReward + missionReward).coerceAtMost(21)
            if (totalReward > 0) AppSettings.addGameStars(context, totalReward)
        }
    }

    LaunchedEffect(feedbackTick) {
        if (feedbackTick == 0) return@LaunchedEffect
        delay(850)
        feedbackText = ""
    }

    val smoothPlayerX = remember { Animatable(0f) }
    LaunchedEffect(playerLane) {
        smoothPlayerX.animateTo((playerLane - 1) * 0.78f, tween(150))
    }

    LaunchedEffect(jumping) {
        if (jumping) {
            delay(if (equippedGameItem == "jump_badge") 520 else 680)
            jumping = false
        }
    }

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val characterAsset = when (equippedGameOutfit) {
        "knight_outfit" -> "characters/KayKit/Outfits/Knight.glb"
        "rogue_outfit" -> "characters/KayKit/Outfits/Rogue.glb"
        "mage_outfit" -> "characters/KayKit/Outfits/Mage.glb"
        else -> "Mannequin_Medium_Anim.glb"
    }
    val model = remember(characterAsset) {
        runCatching { modelLoader.createModelInstance(characterAsset) }.getOrNull()
    }
    val equipmentAsset = when (equippedGameEquipment) {
        "sword_1handed" -> "characters/KayKit/Accessories/sword_1handed.gltf"
        "sword_2handed" -> "characters/KayKit/Accessories/sword_2handed.gltf"
        "axe_1handed" -> "characters/KayKit/Accessories/axe_1handed.gltf"
        "dagger" -> "characters/KayKit/Accessories/dagger.gltf"
        "shield_round" -> "characters/KayKit/Accessories/shield_round.gltf"
        "shield_spikes" -> "characters/KayKit/Accessories/shield_spikes.gltf"
        else -> null
    }
    val equipmentModel = remember(equipmentAsset) {
        equipmentAsset?.let { runCatching { modelLoader.createModelInstance(it) }.getOrNull() }
    }
    val cameraNode = rememberCameraNode(engine) {
        position = Position(x = 0f, y = 0.45f, z = 10.5f)
    }
    val characterNode = remember(model) {
        model?.let { instance ->
            ModelNode(
                modelInstance = instance,
                autoAnimate = false,
                scaleToUnits = 1.08f,
                centerOrigin = Position(x = 0f, y = -0.88f, z = 0f)
            )
            .apply {
                rotation = Rotation(y = 180f)
            }
        }
    }
    val playerX = smoothPlayerX.value
    val playerY = if (jumping) (if (equippedGameItem == "jump_badge") 0.84f else 0.72f) else 0f
    LaunchedEffect(characterNode, running, finished, countdown, jumping, fastMode) {
        val node = characterNode ?: return@LaunchedEffect
        if (running && !finished && countdown == 0) {
            val animation = when {
                jumping -> "Jump_Full_Short"
                fastMode -> "Running_A"
                else -> "Walking_A"
            }
            val speed = when {
                jumping -> 1.0f
                fastMode -> 1.05f
                else -> 0.95f
            }
            runCatching { node.playAnimation(animation, speed, true) }
        } else {
            runCatching { node.stopAnimation("Walking_A") }
            runCatching { node.stopAnimation("Running_A") }
            runCatching { node.stopAnimation("Jump_Full_Short") }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF071421)) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color(0xFF06111F), Color(0xFF102D43), Color(0xFF17606A), Color(0xFFB7D78D)))
            )
        ) {
            Scene(
                modifier = Modifier.fillMaxSize(),
                engine = engine,
                modelLoader = modelLoader,
                cameraNode = cameraNode,
                cameraManipulator = null,
                isOpaque = false,
                childNodes = listOfNotNull(
                    characterNode?.also { it.position = Position(x = playerX, y = playerY, z = 0f) },
                    equipmentModel?.let { instance ->
                        ModelNode(
                            modelInstance = instance,
                            autoAnimate = false,
                            scaleToUnits = 0.34f
                        ).apply {
                            position = Position(x = playerX + 0.28f, y = playerY + 0.48f, z = 0.02f)
                            rotation = Rotation(y = 180f)
                        }
                    }
                )
            )

            Canvas(modifier = Modifier.fillMaxSize().padding(top = 102.dp, bottom = 178.dp)) {
                val w = size.width
                val h = size.height
                val horizon = h * 0.36f
                val bottom = h * 1.02f
                val center = w / 2f
                // Animated cinematic sky: sun, clouds and subtle atmosphere.
                val skyPulse = 0.18f + ((tick % 80) / 80f) * 0.08f
                drawCircle(Color(0xFFFFD66B).copy(alpha = skyPulse), radius = w * 0.17f, center = Offset(center, horizon * 0.72f))
                for (cloud in 0..3) {
                    val cloudX = ((w * (0.08f + cloud * 0.29f)) + (tick % 160) * (1f + cloud * 0.2f)) % (w * 1.18f) - w * 0.08f
                    val cloudY = h * (0.10f + (cloud % 2) * 0.08f)
                    drawCircle(Color.White.copy(alpha = 0.10f), w * 0.045f, Offset(cloudX, cloudY))
                    drawCircle(Color.White.copy(alpha = 0.08f), w * 0.065f, Offset(cloudX + w * 0.035f, cloudY + 4f))
                    drawCircle(Color.White.copy(alpha = 0.07f), w * 0.04f, Offset(cloudX + w * 0.075f, cloudY + 8f))
                }
                val skyline = listOf(0.04f to 0.17f, 0.13f to 0.24f, 0.23f to 0.14f, 0.76f to 0.20f, 0.86f to 0.13f, 0.95f to 0.25f)
                skyline.forEachIndexed { i, pair ->
                    val bw = w * (if (i % 2 == 0) 0.09f else 0.07f)
                    val bh = h * pair.second
                    drawRect(Color(0xFF071A2A).copy(alpha = 0.72f), Offset(w * pair.first, horizon - bh), Size(bw, bh))
                    for (row in 0..3) for (col in 0..1) {
                        drawRect(Color(0xFFFFD66B).copy(alpha = 0.30f), Offset(w * pair.first + bw * (0.2f + col * 0.42f), horizon - bh + bh * (0.18f + row * 0.18f)), Size(bw * 0.12f, bh * 0.07f))
                    }
                }
                // Road shoulders, soft lane glow and three playable lanes.
                val roadGlow = Path().apply {
                    moveTo(w * 0.425f, horizon)
                    lineTo(w * 0.575f, horizon)
                    lineTo(w * 1.07f, bottom)
                    lineTo(w * -0.07f, bottom)
                    close()
                }
                drawPath(roadGlow, Color(0xFF3DD6C6).copy(alpha = 0.07f))
                val road = Path().apply {
                    moveTo(w * 0.43f, horizon)
                    lineTo(w * 0.57f, horizon)
                    lineTo(w * 1.04f, bottom)
                    lineTo(w * -0.04f, bottom)
                    close()
                }
                drawPath(road, Color(0xFF102632).copy(alpha = 0.96f))
                val leftEdge = Path().apply { moveTo(w * 0.43f, horizon); lineTo(w * -0.04f, bottom) }
                val rightEdge = Path().apply { moveTo(w * 0.57f, horizon); lineTo(w * 1.04f, bottom) }
                drawPath(leftEdge, Color(0xFF50D8D2).copy(alpha = 0.78f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5f))
                drawPath(rightEdge, Color(0xFF50D8D2).copy(alpha = 0.78f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5f))
                drawLine(Color(0xFFFFD54F).copy(alpha = 0.38f), Offset(w * 0.50f, horizon), Offset(w * 0.50f, bottom), strokeWidth = 2f)
                for (laneLine in 1..2) {
                    val topX = w * (0.43f + 0.14f * laneLine)
                    val bottomX = w * (laneLine / 3f)
                    drawLine(Color.White.copy(alpha = 0.23f), Offset(topX, horizon), Offset(bottomX, bottom), strokeWidth = 3f)
                }
                // Moving dash marks give the road a sense of forward motion.
                val dashShift = (tick % 24) / 24f
                for (i in 0..7) {
                    val t = ((i / 8f) + dashShift / 8f) % 1f
                    val y = horizon + (bottom - horizon) * t
                    val spread = 0.12f + t * 0.88f
                    val dashW = w * (0.008f + t * 0.025f)
                    for (laneLine in 1..2) {
                        val x = center + (laneLine - 1.5f) * w * 0.14f * spread * 2f
                        drawRoundRect(Color.White.copy(alpha = 0.10f + t * 0.23f), Offset(x - dashW / 2f, y), Size(dashW, 5f + t * 13f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f))
                    }
                }
                if (fastMode && running && countdown == 0) {
                    for (i in 0..13) {
                        val x = (w * ((i * 37 % 100) / 100f))
                        val y = horizon + ((i * 61 + tick * 11) % (h.toInt().coerceAtLeast(1))).toFloat()
                        drawLine(Color(0xFFBDEFFF).copy(alpha = 0.22f), Offset(x, y), Offset(x - w * 0.035f, y + h * 0.055f), strokeWidth = 2f)
                    }
                }
                // Pickups and spike strips scale as they approach the player.
                items.forEach { item ->
                    val t = item.progress.coerceIn(0f, 1f)
                    val perspective = 0.15f + t * 0.85f
                    val x = center + (item.lane - 1) * w * 0.235f * perspective
                    val y = horizon + (bottom - horizon) * t
                    val radius = 8f + t * if (item.type == GOLD_STAR) 28f else 23f
                    if (item.type == STAR) {
                        drawCircle(Color(0xFFFFB51B).copy(alpha = 0.25f), radius * 1.55f, Offset(x, y))
                        drawCircle(Color(0xFFFFD54F), radius, Offset(x, y))
                        drawCircle(Color.White.copy(alpha = 0.72f), radius * 0.27f, Offset(x - radius * 0.28f, y - radius * 0.3f))
                    } else if (item.type == GOLD_STAR) {
                        drawCircle(Color(0xFFFF8F00).copy(alpha = 0.3f), radius * 1.55f, Offset(x, y))
                        drawCircle(Color(0xFFFFA000), radius * 1.18f, Offset(x, y))
                        drawCircle(Color(0xFFFFE082), radius * 0.88f, Offset(x, y))
                        drawLine(Color.White, Offset(x - radius * 0.48f, y), Offset(x + radius * 0.48f, y), strokeWidth = 3f + t * 2f)
                        drawLine(Color.White, Offset(x, y - radius * 0.48f), Offset(x, y + radius * 0.48f), strokeWidth = 3f + t * 2f)
                    } else {
                        val spike = Path().apply {
                            moveTo(x - radius * 1.3f, y + radius * 0.65f)
                            lineTo(x - radius * 0.8f, y - radius * 0.85f)
                            lineTo(x - radius * 0.3f, y + radius * 0.65f)
                            lineTo(x + radius * 0.15f, y - radius * 0.85f)
                            lineTo(x + radius * 0.65f, y + radius * 0.65f)
                            lineTo(x + radius * 1.1f, y - radius * 0.85f)
                            lineTo(x + radius * 1.5f, y + radius * 0.65f)
                            close()
                        }
                        drawRoundRect(Color(0xFF501E2A), Offset(x - radius * 1.65f, y + radius * 0.45f), Size(radius * 3.3f, radius * 0.48f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f, 5f))
                        drawPath(spike, Color(0xFFFF5964))
                        drawLine(Color(0xFFFFE4E6), Offset(x - radius * 1.3f, y + radius * 0.55f), Offset(x + radius * 1.4f, y + radius * 0.55f), strokeWidth = 2f + t * 4f)
                    }
                }
            }

            // Compact glass HUD with readable objective and round status.
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 9.dp),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xDD071522))
            ) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 9.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (finished) onBack() else running = !running }, modifier = Modifier.size(43.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.10f))) {
                            Text(if (finished) "‹" else if (running) "Ⅱ" else "▶", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Black)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("مغامرة ريبو", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                            Text(
                                if (fastMode) "وضع الاندفاع • المرحلة $stage/3"
                                else "المرحلة $stage/3 • اجمع النجوم وتفادَ الفخاخ",
                                color = Color(0xFFB9D9E7),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("⭐ $score", color = Color(0xFFFFD54F), fontSize = 18.sp, fontWeight = FontWeight.Black)
                            Text("💰 ${AppSettings.gameStars(context)}", color = Color(0xFFFFC857), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("⏱ $remaining ث", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.13f))) {
                            Box(Modifier.fillMaxWidth((remaining.toFloat() / GAME_SECONDS).coerceIn(0f, 1f)).fillMaxSize().background(Brush.horizontalGradient(listOf(Color(0xFF37D6C0), Color(0xFFFFD54F)))))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("المستوى $stage/3", color = Color(0xFF7FE6D9), fontSize = 11.sp, fontWeight = FontWeight.Black)
                                Text("🔥 $combo", color = Color(0xFFFFD54F), fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                    }
                }
            }

            Card(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 111.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xA6071D2A))
            ) {
                Row(
                    Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        when (stage) {
                            1 -> "🌤 بداية المغامرة"
                            2 -> "⚡ التحدي يتصاعد"
                            else -> "🔥 المرحلة النهائية"
                        },
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                    repeat(3) { index ->
                        Box(
                            Modifier.size(if (index < stage) 7.dp else 5.dp)
                                .clip(CircleShape)
                                .background(if (index < stage) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.25f))
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().navigationBarsPadding().padding(horizontal = 12.dp, vertical = 10.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xE610211E))
            ) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 9.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    LaneJoystick(lane = playerLane, onLaneChange = { delta ->
                        if (running && !finished && countdown == 0) playerLane = (playerLane + delta).coerceIn(0, 2)
                    })
                    Row(horizontalArrangement = Arrangement.spacedBy(13.dp), verticalAlignment = Alignment.CenterVertically) {
                        ControlButton(label = if (fastMode) "⚡" else "🏃", caption = if (fastMode) "اندفاع ON" else "جري", active = fastMode) {
                            if (running && !finished && countdown == 0) fastMode = !fastMode
                        }
                        ControlButton(label = "↑", caption = "قفز", active = jumping) {
                            if (running && !finished && countdown == 0 && !jumping) jumping = true
                        }
                    }
                }
            }

            if (feedbackText.isNotEmpty() && !finished) {
                Card(
                    modifier = Modifier.align(Alignment.Center).padding(top = 82.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (feedbackKind) {
                            3 -> Color(0xE6B83A4A)
                            4 -> Color(0xE61A8B79)
                            2 -> Color(0xE6B77900)
                            else -> Color(0xE61A4964)
                        }
                    )
                ) {
                    Text(
                        feedbackText,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            if (countdown > 0 && !finished) {
                Box(Modifier.fillMaxSize().background(Color(0x99020A13)), contentAlignment = Alignment.Center) {
                    Card(shape = RoundedCornerShape(30.dp), colors = CardDefaults.cardColors(containerColor = Color(0xF20D2638))) {
                        Column(Modifier.padding(horizontal = 35.dp, vertical = 25.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("استعد يا بطل!", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(countdown.toString(), color = Color(0xFFFFD54F), fontSize = 64.sp, fontWeight = FontWeight.Black)
                            Text("حرّك ريبو بين المسارات", color = Color(0xFFB9D9E7), fontSize = 13.sp)
                        }
                    }
                }
            }

            if (!running && !finished) {
                Box(Modifier.fillMaxSize().background(Color(0xB8000911)), contentAlignment = Alignment.Center) {
                    Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F8FB))) {
                        Column(Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("توقف مؤقت", fontSize = 25.sp, fontWeight = FontWeight.Black, color = Color(0xFF102B3E))
                            Text("ريبو ينتظرك لتكمل التحدي", fontSize = 14.sp, color = Color(0xFF476174))
                            Button(onClick = { running = true }) { Text("متابعة اللعب") }
                        }
                    }
                }
            }

            if (finished) {
                Box(Modifier.fillMaxSize().background(Color(0xD9000810)), contentAlignment = Alignment.Center) {
                    Card(Modifier.fillMaxWidth(0.9f).padding(8.dp), shape = RoundedCornerShape(30.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FBFF))) {
                        Column(Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(9.dp)) {
                            Text("أحسنت يا بطل! 🎉", color = Color(0xFF102B3E), fontSize = 27.sp, fontWeight = FontWeight.Black)
                            Text("انتهت مغامرة ريبو", color = Color(0xFF547083), fontSize = 14.sp)
                            Text("⭐ $collected نجمة    •    النقاط $score", color = Color(0xFF163D56), fontSize = 18.sp, fontWeight = FontWeight.Black)
                            Text("تجاوزت $dodged فخًا  |  اصطدامات: $misses", color = Color(0xFF547083), fontSize = 13.sp)
                            Text("المرحلة الأخيرة: $stage/3   •   النجوم الذهبية: $goldCollected   •   خسائر الفخاخ: $trapHits", color = Color(0xFF547083), fontSize = 12.sp)
                            Text("أفضل سلسلة: $bestCombo", color = Color(0xFFB77900), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            val missionType = roundId % 3
                            val missionText = when (missionType) {
                                0 -> "مهمة الجولة: اجمع 12 نجمة"
                                1 -> "مهمة الجولة: تفادَ 4 فخاخ"
                                else -> "مهمة الجولة: اجمع نجمتين ذهبيتين"
                            }
                            Text("$missionText ${if (missionReward > 0) "✓ +3 💰" else ""}", color = if (missionReward > 0) Color(0xFF16806B) else Color(0xFF547083), fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Text("مكافأة الجولة: 💰 $roundReward   •   مكافأة المهمة: 💰 $missionReward", color = Color(0xFF163D56), fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Spacer(Modifier.height(3.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(onClick = { resetGame() }) { Text("العب مجددًا") }
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
private fun BreakGameStore(
    context: android.content.Context,
    refreshKey: Int,
    gameBalance: Int,
    onPurchased: () -> Unit,
    onClose: () -> Unit
) {
    val upgrades = listOf(
        "speed_badge" to ("شارة الاندفاع" to 200),
        "jump_badge" to ("شارة القفز" to 300),
        "gold_badge" to ("شارة النجم الذهبي" to 400)
    )
    val outfits = listOf(
        "knight_outfit" to ("زي الفارس" to 500),
        "rogue_outfit" to ("زي المغامر" to 600),
        "mage_outfit" to ("زي الساحر" to 700)
    )
    val titles = listOf(
        "title_reebo_star" to ("نجم ريبو" to 50),
        "title_brave_hero" to ("البطل الشجاع" to 55),
        "title_smart_explorer" to ("المستكشف الذكي" to 60),
        "title_dodge_master" to ("سيد المراوغة" to 65),
        "title_speed_champion" to ("بطل السرعة" to 70),
        "title_reebo_friend" to ("صديق ريبو" to 80),
        "title_challenge_hero" to ("بطل التحدي" to 85),
        "title_shining_star" to ("النجم اللامع" to 90),
        "title_trap_breaker" to ("قاهر الفخاخ" to 95),
        "title_adventure_legend" to ("أسطورة المغامرة" to 100)
    )
    var owned by remember(refreshKey) { mutableStateOf(AppSettings.gameOwnedItems(context)) }
    var ownedOutfits by remember(refreshKey) { mutableStateOf(AppSettings.gameOwnedOutfits(context)) }
    val equippedOutfit = AppSettings.equippedGameOutfit(context)

    fun refreshOwnership() {
        owned = AppSettings.gameOwnedItems(context)
        ownedOutfits = AppSettings.gameOwnedOutfits(context)
    }

    val storeScrollState = rememberScrollState()

    Box(Modifier.fillMaxSize().background(Color(0xD9000810)), contentAlignment = Alignment.Center) {
        Card(Modifier.fillMaxWidth(0.94f).padding(8.dp), shape = RoundedCornerShape(30.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FBFF))) {
            Column(Modifier.padding(20.dp).verticalScroll(storeScrollState), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("متجر مغامرة ريبو", color = Color(0xFF102B3E), fontSize = 23.sp, fontWeight = FontWeight.Black)
                        Text("💰 $gameBalance عملة اللعبة", color = Color(0xFFB77900), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onClose) { Text("✕", fontSize = 22.sp) }
                }
                Text("العملات هنا منفصلة تمامًا عن نجوم التعليم. الشراء يجعل العنصر ملكك ويبقى في مقتنياتك.", color = Color(0xFF547083), fontSize = 12.sp)
                Text("الترقيات", color = Color(0xFF17384D), fontSize = 18.sp, fontWeight = FontWeight.Black)
                upgrades.forEach { (id, data) ->
                    val (name, price) = data
                    val isOwned = id in owned
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF3F7))) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(name, color = Color(0xFF17384D), fontSize = 16.sp, fontWeight = FontWeight.Black)
                                Text(when (id) {
                                    "speed_badge" -> "يزيد سرعة الحركة وتواتر ظهور العناصر"
                                    "jump_badge" -> "قفزة أعلى وعودة أسرع من القفز"
                                    "gold_badge" -> "يرفع مكافأة الجولة"
                                    else -> "ترقية خاصة باللعبة"
                                }, color = Color(0xFF5A7484), fontSize = 11.sp)
                            }
                            Button(onClick = {
                                if (!isOwned && AppSettings.buyGameItem(context, id, price)) {
                                    refreshOwnership(); onPurchased()
                                } else if (isOwned) {
                                    AppSettings.equipGameItem(context, id); onPurchased()
                                }
                            }) {
                                Text(if (isOwned && AppSettings.equippedGameItem(context) == id) "مجهّز" else if (isOwned) "تجهيز" else "$price 💰")
                            }
                        }
                    }
                }
                Text("الألقاب التشجيعية", color = Color(0xFF17384D), fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text("اجمع الألقاب من متجر المغامرة واختر لقبك المفضل. الألقاب لا تؤثر على نجوم التعليم.", color = Color(0xFF5A7484), fontSize = 11.sp)
                val ownedTitles = gameOwnedTitles(context)
                val equippedTitle = equippedGameTitle(context)
                titles.forEach { (id, data) ->
                    val (name, price) = data
                    val isOwned = id in ownedTitles
                    Card(
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4D9))
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("🏅 $name", color = Color(0xFF17384D), fontSize = 16.sp, fontWeight = FontWeight.Black)
                                Text("لقب تشجيعي محفوظ في مقتنياتك", color = Color(0xFF5A7484), fontSize = 11.sp)
                            }
                            Button(onClick = {
                                if (!isOwned) {
                                    if (buyGameTitle(context, id, price)) onPurchased()
                                } else {
                                    equipGameTitle(context, id)
                                    onPurchased()
                                }
                            }) {
                                Text(if (isOwned && equippedTitle == id) "مجهّز" else if (isOwned) "تجهيز" else "$price 💰")
                            }
                        }
                    }
                }
                Text("الأزياء", color = Color(0xFF17384D), fontSize = 18.sp, fontWeight = FontWeight.Black)
                outfits.forEach { (id, data) ->
                    val (name, price) = data
                    val isOwned = id in ownedOutfits
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF3F7))) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(name, color = Color(0xFF17384D), fontSize = 16.sp, fontWeight = FontWeight.Black)
                                Text("زي KayKit ثلاثي الأبعاد — الشراء يجهزه مباشرة، والزي السابق يبقى في مقتنياتك.", color = Color(0xFF5A7484), fontSize = 11.sp)
                            }
                            Button(onClick = {
                                if (!isOwned) {
                                    if (AppSettings.buyGameOutfit(context, id, price)) { refreshOwnership(); onPurchased() }
                                } else {
                                    AppSettings.equipGameOutfit(context, id); onPurchased()
                                }
                            }) {
                                Text(if (isOwned && equippedOutfit == id) "مجهّز" else if (isOwned) "ارتداء" else "$price 💰")
                            }
                        }
                    }
                }
                Text("الأسلحة والدروع", color = Color(0xFF17384D), fontSize = 18.sp, fontWeight = FontWeight.Black)
                val equipment = listOf(
                    "sword_1handed" to ("سيف خفيف" to 800),
                    "sword_2handed" to ("سيف ثقيل" to 900),
                    "axe_1handed" to ("فأس قتالي" to 1000),
                    "dagger" to ("خنجر" to 1100),
                    "shield_round" to ("درع دائري" to 1200),
                    "shield_spikes" to ("درع الأشواك" to 1300)
                )
                val ownedEquipment = AppSettings.gameOwnedEquipment(context)
                val equippedEquipment = AppSettings.equippedGameEquipment(context)
                equipment.forEach { (id, data) ->
                    val (name, price) = data
                    val isOwned = id in ownedEquipment
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF3F7))) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(name, color = Color(0xFF17384D), fontSize = 16.sp, fontWeight = FontWeight.Black)
                                Text("معدات KayKit CC0 — تُحفظ في مقتنياتك وتظهر مع ريبو عند تجهيزها.", color = Color(0xFF5A7484), fontSize = 11.sp)
                            }
                            Button(onClick = {
                                if (!isOwned) {
                                    if (AppSettings.buyGameEquipment(context, id, price)) { refreshOwnership(); onPurchased() }
                                } else {
                                    AppSettings.equipGameEquipment(context, id); onPurchased()
                                }
                            }) {
                                Text(if (isOwned && equippedEquipment == id) "مجهّز" else if (isOwned) "تجهيز" else "$price 💰")
                            }
                        }
                    }
                }
                Text("مقتنياتي: ${ownedOutfits.size} أزياء • ${ownedTitles.size} ألقاب • عند تغيير التجهيز تبقى المقتنيات محفوظة.", color = Color(0xFF16806B), fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
@Composable
private fun LaneJoystick(lane: Int, onLaneChange: (Int) -> Unit) {
    var dragStartX by remember { mutableStateOf<Float?>(null) }
    Box(
        modifier = Modifier.size(108.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.09f))
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
        Box(Modifier.size(92.dp).clip(CircleShape).background(Color(0x3510E0D0)))
        Box(Modifier.size(82.dp).clip(CircleShape).background(Color(0xC00A1720)))
        Row(Modifier.align(Alignment.TopCenter).padding(top = 9.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(3) { index ->
                Box(Modifier.size(if (index == lane) 8.dp else 6.dp).clip(CircleShape).background(if (index == lane) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.3f)))
            }
        }
        Box(Modifier.size(52.dp).clip(CircleShape).background(Color(0xFFF1F7F9)), contentAlignment = Alignment.Center) {
            Text(when (lane) { 0 -> "←"; 2 -> "→"; else -> "↔" }, color = Color(0xFF183047), fontSize = 25.sp, fontWeight = FontWeight.Black)
        }
        Text("اسحب", Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp), color = Color.White.copy(alpha = 0.86f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ControlButton(label: String, caption: String, active: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(68.dp).clip(CircleShape).background(if (active) Color(0xFFFFD54F) else Color(0xB90A1720)).padding(3.dp), contentAlignment = Alignment.Center) {
            IconButton(onClick = onClick, modifier = Modifier.fillMaxSize().clip(CircleShape).background(if (active) Color(0xFFFFE27A) else Color.White.copy(alpha = 0.08f))) {
                Text(label, fontSize = 29.sp, color = if (active) Color(0xFF24324A) else Color.White, fontWeight = FontWeight.Black)
            }
        }
        Spacer(Modifier.height(3.dp))
        Text(caption, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
    }
}
