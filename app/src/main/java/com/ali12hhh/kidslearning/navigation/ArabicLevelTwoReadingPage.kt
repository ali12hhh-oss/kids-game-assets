package com.ali12hhh.kidslearning.navigation

import com.ali12hhh.kidslearning.navigation.ProLessonButton

import android.speech.tts.TextToSpeech
import com.ali12hhh.kidslearning.navigation.LessonSpeech
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.min

private data class ArabicLetterForms(
    val letter: String,
    val name: String,
    val initial: String,
    val medial: String,
    val final: String
)

private val levelTwoLetters = listOf(
    ArabicLetterForms("ا", "ألف", "ا", "ـا", "ا"),
    ArabicLetterForms("ب", "باء", "بـ", "ـبـ", "ـب"),
    ArabicLetterForms("ت", "تاء", "تـ", "ـتـ", "ـت"),
    ArabicLetterForms("ث", "ثاء", "ثـ", "ـثـ", "ـث"),
    ArabicLetterForms("ج", "جيم", "جـ", "ـجـ", "ـج"),
    ArabicLetterForms("ح", "حاء", "حـ", "ـحـ", "ـح"),
    ArabicLetterForms("خ", "خاء", "خـ", "ـخـ", "ـخ"),
    ArabicLetterForms("د", "دال", "د", "ـد", "ـد"),
    ArabicLetterForms("ذ", "ذال", "ذ", "ـذ", "ـذ"),
    ArabicLetterForms("ر", "راء", "ر", "ـر", "ـر"),
    ArabicLetterForms("ز", "زاي", "ز", "ـز", "ـز"),
    ArabicLetterForms("س", "سين", "سـ", "ـسـ", "ـس"),
    ArabicLetterForms("ش", "شين", "شـ", "ـشـ", "ـش"),
    ArabicLetterForms("ص", "صاد", "صـ", "ـصـ", "ـص"),
    ArabicLetterForms("ض", "ضاد", "ضـ", "ـضـ", "ـض"),
    ArabicLetterForms("ط", "طاء", "طـ", "ـطـ", "ـط"),
    ArabicLetterForms("ظ", "ظاء", "ظـ", "ـظـ", "ـظ"),
    ArabicLetterForms("ع", "عين", "عـ", "ـعـ", "ـع"),
    ArabicLetterForms("غ", "غين", "غـ", "ـغـ", "ـغ"),
    ArabicLetterForms("ف", "فاء", "فـ", "ـفـ", "ـف"),
    ArabicLetterForms("ق", "قاف", "قـ", "ـقـ", "ـق"),
    ArabicLetterForms("ك", "كاف", "كـ", "ـكـ", "ـك"),
    ArabicLetterForms("ل", "لام", "لـ", "ـلـ", "ـل"),
    ArabicLetterForms("م", "ميم", "مـ", "ـمـ", "ـم"),
    ArabicLetterForms("ن", "نون", "نـ", "ـنـ", "ـن"),
    ArabicLetterForms("ه", "هاء", "هـ", "ـهـ", "ـه"),
    ArabicLetterForms("و", "واو", "و", "ـو", "ـو"),
    ArabicLetterForms("ي", "ياء", "يـ", "ـيـ", "ـي")
)

private enum class LevelTwoReadingTab { LETTERS, WRITING }

@Composable
fun ArabicLevelTwoReadingPage(onBack: () -> Unit) {
    var tab by remember { mutableStateOf(LevelTwoReadingTab.LETTERS) }
    var index by remember { mutableStateOf(0) }
    val lesson = levelTwoLetters[index]

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF6F9FF)).padding(14.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) {
                Text("‹ رجوع", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }
            Spacer(Modifier.weight(1f))
            Text("القراءة • المستوى الثاني", fontSize = 21.sp, fontWeight = FontWeight.Black, color = Color(0xFF24324A))
        }

        Row(
            Modifier.fillMaxWidth().background(Color(0xFFE6ECF8), RoundedCornerShape(22.dp)).padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            TabButton(Modifier.weight(1f), tab == LevelTwoReadingTab.LETTERS, "🔤  الحروف") {
                tab = LevelTwoReadingTab.LETTERS
            }
            TabButton(Modifier.weight(1f), tab == LevelTwoReadingTab.WRITING, "✍️  الكتابة") {
                tab = LevelTwoReadingTab.WRITING
            }
        }

        Spacer(Modifier.height(10.dp))
        if (tab == LevelTwoReadingTab.LETTERS) {
            LetterFormsSection(
                lesson = lesson,
                index = index,
                onPrevious = { if (index > 0) index-- },
                onNext = { index = (index + 1) % levelTwoLetters.size }
            )
        } else {
            WritingSection(
                lesson = lesson,
                onPrevious = { if (index > 0) index-- },
                onNext = { index = (index + 1) % levelTwoLetters.size }
            )
        }
    }
}

@Composable
private fun TabButton(modifier: Modifier, selected: Boolean, title: String, onClick: () -> Unit) {
    ProLessonButton(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFF315CFF) else Color.Transparent,
            contentColor = if (selected) Color.White else Color(0xFF43516A)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = if (selected) 5.dp else 0.dp)
    ) {
        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
    }
}

@Composable
private fun LetterFormsSection(
    lesson: ArabicLetterForms,
    index: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ready by remember { mutableStateOf(false) }
    var selectedForm by remember(lesson.letter) { mutableStateOf(0) }
    val forms = listOf(lesson.initial, lesson.medial, lesson.final)
    val labels = listOf("أولي", "وسطي", "أخري")

    DisposableEffect(Unit) {
        val engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.let { LessonSpeech.configure(it, LessonSpeech.ARABIC_LOCALE) }
                ready = true
            }
        }
        tts = engine
        onDispose { engine.stop(); engine.shutdown(); tts = null }
    }

    LaunchedEffect(lesson.letter, selectedForm, ready) {
        if (ready && AppSettings.isSpeechEnabled(context)) {
            tts?.speak(lesson.name + "، " + labels[selectedForm], TextToSpeech.QUEUE_FLUSH, null, "letter_form_" + lesson.letter + "_" + selectedForm)
        }
    }

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("أشكال الحرف حسب موقعه في الكلمة", fontSize = 15.sp, color = Color(0xFF66748B), fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(7.dp))

        Card(
            Modifier.fillMaxWidth().weight(1f).shadow(10.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                Modifier.fillMaxSize().padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(lesson.letter, fontSize = 78.sp, fontWeight = FontWeight.Black, color = Color(0xFF315CFF))
                Text(lesson.name, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF25344E))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    forms.forEachIndexed { formIndex, form ->
                        LetterFormCard(
                            title = labels[formIndex],
                            form = form,
                            selected = selectedForm == formIndex,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedForm = formIndex }
                        )
                    }
                }
                ProLessonButton(
                    onClick = { if (ready) if (AppSettings.isSpeechEnabled(context)) tts?.speak(lesson.name, TextToSpeech.QUEUE_FLUSH, null, "letter_name") },
                    shape = RoundedCornerShape(18.dp)
                ) { Text("🔊  سماع اسم الحرف", fontWeight = FontWeight.ExtraBold) }
                Text("${index + 1} / ${levelTwoLetters.size}", color = Color(0xFF718099), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(9.dp))
        NavigationButtons(onPrevious, onNext)
    }
}

@Composable
private fun LetterFormCard(
    title: String,
    form: String,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier.height(145.dp).shadow(5.dp, RoundedCornerShape(20.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (selected) Color(0xFFE8EEFF) else Color(0xFFF1F5FF))
    ) {
        Column(
            Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = if (selected) Color(0xFF315CFF) else Color(0xFF63718A))
            Text(form, fontSize = 45.sp, fontWeight = FontWeight.Black, color = if (selected) Color(0xFF315CFF) else Color(0xFF263B72))
        }
    }
}

@Composable
private fun WritingSection(lesson: ArabicLetterForms, onPrevious: () -> Unit, onNext: () -> Unit) {
    var selectedForm by remember(lesson.letter) { mutableStateOf(0) }
    val strokes = remember(lesson.letter) { mutableStateListOf<List<Offset>>() }
    var currentStroke by remember(lesson.letter) { mutableStateOf<List<Offset>>(emptyList()) }
    val forms = listOf(lesson.initial, lesson.medial, lesson.final)
    val labels = listOf("أولي", "وسطي", "أخري")
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ready by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        lateinit var engine: TextToSpeech
        engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                LessonSpeech.configure(engine, LessonSpeech.ARABIC_LOCALE)
                ready = true
            }
        }
        tts = engine
        onDispose { engine.stop(); engine.shutdown(); tts = null }
    }

    LaunchedEffect(lesson.letter, ready) {
        if (ready && AppSettings.isSpeechEnabled(context)) {
            tts?.speak(lesson.letter, TextToSpeech.QUEUE_FLUSH, null, "writing_letter_" + lesson.letter)
        }
    }

    val dots = when (lesson.letter) {
        "ب", "ت", "ث", "ن" -> 1
        "ي" -> 2
        "ق" -> 2
        "ف", "خ", "ذ", "ز", "ض", "ظ", "غ" -> 1
        "ش" -> 3
        else -> 0
    }

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            forms.forEachIndexed { i, form ->
                val selected = selectedForm == i
                Card(
                    Modifier.weight(1f).height(62.dp).clickable { selectedForm = i
                        if (ready && AppSettings.isSpeechEnabled(context)) {
                            tts?.speak(lesson.name + "، " + labels[i], TextToSpeech.QUEUE_FLUSH, null, "writing_form_" + lesson.letter + "_" + i)
                        } },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = if (selected) Color(0xFF315CFF) else Color.White)
                ) {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(labels[i], fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selected) Color.White else Color(0xFF66748B))
                        Text(form, fontSize = 30.sp, fontWeight = FontWeight.Black, color = if (selected) Color.White else Color(0xFF263B72))
                    }
                }
            }
        }

        Spacer(Modifier.height(7.dp))
        Card(
            Modifier.fillMaxWidth().weight(1f).shadow(12.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF4))
        ) {
            Box(Modifier.fillMaxSize().padding(10.dp), contentAlignment = Alignment.Center) {
                Canvas(
                    Modifier.fillMaxSize().background(Color(0xFFFFFDF4)).pointerInput(lesson.letter, selectedForm) {
                        detectDragGestures(
                            onDragStart = { offset -> currentStroke = listOf(offset) },
                            onDrag = { change, _ ->
                                change.consume()
                                currentStroke = currentStroke + change.position
                            },
                            onDragEnd = {
                                if (currentStroke.size > 1) strokes.add(currentStroke)
                                currentStroke = emptyList()
                            }
                        )
                    }
                ) {
                    val guideSize = min(size.width, size.height) * 0.62f
                    drawCircle(Color(0xFFE7EBF3), guideSize * 0.50f, center, style = Stroke(3f))
                    drawLine(Color(0xFFD9DFEA), Offset(size.width * 0.08f, size.height * 0.70f), Offset(size.width * 0.92f, size.height * 0.70f), 3f)
                    drawLine(Color(0xFFE6EAF2), Offset(size.width * 0.08f, size.height * 0.45f), Offset(size.width * 0.92f, size.height * 0.45f), 2f)

                    drawTextGuide(forms[selectedForm], center, guideSize)

                    strokes.forEach { points ->
                        val path = Path().apply {
                            points.firstOrNull()?.let { moveTo(it.x, it.y) }
                            points.drop(1).forEach { lineTo(it.x, it.y) }
                        }
                        drawPath(path, Color(0xFF315CFF), style = Stroke(width = 30f, cap = StrokeCap.Round))
                    }
                    if (currentStroke.size > 1) {
                        val path = Path().apply {
                            moveTo(currentStroke.first().x, currentStroke.first().y)
                            currentStroke.drop(1).forEach { lineTo(it.x, it.y) }
                        }
                        drawPath(path, Color(0xFF315CFF), style = Stroke(width = 9f, cap = StrokeCap.Round))
                    }

                    if (dots > 0) {
                        val baseY = size.height * 0.69f
                        val spacing = 22f
                        val startX = center.x - (dots - 1) * spacing / 2f
                        repeat(dots) { d ->
                            drawCircle(Color(0xFFEF476F), 7f, Offset(startX + d * spacing, baseY))
                            drawCircle(Color.White, 3f, Offset(startX + d * spacing, baseY))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(5.dp))
        Text("اكتب الحرف الظاهر بإصبعك، ولا تنسَ نقاط الحرف ✨", fontSize = 13.sp, color = Color(0xFF63718A), fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(5.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ProLessonButton(
                onClick = { strokes.clear(); currentStroke = emptyList() },
                modifier = Modifier.weight(1f).height(55.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE05A5A))
            ) { Text("مسح", fontWeight = FontWeight.ExtraBold) }
            NavigationButtons(onPrevious, onNext, Modifier.weight(2f))
        }
    }
}

private fun DrawScope.drawTextGuide(text: String, center: Offset, size: Float) {
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.argb(55, 38, 59, 114)
        textSize = size
        textAlign = android.graphics.Paint.Align.CENTER
        typeface = android.graphics.Typeface.DEFAULT
    }
    drawContext.canvas.nativeCanvas.drawText(
        text,
        center.x,
        center.y - (paint.ascent() + paint.descent()) / 2f,
        paint
    )
}

@Composable
private fun NavigationButtons(
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
        ProLessonButton(
            onClick = onPrevious,
            modifier = Modifier.weight(1f).height(55.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B6B88))
        ) { Text("◀  السابق", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold) }
        ProLessonButton(
            onClick = onNext,
            modifier = Modifier.weight(1f).height(55.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF315CFF))
        ) { Text("التالي  ▶", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold) }
    }
}
