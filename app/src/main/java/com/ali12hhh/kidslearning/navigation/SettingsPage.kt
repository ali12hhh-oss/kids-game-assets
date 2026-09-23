package com.ali12hhh.kidslearning.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsPage(onBack: () -> Unit) {
    var darkMode by remember { mutableStateOf(false) }
    var sounds by remember { mutableStateOf(true) }
    var speech by remember { mutableStateOf(true) }
    var notifications by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf<String?>(null) }

    val bg = if (darkMode) Color(0xFF101827) else Color(0xFFF4F8FF)
    val card = if (darkMode) Color(0xFF1E2A3D) else Color.White
    val text = if (darkMode) Color.White else Color(0xFF1D2A3D)
    val secondary = if (darkMode) Color(0xFFB8C4D6) else Color(0xFF68788F)

    Scaffold(
        containerColor = bg,
        topBar = {
            Row(
                Modifier.fillMaxWidth().background(card).padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) { Text("رجوع", fontWeight = FontWeight.Bold) }
                Text(
                    "⚙️  الإعدادات",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = text
                )
                Spacer(Modifier.width(64.dp))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { SettingsHeader("👤", "الحساب وملف الطفل", secondary) }
            item {
                SettingsCard(card, text, secondary) {
                    SettingRow("👦", "ملف الطفل", "الاسم، الصورة، النجوم والمقتنيات") {
                        showDialog = "profile"
                    }
                }
            }

            item { SettingsHeader("🎨", "المظهر واللغة", secondary) }
            item {
                SettingsCard(card, text, secondary) {
                    ToggleRow("🌙", "الوضع الليلي", "تغيير مظهر التطبيق", darkMode, { darkMode = it })
                    Divider()
                    SettingRow("🌐", "لغة التطبيق", "العربية / English") { showDialog = "language" }
                }
            }

            item { SettingsHeader("🔊", "الصوت والتعلّم", secondary) }
            item {
                SettingsCard(card, text, secondary) {
                    ToggleRow("🔔", "الأصوات", "أصوات الأزرار والتفاعل", sounds, { sounds = it })
                    Divider()
                    ToggleRow("🗣️", "النطق الصوتي", "نطق الحروف والكلمات والشرح", speech, { speech = it })
                    Divider()
                    SettingRow("🎙️", "إعدادات النطق", "السرعة والصوت واللغة") { showDialog = "speech" }
                }
            }

            item { SettingsHeader("🔔", "الإشعارات", secondary) }
            item {
                SettingsCard(card, text, secondary) {
                    ToggleRow("🔔", "الإشعارات", "إشعارات التعلّم والتذكير", notifications, { notifications = it })
                }
            }

            item { SettingsHeader("🔒", "الخصوصية وحماية الطفل", secondary) }
            item {
                SettingsCard(card, text, secondary) {
                    SettingRow("🛡️", "الخصوصية", "البيانات، الأذونات، التخزين والمعلومات التي يجمعها التطبيق") { showDialog = "privacy" }
                    Divider()
                    SettingRow("👨‍👩‍👧", "رقابة الوالدين", "إعدادات مخصصة للوالدين وحماية الطفل") { showDialog = "parent" }
                    Divider()
                    SettingRow("📊", "البيانات والتقدّم", "إدارة التقدّم والبيانات المحفوظة") { showDialog = "data" }
                }
            }

            item { SettingsHeader("📄", "المعلومات القانونية", secondary) }
            item {
                SettingsCard(card, text, secondary) {
                    SettingRow("📜", "شروط الاستخدام", "الشروط والأحكام") { showDialog = "terms" }
                    Divider()
                    SettingRow("🔐", "سياسة الخصوصية", "عرض سياسة الخصوصية") { showDialog = "privacyPolicy" }
                    Divider()
                    SettingRow("ℹ️", "عن التطبيق", "الإصدار والمعلومات") { showDialog = "about" }
                }
            }

            item { SettingsHeader("🧹", "إدارة التطبيق", secondary) }
            item {
                SettingsCard(card, text, secondary) {
                    SettingRow("♻️", "إعادة ضبط التقدّم", "حذف تقدّم الطفل والنجوم والمقتنيات المحفوظة") { showDialog = "reset" }
                }
            }
        }
    }

    showDialog?.let { type ->
        val title: String
        val body: String
        when (type) {
            "profile" -> { title = "ملف الطفل"; body = "هنا يمكن إدارة اسم الطفل وصورته ونجومه ومقتنياته." }
            "language" -> { title = "لغة التطبيق"; body = "يمكن اختيار العربية أو English. سيتم تطبيق اتجاه الواجهة المناسب للغة." }
            "speech" -> { title = "إعدادات النطق"; body = "يمكن تخصيص سرعة النطق والصوت واللغة بحسب محرك تحويل النص إلى كلام المتوفر على الجهاز." }
            "privacy" -> { title = "الخصوصية وحماية الطفل"; body = "هذا القسم مخصص للتحكم بالبيانات والأذونات وإعدادات حماية الطفل. سيتم عرض أي بيانات يجمعها التطبيق وأسباب استخدامها بشكل واضح." }
            "parent" -> { title = "رقابة الوالدين"; body = "قسم للوالدين لإدارة إعدادات الطفل، المشتريات، التقدّم، الإشعارات وأي ميزات تتطلب موافقة ولي الأمر." }
            "data" -> { title = "البيانات والتقدّم"; body = "يمكنك مراجعة البيانات المحلية والتقدّم المحفوظ وإدارة أو حذف البيانات عند الحاجة." }
            "terms" -> { title = "شروط الاستخدام"; body = "سيتم عرض شروط الاستخدام الرسمية للتطبيق هنا." }
            "privacyPolicy" -> { title = "سياسة الخصوصية"; body = "سيتم عرض سياسة الخصوصية الرسمية للتطبيق هنا مع توضيح البيانات المستخدمة وحقوق المستخدم والوالدين." }
            "about" -> { title = "عن التطبيق"; body = "تعلّم مع دبدوب\nتطبيق تعليمي وترفيهي للأطفال." }
            else -> { title = "إعادة ضبط التقدّم"; body = "سيتم طلب تأكيد إضافي قبل حذف تقدّم الطفل والنجوم والمقتنيات." }
        }
        AlertDialog(
            onDismissRequest = { showDialog = null },
            title = { Text(title, fontWeight = FontWeight.ExtraBold) },
            text = { Text(body) },
            confirmButton = { TextButton(onClick = { showDialog = null }) { Text("إغلاق") } }
        )
    }
}

@Composable
private fun SettingsHeader(icon: String, title: String, secondary: Color) {
    Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 22.sp)
        Spacer(Modifier.width(8.dp))
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = secondary)
    }
}

@Composable
private fun SettingsCard(card: Color, text: Color, secondary: Color, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = card),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 14.dp)) { content() }
    }
}

@Composable
private fun SettingRow(icon: String, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 27.sp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, fontSize = 11.sp, color = Color(0xFF718096))
        }
        Text("‹", fontSize = 28.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ToggleRow(icon: String, title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 27.sp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, fontSize = 11.sp, color = Color(0xFF718096))
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
