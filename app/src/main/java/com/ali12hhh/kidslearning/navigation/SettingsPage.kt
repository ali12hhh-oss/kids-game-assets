package com.ali12hhh.kidslearning.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private enum class SettingsScreen { MAIN, PROFILE, SPEECH, PRIVACY, PARENT, DATA, TERMS, PRIVACY_POLICY, ABOUT }

@Composable
fun SettingsPage(onBack: () -> Unit) {
    val context = LocalContext.current
    var screen by remember { mutableStateOf(SettingsScreen.MAIN) }
    when (screen) {
        SettingsScreen.MAIN -> MainSettings(context, onBack) { screen = it }
        SettingsScreen.PROFILE -> ProfileSettings(context) { screen = SettingsScreen.MAIN }
        SettingsScreen.SPEECH -> SpeechSettings(context) { screen = SettingsScreen.MAIN }
        SettingsScreen.PRIVACY -> PrivacyPage { screen = SettingsScreen.MAIN }
        SettingsScreen.PARENT -> ParentControlsPage(context) { screen = SettingsScreen.MAIN }
        SettingsScreen.DATA -> DataProgressPage(context) { screen = SettingsScreen.MAIN }
        SettingsScreen.TERMS -> SimpleInfoPage("📜 شروط الاستخدام", "استخدام التطبيق مخصص للتعلّم والترفيه للأطفال تحت إشراف ولي الأمر.") { screen = SettingsScreen.MAIN }
        SettingsScreen.PRIVACY_POLICY -> PrivacyPolicyPage { screen = SettingsScreen.MAIN }
        SettingsScreen.ABOUT -> SimpleInfoPage("ℹ️ عن التطبيق", "تعلم مع ريبو\\nتطبيق تعليمي وترفيهي للأطفال.\\n\\nالإعدادات الظاهرة هنا مرتبطة بوظائف حقيقية داخل التطبيق.") { screen = SettingsScreen.MAIN }
    }
}

@Composable
private fun MainSettings(context: Context, onBack: () -> Unit, onOpen: (SettingsScreen) -> Unit) {
    var darkMode by remember { mutableStateOf(AppSettings.isDarkMode(context)) }
    var speech by remember { mutableStateOf(AppSettings.isSpeechEnabled(context)) }
    val bg = if (darkMode) Color(0xFF101827) else Color(0xFFF4F8FF)
    val card = if (darkMode) Color(0xFF1E2A3D) else Color.White
    val text = if (darkMode) Color.White else Color(0xFF1D2A3D)
    val secondary = if (darkMode) Color(0xFFB8C4D6) else Color(0xFF68788F)
    Scaffold(containerColor = bg, topBar = {
        Row(Modifier.fillMaxWidth().background(card).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("رجوع", fontWeight = FontWeight.Bold) }
            Text("⚙️  الإعدادات", Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, color = text)
            Spacer(Modifier.width(64.dp))
        }
    }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Header("🎨", "المظهر", secondary) }
            item { CardBox(card) { ToggleItem("🌙", "الوضع الليلي", "يُحفظ ويُطبّق على الشاشة الرئيسية", darkMode, text) { darkMode = it; AppSettings.setDarkMode(context, it) } } }
            item { Header("🗣️", "النطق", secondary) }
            item { CardBox(card) {
                ToggleItem("🗣️", "النطق الصوتي", "التحكم في النطق الصوتي", speech, text) { speech = it; AppSettings.setSpeechEnabled(context, it) }
                Divider()
                RowItem("🎙️", "إعدادات النطق", "سرعة النطق", text) { onOpen(SettingsScreen.SPEECH) }
            } }
            item { Header("🔒", "الخصوصية وحماية الطفل", secondary) }
            item { CardBox(card) {
                RowItem("🛡️", "الخصوصية", "البيانات المحلية وحماية الطفل", text) { onOpen(SettingsScreen.PRIVACY) }
                Divider()
                RowItem("👨‍👩‍👧", "رقابة الوالدين", "حماية الإعدادات الحساسة برمز PIN", text) { onOpen(SettingsScreen.PARENT) }
                Divider()
                RowItem("📊", "البيانات والتقدّم", "عرض وإدارة التقدّم المحلي", text) { onOpen(SettingsScreen.DATA) }
            } }
            item { Header("📄", "المعلومات", secondary) }
            item { CardBox(card) {
                RowItem("📜", "شروط الاستخدام", "معلومات الاستخدام", text) { onOpen(SettingsScreen.TERMS) }
                Divider()
                RowItem("🔐", "سياسة الخصوصية", "معلومات الخصوصية", text) { onOpen(SettingsScreen.PRIVACY_POLICY) }
                Divider()
                RowItem("ℹ️", "عن التطبيق", "معلومات التطبيق", text) { onOpen(SettingsScreen.ABOUT) }
            } }
        }
    }
}

@Composable private fun ProfileSettings(context: Context, onBack: () -> Unit) {
    var name by remember { mutableStateOf(AppSettings.childName(context)) }
    var saved by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try { context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) {}
            AppSettings.setChildImageUri(context, uri.toString())
        }
    }
    DetailScaffold("👤 ملف الطفل", onBack) {
        Text("صورة الطفل", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        val imageUri = AppSettings.childImageUri(context)
        if (imageUri != null) AsyncImage(model = imageUri, contentDescription = "صورة الطفل", modifier = Modifier.size(120.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { launcher.launch(arrayOf("image/*")) }) { Text("اختيار من المعرض") }
            OutlinedButton(onClick = { AppSettings.setChildImageUri(context, null) }) { Text("إزالة") }
        }
        Text("اسم الطفل", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        OutlinedTextField(name, { name = it }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Text("النجوم الحالية: ⭐ " + AppSettings.childStars(context), fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Text("🛍️ مقتنياتي", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
        val owned = AppSettings.ownedItems(context)
        Text(if (owned.isEmpty()) "لا توجد مقتنيات بعد." else owned.joinToString(" • ") {
            when (it) { "hat" -> "🎩 قبعة الدب"; "balloon" -> "🎈 بالون ملوّن"; "toy" -> "🧸 دمية صغيرة"; "car" -> "🚗 سيارة لعبة"; else -> it }
        }, color = Color(0xFF68788F))
        Button({ AppSettings.setChildName(context, name); saved = true }, Modifier.fillMaxWidth()) { Text("حفظ الملف") }
        if (saved) Text("تم حفظ ملف الطفل.", color = Color(0xFF16803C), fontWeight = FontWeight.Bold)
    }
}

@Composable private fun SpeechSettings(context: Context, onBack: () -> Unit) {
    var rate by remember { mutableFloatStateOf(AppSettings.speechRate(context)) }
    DetailScaffold("🎙️ إعدادات النطق", onBack) {
        Text("سرعة النطق: " + String.format("%.2f", rate), fontWeight = FontWeight.Bold)
        Slider(rate, { rate = it }, valueRange = .5f..1.5f)
        Text("يُحفظ هذا الإعداد ويُستخدم في التحية الصوتية القادمة.", color = Color(0xFF68788F))
        Button({ AppSettings.setSpeechRate(context, rate) }, Modifier.fillMaxWidth()) { Text("حفظ سرعة النطق") }
    }
}

@Composable private fun ParentControlsPage(context: Context, onBack: () -> Unit) {
    var pin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var verified by remember { mutableStateOf(!AppSettings.hasParentPin(context)) }
    var message by remember { mutableStateOf("") }
    DetailScaffold("👨‍👩‍👧 رقابة الوالدين", onBack) {
        if (!verified) {
            Text("أدخل رمز الوالدين للوصول إلى الحماية.", fontWeight = FontWeight.Bold)
            OutlinedTextField(pin, { pin = it }, label = { Text("PIN") }, singleLine = true)
            Button({ verified = AppSettings.verifyParentPin(context, pin); message = if (verified) "تم التحقق." else "رمز PIN غير صحيح." }) { Text("تحقق") }
        } else {
            Text("رمز الوالدين يحمي إدارة البيانات والتقدّم.", fontWeight = FontWeight.Bold)
            OutlinedTextField(newPin, { newPin = it }, label = { Text("PIN جديد من 4 أرقام") }, singleLine = true)
            Button(enabled = newPin.length == 4 && newPin.all(Char::isDigit), onClick = { AppSettings.setParentPin(context, newPin); newPin = ""; message = "تم حفظ رمز الوالدين." }) { Text("حفظ رمز الوالدين") }
        }
        if (message.isNotBlank()) Text(message, fontWeight = FontWeight.Bold)
    }
}

@Composable private fun DataProgressPage(context: Context, onBack: () -> Unit) {
    var showReset by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    DetailScaffold("📊 البيانات والتقدّم", onBack) {
        Text("النجوم المحفوظة محليًا: ⭐ " + AppSettings.childStars(context), fontWeight = FontWeight.Bold)
        Text("إعدادات الطفل وبيانات التقدّم الحالية محلية على الجهاز.", color = Color(0xFF68788F))
        Button({ showReset = true }, Modifier.fillMaxWidth()) { Text("إعادة ضبط التقدّم") }
        if (message.isNotBlank()) Text(message, color = Color(0xFF16803C), fontWeight = FontWeight.Bold)
    }
    if (showReset) AlertDialog(
        onDismissRequest = { showReset = false },
        title = { Text("تأكيد إعادة الضبط") },
        text = { Column { Text("سيتم حذف التقدّم والنجوم المحفوظة."); if (AppSettings.hasParentPin(context)) { Spacer(Modifier.height(10.dp)); OutlinedTextField(pin, { pin = it }, label = { Text("PIN الوالدين") }, singleLine = true) } } },
        confirmButton = { TextButton(onClick = { if (!AppSettings.hasParentPin(context) || AppSettings.verifyParentPin(context, pin)) { AppSettings.resetProgress(context); showReset = false; message = "تمت إعادة ضبط التقدّم." } else message = "رمز الوالدين غير صحيح." }) { Text("حذف التقدّم") } },
        dismissButton = { TextButton({ showReset = false }) { Text("إلغاء") } }
    )
}

@Composable
private fun PrivacyPolicyPage(onBack: () -> Unit) {
    val context = LocalContext.current
    DetailScaffold("🔐 سياسة الخصوصية", onBack) {
        Text("تطبيق تعلم مع ريبو يحفظ اسم الطفل وصورته وإعدادات التطبيق والتقدّم والنجوم والمقتنيات محليًا على الجهاز فقط. لا توجد حاليًا إعلانات أو حسابات أو خوادم أو تحليلات أو مشاركة لهذه البيانات مع أطراف أخرى.", fontSize = 16.sp, lineHeight = 25.sp)
        Text("الصورة يختارها ولي الأمر من منتقي الملفات في النظام، ولا يرسلها التطبيق إلى الإنترنت. النطق الصوتي يستخدم محرك تحويل النص إلى كلام الموجود على الجهاز.", fontSize = 16.sp, lineHeight = 25.sp)
        Text("يمكن لولي الأمر حذف بيانات التطبيق من صفحة البيانات والتقدّم أو من إعدادات Android. لا يحتفظ التطبيق بحسابات مستخدمين.", fontSize = 16.sp, lineHeight = 25.sp)
        Text("المطور: Ali12hhh-oss. للتواصل وطلبات الخصوصية: مستودع المشروع العام على GitHub.", fontSize = 16.sp, lineHeight = 25.sp)
        Button(onClick = {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ali12hhh-oss/kids-game-assets/blob/main/docs/privacy-policy.html")))
        }, modifier = Modifier.fillMaxWidth()) { Text("فتح صفحة الخصوصية العامة") }
    }
}

@Composable private fun PrivacyPage(onBack: () -> Unit) = SimpleInfoPage("🛡️ الخصوصية وحماية الطفل", "الإعدادات وملف الطفل والتقدّم الحالي تُحفظ محليًا على الجهاز. أي صلاحية جديدة مستقبلًا يجب أن تكون مرتبطة بميزة واضحة ويُشرح سبب استخدامها للوالدين.", onBack)
@Composable private fun SimpleInfoPage(title: String, body: String, onBack: () -> Unit) = DetailScaffold(title, onBack) { Text(body, fontSize = 16.sp, lineHeight = 25.sp) }
@Composable private fun DetailScaffold(title: String, onBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Scaffold(containerColor = Color(0xFFF4F8FF)) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { TextButton(onClick = onBack) { Text("‹ رجوع", fontWeight = FontWeight.Bold) }; Text(title, Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.width(56.dp)) }
            content()
        }
    }
}
@Composable private fun Header(icon: String, title: String, secondary: Color) { Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) { Text(icon, fontSize = 22.sp); Spacer(Modifier.width(8.dp)); Text(title, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = secondary) } }
@Composable private fun CardBox(card: Color, content: @Composable ColumnScope.() -> Unit) { Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = card), elevation = CardDefaults.cardElevation(5.dp)) { Column(Modifier.fillMaxWidth().padding(horizontal = 14.dp)) { content() } } }
@Composable private fun RowItem(icon: String, title: String, subtitle: String, text: Color, onClick: () -> Unit) { Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) { Text(icon, fontSize = 27.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = text); Text(subtitle, fontSize = 11.sp, color = Color(0xFF718096)) }; Text("‹", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = text) } }
@Composable private fun ToggleItem(icon: String, title: String, subtitle: String, checked: Boolean, text: Color, onChange: (Boolean) -> Unit) { Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) { Text(icon, fontSize = 27.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = text); Text(subtitle, fontSize = 11.sp, color = Color(0xFF718096)) }; Switch(checked, onChange) } }