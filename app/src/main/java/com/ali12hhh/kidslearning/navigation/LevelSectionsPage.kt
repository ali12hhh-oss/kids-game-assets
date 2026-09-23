package com.ali12hhh.kidslearning.navigation

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalLayoutDirection

@Composable
fun LevelSectionsPage(
    language: String,
    level: Int,
    onBack: () -> Unit,
    onSelectSection: (String) -> Unit
) {
    val arabic = language == "ar"
    val title = if (arabic) "المستوى $level" else "Level $level"
    val sections = if (arabic) {
        listOf(
            Triple("reading", "📖", "القراءة"),
            Triple("math", "🔢", "الرياضيات")
        )
    } else {
        when (level) {
            1 -> listOf(
                Triple("letters", "🔤", "الحروف"),
                Triple("numbers", "🔢", "الأرقام")
            )
            2 -> listOf(
                Triple("words", "🧩", "كلمات"),
                Triple("counting", "🔢", "الأعداد")
            )
            else -> listOf(
                Triple("words", "🧩", "كلمات"),
                Triple("sentences", "💬", "جمل")
            )
        }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides if (arabic) LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    if (arabic) listOf(Color(0xFFFFF4DE), Color(0xFFFFD6C2))
                    else listOf(Color(0xFFE8F7FF), Color(0xFFD9E4FF))
                )
            ).padding(18.dp)
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (arabic) "‹ رجوع" else "Back ›",
                        Modifier.clickable { onBack() }.padding(8.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(8.dp))
                Text(title, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    if (arabic) "اختر القسم الذي تريد التعلّم فيه" else "Choose a learning section",
                    Modifier.padding(top = 6.dp),
                    fontSize = 15.sp,
                    color = Color(0xFF53647A),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(30.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    sections.forEach { (key, icon, label) ->
                        SectionCard(
                            modifier = Modifier.weight(1f),
                            icon = icon,
                            title = label,
                            onClick = { onSelectSection(key) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    modifier: Modifier,
    icon: String,
    title: String,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(30.dp)
    Card(
        onClick = onClick,
        modifier = modifier.height(220.dp),
        shape = shape,
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.97f))
    ) {
        Column(
            Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(icon, fontSize = 64.sp)
            Spacer(Modifier.height(18.dp))
            Text(
                title,
                fontSize = 23.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF25344E),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "اضغط للبدء",
                fontSize = 12.sp,
                color = Color(0xFF6A7890)
            )
        }
    }
}
