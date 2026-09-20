package com.ali12hhh.kidslearning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { KidsLearningApp() }
    }
}

private val Ink = Color(0xFF24324B)
private val Sky = Color(0xFF75C9F2)
private val Mint = Color(0xFF68D6B2)
private val Coral = Color(0xFFFFA58B)
private val Sun = Color(0xFFFFD76A)

@Composable
fun KidsLearningApp() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF7FAFF)) {
            Column(
                modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color(0xFFEAF7FF), Color(0xFFFFF8E8)))
                ).padding(horizontal = 22.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("مرحباً يا بطل!", color = Ink, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                        Text("هيا نتعلم ونلعب معاً", color = Color(0xFF64748B), fontSize = 16.sp)
                    }
                    Box(
                        modifier = Modifier.background(Sun, RoundedCornerShape(20.dp)).padding(horizontal = 15.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("⭐ 0", color = Ink, fontWeight = FontWeight.Bold, fontSize = 17.sp) }
                }

                Spacer(Modifier.height(22.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(190.dp)
                        .background(Brush.linearGradient(listOf(Color(0xFF7BD7D1), Color(0xFF8AC7F7))), RoundedCornerShape(30.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🌈", fontSize = 36.sp)
                        Text("رحلتنا تبدأ هنا!", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                        Text("اختر نشاطاً واكتشف شيئاً جديداً", color = Color.White, fontSize = 15.sp, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(10.dp))
                        Text("🧸", fontSize = 48.sp)
                    }
                }

                Spacer(Modifier.height(22.dp))
                Text("ماذا تريد أن تتعلم اليوم؟", modifier = Modifier.fillMaxWidth(), color = Ink, fontSize = 21.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Start)
                Spacer(Modifier.height(12.dp))
                ActivityTile("🔤", "الحروف العربية", "نتعرف على الحروف ونسمع نطقها", Coral) { }
                Spacer(Modifier.height(10.dp))
                ActivityTile("🔠", "English Letters", "Learn letters and their sounds", Sky) { }
                Spacer(Modifier.height(10.dp))
                ActivityTile("🔢", "الأرقام والعدّ", "نعدّ ونكتشف الأرقام", Mint) { }
                Spacer(Modifier.height(10.dp))
                ActivityTile("🎮", "وقت اللعب", "ألعاب صغيرة وتحديات ممتعة", Sun) { }
                Spacer(Modifier.weight(1f))
                Text("نتعلم خطوة بخطوة ✨", color = Color(0xFF8793A7), fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun ActivityTile(emoji: String, title: String, subtitle: String, tint: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(76.dp),
        shape = RoundedCornerShape(22.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Ink),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.background(tint.copy(alpha = 0.28f), RoundedCornerShape(16.dp)).padding(10.dp)) {
                Text(emoji, fontSize = 26.sp)
            }
            Column(modifier = Modifier.weight(1f).padding(start = 14.dp)) {
                Text(title, color = Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color(0xFF78869B), fontSize = 12.sp)
            }
            Text("‹", color = tint, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        }
    }
}
