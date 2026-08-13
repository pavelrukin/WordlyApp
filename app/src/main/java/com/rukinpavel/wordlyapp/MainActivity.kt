package com.rukinpavel.wordlyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rukinpavel.wordlyapp.core.ui.WordlyTheme
import com.rukinpavel.wordlyapp.feature.game.GameScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WordlyTheme {
                GameScreen()
            }
        }
    }
}
