package mx.edu.utez.musicp.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.utez.musicp.data.model.Playlist
import mx.edu.utez.musicp.data.repository.PlaylistRepository
import kotlin.jvm.java

class PlaylistViewModel(private val repository: PlaylistRepository) : ViewModel() {


    // Usamos MutableStateFlow para la lista de playlists.
    private val _playlistsUiState = MutableStateFlow<List<Playlist>>(emptyList())
    val playlistUiState: StateFlow<List<Playlist>> = _playlistsUiState.asStateFlow()

    // ------------------ ESTADOS PARA LA EDICIÓN ------------------
    // Estado que contiene la playlist actualmente seleccionada para editar
    private val _playlistToEdit = MutableStateFlow<Playlist?>(null)
    val playlistToEdit: StateFlow<Playlist?> = _playlistToEdit.asStateFlow()


    init {
        fetchPlaylist() // Cargamos las playlists al iniciar
    }

    // ------------------ FUNCIONES DE CARGA ------------------

    // Función para obtener las playlists (la hemos renombrado consistentemente a fetchPlaylist)
    fun fetchPlaylist() {
        viewModelScope.launch {
            try {
                _playlistsUiState.value = repository.getPlaylist()
            } catch (e: Exception) {
                // Opcional: Manejar error, por ejemplo, dejando la lista vacía o mostrando un Toast
                android.util.Log.e("PLAYLIST_VM", "Error fetching playlists: ${e.message}")
            }
        }
    }

    // 💡 FUNCIÓN CORREGIDA: Obtener datos de la lista en memoria para editar
    fun loadPlaylistForEdit(playlistId: Int) {
        // Buscar la playlist por ID dentro del estado actual
        val currentList = _playlistsUiState.value
        val playlist = currentList.find { it.id == playlistId }

        // Actualizar el estado 'playlistToEdit'
        _playlistToEdit.value = playlist
    }

    // ------------------ FUNCIONES DE MANIPULACIÓN (CRUD) ------------------

    fun addNewPlaylist(titulo: String, descripcion : String, imageUri: Uri?) {
        viewModelScope.launch {
            repository.insertPlaylist(titulo, descripcion , imageUri)
            fetchPlaylist() // Refrescar la lista
        }
    }

    // 💡 FUNCIÓN CORREGIDA: Eliminar y recargar
    fun deletePlaylist(playlistId: Int) {
        viewModelScope.launch {
            try {
                repository.deletePlaylist(playlistId)
                //  CORRECCIÓN: Llamamos a fetchPlaylist
                fetchPlaylist()
            } catch (e: Exception) {
                android.util.Log.e("PLAYLIST_VM", "Error deleting playlist: ${e.message}")
            }
        }
    }

    // 💡 FUNCIÓN CORREGIDA: Actualizar y recargar
    fun updatePlaylist(
        playlistId: Int,
        titulo: String,
        descripcion: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            try {
                repository.updatePlaylist(playlistId, titulo, descripcion, imageUri)
                //  CORRECCIÓN: Llamamos a fetchPlaylist
                fetchPlaylist()
            } catch (e: Exception) {
                android.util.Log.e("PLAYLIST_VM", "Error updating playlist: ${e.message}")
            }
        }
    }
}

class PlaylistViewModelFactory(
    private val repository: PlaylistRepository,
    context: Context,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaylistViewModel::class.java)) {
            // El create solo pasa el repository
            return PlaylistViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}