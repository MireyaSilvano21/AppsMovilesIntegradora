package mx.edu.utez.musicp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import mx.edu.utez.musicp.ui.components.PlaylistList
import mx.edu.utez.musicp.viewmodel.PlaylistViewModel
import mx.edu.utez.peeeli.ui.components.texts.Label
import mx.edu.utez.peeeli.ui.components.texts.Title

@Composable
fun PlaylistScreen(viewModel : PlaylistViewModel, navController: NavController, modifier: Modifier = Modifier) {
    val playlist by viewModel.playlistUiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Title("Playlist")
        Spacer(modifier = Modifier.height(8.dp))

        // Mostramos la lista

        if (playlist.isEmpty()) {
            // Asumo que 'Label' es un Composable para mostrar texto
            Label("No hay playlists registradas.")
        } else {
            // 💡 CORRECCIÓN: Usamos la sintaxis de trailing lambda para pasar los dos callbacks
            PlaylistList(
                lista = playlist,

                // 1. onPlaylistClick (Para ir a canciones)
                onPlaylistClick = { playlistId ->
                    if (playlistId > 0) {
                        navController.navigate("songs/$playlistId")
                    } else {
                        Log.e(
                            "NAV_ERROR",
                            "Intento de navegación a canciones con ID inválido: $playlistId"
                        )
                    }
                },

                // 2.  (onEditClick: Para ir a edición)
                onEditClick = { playlistId ->
                    if (playlistId > 0) {
                        // Navegamos a la ruta de edición que creamos
                        navController.navigate("edit_playlist/$playlistId")
                    } else {
                        Log.e(
                            "NAV_ERROR",
                            "Intento de navegación a edición con ID inválido: $playlistId"
                        )
                    }
                }
            )
        }
    }
}