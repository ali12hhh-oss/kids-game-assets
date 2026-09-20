package com.ali12hhh.kidslearning.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ali12hhh.kidslearning.core.LearningCatalog

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppRoutes.HOME) {
        composable(AppRoutes.HOME) {
            PlaceholderPage(title = "تعلّم مع دبدوب", subtitle = "اختر نشاطك") {
                RouteButton("الحروف العربية") { navController.navigate(AppRoutes.ARABIC_LETTERS) }
                RouteButton("English Letters") { navController.navigate(AppRoutes.ENGLISH_LETTERS) }
                RouteButton("الأرقام والعدّ") { navController.navigate(AppRoutes.NUMBERS) }
                RouteButton("وقت اللعب") { navController.navigate(AppRoutes.PLAY) }
            }
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
private fun PlaceholderPage(title: String, subtitle: String, content: @Composable () -> Unit) {
    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.headlineMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyLarge)
            content()
        }
    }
}

@Composable
private fun ContentPage(title: String, content: String) {
    PlaceholderPage(title, content) {
        Text("المحتوى التعليمي الأولي — ستتم إضافة الصوت والتفاعل في المرحلة التالية.")
    }
}

@Composable
private fun RouteButton(label: String, onClick: () -> Unit) {
    Button(onClick = onClick) { Text(label) }
}
