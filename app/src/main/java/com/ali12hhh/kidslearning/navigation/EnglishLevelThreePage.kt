package com.ali12hhh.kidslearning.navigation

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

private data class ColorItem(val name: String, val color: Color)
private data class ShapeItem(val name: String, val kind: Int)
private data class WordItem(val emoji: String, val word: String, val category: String)

private val colorItems = listOf(
    ColorItem("White", Color.White), ColorItem("Black", Color.Black),
    ColorItem("Blue", Color(0xFF1976D2)), ColorItem("Green", Color(0xFF2E7D32)),
    ColorItem("Orange", Color(0xFFFF8F00)), ColorItem("Yellow", Color(0xFFFFD600)),
    ColorItem("Brown", Color(0xFF795548)), ColorItem("Red", Color(0xFFD32F2F)),
    ColorItem("Purple", Color(0xFF7B1FA2))
)

private val shapeItems = listOf(
    ShapeItem("Square", 0), ShapeItem("Rectangle", 1),
    ShapeItem("Circle", 2), ShapeItem("Triangle", 3)
)

private val wordItems = listOf(
    WordItem("🌹", "Rose", "Plants"), WordItem("🌷", "Flower", "Plants"),
    WordItem("🌳", "Tree", "Plants"), WordItem("🌱", "Plant", "Plants"),
    WordItem("🌻", "Sunflower", "Plants"), WordItem("🍎", "Apple", "Fruits"),
    WordItem("🍌", "Banana", "Fruits"), WordItem("🍊", "Orange", "Fruits"),
    WordItem("🍇", "Grapes", "Fruits"), WordItem("🍓", "Strawberry", "Fruits"),
    WordItem("🍉", "Watermelon", "Fruits"), WordItem("🍍", "Pineapple", "Fruits"),
    WordItem("🥭", "Mango", "Fruits"), WordItem("🍋", "Lemon", "Fruits"),
    WordItem("🐱", "Cat", "Animals"), WordItem("🐶", "Dog", "Animals"),
    WordItem("🦁", "Lion", "Animals"), WordItem("🐯", "Tiger", "Animals"),
    WordItem("🐘", "Elephant", "Animals"), WordItem("🐰", "Rabbit", "Animals"),
    WordItem("🐻", "Bear", "Animals"), WordItem("🐼", "Panda", "Animals"),
    WordItem("🐵", "Monkey", "Animals"), WordItem("🦒", "Giraffe", "Animals"),
    WordItem("🐘", "Elephant", "Animals"), WordItem("🐦", "Bird", "Animals"),
    WordItem("🐟", "Fish", "Animals"), WordItem("🦋", "Butterfly", "Animals"),
    WordItem("🐝", "Bee", "Animals"), WordItem("🐢", "Turtle", "Animals"),
    WordItem("🚗", "Car", "Transport"), WordItem("🚆", "Train", "Transport"),
    WordItem("✈️", "Airplane", "Transport"), WordItem("🚌", "Bus", "Transport"),
    WordItem("🚲", "Bicycle", "Transport"), WordItem("🚢", "Ship", "Transport"),
    WordItem("🏠", "House", "Everyday"), WordItem("📚", "Book", "Everyday"),
    WordItem("✏️", "Pencil", "Everyday"), WordItem("⚽", "Ball", "Everyday"),
    WordItem("☀️", "Sun", "Nature"), WordItem("🌙", "Moon", "Nature"),
    WordItem("⭐", "Star", "Nature"), WordItem("☁️", "Cloud", "Nature"),
    WordItem("🌧️", "Rain", "Nature"), WordItem("🔥", "Fire", "Nature")
)

@Composable
fun EnglishLevelThreePage(onBack: () -> Unit, initialSection: Int = 0) {
    var section by remember { mutableIntStateOf(initialSection.coerceIn(0, 2)) }
    Scaffold(
        containerColor = Color(0xFFF4F8FF),
        topBar = {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Column(Modifier.weight(1f)) {
                    Text("English — Level 3", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Colors, shapes and useful words", fontSize = 12.sp, color = Color(0xFF63738A))
                }
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).background(Color(0xFFEAF3FF))) {
            TabRow(selectedTabIndex = section, containerColor = Color.White) {
                Tab(section == 0, { section = 0 }) { Text("🎨\nColors", Modifier.padding(12.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold) }
                Tab(section == 1, { section = 1 }) { Text("🔷\nShapes", Modifier.padding(12.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold) }
                Tab(section == 2, { section = 2 }) { Text("📚\nWords", Modifier.padding(12.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold) }
            }
            when (section) {
                0 -> ColorsSection()
                1 -> ShapesSection()
                else -> WordsSection()
            }
        }
    }
}

@Composable
private fun SpeechButton(text: String, tts: TextToSpeech?) {
    IconButton(onClick = { tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "level3") }) {
        Text("🔊", fontSize = 20.sp)
    }
}

@Composable
private fun ColorsSection() {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(context) {
        val engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.ENGLISH
                tts?.setSpeechRate(.82f)
            }
        }
        tts = engine
        onDispose { engine.stop(); engine.shutdown() }
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        items(colorItems) { item ->
            Card(
                onClick = { tts?.speak(item.name, TextToSpeech.QUEUE_FLUSH, null, "color") },
                modifier = Modifier.height(150.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(7.dp)
            ) {
                Column(Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Box(Modifier.size(70.dp).clip(RoundedCornerShape(18.dp)).background(item.color))
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(item.name, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        SpeechButton(item.name, tts)
                    }
                }
            }
        }
    }
}

@Composable
private fun ShapesSection() {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(context) {
        val engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.ENGLISH
                tts?.setSpeechRate(.82f)
            }
        }
        tts = engine
        onDispose { engine.stop(); engine.shutdown() }
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        items(shapeItems) { item ->
            Card(
                onClick = { tts?.speak(item.name, TextToSpeech.QUEUE_FLUSH, null, "shape") },
                modifier = Modifier.height(210.dp),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    ShapePreview(item.kind)
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(item.name, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        SpeechButton(item.name, tts)
                    }
                }
            }
        }
    }
}

@Composable
private fun ShapePreview(kind: Int) {
    Canvas(Modifier.size(105.dp)) {
        val fill = Color(0xFF4F8DFF)
        when (kind) {
            0 -> drawRect(fill)
            1 -> drawRect(fill, topLeft = Offset(size.width * .12f, size.height * .28f), size = Size(size.width * .76f, size.height * .44f))
            2 -> drawCircle(fill)
            3 -> {
                val path = Path().apply {
                    moveTo(size.width / 2f, 0f)
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }
                drawPath(path, fill)
            }
        }
    }
}

@Composable
private fun WordsSection() {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(context) {
        val engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.ENGLISH
                tts?.setSpeechRate(.78f)
            }
        }
        tts = engine
        onDispose { engine.stop(); engine.shutdown() }
    }
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Tap any picture to hear the English word", Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color(0xFF60728B), fontSize = 13.sp)
        Spacer(Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(wordItems) { item ->
                Card(
                    onClick = { tts?.speak(item.word, TextToSpeech.QUEUE_FLUSH, null, "word") },
                    modifier = Modifier.height(145.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(item.emoji, fontSize = 42.sp)
                        Text(item.word, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                        Text(item.category, fontSize = 10.sp, color = Color(0xFF72829A))
                    }
                }
            }
        }
    }
}
