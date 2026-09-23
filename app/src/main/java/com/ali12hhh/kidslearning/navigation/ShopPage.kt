package com.ali12hhh.kidslearning.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider

private data class StoreItem(
    val id: String,
    val title: String,
    val category: String,
    val price: Int,
    val art: Int
)

private val storeItems = listOf(
    StoreItem("title_01", "ملك النجوم", "ألقاب النجوم", 15, 1),
    StoreItem("title_02", "أمير المعرفة", "ألقاب النجوم", 20, 2),
    StoreItem("title_03", "نجم متألق", "ألقاب النجوم", 25, 3),
    StoreItem("title_04", "بطل الماس", "ألقاب النجوم", 30, 4),
    StoreItem("title_05", "نجم المستقبل", "ألقاب النجوم", 35, 5),
    StoreItem("title_06", "كأس التفوق", "ألقاب النجوم", 40, 6),
    StoreItem("title_07", "ساحر الكلمات", "ألقاب المعرفة", 45, 7),
    StoreItem("title_08", "حارس النجاح", "ألقاب المعرفة", 50, 8),
    StoreItem("title_09", "فارس الإنجاز", "ألقاب المعرفة", 55, 9),
    StoreItem("title_10", "بطل الشجاعة", "ألقاب المعرفة", 60, 10),
    StoreItem("title_11", "حالم النجوم", "ألقاب المعرفة", 20, 11),
    StoreItem("title_12", "صانع النور", "ألقاب المعرفة", 25, 12),
    StoreItem("title_13", "قلب ذهبي", "ألقاب الأبطال", 30, 13),
    StoreItem("title_14", "قوس الفرح", "ألقاب الأبطال", 35, 14),
    StoreItem("title_15", "كنز المعرفة", "ألقاب الأبطال", 40, 15),
    StoreItem("title_16", "نجم المرح", "ألقاب الأبطال", 45, 16),
    StoreItem("title_17", "صديق الجميع", "ألقاب الأبطال", 50, 17),
    StoreItem("title_18", "صانع الابتسامة", "ألقاب الأبطال", 55, 18),
    StoreItem("title_19", "بطل الحروف", "ألقاب التعلم", 60, 19),
    StoreItem("title_20", "فارس الأرقام", "ألقاب التعلم", 65, 20),
    StoreItem("title_21", "المستكشف الصغير", "ألقاب المغامرة", 35, 21),
    StoreItem("title_22", "أسطورة صغيرة", "ألقاب المغامرة", 45, 22),
    StoreItem("title_23", "بطل الانطلاق", "ألقاب المغامرة", 55, 23),
    StoreItem("title_24", "مستكشف الكواكب", "ألقاب المغامرة", 65, 24),
    StoreItem("title_25", "قائد الإبداع", "ألقاب الإبداع", 70, 25),
    StoreItem("title_26", "بطل التحدي", "ألقاب الإبداع", 75, 26),
    StoreItem("title_27", "قلب طيب", "ألقاب الإبداع", 80, 27),
    StoreItem("title_28", "سريع التعلم", "ألقاب الإبداع", 90, 28),
    StoreItem("title_29", "عقل لامع", "ألقاب الأساطير", 105, 29),
    StoreItem("title_30", "أسطورة التعلم", "ألقاب الأساطير", 120, 30)
)

@Composable
fun ShopPage(initialCollection: Boolean = false, onBack: () -> Unit) {
    val context = LocalContext.current
    var collection by remember { mutableStateOf(initialCollection) }
    var category by remember { mutableStateOf("الكل") }
    var stars by remember { mutableIntStateOf(AppSettings.childStars(context)) }
    var owned by remember { mutableStateOf(AppSettings.ownedItems(context)) }

    val categories = listOf(
        "الكل", "ألقاب النجوم", "ألقاب المعرفة", "ألقاب الأبطال",
        "ألقاب التعلم", "ألقاب المغامرة", "ألقاب الإبداع", "ألقاب الأساطير"
    )
    val visibleItems = if (collection) {
        storeItems.filter { it.id in owned }
    } else {
        storeItems.filter { category == "الكل" || it.category == category }
    }

    CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF3F7FF))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) { Text("رجوع", fontWeight = FontWeight.Bold) }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("متجر النجوم", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                    Text("مكافآت تشجّع الطفل على التعلّم", fontSize = 12.sp, color = Color(0xFF60708A))
                }
                Text("⭐ " + stars, fontWeight = FontWeight.Black, fontSize = 18.sp)
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { collection = false }, modifier = Modifier.weight(1f)) { Text("المتجر") }
                Button(onClick = { collection = true }, modifier = Modifier.weight(1f)) {
                    Text("مقتنياتي (" + owned.size + ")")
                }
            }

            if (!collection) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    categories.forEach { value ->
                        TextButton(onClick = { category = value }) {
                            Text(
                                if (value == category) "● " + value else value,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            if (visibleItems.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "لا توجد مقتنيات بعد. اجمع النجوم من الدروس ثم عد إلى المتجر ⭐",
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items((visibleItems.size + 1) / 2) { rowIndex ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val first = visibleItems[rowIndex * 2]
                            StoreItemCard(
                                item = first,
                                owned = first.id in owned,
                                stars = stars,
                                modifier = Modifier.weight(1f),
                                onBuy = {
                                    if (AppSettings.buyItem(context, first.id, first.price)) {
                                        stars = AppSettings.childStars(context)
                                        owned = AppSettings.ownedItems(context)
                                    }
                                }
                            )
                            if (rowIndex * 2 + 1 < visibleItems.size) {
                                val second = visibleItems[rowIndex * 2 + 1]
                                StoreItemCard(
                                    item = second,
                                    owned = second.id in owned,
                                    stars = stars,
                                    modifier = Modifier.weight(1f),
                                    onBuy = {
                                        if (AppSettings.buyItem(context, second.id, second.price)) {
                                            stars = AppSettings.childStars(context)
                                            owned = AppSettings.ownedItems(context)
                                        }
                                    }
                                )
                            } else {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreItemCard(
    item: StoreItem,
    owned: Boolean,
    stars: Int,
    modifier: Modifier,
    onBuy: () -> Unit
) {
    Card(
        modifier = modifier.shadow(5.dp, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StoreArtwork(item.art, item.title, Modifier.size(82.dp))
            Text(item.title, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
            Text(item.category, fontSize = 10.sp, color = Color(0xFF6B7890), textAlign = TextAlign.Center)
            Spacer(Modifier.height(5.dp))
            when {
                owned -> Text("✓ في مقتنياتك", color = Color(0xFF16803C), fontWeight = FontWeight.Black)
                stars >= item.price -> Button(onClick = onBuy, modifier = Modifier.fillMaxWidth()) {
                    Text("شراء ⭐ " + item.price, fontWeight = FontWeight.Black)
                }
                else -> Button(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) {
                    Text("تحتاج ⭐ " + item.price)
                }
            }
        }
    }
}

@Composable
private fun StoreArtwork(art: Int, description: String, modifier: Modifier = Modifier) {
    val icons = listOf(
        "👑","🏆","⭐","💎","🚀","🥇","🪄","🛡️","🎖️","🔥",
        "🌙","☀️","💖","🌈","📚","🎈","🎁","✨","🦸","🧠",
        "🧭","🌟","🏅","🪐","🎨","⚡","💝","🚀","📖","🏆"
    )
    Box(
        modifier = modifier.background(Color(0xFFF5F8FF), RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(icons[(art - 1).coerceIn(0, icons.lastIndex)], fontSize = 50.sp)
    }
}
