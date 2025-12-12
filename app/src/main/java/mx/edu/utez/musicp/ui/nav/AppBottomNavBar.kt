package mx.edu.utez.musicp.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import mx.edu.utez.musicp.ui.screens.AñadirScreen
import mx.edu.utez.musicp.ui.screens.LoginScreen
import mx.edu.utez.musicp.ui.screens.PlaylistScreen
import mx.edu.utez.musicp.ui.screens.SongsListScreen
import mx.edu.utez.musicp.viewmodel.LoginViewModel
import mx.edu.utez.musicp.viewmodel.PlaylistViewModel


@Composable
fun AppBottomNavBar(navController: NavController, viewModel: PlaylistViewModel) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    val navItems = listOf("Playlist", "Agregar", "Salir")

    Scaffold(
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            when (index) {
                                2 -> {
                                    // ÚNICA OPCIÓN que navega fuera del menú
                                    navController.navigate("login")
                                }
                                else -> {
                                    // Cambiar solo el contenido del scaffold
                                    selectedIndex = index
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Default.PlaylistPlay
                                    1 -> Icons.Default.PlaylistAdd
                                    2  -> Icons.Default.ExitToApp
                                    else -> Icons.Default.ExitToApp
                                },
                                contentDescription = label
                            )
                        },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { padding ->

        // --- EL CONTENIDO DEL MENU CAMBIA SIN NAVEGAR ---
        when (selectedIndex) {

            0 -> PlaylistScreen(
                viewModel = viewModel,
                navController = navController,
                modifier = Modifier.padding(padding)
            )

            1 -> AñadirScreen(
                viewModel = viewModel,
                navController = navController,
                modifier = Modifier.padding(padding)
            )


            // "Salir" no se dibuja aquí porque navega fuera del menú
        }
    }
}