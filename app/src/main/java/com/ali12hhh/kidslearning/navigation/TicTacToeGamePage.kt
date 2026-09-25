package com.ali12hhh.kidslearning.navigation

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.core.content.edit
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min

private const val XO_PREFS = "kids_learning_xo"
private const val XO_COINS = "coins"
private const val XO_WINS = "wins"
private const val XO_DRAWS = "draws"
private const val XO_LOSSES = "losses"
private const val XO_X_COLOR = "x_color"
private const val XO_EFFECT = "effect"
private const val XO_FLOOR = "floor"
private const val XO_OWNED_COLORS = "owned_colors"
private const val XO_OWNED_EFFECTS = "owned_effects"
private const val XO_OWNED_FLOORS = "owned_floors"
private const val XO_FRAME = "frame"
private const val XO_OWNED_FRAMES = "owned_frames"

private data class XoColorItem(val id: String, val name: String, val color: Color, val price: Int)
private data class XoEffectItem(val id: String, val name: String, val price: Int)
private data class XoFloorItem(val id: String, val name: String, val top: Color, val bottom: Color, val price: Int)
private data class XoFrameItem(val id: String, val name: String, val color: Color, val price: Int)

private val xoColors = listOf(
    XoColorItem("gold", "ذهبي ملكي", Color(0xFFFFC107), 0),
    XoColorItem("ruby", "ياقوتي", Color(0xFFFF365F), 20),
    XoColorItem("ocean", "أزرق محيطي", Color(0xFF20BFFF), 25),
    XoColorItem("violet", "بنفسجي ملكي", Color(0xFFA56BFF), 30),
    XoColorItem("mint", "نعناعي", Color(0xFF20D9A6), 35),
    XoColorItem("orange", "برتقالي ناري", Color(0xFFFF8A35), 40),
    XoColorItem("pink", "وردي لامع", Color(0xFFFF4FAF), 45),
    XoColorItem("ice", "جليدي", Color(0xFFB8F2FF), 55),
    XoColorItem("emerald", "زمردي", Color(0xFF31D17C), 65),
    XoColorItem("cosmic", "كوني", Color(0xFF8C7CFF), 80)
)

private val xoEffects = listOf(
    XoEffectItem("none", "كلاسيكي نظيف", 0),
    XoEffectItem("pulse", "نبضة طاقة", 35),
    XoEffectItem("spark", "شرارات ذهبية", 50),
    XoEffectItem("fire", "لهب ناري", 65),
    XoEffectItem("rainbow", "طيف قوس قزح", 80),
    XoEffectItem("electric", "كهرباء زرقاء", 95),
    XoEffectItem("orbit", "مدار نجمي", 110),
    XoEffectItem("starburst", "انفجار نجمي", 125)
)

private val xoFloors = listOf(
    XoFloorItem("classic", "كريستال أزرق", Color(0xFF071A2B), Color(0xFF1D5C83), 0),
    XoFloorItem("neon", "نيون بنفسجي", Color(0xFF13052A), Color(0xFF5A1887), 55),
    XoFloorItem("candy", "حلوى وردية", Color(0xFF54103F), Color(0xFFD94F8A), 65),
    XoFloorItem("space", "فضاء عميق", Color(0xFF02051A), Color(0xFF1E2F70), 75),
    XoFloorItem("ocean", "محيط مضيء", Color(0xFF023A46), Color(0xFF0799A5), 85),
    XoFloorItem("sunset", "غروب ناري", Color(0xFF3D102D), Color(0xFFE46C3C), 95),
    XoFloorItem("forest", "غابة زمردية", Color(0xFF071F18), Color(0xFF147A58), 110),
    XoFloorItem("royal", "ملكي داكن", Color(0xFF120A27), Color(0xFF49308A), 130)
)

private val xoFrames = listOf(
    XoFrameItem("steel", "فولاذ", Color(0xFFB9C8D6), 0),
    XoFrameItem("gold", "ذهب فاخر", Color(0xFFFFC107), 45),
    XoFrameItem("ruby", "ياقوت", Color(0xFFFF416C), 60),
    XoFrameItem("ice", "جليد", Color(0xFF9DEBFF), 70),
    XoFrameItem("emerald", "زمرد", Color(0xFF29D391), 85),
    XoFrameItem("carbon", "كربون", Color(0xFF384A5A), 100)
)

private fun xoPrefs(context: Context) =
    context.getSharedPreferences(XO_PREFS, Context.MODE_PRIVATE)

private fun ownedSet(context: Context, key: String, default: Set<String>) =
    xoPrefs(context).getStringSet(key, default)?.toSet() ?: default

private fun xoCoins(context: Context) = xoPrefs(context).getInt(XO_COINS, 0)

private fun addXoCoins(context: Context, amount: Int) {
    val prefs = xoPrefs(context)
    prefs.edit { putInt(XO_COINS, max(0, prefs.getInt(XO_COINS, 0) + amount)) }
}

private fun spendXoCoins(context: Context, price: Int): Boolean {
    val prefs = xoPrefs(context)
    val coins = prefs.getInt(XO_COINS, 0)
    if (coins < price) return false
    prefs.edit { putInt(XO_COINS, coins - price) }
    return true
}

private fun buyXoItem(context: Context, key: String, id: String, price: Int): Boolean {
    val owned = ownedSet(context, key, emptySet())
    if (id in owned) return true
    if (price > 0 && !spendXoCoins(context, price)) return false
    xoPrefs(context).edit { putStringSet(key, owned + id) }
    return true
}

private fun initialXoInventory(context: Context) {
    val prefs = xoPrefs(context)
    prefs.edit {
        putStringSet(XO_OWNED_COLORS, ownedSet(context, XO_OWNED_COLORS, emptySet()) + "gold")
        putStringSet(XO_OWNED_EFFECTS, ownedSet(context, XO_OWNED_EFFECTS, emptySet()) + "none")
        putStringSet(XO_OWNED_FLOORS, ownedSet(context, XO_OWNED_FLOORS, emptySet()) + "classic")
        putStringSet(XO_OWNED_FRAMES, ownedSet(context, XO_OWNED_FRAMES, emptySet()) + "steel")
    }
}

private fun xoSelectedColor(context: Context) = xoPrefs(context).getString(XO_X_COLOR, "gold") ?: "gold"
private fun xoSelectedEffect(context: Context) = xoPrefs(context).getString(XO_EFFECT, "none") ?: "none"
private fun xoSelectedFloor(context: Context) = xoPrefs(context).getString(XO_FLOOR, "classic") ?: "classic"
private fun xoSelectedFrame(context: Context) = xoPrefs(context).getString(XO_FRAME, "steel") ?: "steel"

private fun colorDistance(a: Color, b: Color): Float {
    val dr = a.red - b.red
    val dg = a.green - b.green
    val db = a.blue - b.blue
    return kotlin.math.sqrt(dr * dr + dg * dg + db * db)
}

private fun colorLuminance(color: Color): Float =
    color.red * 0.2126f + color.green * 0.7152f + color.blue * 0.0722f

private fun effectiveXoFloor(selected: XoFloorItem, xColor: Color, oColor: Color): XoFloorItem {
    val selectedAverage = Color(
        red = (selected.top.red + selected.bottom.red) * .5f,
        green = (selected.top.green + selected.bottom.green) * .5f,
        blue = (selected.top.blue + selected.bottom.blue) * .5f
    )
    val tooSimilarToX = colorDistance(selectedAverage, xColor) < .34f
    val tooSimilarToO = colorDistance(selectedAverage, oColor) < .34f
    val tooCloseInBrightness =
        kotlin.math.abs(colorLuminance(selectedAverage) - colorLuminance(xColor)) < .12f ||
            kotlin.math.abs(colorLuminance(selectedAverage) - colorLuminance(oColor)) < .12f
    if (!tooSimilarToX && !tooSimilarToO && !tooCloseInBrightness) return selected

    val candidates = listOf(
        XoFloorItem("auto_dark", "تباين تلقائي", Color(0xFF050914), Color(0xFF101D3D), 0),
        XoFloorItem("auto_gold", "تباين ذهبي", Color(0xFF171006), Color(0xFF4B2E08), 0),
        XoFloorItem("auto_plum", "تباين بنفسجي", Color(0xFF100817), Color(0xFF35164A), 0),
        XoFloorItem("auto_teal", "تباين فيروزي", Color(0xFF031A1C), Color(0xFF07545A), 0)
    )
    return candidates.maxBy { candidate ->
        val average = Color(
            red = (candidate.top.red + candidate.bottom.red) * .5f,
            green = (candidate.top.green + candidate.bottom.green) * .5f,
            blue = (candidate.top.blue + candidate.bottom.blue) * .5f
        )
        minOf(colorDistance(average, xColor), colorDistance(average, oColor))
    }
}

private fun chooseXo(context: Context, key: String, id: String) {
    xoPrefs(context).edit { putString(key, id) }
}

private val winningLines = listOf(
    intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8),
    intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8),
    intArrayOf(0, 4, 8), intArrayOf(2, 4, 6)
)

private fun winner(board: List<Char>): Char? {
    for (line in winningLines) {
        val a = board[line[0]]
        if (a != ' ' && a == board[line[1]] && a == board[line[2]]) return a
    }
    return null
}

private fun boardFull(board: List<Char>) = board.none { it == ' ' }

private fun minimax(board: MutableList<Char>, maximizing: Boolean): Int {
    when (winner(board)) {
        'O' -> return 10
        'X' -> return -10
    }
    if (boardFull(board)) return 0
    return if (maximizing) {
        var best = -100
        for (i in board.indices) if (board[i] == ' ') {
            board[i] = 'O'
            best = max(best, minimax(board, false))
            board[i] = ' '
        }
        best
    } else {
        var best = 100
        for (i in board.indices) if (board[i] == ' ') {
            board[i] = 'X'
            best = min(best, minimax(board, true))
            board[i] = ' '
        }
        best
    }
}

private fun bestDeviceMove(board: List<Char>): Int {
    var bestScore = -100
    var bestMove = -1
    for (i in board.indices) if (board[i] == ' ') {
        val next = board.toMutableList().also { it[i] = 'O' }
        val score = minimax(next, false)
        if (score > bestScore) {
            bestScore = score
            bestMove = i
        }
    }
    return if (bestMove >= 0) bestMove else board.indexOfFirst { it == ' ' }
}

private enum class XoScreen { GAME, SHOP, INVENTORY }

@Composable
fun TicTacToeGamePage(
    onBack: () -> Unit,
    startInShop: Boolean = false,
    startInInventory: Boolean = false,
    onShopBack: () -> Unit = onBack
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) { initialXoInventory(context) }
    var screen by remember {
        mutableStateOf(
            when {
                startInShop -> XoScreen.SHOP
                startInInventory -> XoScreen.INVENTORY
                else -> XoScreen.GAME
            }
        )
    }
    var refresh by remember { mutableIntStateOf(0) }

    when (screen) {
        XoScreen.GAME -> XoGame(context, refresh, { screen = XoScreen.SHOP }, { screen = XoScreen.INVENTORY }, onBack)
        XoScreen.SHOP -> XoShop(context, refresh, { refresh++ }) { onShopBack() }
        XoScreen.INVENTORY -> XoInventory(context, refresh, { refresh++ }) { onShopBack() }
    }
}

@Composable
private fun XoGame(context: Context, refreshKey: Int, onShop: () -> Unit, onInventory: () -> Unit, onBack: () -> Unit) {
    val selectedColor = xoColors.firstOrNull { it.id == xoSelectedColor(context) } ?: xoColors.first()
    val selectedEffect = xoSelectedEffect(context)
    val selectedFloor = xoFloors.firstOrNull { it.id == xoSelectedFloor(context) } ?: xoFloors.first()
    val selectedFrame = xoFrames.firstOrNull { it.id == xoSelectedFrame(context) } ?: xoFrames.first()
    val oColor = Color(0xFF00E5FF)
    val displayFloor = effectiveXoFloor(selectedFloor, selectedColor.color, oColor)
    var board by remember(refreshKey) { mutableStateOf(List(9) { ' ' }) }
    var turn by remember(refreshKey) { mutableStateOf('X') }
    var result by remember(refreshKey) { mutableStateOf<Char?>(null) }
    var thinking by remember(refreshKey) { mutableStateOf(false) }
    var winningCells by remember(refreshKey) { mutableStateOf(emptySet<Int>()) }
    fun finishIfNeeded(next: List<Char>): Boolean {
        val win = winner(next)
        if (win != null) {
            result = win
            winningCells = winningLines.first { line -> line.all { next[it] == win } }.toSet()
            val p = xoPrefs(context)
            if (win == 'X') {
                addXoCoins(context, 10)
                p.edit { putInt(XO_WINS, p.getInt(XO_WINS, 0) + 1) }
            } else p.edit { putInt(XO_LOSSES, p.getInt(XO_LOSSES, 0) + 1) }
            return true
        }
        if (boardFull(next)) {
            result = 'D'
            val p = xoPrefs(context)
            p.edit { putInt(XO_DRAWS, p.getInt(XO_DRAWS, 0) + 1) }
            return true
        }
        return false
    }
    fun playerMove(index: Int) {
        if (turn != 'X' || result != null || thinking || board[index] != ' ') return
        val next = board.toMutableList().also { it[index] = 'X' }
        board = next
        if (!finishIfNeeded(next)) { turn = 'O'; thinking = true }
    }
    LaunchedEffect(board, turn, thinking, result) {
        if (turn == 'O' && thinking && result == null) {
            delay(420)
            val move = bestDeviceMove(board)
            if (move >= 0) {
                val next = board.toMutableList().also { it[move] = 'O' }
                board = next
                thinking = false
                if (!finishIfNeeded(next)) turn = 'X'
            }
        }
    }
    val status = when (result) {
        'X' -> "أحسنت! فزت +10 💰"
        'O' -> "ريبو فاز هذه الجولة"
        'D' -> "تعادل رائع! 🤝"
        else -> if (thinking) "ريبو يفكر..." else "دورك — اختر مكانك"
    }
    val wins = xoPrefs(context).getInt(XO_WINS, 0)
    val draws = xoPrefs(context).getInt(XO_DRAWS, 0)
    val losses = xoPrefs(context).getInt(XO_LOSSES, 0)
    Surface(Modifier.fillMaxSize(), color = displayFloor.top) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(displayFloor.top, displayFloor.bottom)))) {
            XoAmbientEffect(selectedEffect, selectedColor.color)
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Text("‹", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Black)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("لعبة XO", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Black)
                        Text("× أنت  •  O ريبو", color = Color.White.copy(alpha = .82f), fontSize = 11.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("💰 " + xoCoins(context), color = Color(0xFFFFD54F), fontSize = 16.sp, fontWeight = FontWeight.Black)
                        Text("ذهب XO", color = Color.White.copy(alpha = .72f), fontSize = 9.sp)
                    }
                }
                Spacer(Modifier.height(7.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    XoScoreCard("أنت", "×", wins, selectedColor.color, Modifier.weight(1f))
                    XoScoreCard("تعادل", "•", draws, Color.White, Modifier.weight(1f))
                    XoScoreCard("ريبو", "O", losses, Color(0xFFFF6B8A), Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                XoBoard(
                    board = board,
                    xColor = selectedColor.color,
                    oColor = oColor,
                    frameColor = selectedFrame.color,
                    winningCells = winningCells,
                    effect = selectedEffect,
                    onMove = ::playerMove,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(horizontal = 2.dp)
                )
                Spacer(Modifier.height(10.dp))
                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xD90A1826))
                ) {
                    Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(status, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
                        Text("× أنت  مقابل  O ريبو", color = Color.White.copy(alpha = .68f), fontSize = 10.sp)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { board = List(9) { ' ' }; turn = 'X'; result = null; thinking = false; winningCells = emptySet() },
                        modifier = Modifier.weight(1f),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF1FA774), contentColor = Color.White)
                    ) {
                        Text("↻ جولة جديدة", fontWeight = FontWeight.Black)
                    }
                    Button(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFE45A4F), contentColor = Color.White)
                    ) {
                        Text("رجوع", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun XoBoard(
    board: List<Char>,
    xColor: Color,
    oColor: Color,
    frameColor: Color,
    winningCells: Set<Int>,
    effect: String,
    onMove: (Int) -> Unit,
    modifier: Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = .10f)),
        border = androidx.compose.foundation.BorderStroke(2.dp, frameColor.copy(alpha = .72f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) { col ->
                        val index = row * 3 + col
                        XoCell(
                            value = board[index],
                            xColor = xColor,
                            oColor = oColor,
                            frameColor = frameColor,
                            isWinning = index in winningCells,
                            effect = effect,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            onClick = { onMove(index) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun XoScoreCard(label: String, symbol: String, score: Int, color: Color, modifier: Modifier) {
    Card(modifier, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = .11f))) {
        Column(Modifier.fillMaxWidth().padding(vertical = 7.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(symbol, color = color, fontSize = 23.sp, fontWeight = FontWeight.Black)
            Text(label, color = Color.White.copy(alpha = .78f), fontSize = 10.sp)
            Text(score.toString(), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun XoCell(
    value: Char,
    xColor: Color,
    oColor: Color,
    frameColor: Color,
    isWinning: Boolean,
    effect: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val scale = remember { Animatable(1f) }
    LaunchedEffect(value) {
        if (value != ' ') {
            scale.snapTo(.72f)
            scale.animateTo(1f, tween(220))
        }
    }
    val transition = rememberInfiniteTransition(label = "cellPulse")
    val pulse by transition.animateFloat(
        .94f, 1.06f,
        infiniteRepeatable(tween(550), RepeatMode.Reverse),
        label = "pulse"
    )
    val top = if (isWinning) Color.White.copy(alpha = .24f) else Color.White.copy(alpha = .13f)
    val bottom = if (isWinning) Color.White.copy(alpha = .12f) else Color.White.copy(alpha = .055f)

    Box(
        modifier
            .scale(scale.value * if (isWinning && effect == "pulse") pulse else 1f)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.verticalGradient(listOf(top, bottom)))
            .border(
                width = if (isWinning) 2.5.dp else 1.5.dp,
                color = if (isWinning) Color(0xFFFFD54F) else frameColor.copy(alpha = .58f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(enabled = value == ' ', onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        when (value) {
            'X' -> Text("×", color = xColor, fontSize = 62.sp, fontWeight = FontWeight.Black)
            'O' -> Text("○", color = oColor, fontSize = 60.sp, fontWeight = FontWeight.Black)
        }
        if (isWinning && effect == "spark") {
            Text(
                "✦",
                color = Color(0xFFFFD54F),
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(7.dp)
            )
        }
    }
}

@Composable
private fun XoAmbientEffect(effect: String, color: Color) {
    if (effect == "none") return
    val transition = rememberInfiniteTransition(label = "xoEffect")
    val alpha by transition.animateFloat(.05f, .20f, infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "alpha")
    Canvas(Modifier.fillMaxSize()) {
        drawCircle(color.copy(alpha = alpha), size.minDimension * .34f, center = androidx.compose.ui.geometry.Offset(size.width*.5f, size.height*.40f))
        when (effect) {
            "spark", "starburst" -> repeat(10) { i -> drawCircle(Color(0xFFFFD54F).copy(alpha=alpha), 4f, androidx.compose.ui.geometry.Offset(size.width*(.1f+i*.08f), size.height*(.2f+(i%4)*.13f))) }
            "fire" -> repeat(8) { i -> drawCircle(Color(0xFFFF6A2A).copy(alpha=alpha), 8f, androidx.compose.ui.geometry.Offset(size.width*(.25f+i*.07f), size.height*(.72f-(i%3)*.04f))) }
            "rainbow" -> { drawCircle(Color(0xFFFF4F72).copy(alpha=alpha),26f,androidx.compose.ui.geometry.Offset(size.width*.18f,size.height*.25f)); drawCircle(Color(0xFF20BFFF).copy(alpha=alpha),20f,androidx.compose.ui.geometry.Offset(size.width*.82f,size.height*.30f)); drawCircle(Color(0xFF20D9A6).copy(alpha=alpha),14f,androidx.compose.ui.geometry.Offset(size.width*.5f,size.height*.18f)) }
            "electric" -> repeat(7) { i -> drawCircle(Color(0xFF55C7FF).copy(alpha=alpha),4f,androidx.compose.ui.geometry.Offset(size.width*(.08f+i*.14f),size.height*(.22f+(i%2)*.12f))) }
            "orbit" -> { drawCircle(Color(0xFFFFD54F).copy(alpha=alpha),7f,androidx.compose.ui.geometry.Offset(size.width*.25f,size.height*.34f)); drawCircle(Color(0xFFA56BFF).copy(alpha=alpha),5f,androidx.compose.ui.geometry.Offset(size.width*.75f,size.height*.46f)) }
        }
    }
}

@Composable
private fun XoShop(context: Context, refreshKey: Int, onChanged: () -> Unit, onBack: () -> Unit) {
    val coins = xoCoins(context)
    val ownedColors = ownedSet(context, XO_OWNED_COLORS, setOf("gold"))
    val ownedEffects = ownedSet(context, XO_OWNED_EFFECTS, setOf("none"))
    val ownedFloors = ownedSet(context, XO_OWNED_FLOORS, setOf("classic"))
    val ownedFrames = ownedSet(context, XO_OWNED_FRAMES, setOf("steel"))
    var tab by remember(refreshKey) { mutableIntStateOf(0) }
    Box(Modifier.fillMaxSize().background(Color(0xFF06111F)), contentAlignment = Alignment.Center) {
        Card(Modifier.fillMaxWidth(.95f).fillMaxSize(.96f), shape = RoundedCornerShape(32.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF5FAFE))) {
            Column(Modifier.fillMaxSize().padding(17.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("متجر XO 3D", color = Color(0xFF102B3E), fontSize = 28.sp, fontWeight = FontWeight.Black)
                        Text("💰 " + coins + " • كل فوز = +10", color = Color(0xFFB77900), fontSize = 13.sp, fontWeight = FontWeight.Black)
                    }
                    IconButton(onClick = onBack) { Text("✕", fontSize = 24.sp, color = Color(0xFF17384D)) }
                }
                Text("الألوان والتأثيرات والأرضيات والإطارات مرتبطة فعليًا بالساحة ثلاثية الأبعاد.", color = Color(0xFF5A7484), fontSize = 12.sp)
                Spacer(Modifier.height(7.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("× ألوان", "✨ تأثيرات", "▦ أرضيات", "⬡ إطارات").forEachIndexed { index, title ->
                        Button(
                            onClick = { tab = index },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 2.dp, vertical = 0.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = if (tab == index) Color(0xFF4D72E8) else Color(0xFFE7EFF6),
                                contentColor = if (tab == index) Color.White else Color(0xFF17384D)
                            )
                        ) {
                            Text(
                                title,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                Spacer(Modifier.height(7.dp))
                Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()), verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    when(tab) {
                        0 -> xoColors.forEach { item -> XoShopItem(item.name,"لون × 3D",item.price,item.id in ownedColors,item.id==xoSelectedColor(context),item.color) { if(item.id !in ownedColors && !buyXoItem(context,XO_OWNED_COLORS,item.id,item.price)) return@XoShopItem; chooseXo(context,XO_X_COLOR,item.id); onChanged() } }
                        1 -> xoEffects.forEach { item -> XoShopItem(item.name,"تأثير 3D",item.price,item.id in ownedEffects,item.id==xoSelectedEffect(context),Color(0xFF8D70FF)) { if(item.id !in ownedEffects && !buyXoItem(context,XO_OWNED_EFFECTS,item.id,item.price)) return@XoShopItem; chooseXo(context,XO_EFFECT,item.id); onChanged() } }
                        2 -> xoFloors.forEach { item -> XoShopItem(item.name,"أرضية 3D",item.price,item.id in ownedFloors,item.id==xoSelectedFloor(context),item.top) { if(item.id !in ownedFloors && !buyXoItem(context,XO_OWNED_FLOORS,item.id,item.price)) return@XoShopItem; chooseXo(context,XO_FLOOR,item.id); onChanged() } }
                        else -> xoFrames.forEach { item -> XoShopItem(item.name,"إطار 3D",item.price,item.id in ownedFrames,item.id==xoSelectedFrame(context),item.color) { if(item.id !in ownedFrames && !buyXoItem(context,XO_OWNED_FRAMES,item.id,item.price)) return@XoShopItem; chooseXo(context,XO_FRAME,item.id); onChanged() } }
                    }
                }
                Text("🎒 العناصر المملوكة محفوظة وتُطبّق فورًا داخل اللعبة.",color=Color(0xFF16806B),fontSize=12.sp,fontWeight=FontWeight.Bold,textAlign=TextAlign.Center,modifier=Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun XoShopItem(name: String, kind: String, price: Int, owned: Boolean, selected: Boolean, previewColor: Color, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = if (selected) Color(0xFFE4F8F2) else Color(0xFFEAF3F7))) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(48.dp).clip(RoundedCornerShape(15.dp)).background(previewColor), contentAlignment = Alignment.Center) {
                Text(if (kind.contains("لون")) "×" else if (kind.contains("تأثير")) "✦" else "▦", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black)
            }
            Column(Modifier.weight(1f)) {
                Text(name, color = Color(0xFF17384D), fontSize = 16.sp, fontWeight = FontWeight.Black)
                Text(kind, color = Color(0xFF5A7484), fontSize = 11.sp)
            }
            Text(if (selected) "✓ مطبّق" else if (owned) "تطبيق" else "$price 💰", color = if (selected) Color(0xFF16806B) else Color(0xFFB77900), fontWeight = FontWeight.Black, fontSize = 12.sp)
        }
    }
}

@Composable
private fun XoInventory(context: Context, refreshKey: Int, onChanged: () -> Unit, onBack: () -> Unit) {
    val ownedColors=ownedSet(context,XO_OWNED_COLORS,setOf("gold"))
    val ownedEffects=ownedSet(context,XO_OWNED_EFFECTS,setOf("none"))
    val ownedFloors=ownedSet(context,XO_OWNED_FLOORS,setOf("classic"))
    val ownedFrames=ownedSet(context,XO_OWNED_FRAMES,setOf("steel"))
    Box(Modifier.fillMaxSize().background(Color(0xFF06111F)),contentAlignment=Alignment.Center) {
        Card(Modifier.fillMaxWidth(.95f).fillMaxSize(.96f),shape=RoundedCornerShape(32.dp),colors=CardDefaults.cardColors(containerColor=Color(0xFFF5FAFE))) {
            Column(Modifier.fillMaxSize().padding(17.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
                    Column { Text("🎒 مقتنياتي 3D",color=Color(0xFF102B3E),fontSize=28.sp,fontWeight=FontWeight.Black); Text("💰 "+xoCoins(context)+" عملة ذهبية",color=Color(0xFFB77900),fontWeight=FontWeight.Black) }
                    IconButton(onClick=onBack){Text("✕",fontSize=24.sp)}
                }
                Text("كل ما تملكه يبقى محفوظًا. اضغط أي عنصر لتطبيقه فورًا.",color=Color(0xFF5A7484),fontSize=12.sp)
                InventorySection("ألوان ×",ownedColors,xoColors.map{it.id to it.name}){id->chooseXo(context,XO_X_COLOR,id);onChanged()}
                InventorySection("التأثيرات",ownedEffects,xoEffects.map{it.id to it.name}){id->chooseXo(context,XO_EFFECT,id);onChanged()}
                InventorySection("أرضيات 3D",ownedFloors,xoFloors.map{it.id to it.name}){id->chooseXo(context,XO_FLOOR,id);onChanged()}
                InventorySection("إطارات 3D",ownedFrames,xoFrames.map{it.id to it.name}){id->chooseXo(context,XO_FRAME,id);onChanged()}
                Text("المجموع: "+(ownedColors.size+ownedEffects.size+ownedFloors.size+ownedFrames.size)+" مقتنى",color=Color(0xFF16806B),fontWeight=FontWeight.Black,textAlign=TextAlign.Center,modifier=Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun InventorySection(title: String, owned: Set<String>, all: List<Pair<String, String>>, onApply: (String) -> Unit) {
    Text(title, color = Color(0xFF17384D), fontSize = 18.sp, fontWeight = FontWeight.Black)
    all.filter { it.first in owned }.forEach { (id, name) ->
        Card(Modifier.fillMaxWidth().clickable { onApply(id) }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF3F7))) {
            Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(name, color = Color(0xFF17384D), fontWeight = FontWeight.Bold)
                Text("تطبيق", color = Color(0xFF16806B), fontWeight = FontWeight.Black)
            }
        }
    }
}
