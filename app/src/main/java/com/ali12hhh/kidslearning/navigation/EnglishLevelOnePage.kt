package com.ali12hhh.kidslearning.navigation

import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

private data class EnglishLetterLesson(
    val lower: String, val upper: String, val name: String,
    val example: String, val arabicMeaning: String, val picture: String,
    val soundHint: String
)

private val englishLetters = listOf(
    EnglishLetterLesson("a","A","A","Apple","تفاحة","🍎","A as in apple"),
    EnglishLetterLesson("b","B","B","Bear","دب","🐻","B as in bear"),
    EnglishLetterLesson("c","C","C","Cat","قطة","🐱","C as in cat"),
    EnglishLetterLesson("d","D","D","Dog","كلب","🐶","D as in dog"),
    EnglishLetterLesson("e","E","E","Elephant","فيل","🐘","E as in elephant"),
    EnglishLetterLesson("f","F","F","Fish","سمكة","🐟","F as in fish"),
    EnglishLetterLesson("g","G","G","Goat","ماعز","🐐","G as in goat"),
    EnglishLetterLesson("h","H","H","Horse","حصان","🐴","H as in horse"),
    EnglishLetterLesson("i","I","I","Igloo","بيت جليدي","🧊","I as in igloo"),
    EnglishLetterLesson("j","J","J","Juice","عصير","🧃","J as in juice"),
    EnglishLetterLesson("k","K","K","Kite","طائرة ورقية","🪁","K as in kite"),
    EnglishLetterLesson("l","L","L","Lion","أسد","🦁","L as in lion"),
    EnglishLetterLesson("m","M","M","Monkey","قرد","🐒","M as in monkey"),
    EnglishLetterLesson("n","N","N","Nest","عش","🪺","N as in nest"),
    EnglishLetterLesson("o","O","O","Orange","برتقالة","🍊","O as in orange"),
    EnglishLetterLesson("p","P","P","Penguin","بطريق","🐧","P as in penguin"),
    EnglishLetterLesson("q","Q","Q","Queen","ملكة","👑","Q as in queen"),
    EnglishLetterLesson("r","R","R","Rabbit","أرنب","🐰","R as in rabbit"),
    EnglishLetterLesson("s","S","S","Sun","شمس","☀️","S as in sun"),
    EnglishLetterLesson("t","T","T","Tiger","نمر","🐯","T as in tiger"),
    EnglishLetterLesson("u","U","U","Umbrella","مظلة","☂️","U as in umbrella"),
    EnglishLetterLesson("v","V","V","Violin","كمان","🎻","V as in violin"),
    EnglishLetterLesson("w","W","W","Whale","حوت","🐋","W as in whale"),
    EnglishLetterLesson("x","X","X","Xylophone","آلة إكسيلوفون","🎼","X as in xylophone"),
    EnglishLetterLesson("y","Y","Y","Yo-yo","يويو","🪀","Y as in yo-yo"),
    EnglishLetterLesson("z","Z","Z","Zebra","حمار وحشي","🦓","Z as in zebra")
)

private fun englishNumberText(number: Int): String = when (number) {
    in 1..20 -> listOf("","one","two","three","four","five","six","seven","eight","nine","ten",
        "eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen","eighteen","nineteen","twenty")[number]
    else -> {
        val tens = listOf("","","twenty","thirty","forty","fifty","sixty","seventy","eighty","ninety")
        if (number % 10 == 0) tens[number / 10] else "${tens[number / 10]}-${englishNumberText(number % 10)}"
    }
}

private fun arabicDigits(number: Int): String =
    number.toString().map { if (it in '0'..'9') ('٠'.code + (it - '0')).toChar() else it }.joinToString("")

@Composable
fun EnglishLevelOnePage(onBack: () -> Unit, initialSection: Int = 0) {
    var section by remember { mutableIntStateOf(initialSection.coerceIn(0, 1)) }
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Row(
                Modifier.fillMaxWidth().background(Color(0xFFF4F8FF)).padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "رجوع") }
                Column(Modifier.weight(1f)) {
                    Text("الإنجليزية — المستوى الأول", fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                    Text("شرح عربي مع تعلّم الحروف والأرقام الإنجليزية", fontSize = 12.sp, color = Color(0xFF5B6B82))
                }
            }
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding)
                .background(Brush.verticalGradient(listOf(Color(0xFFF7FBFF), Color(0xFFE7F0FF))))
        ) {
            TabRow(selectedTabIndex = section, containerColor = Color.White) {
                Tab(section == 0, { section = 0 }) {
                    Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔤", fontSize = 22.sp); Text("الحروف", fontWeight = FontWeight.Bold)
                    }
                }
                Tab(section == 1, { section = 1 }) {
                    Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔢", fontSize = 22.sp); Text("الأرقام", fontWeight = FontWeight.Bold)
                    }
                }
            }
            AnimatedContent(section, label = "english_sections") { if (it == 0) EnglishLettersSection() else EnglishNumbersSection() }
        }
    }
}

@Composable
private fun EnglishLettersSection() {
    val context = LocalContext.current
    var caseTab by remember { mutableIntStateOf(0) }
    var index by remember { mutableIntStateOf(0) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    val lesson = englishLetters[index]
    val activeColor by animateColorAsState(
        listOf(Color(0xFF2563EB),Color(0xFF7C3AED),Color(0xFF059669),Color(0xFFEA580C))[index % 4],
        label = "letter_color"
    )

    DisposableEffect(context) {
        val engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.ENGLISH
                tts?.setSpeechRate(0.82f)
                tts?.setPitch(1.0f)
            }
        }
        tts = engine
        onDispose { engine.stop(); engine.shutdown() }
    }

    Column(Modifier.fillMaxSize().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("الحروف — Letters", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
        Text("الحروف الصغيرة والكبيرة، صوت الحرف، اسم الحرف، وكلمة تعريفية.",
            Modifier.padding(top = 4.dp), fontSize = 13.sp, color = Color(0xFF61728B), textAlign = TextAlign.Center)

        TabRow(caseTab, modifier = Modifier.padding(top = 12.dp), containerColor = Color.White) {
            Tab(caseTab == 0, { caseTab = 0 }) { Text("حروف صغيرة  a–z", Modifier.padding(12.dp), fontWeight = FontWeight.Bold) }
            Tab(caseTab == 1, { caseTab = 1 }) { Text("حروف كبيرة  A–Z", Modifier.padding(12.dp), fontWeight = FontWeight.Bold) }
        }

        Card(Modifier.fillMaxWidth().padding(top = 14.dp).shadow(10.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(Color.White.copy(alpha = .97f))) {
            Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (caseTab == 0) lesson.lower else lesson.upper, fontSize = 104.sp, fontWeight = FontWeight.Black, color = activeColor)
                Text("الحرف الحالي: ${lesson.lower.uppercase()} / ${lesson.lower}", fontSize = 13.sp, color = Color(0xFF66758B))
                Spacer(Modifier.height(8.dp))
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(Color(0xFFF1F6FF))) {
                    Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(lesson.picture, fontSize = 54.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(lesson.example, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                            Text(lesson.arabicMeaning, fontSize = 15.sp, color = Color(0xFF4F6078))
                            Text("صورة تعريفية تساعد الطفل على ربط الحرف بالكلمة.", fontSize = 11.sp, color = Color(0xFF77869A))
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text("اسم الحرف: ${lesson.name}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("الصوت التدريبي: ${lesson.soundHint}", fontSize = 14.sp, color = activeColor)
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(modifier = Modifier.weight(1f), onClick = { tts?.speak(lesson.name, TextToSpeech.QUEUE_FLUSH, null, "letter_name") },
                        colors = ButtonDefaults.buttonColors(Color(0xFF5B4BCE))) {
                        Text("🔊"); Spacer(Modifier.width(5.dp)); Text("اسم الحرف")
                    }
                    Button(modifier = Modifier.weight(1f), onClick = { tts?.speak(lesson.soundHint, TextToSpeech.QUEUE_FLUSH, null, "letter_sound") },
                        colors = ButtonDefaults.buttonColors(activeColor)) {
                        Text("🔉"); Spacer(Modifier.width(5.dp)); Text("صوت الحرف")
                    }
                }
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { tts?.speak(lesson.example, TextToSpeech.QUEUE_FLUSH, null, "example") }) {
                    Text("🔊 اسمع الكلمة: ${lesson.example}")
                }
                Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(modifier = Modifier.weight(1f), enabled = index > 0, onClick = { index-- }, shape = RoundedCornerShape(18.dp)) { Text("السابق") }
                    Button(modifier = Modifier.weight(1f), enabled = index < englishLetters.lastIndex, onClick = { index++ }, shape = RoundedCornerShape(18.dp)) { Text("التالي") }
                }
                Spacer(Modifier.height(5.dp))
                Text("${index + 1} / ${englishLetters.size}", fontWeight = FontWeight.Bold, color = activeColor)
            }
        }
    }
}

@Composable
private fun EnglishNumbersSection() {
    val context = LocalContext.current
    var number by remember { mutableIntStateOf(1) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(context) {
        val engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.ENGLISH
                tts?.setSpeechRate(0.82f)
                tts?.setPitch(1.0f)
            }
        }
        tts = engine
        onDispose { engine.stop(); engine.shutdown() }
    }

    val english = englishNumberText(number)
    val color = listOf(Color(0xFF2563EB),Color(0xFF7C3AED),Color(0xFF059669),Color(0xFFEA580C))[number % 4]

    Column(Modifier.fillMaxSize().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("الأرقام — Numbers", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
        Text("من 1 إلى 100، مع الرقم الكبير والاسم الإنجليزي والشرح العربي والنطق.",
            Modifier.padding(top = 4.dp), fontSize = 13.sp, color = Color(0xFF61728B), textAlign = TextAlign.Center)

        Card(Modifier.fillMaxWidth().padding(top = 14.dp).shadow(10.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(Color.White.copy(alpha = .97f))) {
            Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(arabicDigits(number), fontSize = 88.sp, fontWeight = FontWeight.Black, color = color)
                Text(number.toString(), fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Text(english, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = color)
                Spacer(Modifier.height(8.dp))
                Text("هذا هو العدد رقم ${arabicDigits(number)}. نتعلم شكله واسمه ونطقه باللغة الإنجليزية.",
                    Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 15.sp, color = Color(0xFF53647A))
                Spacer(Modifier.height(10.dp))
                Button(onClick = { tts?.speak(english, TextToSpeech.QUEUE_FLUSH, null, "number") },
                    shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(color)) {
                    Text("🔊"); Spacer(Modifier.width(6.dp)); Text("نطق العدد بالإنجليزية")
                }
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(modifier = Modifier.weight(1f), enabled = number > 1, onClick = { number-- }, shape = RoundedCornerShape(18.dp)) { Text("السابق") }
                    Button(modifier = Modifier.weight(1f), enabled = number < 100, onClick = { number++ }, shape = RoundedCornerShape(18.dp)) { Text("التالي") }
                }
                Spacer(Modifier.height(5.dp))
                Text("العدد ${arabicDigits(number)} من ١٠٠", fontWeight = FontWeight.Bold, color = color)
            }
        }
    }
}