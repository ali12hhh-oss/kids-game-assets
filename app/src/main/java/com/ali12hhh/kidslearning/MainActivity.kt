package com.ali12hhh.kidslearning

import android.os.Bundle
import java.util.UUID
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.ali12hhh.kidslearning.navigation.AppNavigation
import com.ali12hhh.kidslearning.navigation.AppSettings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppSettings.awardSessionStars(this, UUID.randomUUID().toString())
        setContent {
            val showSplash = remember { mutableStateOf(true) }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (showSplash.value) {
                        ReboSplashScreen(
                            onFinished = { showSplash.value = false }
                        )
                    } else {
                        AppNavigation()
                    }
                }
            }
        }
    }
}
