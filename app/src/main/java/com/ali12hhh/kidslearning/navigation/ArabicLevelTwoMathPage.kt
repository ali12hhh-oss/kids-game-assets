package com.ali12hhh.kidslearning.navigation

import com.ali12hhh.kidslearning.navigation.ProLessonButton
import android.speech.tts.TextToSpeech
import com.ali12hhh.kidslearning.navigation.LessonSpeech
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import io.github.sceneview.Scene
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.node.ModelNode
import io.github.sceneview.math.Position
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun arDigits(v: Int) = v.toString().map { ('٠'.code + (it.code - '0'.code)).toChar() }.joinToString("")
private data class StrokeLine(val points: List<Offset>)
private data class PlaceQuiz(val number: Int, val asked: String, val correct: Int, val options: List<Int>)

private val placeQuizzes = listOf(
    PlaceQuiz(1, "ما رقم الآحاد في العدد ١؟", 1, listOf(1, 0, 2)),
    PlaceQuiz(14, "ما رقم الآحاد في العدد ١٤؟", 4, listOf(1, 4, 6)),
    PlaceQuiz(23, "ما رقم العشرات في العدد ٢٣؟", 2, listOf(2, 3, 5)),
    PlaceQuiz(35, "ما رقم الآحاد في العدد ٣٥؟", 5, listOf(3, 5, 2)),
    PlaceQuiz(47, "ما رقم العشرات في العدد ٤٧؟", 4, listOf(7, 4, 5)),
    PlaceQuiz(52, "ما رقم الآحاد في العدد ٥٢؟", 2, listOf(5, 2, 0)),
    PlaceQuiz(68, "ما رقم العشرات في العدد ٦٨؟", 6, listOf(8, 6, 4)),
    PlaceQuiz(71, "ما رقم الآحاد في العدد ٧١؟", 1, listOf(7, 1, 0)),
    PlaceQuiz(84, "ما رقم العشرات في العدد ٨٤؟", 8, listOf(4, 8, 6)),
    PlaceQuiz(99, "ما رقم الآحاد في العدد ٩٩؟", 9, listOf(9, 0, 8))
)

@Composable
fun ArabicLevelTwoMathPage(onBack: () -> Unit) {
    var tab by remember { mutableStateOf(0) }
    CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
        Column(Modifier.fillMaxSize().background(Color(0xFFF4F7FF)).padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onBack) { Text("‹ رجوع", fontWeight = FontWeight.ExtraBold) }
                Spacer(Modifier.weight(1f))
                Text("الرياضيات • المستوى الثاني", fontSize = 21.sp, fontWeight = FontWeight.Black, color = Color(0xFF24324A))
            }
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MathModeButton(
                    modifier = Modifier.weight(1f),
                    selected = tab == 0,
                    icon = "🔢",
                    title = "كتابة الأعداد",
                    color = Color(0xFF315CFF),
                    onClick = { tab = 0 }
                )
                MathModeButton(
                    modifier = Modifier.weight(1f),
                    selected = tab == 1,
                    icon = "🏷️",
                    title = "مراتب الأعداد",
                    color = Color(0xFF16A085),
                    onClick = { tab = 1 }
                )
            }
            Spacer(Modifier.height(8.dp))
            AnimatedContent(targetState = tab, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "math2tabs") {
                if (it == 0) NumberWritingSection() else PlaceValueSection()
            }
        }
    }
}

@Composable
private fun MathModeButton(
    modifier: Modifier,
    selected: Boolean,
    icon: String,
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    ProLessonButton(
        onClick = onClick,
        modifier = modifier.height(62.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) color else Color.White,
            contentColor = if (selected) Color.White else Color(0xFF35445C)
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 21.sp)
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun NumberWritingSection() {
    var number by remember { mutableStateOf(1) }
    val strokes = remember(number) { mutableStateListOf<StrokeLine>() }
    var activeStroke by remember { mutableStateOf<StrokeLine?>(null) }
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

    LaunchedEffect(number, ready) {
        if (ready && AppSettings.isSpeechEnabled(context)) {
            tts?.speak(arDigits(number), TextToSpeech.QUEUE_FLUSH, null, "number_writing_" + number)
        }
    }

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("اكتب الرقم كما تراه", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF53647A))
        Text(arDigits(number), fontSize = 72.sp, fontWeight = FontWeight.Black, color = mathNumberColor(number))
        Card(Modifier.fillMaxWidth().weight(1f).shadow(10.dp, RoundedCornerShape(26.dp)), shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(Color.White)) {
            Box(Modifier.fillMaxSize().padding(12.dp)) {
                Text(arDigits(number), Modifier.align(Alignment.Center), fontSize = 118.sp, fontWeight = FontWeight.Black, color = Color(0xFFE9EDF6))
                Canvas(Modifier.fillMaxSize().pointerInput(number) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Main)
                        down.consume()
                        var points = listOf(down.position)

                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Main)
                            val change = event.changes.firstOrNull() ?: break
                            if (!change.pressed) {
                                val finished = if (points.size > 1) points else listOf(change.position)
                                strokes.add(StrokeLine(finished))
                                activeStroke = null
                                break
                            }
                            change.consume()
                            if (change.position != points.last()) {
                                points = points + change.position
                                activeStroke = StrokeLine(points)
                            }
                        }
                    }
                }) {
                    strokes.forEach { line ->
                        if (line.points.size > 1) {
                            val path = Path().apply {
                                moveTo(line.points.first().x, line.points.first().y)
                                line.points.drop(1).forEach { lineTo(it.x, it.y) }
                            }
                            drawPath(path, Color(0xFF315CFF), style = Stroke(width = 30f, cap = StrokeCap.Round))
                        } else if (line.points.isNotEmpty()) drawCircle(Color(0xFF315CFF), 15f, line.points.first())
                    }
                    // The active stroke is rendered on every Canvas redraw while the finger is down.
                    activeStroke?.let { line ->
                        if (line.points.size > 1) {
                            val path = Path().apply {
                                moveTo(line.points.first().x, line.points.first().y)
                                line.points.drop(1).forEach { lineTo(it.x, it.y) }
                            }
                            drawPath(path, Color(0xFF315CFF), style = Stroke(width = 30f, cap = StrokeCap.Round))
                        } else if (line.points.isNotEmpty()) {
                            drawCircle(Color(0xFF315CFF), 15f, line.points.first())
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ProLessonButton(
                onClick = { strokes.clear(); activeStroke = null },
                Modifier.weight(1f).height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE05A5A))
            ) { Text("مسح", fontWeight = FontWeight.ExtraBold) }
            ProLessonButton(
                onClick = { if (number > 1) number-- },
                Modifier.weight(1f).height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B6B88))
            ) { Text("السابق", fontWeight = FontWeight.ExtraBold) }
            ProLessonButton(
                onClick = { if (number < 100) number++ },
                Modifier.weight(1f).height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF315CFF))
            ) { Text("التالي", fontWeight = FontWeight.ExtraBold) }
        }
    }
}

@Composable
private fun PlaceValueSection() {
    var mode by remember { mutableStateOf(0) }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MathModeButton(
                modifier = Modifier.weight(1f),
                selected = mode == 0,
                icon = "📚",
                title = "تعلم",
                color = Color(0xFF7A4DCE),
                onClick = { mode = 0 }
            )
            MathModeButton(
                modifier = Modifier.weight(1f),
                selected = mode == 1,
                icon = "⭐",
                title = "اختبر نفسك",
                color = Color(0xFFE67E22),
                onClick = { mode = 1 }
            )
        }
        Spacer(Modifier.height(8.dp))
        if (mode == 0) PlaceValueLearn() else PlaceValueQuiz()
    }
}

private val learnExamples = listOf(
    Triple("١", "الآحاد", "الرقم الموجود أقصى اليمين هو الآحاد. في العدد ١ لدينا آحاد واحد."),
    Triple("٤", "الآحاد", "الرقم ٤ يمثل أربعة آحاد، لذلك ٤ = ٤ آحاد."),
    Triple("١٢", "عشرات وآحاد", "في العدد ١٢: الرقم ١ في العشرات والرقم ٢ في الآحاد؛ أي ١٠ + ٢ = ١٢."),
    Triple("٢٥", "عشرات وآحاد", "في العدد ٢٥: ٢ عشرات = ٢٠، و٥ آحاد = ٥؛ إذن ٢٠ + ٥ = ٢٥."),
    Triple("٣٠", "عشرات وآحاد", "في العدد ٣٠: ٣ في العشرات و٠ في الآحاد؛ أي ٣ عشرات = ٣٠."),
    Triple("٤٦", "عشرات وآحاد", "في العدد ٤٦: ٤ عشرات = ٤٠، و٦ آحاد = ٦؛ إذن ٤٠ + ٦ = ٤٦."),
    Triple("٥٢", "عشرات وآحاد", "في العدد ٥٢: ٥ عشرات = ٥٠، و٢ آحاد = ٢؛ إذن ٥٠ + ٢ = ٥٢."),
    Triple("٧٨", "عشرات وآحاد", "في العدد ٧٨: ٧ عشرات = ٧٠، و٨ آحاد = ٨؛ إذن ٧٠ + ٨ = ٧٨."),
    Triple("٩٠", "عشرات وآحاد", "في العدد ٩٠: ٩ عشرات = ٩٠، و٠ آحاد؛ أي تسع عشرات كاملة."),
    Triple("٩٩", "عشرات وآحاد", "في العدد ٩٩: ٩ عشرات = ٩٠، و٩ آحاد = ٩؛ إذن ٩٠ + ٩ = ٩٩.")
)

@Composable
private fun PlaceValueLearn() {
    var index by remember { mutableStateOf(0) }
    var ready by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        lateinit var e: TextToSpeech
        e = TextToSpeech(context) { s ->
            if (s == TextToSpeech.SUCCESS) {
                LessonSpeech.configure(e, LessonSpeech.ARABIC_LOCALE)
                ready = true
            }
        }
        tts = e
        onDispose { e.stop(); e.shutdown() }
    }
    val ex = learnExamples[index]
    LaunchedEffect(index, ready) {
        if (ready && AppSettings.isSpeechEnabled(context)) {
            tts?.speak(ex.first + "، " + ex.second + "، " + ex.third, TextToSpeech.QUEUE_FLUSH, null, "place_learn_" + index)
        }
    }
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
        Text("مثال ${index + 1} من ${learnExamples.size}", fontWeight = FontWeight.Bold, color = Color(0xFF65738A))
        Card(Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(26.dp)), shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(Color.White)) {
            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(ex.first, fontSize = 76.sp, fontWeight = FontWeight.Black, color = mathNumberColor(index + 1))
                Text(ex.second, fontSize = 23.sp, fontWeight = FontWeight.Black, color = Color(0xFF16A085))
                Spacer(Modifier.height(12.dp))
                Text(ex.third, fontSize = 18.sp, lineHeight = 29.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(14.dp))
                ProLessonButton(onClick = { if (ready) if (AppSettings.isSpeechEnabled(context)) tts?.speak(ex.third, TextToSpeech.QUEUE_FLUSH, null, "learn_${index}") }) { Text("🔊 اسمع الشرح", fontWeight = FontWeight.ExtraBold) }
            }
        }
        Row(Modifier.fillMaxWidth().navigationBarsPadding(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ProLessonButton(onClick = { if (index > 0) index-- }, Modifier.weight(1f).height(56.dp), colors = ButtonDefaults.buttonColors(Color(0xFF5B6B88))) { Text("السابق") }
            ProLessonButton(onClick = { if (index < learnExamples.lastIndex) index++ }, Modifier.weight(1f).height(56.dp)) { Text("التالي") }
        }
    }
}

@Composable
private fun PlaceValueQuiz() {
    var index by remember { mutableStateOf(0) }
    var selected by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableStateOf(0) }
    var reaction by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ready by remember { mutableStateOf(false) }
    val quiz = placeQuizzes[index]
    DisposableEffect(Unit) {
        lateinit var e: TextToSpeech
        e = TextToSpeech(context) { s ->
            if (s == TextToSpeech.SUCCESS) {
                LessonSpeech.configure(e, LessonSpeech.ARABIC_LOCALE)
                ready = true
            }
        }
        tts = e
        onDispose { e.stop(); e.shutdown() }
    }
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("اختبار ${index + 1} / ${placeQuizzes.size}", fontWeight = FontWeight.Bold, color = Color(0xFF65738A))
        Text("العدد ${arDigits(quiz.number)}", fontSize = 48.sp, fontWeight = FontWeight.Black, color = mathNumberColor(index + 1))
        Text(quiz.asked, Modifier.fillMaxWidth(), fontSize = 19.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
        OutlinedButton(onClick = { if (ready) if (AppSettings.isSpeechEnabled(context)) tts?.speak(quiz.asked, TextToSpeech.QUEUE_FLUSH, null, "question_${index}") }) { Text("🔊 صوت السؤال") }
        quiz.options.forEach { option ->
            val color = when {
                selected == option && option == quiz.correct -> Color(0xFF2EAD67)
                selected == option && option != quiz.correct -> Color(0xFFE05A5A)
                else -> Color.White
            }
            ProLessonButton(onClick = {
                if (selected == null) {
                    selected = option
                    if (option == quiz.correct) {
                        AppSettings.awardCorrectAnswer(context)
                        score++
                        reaction = 1
                        if (ready) if (AppSettings.isSpeechEnabled(context)) tts?.speak("أحسنت! إجابة صحيحة", TextToSpeech.QUEUE_FLUSH, null, "answer_${index}")
                    } else {
                        reaction = -1
                        if (ready) if (AppSettings.isSpeechEnabled(context)) tts?.speak("حاول مرة أخرى", TextToSpeech.QUEUE_FLUSH, null, "answer_${index}")
                    }
                    scope.launch {
                        delay(900)
                        if (index < placeQuizzes.lastIndex) {
                            index++
                            selected = null
                            reaction = 0
                        }
                    }
                }
            }, Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = if (selected != null) Color.White else Color(0xFF24324A))) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(arDigits(option), fontSize = 20.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                }
            }
        }
        Spacer(Modifier.height(3.dp))
        CharacterReaction(reaction)
        Spacer(Modifier.height(2.dp))
        Text("النتيجة: " + arDigits(score), fontWeight = FontWeight.ExtraBold, color = Color(0xFF315CFF))
        Spacer(Modifier.height(2.dp))
        Row(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(bottom = 28.dp).height(52.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ProLessonButton(
                onClick = { if (index > 0) { index--; selected = null; reaction = 0 } },
                modifier = Modifier.weight(1f).height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (index > 0) Color(0xFF5B6B88) else Color(0xFFD9DFEA),
                    contentColor = if (index > 0) Color.White else Color(0xFF8A94A6)
                ),
                shape = RoundedCornerShape(16.dp)
            ) { Text("‹  السابق", fontWeight = FontWeight.ExtraBold) }
            ProLessonButton(
                onClick = { if (index < placeQuizzes.lastIndex) { index++; selected = null; reaction = 0 } },
                modifier = Modifier.weight(1f).height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (index < placeQuizzes.lastIndex) Color(0xFF315CFF) else Color(0xFFD9DFEA),
                    contentColor = if (index < placeQuizzes.lastIndex) Color.White else Color(0xFF8A94A6)
                ),
                shape = RoundedCornerShape(16.dp)
            ) { Text("التالي  ›", fontWeight = FontWeight.ExtraBold) }
        }
    }
}

private fun mathNumberColor(number: Int) = listOf(Color(0xFF315CFF), Color(0xFFE64A6B), Color(0xFF16A085), Color(0xFFE67E22), Color(0xFF7A4DCE), Color(0xFF008C95))[(number - 1) % 6]


@Composable
private fun CharacterReaction(reaction: Int) {
    val engine = rememberEngine()
    val loader = rememberModelLoader(engine)
    val model = remember { runCatching { loader.createModelInstance("Mannequin_Medium_Anim.glb") }.getOrNull() }
    val camera = rememberCameraNode(engine) { position = Position(z = 2.9f) }
    val node = remember(model) {
        model?.let {
            ModelNode(modelInstance = it, autoAnimate = false, scaleToUnits = 2.25f).also {
                it.position = Position(x = 0f, y = -1.00f, z = 0f)
            }
        }
    }
    LaunchedEffect(reaction, node) {
        val n = node ?: return@LaunchedEffect
        if (reaction == 1) {
            runCatching { n.stopAnimation(8) }
            runCatching { n.playAnimation(7, 1f, false) }
            delay(1800)
            runCatching { n.stopAnimation(7) }
        } else if (reaction == -1) {
            runCatching { n.stopAnimation(7) }
            runCatching { n.playAnimation(8, 1f, false) }
        } else {
            runCatching { n.playAnimation(0, 1f, true) }
        }
    }
    Card(
        Modifier.fillMaxWidth().height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color(0xFFF8FAFF))
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Scene(
                modifier = Modifier.fillMaxSize(),
                engine = engine,
                modelLoader = loader,
                cameraNode = camera,
                cameraManipulator = null,
                isOpaque = false,
                childNodes = listOfNotNull(node)
            )
        }    }
}
