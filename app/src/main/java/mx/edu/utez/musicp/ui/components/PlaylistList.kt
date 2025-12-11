package mx.edu.utez.musicp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mx.edu.utez.musicp.data.model.Playlist

@Composable
fun PlaylistList(lista: List<Playlist>, onPlaylistClick: (Int) -> Unit,
                 onEditClick:(Int) -> Unit,
                 modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = lista, key = { it.id }) { playlist ->
            PlaylistCard(playlist, onCardClick = onPlaylistClick, onEditClick = onEditClick)
        }
    }
}