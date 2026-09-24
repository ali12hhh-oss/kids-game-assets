package com.ali12hhh.kidslearning.navigation

import com.ali12hhh.kidslearning.navigation.ProLessonButton

import android.speech.tts.TextToSpeech
import com.ali12hhh.kidslearning.navigation.LessonSpeech
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

private data class LetterLesson(val letter: String, val sound: String, val word: String, val emoji: String)

private val arabicLessons = listOf(
    LetterLesson("ا", "أَ", "أَسَد", "🦁"), LetterLesson("ب", "بَ", "بَطَّة", "🦆"),
    LetterLesson("ت", "تَ", "تُفَّاح", "🍎"), LetterLesson("ث", "ثَ", "ثَعْلَب", "🦊"),
    LetterLesson("ج", "جَ", "جَمَل", "🐪"), LetterLesson("ح", "حَ", "حِصَان", "🐎"),
    LetterLesson("خ", "خَ", "خَرُوف", "🐑"), LetterLesson("د", "دَ", "دُبّ", "🐻"),
    LetterLesson("ذ", "ذَ", "ذِئْب", "🐺"), LetterLesson("ر", "رَ", "رُمَّان", "🍎"),
    LetterLesson("ز", "زَ", "زَرَافَة", "🦒"), LetterLesson("س", "سَ", "سَمَكَة", "🐟"),
    LetterLesson("ش", "شَ", "شَمْس", "☀️"), LetterLesson("ص", "صَ", "صَقْر", "🦅"),
    LetterLesson("ض", "ضَ", "ضِفْدَع", "🐸"), LetterLesson("ط", "طَ", "طَاوُوس", "🦚"),
    LetterLesson("ظ", "ظَ", "ظَبْي", "🦌"), LetterLesson("ع", "عَ", "عِنَب", "🍇"),
    LetterLesson("غ", "غَ", "غَزَال", "🦌"), LetterLesson("ف", "فَ", "فِيل", "🐘"),
    LetterLesson("ق", "قَ", "قِطَّة", "🐱"), LetterLesson("ك", "كَ", "كَلْب", "🐶"),
    LetterLesson("ل", "لَ", "لَيْمُون", "🍋"), LetterLesson("م", "مَ", "مَوْز", "🍌"),
    LetterLesson("ن", "نَ", "نَمِر", "🐅"), LetterLesson("ه", "هَ", "هِلَال", "🌙"),
    LetterLesson("و", "وَ", "وَرْدَة", "🌹"), LetterLesson("ي", "يَ", "يَد", "✋")
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ArabicReadingPage(onBack: () -> Unit) {
    val context = LocalContext.current
    var index by remember { mutableIntStateOf(0) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsReady by remember { mutableStateOf(false) }
    DisposableEffect(Unit) {
        val engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
                tts?.let { LessonSpeech.configure(it, LessonSpeech.ARABIC_LOCALE) }
            }
        }
        tts = engine
        onDispose { engine.stop(); engine.shutdown(); tts = null }
    }
    val lesson = arabicLessons[index]
    val colors = listOf(Color(0xFFEF476F), Color(0xFF118AB2), Color(0xFF06A77D), Color(0xFFFF9F1C), Color(0xFF7353BA), Color(0xFF3A86FF))
    val letterColor = colors[index % colors.size]
    val pop by animateFloatAsState(targetValue = 1f, animationSpec = tween(420), label = "letterPop")
    fun speak(text: String) { if (ttsReady) if (AppSettings.isSpeechEnabled(context)) tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "arabic_lesson") }

    CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFFFF7E8), Color(0xFFE5F5FF)))).padding(14.dp)) {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    FilledTonalButton(onClick = onBack, shape = RoundedCornerShape(16.dp)) { Text("‹ رجوع", fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.weight(1f))
                    Text("القراءة • المستوى الأول", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF25344E))
                }
                Spacer(Modifier.height(12.dp))
                Text("هَيَّا نَتَعَلَّمُ الحُرُوفَ!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF52647D))
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Card(Modifier.weight(0.78f).height(180.dp), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE6A7)), elevation = CardDefaults.cardElevation(10.dp)) {
                        Box(Modifier.fillMaxSize().padding(4.dp), contentAlignment = Alignment.Center) {
                            LessonCharacter3D(modifier = Modifier.fillMaxSize(), dancing = true)
                        }
                    }
                    Card(Modifier.weight(1.65f).height(220.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF174B3D)), elevation = CardDefaults.cardElevation(12.dp)) {
                        Box(Modifier.fillMaxSize().padding(10.dp).background(Brush.verticalGradient(listOf(Color(0xFF236B54), Color(0xFF123B32))), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                            AnimatedContent(targetState = index, transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(180)) }, label = "letterChange") { current ->
                                Text(arabicLessons[current].letter, Modifier.scale(pop), fontSize = 126.sp, fontWeight = FontWeight.Black, color = colors[current % colors.size], textAlign = TextAlign.Center)
                            }
                            Text("الصَّبُّورَة", Modifier.align(Alignment.TopCenter).padding(top = 5.dp), color = Color(0xFFD9F3D6), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(6.dp)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("كَلِمَةٌ تَبْدَأُ بِالحَرْفِ", fontSize = 14.sp, color = Color(0xFF6C7890), fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            Text(lesson.emoji, fontSize = 42.sp)
                            Spacer(Modifier.width(12.dp))
                            Text(lesson.word, fontSize = 35.sp, color = letterColor, fontWeight = FontWeight.ExtraBold)
                        }
                        ProLessonButton(onClick = { speak(lesson.word) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF118AB2)), shape = RoundedCornerShape(18.dp)) {
                            Text("🔊 انطق الكلمة", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { speak(lesson.letter) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp)) { Text("🔊 انطق اسم الحرف", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) }
                    ProLessonButton(onClick = { speak(lesson.sound) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = letterColor)) { Text("🎵 صوت الحرف", fontWeight = FontWeight.Bold) }
                }
                Spacer(Modifier.weight(1f))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ProLessonButton(onClick = { if (index > 0) index-- }, enabled = index > 0, modifier = Modifier.weight(1f).height(54.dp), shape = RoundedCornerShape(18.dp)) { Text("السابق", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                    ProLessonButton(onClick = { if (index < arabicLessons.lastIndex) index++ else index = 0 }, modifier = Modifier.weight(1f).height(54.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06A77D))) { Text(if (index == arabicLessons.lastIndex) "ابدأ من جديد ⟲" else "التالي ➜", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                }
                Spacer(Modifier.height(4.dp))
                Text("${index + 1} / ${arabicLessons.size}", color = Color(0xFF6C7890), fontWeight = FontWeight.Bold)
            }
        }
    }
}
