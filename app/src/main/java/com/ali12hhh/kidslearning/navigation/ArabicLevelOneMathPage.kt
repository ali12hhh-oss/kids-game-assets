package com.ali12hhh.kidslearning.navigation

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

private fun arabicDigits(value: Int): String =
    value.toString().map { if (it.isDigit()) ('٠'.code + (it - '0'.code)).toChar() else it }.joinToString("")

private fun ones(v: Int) = v % 10
private fun tens(v: Int) = (v / 10) % 10
private fun hundreds(v: Int) = v / 100

@Composable
fun ArabicLevelOneMathPage(onBack: () -> Unit) {
    var number by remember { mutableStateOf(1) }
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ready by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        lateinit var engine: TextToSpeech
        engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = engine.setLanguage(Locale.forLanguageTag("ar-XA"))
                if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) engine.language = Locale("ar")
                engine.setSpeechRate(0.82f)
                ready = true
            }
        }
        tts = engine
        onDispose { engine.stop(); engine.shutdown(); tts = null }
    }

    Column(Modifier.fillMaxSize().background(Color(0xFFF5F8FF)).padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("‹ رجوع", fontWeight = FontWeight.ExtraBold) }
            Spacer(Modifier.weight(1f))
            Text("الرياضيات • المستوى الأول", fontSize = 21.sp, fontWeight = FontWeight.Black, color = Color(0xFF24324A))
        }
        Spacer(Modifier.height(6.dp))
        Card(Modifier.fillMaxWidth().weight(1f).shadow(12.dp, RoundedCornerShape(28.dp)), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(Color.White)) {
            Column(Modifier.fillMaxSize().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
                Text("العدد " + arabicDigits(number), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF66748B))
                Box(Modifier.fillMaxWidth().background(Color(0xFFF1F5FF), RoundedCornerShape(26.dp)).padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                    Text(arabicDigits(number), fontSize = 88.sp, fontWeight = FontWeight.Black, color = numberColor(number))
                }
                if (number >= 10) PlaceValueCard(number) else SimpleUnitsCard(number)
                Button(onClick = { if (ready) tts?.speak(numberSpeech(number), TextToSpeech.QUEUE_FLUSH, null, "number_" + number) }, modifier = Modifier.height(54.dp), shape = RoundedCornerShape(18.dp)) {
                    Text("🔊  نطق العدد", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
                Text(arabicDigits(number) + " / ١٠٠", fontSize = 14.sp, color = Color(0xFF718099), fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(9.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { if (number > 1) number-- }, Modifier.weight(1f).height(58.dp), shape = RoundedCornerShape(19.dp), colors = ButtonDefaults.buttonColors(Color(0xFF5B6B88))) {
                Text("السابق ◀", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
            }
            Button(onClick = { if (number < 100) number++ }, Modifier.weight(1f).height(58.dp), shape = RoundedCornerShape(19.dp)) {
                Text("التالي ▶", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable private fun SimpleUnitsCard(number: Int) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color(0xFFF4F7FF))) {
        Column(Modifier.fillMaxWidth().padding(11.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("مرتبة العدد", fontWeight = FontWeight.Black)
            Text("آحاد", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF315CFF))
            Text(arabicDigits(number) + " = " + arabicDigits(number) + " آحاد", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable private fun PlaceValueCard(number: Int) {
    val t = tens(number); val o = ones(number); val h = hundreds(number)
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color(0xFFF4F7FF))) {
        Column(Modifier.fillMaxWidth().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("مراتب العدد", fontWeight = FontWeight.Black)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                PlaceValue("آحاد", arabicDigits(o), Color(0xFF315CFF))
                PlaceValue("عشرات", arabicDigits(t), Color(0xFF16A085))
                if (h > 0) PlaceValue("مئات", arabicDigits(h), Color(0xFFE67E22))
            }
            Text(if (number < 100) "العدد " + arabicDigits(number) + " يتكوّن من " + arabicDigits(t) + " عشرات و" + arabicDigits(o) + " آحاد" else "العدد ١٠٠ يتكوّن من ١ مئة و٠ عشرات و٠ آحاد", fontSize = 15.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}

@Composable private fun PlaceValue(title: String, value: String, color: Color) {
    Card(shape = RoundedCornerShape(15.dp), colors = CardDefaults.cardColors(color.copy(alpha = 0.12f))) {
        Column(Modifier.padding(horizontal = 15.dp, vertical = 7.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 25.sp, fontWeight = FontWeight.Black, color = color)
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}

private fun numberColor(number: Int) = listOf(Color(0xFF315CFF), Color(0xFFE64A6B), Color(0xFF16A085), Color(0xFFE67E22), Color(0xFF7A4DCE), Color(0xFF008C95))[(number - 1) % 6]

private fun numberSpeech(number: Int): String {
    val words = mapOf(1 to "واحد", 2 to "اثنان", 3 to "ثلاثة", 4 to "أربعة", 5 to "خمسة", 6 to "ستة", 7 to "سبعة", 8 to "ثمانية", 9 to "تسعة", 10 to "عشرة", 100 to "مئة")
    return words[number] ?: "العدد " + arabicDigits(number)
}
