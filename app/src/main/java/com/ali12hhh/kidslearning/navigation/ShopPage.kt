package com.ali12hhh.kidslearning.navigation

import androidx.compose.foundation.background
import androidx.core.content.edit
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class RewardTitle(val id: String, val name: String, val cost: Int)
private val premiumTitles = listOf(
    "تاج الملوك الصغار", "أسطورة المجرّة", "عبقري الحروف", "قائد الأحلام", "جوهرة التميّز",
    "فارس الضوء", "بطل المستحيل", "صانع المعجزات", "العبقري اللامع", "أسطورة ريبو"
).mapIndexed { index, name -> RewardTitle("lucky_title_${(index + 1).toString().padStart(2, '0')}", name, 0) }
private val regularTitles = listOf(
    "ملك النجوم", "أمير المعرفة", "نجم متألق", "بطل الماس", "نجم المستقبل", "كأس التفوق", "ساحر الكلمات", "حارس النجاح", "فارس الإنجاز", "بطل الشجاعة",
    "حالم النجوم", "صانع النور", "قلب ذهبي", "قوس الفرح", "كنز المعرفة", "نجم المرح", "صديق الجميع", "صانع الابتسامة", "بطل الحروف", "فارس الأرقام",
    "المستكشف الصغير", "أسطورة صغيرة", "بطل الانطلاق", "مستكشف الكواكب", "قائد الإبداع", "بطل التحدي", "قلب طيب", "سريع التعلم", "عقل لامع", "أسطورة التعلم"
).mapIndexed { index, name -> RewardTitle("title_${(index + 1).toString().padStart(2, '0')}", name, 15 + index * 3) }

private val ink = Color(0xFF24345D)
private val muted = Color(0xFF667493)

@Composable
private fun ShopSectionHeading(title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp)) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Black, color = ink)
        Text(subtitle, fontSize = 12.sp, color = muted)
    }
}

@Composable
private fun RewardChestCard(
    title: String, icon: String, description: String, detail: String, buttonLabel: String,
    enabled: Boolean, colors: List<Color>, onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = colors.first()),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(Modifier.fillMaxWidth().background(Brush.horizontalGradient(colors)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(22.dp), color = Color.White.copy(alpha = 0.78f)) {
                Box(Modifier.size(76.dp), contentAlignment = Alignment.Center) { Text(icon, fontSize = 43.sp) }
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 22.sp, fontWeight = FontWeight.Black, color = ink)
                Spacer(Modifier.height(3.dp))
                Text(description, fontSize = 13.sp, color = ink.copy(alpha = 0.88f), lineHeight = 18.sp)
                Spacer(Modifier.height(7.dp))
                Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = 0.72f)) {
                    Text(detail, Modifier.padding(horizontal = 10.dp, vertical = 5.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ink)
                }
                Spacer(Modifier.height(9.dp))
                Button(onClick = onClick, enabled = enabled, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(15.dp), colors = ButtonDefaults.buttonColors(containerColor = ink)) {
                    Text(buttonLabel, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun ShopPage(initialCollection: Boolean = false, onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("kids_learning_settings", android.content.Context.MODE_PRIVATE) }
    var stars by remember { mutableIntStateOf(AppSettings.childStars(context)) }
    var owned by remember { mutableStateOf(AppSettings.ownedItems(context)) }
    var collection by remember { mutableStateOf(initialCollection) }
    var message by remember { mutableStateOf("") }
    var lastDaily by remember { mutableLongStateOf(prefs.getLong("daily_reward_last_open", 0L)) }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val dailyReady = now - lastDaily >= 24L * 60L * 60L * 1000L
    val allTitles = regularTitles + premiumTitles
    fun refresh() { stars = AppSettings.childStars(context); owned = AppSettings.ownedItems(context) }
    fun openDaily() {
        if (!dailyReady) return
        val prize = 5
        AppSettings.addStars(context, prize)
        val openedAt = System.currentTimeMillis()
        prefs.edit { putLong("daily_reward_last_open", openedAt) }
        lastDaily = openedAt; now = openedAt
        message = "🎉 ربحت $prize نجوم من هدية اليوم!"
        refresh()
    }
    fun openLuck() {
        val price = 25
        if (stars < price) { message = "تحتاج إلى $price نجمة لشراء المكافأة المميزة."; return }
        val reward = premiumTitles.firstOrNull { it.id !in owned }
        if (reward == null) { message = "لقد حصلت على جميع الألقاب المميزة."; return }
        AppSettings.addStars(context, -price)
        prefs.edit { putStringSet("owned_items", AppSettings.ownedItems(context) + reward.id) }
        message = "✨ حصلت على اللقب المميز: ${reward.name}"
        refresh()
    }

    Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFF5F7FF), Color(0xFFE8EEFF)))).padding(horizontal = 14.dp, vertical = 10.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("‹ رجوع", fontWeight = FontWeight.Bold, color = ink) }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("متجر النجوم", fontSize = 25.sp, fontWeight = FontWeight.Black, color = ink)
                Text("كل إنجاز يستحق مكافأة ⭐", fontSize = 12.sp, color = muted)
            }
            Surface(shape = RoundedCornerShape(50), color = Color(0xFFFFF0B8), shadowElevation = 3.dp) {
                Text("⭐ $stars", Modifier.padding(horizontal = 13.dp, vertical = 9.dp), fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFF805500))
            }
        }
        Spacer(Modifier.height(12.dp))
        Surface(shape = RoundedCornerShape(18.dp), color = Color.White, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.padding(5.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                if (collection) {
                    OutlinedButton(onClick = { collection = false }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) { Text("🛍 المتجر") }
                    Button(onClick = { collection = true }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) { Text("🎒 مقتنياتي (${owned.size})") }
                } else {
                    Button(onClick = { collection = false }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) { Text("🛍 المتجر") }
                    OutlinedButton(onClick = { collection = true }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) { Text("🎒 مقتنياتي (${owned.size})") }
                }
            }
        }
        if (!collection) {
            LazyColumn(contentPadding = PaddingValues(bottom = 26.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                item { ShopSectionHeading("🎁 هدايا ومفاجآت", "ابدأ بصناديق المكافآت واجمع النجوم والألقاب النادرة") }
                item { RewardChestCard("مكافأة مميزة", "🎁", "مكافأة ثابتة وواضحة: تحصل على اللقب المميز التالي من المجموعة.", "25 ⭐ • بدون عشوائية", "احصل على المكافأة", stars >= 25, listOf(Color(0xFFFFE7A3), Color(0xFFFFF4D5)), ::openLuck) }
                item { RewardChestCard("هدية اليوم", "🌈", "هدية يومية مجانية تمنحك 5 نجوم ثابتة.", if (dailyReady) "هدية اليوم جاهزة!" else "هدية جديدة كل 24 ساعة", "احصل على هديتك", dailyReady, listOf(Color(0xFFB9E8FF), Color(0xFFE3F6FF)), ::openDaily) }
                if (message.isNotBlank()) item {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F7E8))) {
                        Text(message, Modifier.fillMaxWidth().padding(14.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = Color(0xFF176534))
                    }
                }
                item { ShopSectionHeading("🏆 ألقاب الأبطال", "اختر لقبك المفضل وأظهر إنجازاتك") }
                items(regularTitles, key = { it.id }) { title ->
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(15.dp), color = Color(0xFFFFF3D0)) { Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) { Text("🏅", fontSize = 27.sp) } }
                            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                                Text(title.name, fontWeight = FontWeight.Black, fontSize = 16.sp, color = ink)
                                Text("لقب تشجيعي • أضفه إلى مجموعتك", fontSize = 11.sp, color = muted)
                            }
                            if (title.id in owned) Surface(shape = RoundedCornerShape(50), color = Color(0xFFDDF5E5)) {
                                Text("تمتلكه ✓", Modifier.padding(horizontal = 10.dp, vertical = 7.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF176534))
                            } else Button(onClick = {
                                if (AppSettings.buyItem(context, title.id, title.cost)) { message = "🎉 أصبح لقب ${title.name} من مقتنياتك!"; refresh() }
                                else message = "رصيدك من النجوم لا يكفي لشراء هذا اللقب."
                            }, shape = RoundedCornerShape(13.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)) { Text("⭐ ${title.cost}", fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }
        } else {
            Column(Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 8.dp)) {
                Text("🎒 مجموعتك الخاصة", fontSize = 21.sp, fontWeight = FontWeight.Black, color = ink)
                Text("الألقاب التي حصلت عليها ستبقى محفوظة هنا.", fontSize = 12.sp, color = muted)
            }
            val collectionItems = allTitles.filter { it.id in owned }
            if (collectionItems.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎁", fontSize = 48.sp)
                            Spacer(Modifier.height(10.dp))
                            Text("مقتنياتك بانتظار أول هدية!", fontSize = 18.sp, fontWeight = FontWeight.Black, color = ink, textAlign = TextAlign.Center)
                            Text("افتح الصندوق اليومي أو اجمع النجوم من الدروس لشراء ألقابك.", Modifier.padding(top = 7.dp), color = muted, textAlign = TextAlign.Center, fontSize = 13.sp)
                            Spacer(Modifier.height(14.dp))
                            Button(onClick = { collection = false }, shape = RoundedCornerShape(14.dp)) { Text("اكتشف المتجر") }
                        }
                    }
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(collectionItems, key = { it.id }) { title ->
                        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(3.dp)) {
                            Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = RoundedCornerShape(16.dp), color = if (title.id.startsWith("lucky_")) Color(0xFFEDE4FF) else Color(0xFFFFF3D0)) {
                                    Box(Modifier.size(52.dp), contentAlignment = Alignment.Center) { Text(if (title.id.startsWith("lucky_")) "💎" else "🏅", fontSize = 29.sp) }
                                }
                                Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                                    Text(title.name, fontWeight = FontWeight.Black, fontSize = 16.sp, color = ink)
                                    Text(if (title.id.startsWith("lucky_")) "لقب مميز • مكافأة المتجر" else "لقب مكتسب", fontSize = 11.sp, color = muted)
                                }
                                Surface(shape = RoundedCornerShape(50), color = Color(0xFFDDF5E5)) {
                                    Text("مملوك ✓", Modifier.padding(horizontal = 10.dp, vertical = 7.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF176534))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
