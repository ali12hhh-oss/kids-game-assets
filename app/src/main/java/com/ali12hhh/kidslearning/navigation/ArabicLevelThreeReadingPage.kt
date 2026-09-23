package com.ali12hhh.kidslearning.navigation

import android.speech.tts.TextToSpeech
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.min

private data class TwoLetterLesson(val first: String, val second: String, val result: String, val example: String)

private val levelThreeLessons = listOf(
    TwoLetterLesson("د", "ا", "دا", "د + ا = دا"),
    TwoLetterLesson("د", "و", "دو", "د + و = دو"),
    TwoLetterLesson("د", "ي", "دي", "د + ي = دي"),
    TwoLetterLesson("ب", "ا", "با", "ب + ا = با"),
    TwoLetterLesson("ب", "و", "بو", "ب + و = بو"),
    TwoLetterLesson("ب", "ي", "بي", "ب + ي = بي"),
    TwoLetterLesson("ت", "ا", "تا", "ت + ا = تا"),
    TwoLetterLesson("ت", "و", "تو", "ت + و = تو"),
    TwoLetterLesson("ت", "ي", "تي", "ت + ي = تي"),
    TwoLetterLesson("م", "ا", "ما", "م + ا = ما"),
    TwoLetterLesson("م", "و", "مو", "م + و = مو"),
    TwoLetterLesson("م", "ي", "مي", "م + ي = مي"),
    TwoLetterLesson("س", "ا", "سا", "س + ا = سا"),
    TwoLetterLesson("س", "و", "سو", "س + و = سو"),
    TwoLetterLesson("س", "ي", "سي", "س + ي = سي"),
    TwoLetterLesson("ن", "ا", "نا", "ن + ا = نا"),
    TwoLetterLesson("ن", "و", "نو", "ن + و = نو"),
    TwoLetterLesson("ن", "ي", "ني", "ن + ي = ني"),
    TwoLetterLesson("ل", "ا", "لا", "ل + ا = لا"),
    TwoLetterLesson("ل", "و", "لو", "ل + و = لو"),
    TwoLetterLesson("ل", "ي", "لي", "ل + ي = لي"),
    TwoLetterLesson("ر", "ا", "را", "ر + ا = را"),
    TwoLetterLesson("ر", "و", "رو", "ر + و = رو"),
    TwoLetterLesson("ر", "ي", "ري", "ر + ي = ري"),
    TwoLetterLesson("ك", "ا", "كا", "ك + ا = كا"),
    TwoLetterLesson("ك", "و", "كو", "ك + و = كو"),
    TwoLetterLesson("ك", "ي", "كي", "ك + ي = كي"),
    TwoLetterLesson("ف", "ا", "فا", "ف + ا = فا"),
    TwoLetterLesson("ف", "و", "فو", "ف + و = فو"),
    TwoLetterLesson("ف", "ي", "في", "ف + ي = في")
)

private enum class LevelThreeReadingTab { LEARN, WRITING }

@Composable
fun ArabicLevelThreeReadingPage(onBack: () -> Unit) {
    var tab by remember { mutableStateOf(LevelThreeReadingTab.LEARN) }
    var index by remember { mutableStateOf(0) }
    val lesson = levelThreeLessons[index]

    Column(Modifier.fillMaxSize().background(Color(0xFFF6F9FF)).padding(14.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("‹ رجوع", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp) }
            Spacer(Modifier.weight(1f))
            Text("القراءة • المستوى الثالث", fontSize = 21.sp, fontWeight = FontWeight.Black, color = Color(0xFF24324A))
        }

        Row(
            Modifier.fillMaxWidth().background(Color(0xFFE6ECF8), RoundedCornerShape(22.dp)).padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            TabButton(Modifier.weight(1f), tab == LevelThreeReadingTab.LEARN, "◉  تعلم القراءة") { tab = LevelThreeReadingTab.LEARN }
            TabButton(Modifier.weight(1f), tab == LevelThreeReadingTab.WRITING, "✎  الكتابة") { tab = LevelThreeReadingTab.WRITING }
        }

        Spacer(Modifier.height(10.dp))
        if (tab == LevelThreeReadingTab.LEARN) {
            LearnReadingSection(lesson, index, { if (index > 0) index-- }, { index = (index + 1) % levelThreeLessons.size })
        } else {
            WritingSection(lesson, index, { if (index > 0) index-- }, { index = (index + 1) % levelThreeLessons.size })
        }
    }
}

@Composable
private fun TabButton(modifier: Modifier, selected: Boolean, title: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFF315CFF) else Color.Transparent,
            contentColor = if (selected) Color.White else Color(0xFF43516A)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = if (selected) 5.dp else 0.dp)
    ) { Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp) }
}

@Composable
private fun LearnReadingSection(
    lesson: TwoLetterLesson,
    index: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ready by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        lateinit var engine: TextToSpeech
        engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val preferred = engine.setLanguage(Locale.forLanguageTag("ar-XA"))
                if (preferred == TextToSpeech.LANG_NOT_SUPPORTED || preferred == TextToSpeech.LANG_MISSING_DATA) {
                    engine.language = Locale("ar")
                }
                engine.setSpeechRate(0.82f)
                engine.setPitch(0.98f)
                ready = true
            }
        }
        tts = engine
        onDispose {
            engine.stop()
            engine.shutdown()
            tts = null
        }
    }

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "نركّب الحرفين معًا ثم ننطق المقطع بوضوح",
            fontSize = 15.sp, color = Color(0xFF66748B), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(7.dp))

        Card(
            Modifier.fillMaxWidth().weight(1f).shadow(12.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                Modifier.fillMaxSize().padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text("مثال تعليمي ${index + 1}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF718099))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    LessonLetterCard(lesson.first, Modifier.weight(1f))
                    Text("+", modifier = Modifier.padding(horizontal = 8.dp), fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF6C7890))
                    LessonLetterCard(lesson.second, Modifier.weight(1f))
                    Text("=", modifier = Modifier.padding(horizontal = 8.dp), fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF6C7890))
                    Card(
                        modifier = Modifier.height(112.dp).weight(1f).shadow(8.dp, RoundedCornerShape(22.dp)),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF0FF))
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(lesson.result, fontSize = 58.sp, fontWeight = FontWeight.Black, color = Color(0xFF315CFF))
                        }
                    }
                }

                Text(lesson.example, fontSize = 27.sp, fontWeight = FontWeight.Black, color = Color(0xFF263B72), textAlign = TextAlign.Center)

                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F7FF))
                ) {
                    Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("اسمع ثم كرّر", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF66748B))
                        Button(
                            onClick = {
                                if (ready) {
                                    tts?.speak(
                                        "حرف ${lesson.first} مع حرف ${lesson.second} يساوي ${lesson.result}",
                                        TextToSpeech.QUEUE_FLUSH, null, "lesson_${index}"
                                    )
                                }
                            },
                            shape = RoundedCornerShape(18.dp)
                        ) { Text("🔊  نطق وشرح المثال", fontWeight = FontWeight.ExtraBold) }
                    }
                }

                Text("${index + 1} / ${levelThreeLessons.size}", color = Color(0xFF718099), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(9.dp))
        NavigationButtons(onPrevious, onNext)
    }
}

@Composable
private fun LessonLetterCard(letter: String, modifier: Modifier) {
    Card(
        modifier = modifier.height(112.dp).shadow(7.dp, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5FF))
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(letter, fontSize = 52.sp, fontWeight = FontWeight.Black, color = Color(0xFF263B72))
        }
    }
}

@Composable
private fun WritingSection(
    lesson: TwoLetterLesson,
    index: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    val strokes = remember(lesson.result) { mutableStateListOf<List<Offset>>() }
    var currentStroke by remember(lesson.result) { mutableStateOf<List<Offset>>(emptyList()) }

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("اكتب المقطع الظاهر بإصبعك", fontSize = 15.sp, color = Color(0xFF66748B), fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(7.dp))

        Card(
            Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.fillMaxWidth().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("الكلمة المطلوبة", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF718099))
                Text(lesson.result, fontSize = 44.sp, fontWeight = FontWeight.Black, color = Color(0xFF315CFF))
                Text(lesson.example, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF263B72))
            }
        }

        Spacer(Modifier.height(7.dp))

        Card(
            Modifier.fillMaxWidth().weight(1f).shadow(12.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF4))
        ) {
            Box(Modifier.fillMaxSize().padding(10.dp)) {
                Canvas(
                    Modifier.fillMaxSize().background(Color(0xFFFFFDF4)).pointerInput(lesson.result) {
                        detectDragGestures(
                            onDragStart = { offset -> currentStroke = listOf(offset) },
                            onDrag = { change, _ -> change.consume(); currentStroke = currentStroke + change.position },
                            onDragEnd = {
                                if (currentStroke.size > 1) strokes.add(currentStroke)
                                currentStroke = emptyList()
                            }
                        )
                    }
                ) {
                    drawWritingGuides()
                    drawTextGuide(lesson.result, center, min(size.width, size.height) * 0.58f)
                    strokes.forEach { drawStroke(it) }
                    if (currentStroke.size > 1) drawStroke(currentStroke)
                }
            }
        }

        Spacer(Modifier.height(5.dp))
        Text("تتبّع شكل المقطع، ثم حاول كتابته بنفسك ✨", fontSize = 13.sp, color = Color(0xFF63718A), fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(5.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            Button(
                onClick = { strokes.clear(); currentStroke = emptyList() },
                modifier = Modifier.weight(0.9f).height(55.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF476F))
            ) { Text("⌫  المسح", fontWeight = FontWeight.ExtraBold) }

            Button(
                onClick = onPrevious,
                modifier = Modifier.weight(1.2f).height(55.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B6B88))
            ) { Text("◀  السابق", fontWeight = FontWeight.ExtraBold) }

            Button(
                onClick = onNext,
                modifier = Modifier.weight(1.2f).height(55.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF315CFF))
            ) { Text("التالي  ▶", fontWeight = FontWeight.ExtraBold) }
        }

        Spacer(Modifier.height(5.dp))
        Text("${index + 1} / ${levelThreeLessons.size}", color = Color(0xFF718099), fontWeight = FontWeight.Bold)
    }
}

private fun DrawScope.drawWritingGuides() {
    val base = size.height * 0.68f
    drawLine(Color(0xFFD8DFEC), Offset(size.width * 0.08f, base), Offset(size.width * 0.92f, base), 3f)
    drawLine(Color(0xFFE7EBF3), Offset(size.width * 0.08f, size.height * 0.42f), Offset(size.width * 0.92f, size.height * 0.42f), 2f)
    drawLine(Color(0xFFF0F2F7), Offset(size.width * 0.08f, size.height * 0.81f), Offset(size.width * 0.92f, size.height * 0.81f), 2f)
}

private fun DrawScope.drawStroke(points: List<Offset>) {
    if (points.size < 2) return
    val path = Path().apply {
        moveTo(points.first().x, points.first().y)
        points.drop(1).forEach { lineTo(it.x, it.y) }
    }
    drawPath(path, Color(0xFF315CFF), style = Stroke(width = 9f, cap = StrokeCap.Round))
}

private fun DrawScope.drawTextGuide(text: String, center: Offset, size: Float) {
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.argb(52, 49, 92, 255)
        textSize = size
        textAlign = android.graphics.Paint.Align.CENTER
        typeface = android.graphics.Typeface.DEFAULT
    }
    drawContext.canvas.nativeCanvas.drawText(text, center.x, center.y - (paint.ascent() + paint.descent()) / 2f, paint)
}

@Composable
private fun NavigationButtons(onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
        Button(
            onClick = onPrevious,
            modifier = Modifier.weight(1f).height(55.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B6B88))
        ) { Text("◀  السابق", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold) }
        Button(
            onClick = onNext,
            modifier = Modifier.weight(1f).height(55.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF315CFF))
        ) { Text("التالي  ▶", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold) }
    }
}
