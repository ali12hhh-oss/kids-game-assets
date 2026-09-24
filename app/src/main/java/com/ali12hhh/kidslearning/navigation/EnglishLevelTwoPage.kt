package com.ali12hhh.kidslearning.navigation

import com.ali12hhh.kidslearning.navigation.ProLessonButton

import android.speech.tts.TextToSpeech
import com.ali12hhh.kidslearning.navigation.LessonSpeech
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

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
            Row(
                Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "رجوع") }
                Column(Modifier.weight(1f)) {
                    Text("الإنجليزية — المستوى الثاني", fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                    Text("تدريب الكتابة والتعرّف على الحروف والأرقام", fontSize = 12.sp, color = Color(0xFF5B6B82))
                }
            }
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).background(Color(0xFFEAF3FF))
        ) {
            TabRow(selectedTabIndex = section, containerColor = Color.White) {
                Tab(section == 0, { section = 0 }) {
                    Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✍️", fontSize = 22.sp); Text("كتابة الحروف", fontWeight = FontWeight.Bold)
                    }
                }
                Tab(section == 1, { section = 1 }) {
                    Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔢", fontSize = 22.sp); Text("كتابة الأرقام", fontWeight = FontWeight.Bold)
                    }
                }
            }
            if (section == 0) LetterWritingSection() else NumberWritingSection()
        }
    }
}

@Composable
private fun WritingBoard(
    guide: String,
    guideSize: androidx.compose.ui.unit.TextUnit,
    onClear: () -> Unit
) {
    val strokes = remember { mutableStateListOf<List<Offset>>() }

    Card(
        Modifier.fillMaxWidth().height(310.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF7)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Canvas(
            Modifier.fillMaxSize().padding(12.dp).pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Main)
                    down.consume()
                    var points = listOf(down.position)
                    var moved = false

                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Main)
                        val change = event.changes.firstOrNull() ?: break
                        if (!change.pressed) {
                            if (moved && points.size > 1) {
                                strokes.add(points)
                            } else {
                                strokes.add(listOf(change.position))
                            }
                            break
                        }
                        change.consume()
                        if (change.position != points.last()) {
                            moved = true
                            points = points + change.position
                        }
                    }
                }
            }
        ) {
            drawRect(Color(0xFFF8F0D8))
            drawLine(
                Color(0xFFD7C79D),
                Offset(0f, size.height * .78f),
                Offset(size.width, size.height * .78f),
                2f
            )
            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.argb(55, 50, 80, 130)
                    textSize = guideSize.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
                canvas.nativeCanvas.drawText(guide, size.width / 2f, size.height * .65f, paint)
            }

            strokes.forEach { points ->
                if (points.size == 1) {
                    drawCircle(Color(0xFF2456A6), 15f, points.first())
                } else {
                    val path = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        points.drop(1).forEach { lineTo(it.x, it.y) }
                    }
                    drawPath(
                        path,
                        Color(0xFF2456A6),
                        style = Stroke(width = 10f, cap = StrokeCap.Round)
                    )
                }
            }
        }
    }

    Spacer(Modifier.height(8.dp))
    ProLessonButton(
        onClick = {
            strokes.clear()
            onClear()
        },
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF476F))
    ) { Text("مسح", fontWeight = FontWeight.ExtraBold) }
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

    Column(Modifier.fillMaxSize().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("اكتب الحرف بنفسك", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
        Text("اختر الحرف الكبير أو الصغير ثم تتبّع الحرف على السبورة.", fontSize = 13.sp, color = Color(0xFF61728B), textAlign = TextAlign.Center)
        Row(Modifier.padding(vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = !upper, onClick = { upper = false }, label = { Text("صغير  a–z") })
            FilterChip(selected = upper, onClick = { upper = true }, label = { Text("كبير  A–Z") })
        }
        WritingBoard(if (upper) lesson.upper else lesson.lower, 190.sp, {})
        Spacer(Modifier.height(8.dp))
        Text("${lesson.upper} — ${lesson.word}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        Text(lesson.meaning, color = Color(0xFF53647A))
        ProLessonButton(onClick = { if (AppSettings.isSpeechEnabled(context)) LetterSpeech.speakEnglish(tts, lesson.lower, "letter_sound") }) {
            Text("🔊 اسمع الحرف والكلمة")
        }
        Row(Modifier.fillMaxWidth().navigationBarsPadding(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ProLessonButton(modifier = Modifier.weight(1f), enabled = index > 0, onClick = { index-- }) { Text("السابق") }
            ProLessonButton(modifier = Modifier.weight(1f), enabled = index < traceLetters.lastIndex, onClick = { index++ }) { Text("التالي") }
        }
        Text("${index + 1} / ${traceLetters.size}", fontWeight = FontWeight.Bold)
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
    Column(Modifier.fillMaxSize().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("اكتب الرقم بنفسك", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
        Text("تدرّب على كتابة الأرقام من 1 إلى 99 مع نطق الاسم بالإنجليزية.", fontSize = 13.sp, color = Color(0xFF61728B), textAlign = TextAlign.Center)
        Spacer(Modifier.height(10.dp))
        WritingBoard(number.toString(), 150.sp, {})
        Spacer(Modifier.height(8.dp))
        Text(arabicDigits(number), fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF2563EB))
        Text("$number — $name", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        ProLessonButton(onClick = { if (AppSettings.isSpeechEnabled(context)) tts?.speak(name, TextToSpeech.QUEUE_FLUSH, null, "number") }) {
            Text("🔊 اسمع الرقم")
        }
        Row(Modifier.fillMaxWidth().navigationBarsPadding(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ProLessonButton(modifier = Modifier.weight(1f), enabled = number > 1, onClick = { number-- }) { Text("السابق") }
            ProLessonButton(modifier = Modifier.weight(1f), enabled = number < 99, onClick = { number++ }) { Text("التالي") }
        }
        Text("${arabicDigits(number)} من ٩٩", fontWeight = FontWeight.Bold)
    }
}
