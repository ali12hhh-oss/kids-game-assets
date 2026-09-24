package com.ali12hhh.kidslearning.navigation

import com.ali12hhh.kidslearning.navigation.ProLessonButton

import android.speech.tts.TextToSpeech
import com.ali12hhh.kidslearning.navigation.LessonSpeech
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

private data class ColorItem(val name: String, val arabic: String, val color: Color)
private data class ShapeItem(val name: String, val arabic: String, val kind: Int)
private data class WordItem(val emoji: String, val word: String, val arabic: String, val category: String)

private val colorItems = listOf(
    ColorItem("White", "أبيض", Color.White), ColorItem("Black", "أسود", Color.Black),
    ColorItem("Blue", "أزرق", Color(0xFF1976D2)), ColorItem("Green", "أخضر", Color(0xFF2E7D32)),
    ColorItem("Orange", "برتقالي", Color(0xFFFF8F00)), ColorItem("Yellow", "أصفر", Color(0xFFFFD600)),
    ColorItem("Brown", "بني", Color(0xFF795548)), ColorItem("Red", "أحمر", Color(0xFFD32F2F)),
    ColorItem("Purple", "بنفسجي", Color(0xFF7B1FA2))
)

private val shapeItems = listOf(
    ShapeItem("Square", "مربع", 0), ShapeItem("Rectangle", "مستطيل", 1),
    ShapeItem("Circle", "دائرة", 2), ShapeItem("Triangle", "مثلث", 3)
)

private val wordItems = listOf(
    WordItem("🌹", "Rose", "وردة", "Plants"), WordItem("🌷", "Flower", "زهرة", "Plants"),
    WordItem("🌳", "Tree", "شجرة", "Plants"), WordItem("🌱", "Plant", "نبات", "Plants"),
    WordItem("🌻", "Sunflower", "عباد الشمس", "Plants"), WordItem("🍎", "Apple", "تفاحة", "Fruits"),
    WordItem("🍌", "Banana", "موز", "Fruits"), WordItem("🍊", "Orange", "برتقال", "Fruits"),
    WordItem("🍇", "Grapes", "عنب", "Fruits"), WordItem("🍓", "Strawberry", "فراولة", "Fruits"),
    WordItem("🍉", "Watermelon", "بطيخ", "Fruits"), WordItem("🍍", "Pineapple", "أناناس", "Fruits"),
    WordItem("🥭", "Mango", "مانجو", "Fruits"), WordItem("🍋", "Lemon", "ليمون", "Fruits"),
    WordItem("🐱", "Cat", "قطة", "Animals"), WordItem("🐶", "Dog", "كلب", "Animals"),
    WordItem("🦁", "Lion", "أسد", "Animals"), WordItem("🐯", "Tiger", "نمر", "Animals"),
    WordItem("🐘", "Elephant", "فيل", "Animals"), WordItem("🐰", "Rabbit", "أرنب", "Animals"),
    WordItem("🐻", "Bear", "دب", "Animals"), WordItem("🐼", "Panda", "باندا", "Animals"),
    WordItem("🐵", "Monkey", "قرد", "Animals"), WordItem("🦒", "Giraffe", "زرافة", "Animals"),
    WordItem("🐴", "Horse", "حصان", "Animals"), WordItem("🐦", "Bird", "طائر", "Animals"),
    WordItem("🐟", "Fish", "سمكة", "Animals"), WordItem("🦋", "Butterfly", "فراشة", "Animals"),
    WordItem("🐝", "Bee", "نحلة", "Animals"), WordItem("🐢", "Turtle", "سلحفاة", "Animals"),
    WordItem("🚗", "Car", "سيارة", "Transport"), WordItem("🚆", "Train", "قطار", "Transport"),
    WordItem("✈️", "Airplane", "طائرة", "Transport"), WordItem("🚌", "Bus", "حافلة", "Transport"),
    WordItem("🚲", "Bicycle", "دراجة", "Transport"), WordItem("🚢", "Ship", "سفينة", "Transport"),
    WordItem("🏠", "House", "منزل", "Everyday"), WordItem("📚", "Book", "كتاب", "Everyday"),
    WordItem("✏️", "Pencil", "قلم رصاص", "Everyday"), WordItem("⚽", "Ball", "كرة", "Everyday"),
    WordItem("☀️", "Sun", "شمس", "Nature"), WordItem("🌙", "Moon", "قمر", "Nature"),
    WordItem("⭐", "Star", "نجمة", "Nature"), WordItem("☁️", "Cloud", "سحابة", "Nature"),
    WordItem("🌧️", "Rain", "مطر", "Nature"), WordItem("🔥", "Fire", "نار", "Nature")
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
                Tab(section == 0, { section = 0 }) { Text("🎨 Colors", Modifier.padding(12.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold) }
                Tab(section == 1, { section = 1 }) { Text("🔷 Shapes", Modifier.padding(12.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold) }
                Tab(section == 2, { section = 2 }) { Text("📚 Words", Modifier.padding(12.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold) }
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
    val context = LocalContext.current
    IconButton(onClick = { if (AppSettings.isSpeechEnabled(context)) tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "level3") }) {
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
                tts?.let { LessonSpeech.configure(it, LessonSpeech.ENGLISH_LOCALE) }
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
                onClick = { if (AppSettings.isSpeechEnabled(context)) tts?.speak(item.name, TextToSpeech.QUEUE_FLUSH, null, "color") },
                modifier = Modifier.height(150.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(7.dp)
            ) {
                Column(Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Box(Modifier.size(70.dp).clip(RoundedCornerShape(18.dp)).background(item.color))
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(item.name, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        Text(item.arabic, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF52657D))
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
                tts?.let { LessonSpeech.configure(it, LessonSpeech.ENGLISH_LOCALE) }
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
                onClick = { if (AppSettings.isSpeechEnabled(context)) tts?.speak(item.name, TextToSpeech.QUEUE_FLUSH, null, "shape") },
                modifier = Modifier.height(210.dp),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    ShapePreview(item.kind)
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(item.name, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        Text(item.arabic, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF52657D))
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
                tts?.let { LessonSpeech.configure(it, LessonSpeech.ENGLISH_LOCALE) }
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
                    onClick = { if (AppSettings.isSpeechEnabled(context)) tts?.speak(item.word, TextToSpeech.QUEUE_FLUSH, null, "word") },
                    modifier = Modifier.height(145.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(item.emoji, fontSize = 42.sp)
                        Text(item.word, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                        Text(item.arabic, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF52657D))
                        Text(item.category, fontSize = 10.sp, color = Color(0xFF72829A))
                    }
                }
            }
        }
    }
}
