package mx.edu.utez.musicp.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import mx.edu.utez.musicp.data.network.RetrofitClient
import mx.edu.utez.musicp.data.repository.PlaylistRepository
import mx.edu.utez.musicp.ui.screens.AñadirScreen
import mx.edu.utez.musicp.ui.screens.EditScreen
import mx.edu.utez.musicp.ui.screens.LoginScreen
import mx.edu.utez.musicp.ui.screens.SongsListScreen
import mx.edu.utez.musicp.viewmodel.LoginViewModel
import mx.edu.utez.musicp.viewmodel.PlaylistViewModel
import mx.edu.utez.musicp.viewmodel.PlaylistViewModelFactory

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // --- 1. CONFIGURACIÓN E INYECCIÓN DEL VIEWMODEL ÚNICO (Playlist) ---
    // Esto previene el error "Cannot create an instance" al navegar.

    val repo = remember {
        // 💡 Ajusta esto a tu forma real de obtener el ApiService
        PlaylistRepository(RetrofitClient.api, context)
    }
    val factory = remember {
        PlaylistViewModelFactory(repo, context)
    }

    // 💡 INSTANCIA ÚNICA: Este ViewModel se crea con el Factory y se comparte.
    val playlistViewModel: PlaylistViewModel = viewModel(factory = factory)

    NavHost(navController = navController, startDestination = "login") {

        // 1. Ruta de Login (Usa su propio ViewModel)
        composable("login") {
            // El LoginViewModel NO requiere el Factory de Playlist
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(viewModel = loginViewModel, navController = navController)
        }

        // 2. Ruta Principal (Compartimos la instancia única)
        composable("main") {
            AppBottomNavBar(navController = navController, viewModel = playlistViewModel)
        }

        // 3. Ruta de Añadir Playlist (Compartimos la instancia única)
        composable("añadir") {
            AñadirScreen(viewModel = playlistViewModel, navController = navController)
        }


        // 4. Ruta de LISTA DE CANCIONES (Requiere ID)
        composable(
            route = "songs/{playlistId}",
            arguments = listOf(
                navArgument("playlistId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getInt("playlistId") ?: -1

            if (playlistId != -1) {
                // SongsListScreen requiere el playlistId (y su propio MusicViewModel)
                SongsListScreen(playlistId = playlistId)
            } else {
                android.util.Log.e("NAV_ERROR", "Error: playlistId nulo o inválido en la navegación a Songs.")
                navController.popBackStack()
            }
        }

        // 5. Ruta de EDICIÓN/ELIMINACIÓN (Requiere ID, Comparte el ViewModel)
        composable(
            route = "edit_playlist/{playlistId}",
            arguments = listOf(navArgument("playlistId") { defaultValue = -1; type = NavType.IntType })
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getInt("playlistId") ?: -1

            // 💡 SOLUCIÓN: Pasamos la instancia única 'playlistViewModel' creada en la raíz.
            EditScreen(
                viewModel = playlistViewModel, // <-- Usa la instancia correcta y compartida
                navController = navController,
                playlistId = playlistId
            )
        }
    }
}