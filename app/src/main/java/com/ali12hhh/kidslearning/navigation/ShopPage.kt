package com.ali12hhh.kidslearning.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

private data class RewardTitle(val id: String, val name: String, val cost: Int)
private val premiumTitles = listOf(
    RewardTitle("lucky_title_01", "تاج الملوك الصغار", 0),
    RewardTitle("lucky_title_02", "أسطورة المجرّة", 0),
    RewardTitle("lucky_title_03", "عبقري الحروف", 0),
    RewardTitle("lucky_title_04", "قائد الأحلام", 0),
    RewardTitle("lucky_title_05", "جوهرة التميّز", 0),
    RewardTitle("lucky_title_06", "فارس الضوء", 0),
    RewardTitle("lucky_title_07", "بطل المستحيل", 0),
    RewardTitle("lucky_title_08", "صانع المعجزات", 0),
    RewardTitle("lucky_title_09", "العبقري اللامع", 0),
    RewardTitle("lucky_title_10", "أسطورة دبدوب", 0)
)
private val regularTitles = listOf(
    "ملك النجوم", "أمير المعرفة", "نجم متألق", "بطل الماس", "نجم المستقبل",
    "كأس التفوق", "ساحر الكلمات", "حارس النجاح", "فارس الإنجاز", "بطل الشجاعة",
    "حالم النجوم", "صانع النور", "قلب ذهبي", "قوس الفرح", "كنز المعرفة",
    "نجم المرح", "صديق الجميع", "صانع الابتسامة", "بطل الحروف", "فارس الأرقام",
    "المستكشف الصغير", "أسطورة صغيرة", "بطل الانطلاق", "مستكشف الكواكب", "قائد الإبداع",
    "بطل التحدي", "قلب طيب", "سريع التعلم", "عقل لامع", "أسطورة التعلم"
).mapIndexed { index, name -> RewardTitle("title_${(index + 1).toString().padStart(2, '0')}", name, 15 + index * 3) }

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

    fun refresh() {
        stars = AppSettings.childStars(context)
        owned = AppSettings.ownedItems(context)
    }
    fun openDaily() {
        if (!dailyReady) return
        val prize = Random.nextInt(1, 11)
        AppSettings.addStars(context, prize)
        prefs.edit().putLong("daily_reward_last_open", System.currentTimeMillis()).apply()
        lastDaily = System.currentTimeMillis()
        now = lastDaily
        message = "🎉 ربحت $prize نجوم من صندوق افتح واربح!"
        refresh()
    }
    fun openLuck() {
        val price = 25
        if (stars < price) {
            message = "تحتاج إلى $price نجمة لشراء صندوق الحظ."
            return
        }
        AppSettings.addStars(context, -price)
        val unownedPremium = premiumTitles.filterNot { it.id in owned }
        val chooseTitle = unownedPremium.isNotEmpty() && Random.nextInt(100) < 35
        if (chooseTitle) {
            val reward = unownedPremium.random()
            // Record the award using the same persistent ownership collection as store items.
            val updated = AppSettings.ownedItems(context) + reward.id
            prefs.edit().putStringSet("owned_items", updated).apply()
            message = "✨ مبروك! حصلت على اللقب المميز: ${reward.name}"
        } else {
            val prize = Random.nextInt(10, 31)
            AppSettings.addStars(context, prize)
            message = "🌟 مبروك! ربحت $prize نجمة من صندوق الحظ!"
        }
        refresh()
    }

    Column(Modifier.fillMaxSize().background(Color(0xFFF2F6FF)).padding(14.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("رجوع", fontWeight = FontWeight.Bold) }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("متجر النجوم", fontSize = 25.sp, fontWeight = FontWeight.Black, color = Color(0xFF233B70))
                Text("مكافآت ومفاجآت صغيرة لأبطال التعلّم", fontSize = 12.sp, color = Color(0xFF697997))
            }
            Text("⭐ $stars", fontWeight = FontWeight.Black, fontSize = 18.sp)
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { collection = false }, modifier = Modifier.weight(1f)) { Text("المتجر") }
            OutlinedButton(onClick = { collection = true }, modifier = Modifier.weight(1f)) { Text("مقتنياتي (${owned.size})") }
        }
        if (!collection) {
            Spacer(Modifier.height(12.dp))
            // Reward chests intentionally lead the store, above every title/category.
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0C9))) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🎁", fontSize = 48.sp)
                    Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                        Text("صندوق الحظ", fontSize = 21.sp, fontWeight = FontWeight.Black, color = Color(0xFF754500))
                        Text("افتحه لتحصل على 10–30 ⭐ أو لقب نادر من 10 ألقاب خاصة.", color = Color(0xFF795D31), fontSize = 13.sp)
                        Text("السعر: 25 ⭐ • قابل للشراء والفتح باستمرار", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8D5A00))
                        Button(onClick = { openLuck() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB87512))) { Text("اشترِ وافتح الصندوق · 25 ⭐") }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFDFF3FF))) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🌈", fontSize = 48.sp)
                    Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                        Text("افتح واربح", fontSize = 21.sp, fontWeight = FontWeight.Black, color = Color(0xFF07517D))
                        Text("هدية مجانية عشوائية من 1 إلى 10 نجوم.", color = Color(0xFF315F7A), fontSize = 13.sp)
                        Text(if (dailyReady) "هدية اليوم جاهزة لك!" else "يمكنك فتحه مجددًا بعد اكتمال 24 ساعة.", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF075B91))
                        Button(onClick = { openDaily() }, enabled = dailyReady, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF147CB5))) { Text(if (dailyReady) "افتح هديتك المجانية الآن" else "تم فتح هدية اليوم ✓") }
                    }
                }
            }
            if (message.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F7E9))) {
                    Text(message, Modifier.padding(14.dp).fillMaxWidth(), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = Color(0xFF176534))
                }
            }
            Spacer(Modifier.height(14.dp))
            Text("🏆 ألقاب ومكافآت المتجر", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF263D70))
        } else {
            Spacer(Modifier.height(12.dp))
            Text("🎒 مقتنياتي", fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
        val shown = if (collection) allTitles.filter { it.id in owned } else regularTitles
        if (shown.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("لم تجمع مقتنيات بعد. جرّب الصندوق اليومي أو أكمل الدروس ⭐", textAlign = TextAlign.Center) }
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                items(shown) { title ->
                    val isPremium = title.id.startsWith("lucky_")
                    val cost = title.cost
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(if (isPremium) "💎" else "🏅", fontSize = 30.sp)
                            Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                                Text(title.name, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                Text(if (isPremium) "لقب نادر من صندوق الحظ" else "لقب تشجيعي", fontSize = 12.sp, color = Color(0xFF71809A))
                            }
                            when {
                                title.id in owned -> Text("في مقتنياتي ✓", color = Color(0xFF188044), fontWeight = FontWeight.Bold)
                                collection -> Unit
                                else -> Button(onClick = { if (AppSettings.buyItem(context, title.id, cost)) { message = "تم شراء لقب ${title.name}!"; refresh() } else { message = "رصيدك من النجوم لا يكفي لشراء هذا اللقب." } }) { Text("⭐ $cost") }
                            }
                        }
                    }
                }
            }
        }
    }
}
