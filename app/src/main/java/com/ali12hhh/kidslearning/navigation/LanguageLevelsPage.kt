package com.ali12hhh.kidslearning.navigation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection

@Composable
fun LanguageLevelsPage(
    language: String,
    onBack: () -> Unit,
    onSelectLevel: (Int) -> Unit,
    onSpeak: (String, String) -> Unit
) {
    val arabic = language == "ar"
    CompositionLocalProvider(
        LocalLayoutDirection provides if (arabic) LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    if (arabic) listOf(Color(0xFFFFF3D8), Color(0xFFFFD6B8))
                    else listOf(Color(0xFFE4F5FF), Color(0xFFD6E4FF))
                )
            ).padding(18.dp)
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(if (arabic) "‹ رجوع" else "Back ›", Modifier.clickable { onBack() }.padding(8.dp), fontSize = 18.sp)
                Text(
                    if (arabic) "اختر مستواك التعليمي" else "Choose Your Learning Level",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    if (arabic) "هيا نتعلم خطوةً خطوة!" else "Let's learn step by step! · هيا نتعلم خطوةً خطوة!",
                    textAlign = TextAlign.Center,
                    color = Color(0xFF42536B)
                )
                Spacer(Modifier.height(4.dp))
                (1..3).forEach { level ->
                    val levelTitle = when (level) {
                        1 -> if (arabic) "المستوى الأول" else "Level 1 · المستوى الأول"
                        2 -> if (arabic) "المستوى الثاني" else "Level 2 · المستوى الثاني"
                        else -> if (arabic) "المستوى الثالث" else "Level 3 · المستوى الثالث"
                    }
                    val subtitle = when (level) {
                        1 -> if (arabic) "البداية والاكتشاف" else "Start & Discover · البداية والاكتشاف"
                        2 -> if (arabic) "التدرب والتعلّم" else "Practice & Learn · التدرب والتعلّم"
                        else -> if (arabic) "التحدي والإتقان" else "Challenge & Master · التحدي والإتقان"
                    }
                    val symbol = listOf("🌱", "🚀", "🏆")[level - 1]
                    Card(
                        modifier = Modifier.fillMaxWidth().animateContentSize().clickable {
                            onSpeak(levelTitle, if (arabic) "ar" else "en")
                            onSelectLevel(level)
                        },
                        shape = RoundedCornerShape(26.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f))
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 22.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(symbol, fontSize = 38.sp)
                            Column(Modifier.weight(1f)) {
                                Text(levelTitle, fontWeight = FontWeight.ExtraBold, fontSize = 21.sp, color = Color(0xFF25344E))
                                Text(subtitle, fontSize = 14.sp, color = Color(0xFF63728A))
                            }
                            Text("🔊", fontSize = 24.sp, modifier = Modifier.clickable {
                                onSpeak(levelTitle, if (arabic) "ar" else "en")
                            })
                        }
                    }
                }
            }
        }
    }
}
