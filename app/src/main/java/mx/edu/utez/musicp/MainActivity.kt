package mx.edu.utez.musicp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import mx.edu.utez.musicp.ui.nav.AppNavigation
import mx.edu.utez.musicp.ui.theme.MusicPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicPTheme {
               AppNavigation()
            }
        }
    }
}