package mx.edu.utez.musicp.viewmodel

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import mx.edu.utez.musicp.data.dao.SongDao
import mx.edu.utez.musicp.data.model.Song

// 💡 MusicViewModel ahora requiere SongDao en el constructor para interactuar con Room.
class  MusicViewModel(private val songDao: SongDao) : ViewModel() {

    // --- ESTADOS REACTIVOS ---
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition.asStateFlow()

    // Job para recolectar el Flow de Room (necesario para la carga asíncrona)
    private var songLoadingJob: Job? = null

    // ------------------ CARGA Y LÓGICA PRINCIPAL (ROOM) ------------------

    /**
     * Carga las canciones de la Playlist dada desde la base de datos Room.
     * Este es el reemplazo del filtro hardcodeado.
     */
    fun loadSongsForPlaylist(playlistId: Int) {
        // 1. Cancelar cualquier carga anterior para evitar conflictos
        songLoadingJob?.cancel()

        // 2. Iniciar un nuevo Job para recolectar el Flow de Room
        songLoadingJob = viewModelScope.launch(Dispatchers.IO) {
            // songDao.getSongsByPlaylistId devuelve un Flow, que recolectamos (collect)
            songDao.getSongsByPlaylistId(playlistId).collectLatest { songsList ->
                // Actualizar el estado en el hilo principal
                _songs.value = songsList
                // Si no hay canción actual, establecer la primera
                if (_currentSong.value == null) {
                    _currentSong.value = songsList.firstOrNull()
                }
            }
        }
    }

    // ------------------ ADICIÓN DE CANCIONES LOCALES ------------------

    /**
     * Procesa la URI del archivo local, extrae metadatos y guarda la nueva canción en Room.
     */
    fun saveLocalSong(songUri: Uri, playlistId: Int, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {

            // 🚨 1. PASO CRÍTICO: PERSISTENCIA DEL PERMISO DE URI
            // Esto asegura que el MusicService pueda acceder al archivo de audio
            // aunque la Activity que seleccionó el archivo ya se haya cerrado.
            try {
                val contentResolver = context.contentResolver
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION

                // Solicitar permiso persistente para la URI
                contentResolver.takePersistableUriPermission(songUri, takeFlags)

            } catch (e: Exception) {
                // Manejar si el permiso persistente falla (algunos dispositivos lo restringen)
                android.util.Log.e("SAVE_SONG", "Fallo al obtener permiso persistente para la URI: ${e.message}")
                // Si esto falla, el archivo solo sonará mientras la app esté activa.
            }

            // 2. Extraer Metadatos
            val metadata = extractSongMetadata(songUri, context)

            // 3. Crear el objeto Song
            val newSong = Song(
                // Asumo que tu modelo Song ha sido actualizado para usar fileUri: String
                title = metadata["title"] ?: "Canción Desconocida",
                artist = metadata["artist"] ?: "Artista Desconocido",
                duration = metadata["duration"] ?: "0:00",
                fileUri = songUri.toString(), // URI guardada como String
                playlistId = playlistId,      // Enlace a la playlist
                id = 0 // Usamos el ID de Room como clave primaria única
            )

            // 4. Insertar en Room (SongDao debe estar inyectado)
            try {
                songDao.insertAll(listOf(newSong))
            } catch (e: Exception) {
                android.util.Log.e("SAVE_SONG", "Error al insertar en Room: ${e.message}")
            }
        }
    }

    // Función auxiliar para extraer título/duración.
    private fun extractSongMetadata(uri: Uri, context: Context): Map<String, String?> {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)

            return mapOf(
                "title" to title,
                "artist" to artist,
                "duration" to formatMillis(duration)
            )
        } catch (e: Exception) {
            // En caso de que el archivo no sea accesible o el formato sea incorrecto
            e.printStackTrace()
            return emptyMap()
        } finally {
            retriever.release()
        }
    }

    // Función de ejemplo para formatear milisegundos a M:SS
    private fun formatMillis(millis: Long): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%d:%02d", minutes, remainingSeconds)
    }

    // ------------------ CONTROL DE REPRODUCCIÓN ------------------
    // Estas funciones usan la lista filtrada '_songs.value' que viene de Room.

    fun setCurrentSong(song: Song?) {
        _currentSong.value = song
    }

    fun setPlayingState(playing: Boolean) {
        _isPlaying.value = playing
    }

    fun setCurrentPosition(position: Int) {
        _currentPosition.value = position
    }

    fun getNextSong(currentSongId: Int): Song? {

        // 1. Necesitamos el objeto Song completo que está sonando
        val currentSong = _currentSong.value

        // 2. Si no hay canción actual, no podemos calcular la siguiente.
        if (currentSong == null) {
            return _songs.value.firstOrNull()
        }

        val index = _songs.value.indexOfFirst { it.id == currentSongId }


        // 4. Determinar la siguiente canción
        return if (index != -1 && index < _songs.value.size - 1) {
            // Si no es la última, devuelve la siguiente
            _songs.value[index + 1]
        } else {
            // Si es la última o no se encuentra, devuelve la primera para repetir
            _songs.value.firstOrNull()
        }
    }

    fun formatDuration(milliseconds: Int): String {
        val minutes = milliseconds / 1000 / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}