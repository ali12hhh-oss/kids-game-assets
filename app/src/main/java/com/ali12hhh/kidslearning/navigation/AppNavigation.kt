package com.ali12hhh.kidslearning.navigation

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalLayoutDirection
import io.github.sceneview.Scene
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.node.ModelNode
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ali12hhh.kidslearning.core.LearningCatalog

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppRoutes.HOME) {
        composable(AppRoutes.HOME) {
            HomePage(
                onArabic = { navController.navigate(AppRoutes.ARABIC_LETTERS) },
                onEnglish = { navController.navigate(AppRoutes.ENGLISH_LETTERS) },
                onPlay = { navController.navigate(AppRoutes.PLAY) }
            )
        }
        composable(AppRoutes.ARABIC_LETTERS) {
            ContentPage("الحروف العربية", LearningCatalog.arabicLetters.joinToString("  "))
        }
        composable(AppRoutes.ENGLISH_LETTERS) {
            ContentPage("English Letters", LearningCatalog.englishLetters.joinToString("  "))
        }
        composable(AppRoutes.NUMBERS) {
            ContentPage("الأرقام والعدّ", LearningCatalog.digits.joinToString("  "))
        }
        composable(AppRoutes.PLAY) {
            ContentPage("وقت اللعب", "منطقة الألعاب قيد التجهيز")
        }
    }
}

@Composable
private fun HomePage(
    onArabic: () -> Unit,
    onEnglish: () -> Unit,
    onPlay: () -> Unit
) {
    var darkMode by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showProfile by remember { mutableStateOf(false) }
    var showCollection by remember { mutableStateOf(false) }
    var avatar by remember { mutableStateOf("👦") }
    var selected by remember { mutableStateOf<String?>(null) }

    val background = if (darkMode) {
        Brush.verticalGradient(listOf(Color(0xFF172033), Color(0xFF253552)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFFF7FBFF), Color(0xFFE8F3FF)))
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TopAction("⚙️", "الإعدادات") { showSettings = true }
                        Text(
                            "تعلّم مع دبدوب",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        TopAction(if (darkMode) "☀️" else "🌙", "الوضع") {
                            darkMode = !darkMode
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    ProfileCard(
                        avatar = avatar,
                        onProfileClick = { showProfile = true },
                        onCollectionClick = { showCollection = true }
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "مرحبًا يا صديقي! اختر ماذا نتعلم اليوم.",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(6.dp))

                    RealCharacterHero()

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LearningCard(
                            modifier = Modifier.weight(1f),
                            icon = "A",
                            title = "English",
                            subtitle = "الحروف الإنجليزية",
                            selected = selected == "en",
                            onClick = {
                                selected = "en"
                                onEnglish()
                            }
                        )
                        LearningCard(
                            modifier = Modifier.weight(1f),
                            icon = "أ",
                            title = "العربية",
                            subtitle = "الحروف العربية",
                            selected = selected == "ar",
                            onClick = {
                                selected = "ar"
                                onArabic()
                            }
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BottomCard(
                            modifier = Modifier.weight(1f),
                            icon = "🎮",
                            title = "استراحة",
                            subtitle = "",
                            onClick = onPlay
                        )
                        BottomCard(
                            modifier = Modifier.weight(1f),
                            icon = "🛍️",
                            title = "المتجر",
                            subtitle = "استخدم نجومك",
                            onClick = { showCollection = true }
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                }

                if (showSettings) {
                    InfoDialog(
                        title = "الإعدادات",
                        text = "إعدادات التطبيق ستتوسع هنا لاحقًا، مع الحفاظ على وظائف التعلم واللعب."
                    ) { showSettings = false }
                }
                if (showCollection) {
                    InfoDialog(
                        title = "مقتنياتي",
                        text = "ستظهر هنا المقتنيات التي يشتريها الطفل بالنجوم."
                    ) { showCollection = false }
                }
                if (showProfile) {
                    AlertDialog(
                        onDismissRequest = { showProfile = false },
                        title = { Text("ملف الطفل") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("اختر صورة بسيطة للطفل:")
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    TextButton(onClick = {
                                        avatar = "👦"
                                        showProfile = false
                                    }) { Text("👦 ولد") }
                                    TextButton(onClick = {
                                        avatar = "👧"
                                        showProfile = false
                                    }) { Text("👧 بنت") }
                                }
                                Text(
                                    "يمكن إضافة اختيار صورة من معرض الهاتف في مرحلة ربط الملف الشخصي."
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showProfile = false }) {
                                Text("إغلاق")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TopAction(icon: String, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TextButton(onClick = onClick) {
            Text(icon, fontSize = 24.sp)
        }
        Text(label, fontSize = 10.sp)
    }
}

@Composable
private fun ProfileCard(
    avatar: String,
    onProfileClick: () -> Unit,
    onCollectionClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onProfileClick) {
                Text(avatar, fontSize = 36.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("صديقي الصغير", fontWeight = FontWeight.Bold)
                Text("ملف الطفل", fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⭐ 0", fontWeight = FontWeight.ExtraBold)
                Text("نجومي", fontSize = 11.sp)
            }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = onCollectionClick) {
                Text("مقتنياتي")
            }
        }
    }
}

@Composable
private fun RealCharacterHero() {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val model = remember(modelLoader) {
        runCatching {
            modelLoader.createModelInstance("Mannequin_Medium.glb")
        }.getOrNull()
    }

    val characterNode = remember(model) {
        model?.let { instance ->
            ModelNode(
                modelInstance = instance,
                autoAnimate = false,
                scaleToUnits = 0.70f
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp),
        contentAlignment = Alignment.Center
    ) {
        Scene(
            modifier = Modifier.size(190.dp),
            engine = engine,
            modelLoader = modelLoader,
            isOpaque = false,
            childNodes = listOfNotNull(characterNode)
        )
    }
}

@Composable
private fun LearningCard(
    modifier: Modifier,
    icon: String,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    Button(
        onClick = onClick,
        modifier = modifier
            .height(112.dp)
            .shadow(if (selected) 12.dp else 6.dp, shape),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFFFFD76A) else Color.White.copy(alpha = 0.96f),
            contentColor = Color(0xFF24324A)
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
            Text(title, fontWeight = FontWeight.ExtraBold)
            Text(subtitle, fontSize = 11.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun BottomCard(
    modifier: Modifier,
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(96.dp)
            .shadow(6.dp, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 30.sp)
            Spacer(Modifier.width(8.dp))
            Column {
                Text(title, fontWeight = FontWeight.ExtraBold)
                if (subtitle.isNotBlank()) {
                    Text(subtitle, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun InfoDialog(title: String, text: String, onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = { TextButton(onClick = onClose) { Text("إغلاق") } }
    )
}

@Composable
private fun ContentPage(title: String, content: String) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.headlineMedium)
            Text(content, textAlign = TextAlign.Center)
            Text("المحتوى التعليمي الأولي — ستتم إضافة الصوت والتفاعل في المرحلة التالية.")
        }
    }
}
