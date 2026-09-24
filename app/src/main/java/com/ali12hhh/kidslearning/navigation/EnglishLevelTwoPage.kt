package com.ali12hhh.kidslearning.navigation

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.awaitPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class TraceLetter(val lower: String, val upper: String, val word: String, val meaning: String)

private val traceLetters = listOf(
    TraceLetter("a", "A", "Apple", "تفاحة"), TraceLetter("b", "B", "Ball", "كرة"),
    TraceLetter("c", "C", "Cat", "قطة"), TraceLetter("d", "D", "Dog", "كلب"),
    TraceLetter("e", "E", "Egg", "بيضة"), TraceLetter("f", "F", "Fish", "سمكة"),
    TraceLetter("g", "G", "Goat", "ماعز"), TraceLetter("h", "H", "Hat", "قبعة"),
    TraceLetter("i", "I", "Ice", "ثلج"), TraceLetter("j", "J", "Juice", "عصير"),
    TraceLetter("k", "K", "Kite", "طائرة ورقية"), TraceLetter("l", "L", "Lion", "أسد"),
    TraceLetter("m", "M", "Moon", "قمر"), TraceLetter("n", "N", "Nose", "أنف"),
    TraceLetter("o", "O", "Orange", "برتقالة"), TraceLetter("p", "P", "Pen", "قلم"),
    TraceLetter("q", "Q", "Queen", "ملكة"), TraceLetter("r", "R", "Rabbit", "أرنب"),
    TraceLetter("s", "S", "Sun", "شمس"), TraceLetter("t", "T", "Tree", "شجرة"),
    TraceLetter("u", "U", "Umbrella", "مظلة"), TraceLetter("v", "V", "Van", "سيارة فان"),
    TraceLetter("w", "W", "Whale", "حوت"), TraceLetter("x", "X", "Xylophone", "إكسيلوفون"),
    TraceLetter("y", "Y", "Yo-yo", "يويو"), TraceLetter("z", "Z", "Zebra", "حمار وحشي")
)

private fun numberName(n: Int): String {
    val ones = listOf("", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
    val teens = listOf("ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen")
    val tens = listOf("", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety")
    return when {
        n < 10 -> ones[n]
        n < 20 -> teens[n - 10]
        n % 10 == 0 -> tens[n / 10]
        else -> "${tens[n / 10]}-${ones[n % 10]}"
    }
}

private fun arabicDigits(n: Int): String =
    n.toString().map { if (it in '0'..'9') ('٠'.code + it.code - '0'.code).toChar() else it }.joinToString("")

@Composable
fun EnglishLevelTwoPage(onBack: () -> Unit, initialSection: Int = 0) {
    var section by remember { mutableIntStateOf(initialSection.coerceIn(0, 1)) }

    Scaffold(
        containerColor = Color(0xFFF4F8FF),
        topBar = {
            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "رجوع") }
                Column(Modifier.weight(1f)) {
                    Text("الإنجليزية — المستوى الثاني", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    Text("تدريب الكتابة والتعرّف على الحروف والأرقام", fontSize = 11.sp, color = Color(0xFF5B6B82))
                }
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).background(Color(0xFFEAF3FF))) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ChoiceCard(Modifier.weight(1f), section == 0, Icons.Default.Edit, "كتابة الحروف", Color(0xFF4F7CFF)) { section = 0 }
                ChoiceCard(Modifier.weight(1f), section == 1, null, "كتابة الأرقام", Color(0xFFFF8A4C)) { section = 1 }
            }
            if (section == 0) LetterWritingSection() else NumberWritingSection()
        }
    }
}

@Composable
private fun ChoiceCard(modifier: Modifier, selected: Boolean, icon: androidx.compose.ui.graphics.vector.ImageVector?, title: String, color: Color, onClick: () -> Unit) {
    Surface(modifier = modifier.height(58.dp), onClick = onClick, shape = RoundedCornerShape(18.dp), color = if (selected) color else Color.White, shadowElevation = if (selected) 5.dp else 2.dp) {
        Row(Modifier.fillMaxSize().padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            if (icon != null) Icon(icon, contentDescription = null, tint = if (selected) Color.White else color) else Text("123", fontSize = 14.sp, fontWeight = FontWeight.Black, color = if (selected) Color.White else color)
            Spacer(Modifier.width(6.dp))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = if (selected) Color.White else Color(0xFF27364D))
        }
    }
}

@Composable
private fun WritingBoard(guide: String, guideSize: androidx.compose.ui.unit.TextUnit, modifier: Modifier = Modifier, onClear: () -> Unit) {
    val strokes = remember { mutableStateListOf<List<Offset>>() }
    var currentStroke by remember { mutableStateOf<List<Offset>>(emptyList()) }

    Card(modifier.fillMaxWidth().padding(horizontal = 2.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF7)), elevation = CardDefaults.cardElevation(7.dp)) {
        Canvas(Modifier.fillMaxSize().padding(10.dp).pointerInput(Unit) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Main)
                var points = listOf(down.position)
                currentStroke = points
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Main)
                    val change = event.changes.firstOrNull() ?: break
                    if (change.pressed) {
                        val point = change.position
                        if (point != points.last()) {
                            points = points + point
                            currentStroke = points
                        }
                    } else {
                        if (points.isNotEmpty()) strokes.add(points)
                        currentStroke = emptyList()
                        break
                    }
                }
            }
        }) {
            drawRect(Color(0xFFF8F0D8))
            drawLine(Color(0xFFD7C79D), Offset(0f, size.height * .78f), Offset(size.width, size.height * .78f), 2f)
            val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.argb(55, 50, 80, 130)
                textSize = guideSize.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            drawIntoCanvas { it.nativeCanvas.drawText(guide, size.width / 2f, size.height * .65f, paint) }

            fun drawStroke(points: List<Offset>) {
                if (points.size == 1) drawCircle(Color(0xFF2456A6), 15f, points.first())
                else if (points.isNotEmpty()) {
                    val path = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        points.drop(1).forEach { lineTo(it.x, it.y) }
                    }
                    drawPath(path, Color(0xFF2456A6), style = Stroke(width = 10f, cap = StrokeCap.Round))
                }
            }
            strokes.forEach(::drawStroke)
            drawStroke(currentStroke)
        }
    }

    Spacer(Modifier.height(6.dp))
    ProLessonButton(modifier = Modifier.height(40.dp), onClick = { strokes.clear(); currentStroke = emptyList(); onClear() }, shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF476F))) {
        Text("✕", fontSize = 18.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.width(5.dp))
        Text("مسح", fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun LetterWritingSection() {
    val context = LocalContext.current
    var index by remember { mutableIntStateOf(0) }
    var upper by remember { mutableStateOf(true) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    val lesson = traceLetters[index]

    DisposableEffect(context) {
        var engineRef: TextToSpeech? = null
        val engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                engineRef?.let { LessonSpeech.configure(it, LessonSpeech.ENGLISH_LOCALE) }
                engineRef?.setPitch(1f)
            }
        }
        engineRef = engine
        tts = engine
        onDispose { engine.stop(); engine.shutdown() }
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("اكتب الحرف بنفسك", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Row(Modifier.padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(!upper, { upper = false }, label = { Text("صغير a–z", fontSize = 12.sp) }, leadingIcon = { Text("a", fontWeight = FontWeight.Black) })
            FilterChip(upper, { upper = true }, label = { Text("كبير A–Z", fontSize = 12.sp) }, leadingIcon = { Icon(Icons.Default.Edit, null) })
        }
        WritingBoard(if (upper) lesson.upper else lesson.lower, 150.sp, modifier = Modifier.fillMaxWidth().weight(1f), onClear = {})
        Row(Modifier.fillMaxWidth().padding(top = 6.dp).navigationBarsPadding(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ProLessonButton(modifier = Modifier.weight(1f).height(46.dp), enabled = index > 0, onClick = { index-- }) { Text("‹  السابق", fontWeight = FontWeight.ExtraBold) }
            ProLessonButton(modifier = Modifier.weight(1f).height(46.dp), enabled = index < traceLetters.lastIndex, onClick = { index++ }) { Text("التالي  ›", fontWeight = FontWeight.ExtraBold) }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${lesson.upper} — ${lesson.word}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            IconButton(onClick = { if (AppSettings.isSpeechEnabled(context)) LetterSpeech.speakEnglish(tts, lesson.lower, "letter_sound") }) { Text("🔊", fontSize = 18.sp) }
            Text("${index + 1}/${traceLetters.size}", fontSize = 12.sp, color = Color(0xFF61728B))
        }
    }
}

@Composable
private fun NumberWritingSection() {
    val context = LocalContext.current
    var number by remember { mutableIntStateOf(1) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(context) {
        var engineRef: TextToSpeech? = null
        val engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                engineRef?.let { LessonSpeech.configure(it, LessonSpeech.ENGLISH_LOCALE) }
                engineRef?.setPitch(1f)
            }
        }
        engineRef = engine
        tts = engine
        onDispose { engine.stop(); engine.shutdown() }
    }

    val name = numberName(number)
    Column(Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("اكتب الرقم بنفسك", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Text("تدرّب على 1 إلى 99", fontSize = 12.sp, color = Color(0xFF61728B))
        WritingBoard(number.toString(), 135.sp, modifier = Modifier.fillMaxWidth().weight(1f), onClear = {})
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Text(arabicDigits(number), fontSize = 21.sp, fontWeight = FontWeight.Black, color = Color(0xFF2563EB))
            Spacer(Modifier.width(8.dp))
            Text("$number — $name", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
            IconButton(onClick = { if (AppSettings.isSpeechEnabled(context)) tts?.speak(name, TextToSpeech.QUEUE_FLUSH, null, "number") }) { Text("🔊", fontSize = 18.sp) }
        }
        Row(Modifier.fillMaxWidth().padding(top = 6.dp).navigationBarsPadding(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ProLessonButton(modifier = Modifier.weight(1f).height(46.dp), enabled = number > 1, onClick = { number-- }) { Text("‹  السابق", fontWeight = FontWeight.ExtraBold) }
            ProLessonButton(Modifier.weight(1f).height(46.dp), enabled = number < 99, onClick = { number++ }) { Text("التالي  ›", fontWeight = FontWeight.ExtraBold) }
        }
        Text("${arabicDigits(number)} من ٩٩", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF61728B))
    }
}
