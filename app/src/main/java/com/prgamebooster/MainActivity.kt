package com.prgamebooster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.prgamebooster.core.theme.PRGameBoosterTheme
import com.prgamebooster.navigation.PRGameBoosterNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PRGameBoosterTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PRGameBoosterNavHost()
                }
            }
        }
    }
}
