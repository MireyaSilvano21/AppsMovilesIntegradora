package mx.edu.utez.musicp.service


import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import mx.edu.utez.musicp.data.model.Song

class MusicService : Service() {
    private val binder = MusicBinder()
    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition

    private var currentSong: Song? = null
    private var currentSongId: Int = -1
    private lateinit var proximitySensorManager: ProximitySensorManager

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        proximitySensorManager = ProximitySensorManager(this)
        setupProximitySensor()
    }

    override fun onBind(intent: Intent): IBinder = binder

    private fun setupProximitySensor() {
        if (proximitySensorManager.hasProximitySensor()) {
            proximitySensorManager.startListening()
        }
    }

    fun getProximitySensorManager(): ProximitySensorManager {
        return proximitySensorManager
    }
    // Dentro de tu clase MusicService.kt

    fun playSong(song: Song) {

        // 1. Manejo de Pausa/Reinicio (Si es la misma canción)
        if (currentSong?.songDbId == song.songDbId && mediaPlayer?.isPlaying == true) {
            pauseSong()
            return
        }

        // 2. Liberar recursos anteriores
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer() // Crear nueva instancia

        try {
            val context = applicationContext
            val uri = Uri.parse(song.fileUri)

            // --- 🚨 PASO CLAVE: CONFIGURAR LISTENERS AQUÍ ---

            // 3. Configurar el Listener de ERROR
            mediaPlayer?.setOnErrorListener { mp, what, extra ->
                android.util.Log.e("MUSIC_SERVICE", "Error de reproducción: what=$what, extra=$extra")
                stopSong() // Detiene la reproducción si hay un error
                true // Devuelve true para indicar que el evento fue manejado
            }

            // 4. Configurar el Listener de PREPARACIÓN (Donde la reproducción inicia)
            mediaPlayer?.setOnPreparedListener { mp ->
                mp.start() // 💡 INICIA la reproducción solo cuando el archivo está cargado
                currentSong = song
                _isPlaying.value = true
                startPositionUpdater()
            }

            // 5. Configurar el Listener de COMPLECIÓN (para la siguiente canción)
            mediaPlayer?.setOnCompletionListener {
                _isPlaying.value = false
                // Llama a la lógica para la siguiente canción aquí
            }

            // --- 6. ESTABLECER FUENTE DE DATOS Y PREPARAR ---

            // Establecer la fuente de datos (URI local)
            mediaPlayer?.setDataSource(context, uri)

            // Iniciar la preparación (asíncrona)
            mediaPlayer?.prepareAsync()

        } catch (e: Exception) {
            android.util.Log.e("MUSIC_SERVICE", "Fallo al configurar la fuente de datos: ${e.message}", e)
            // Manejo de excepción si la URI es inválida
            _isPlaying.value = false
        }
    }

    fun pauseSong() {
        mediaPlayer?.pause()
        _isPlaying.value = false
    }

    fun resumeSong() {
        mediaPlayer?.start()
        _isPlaying.value = true
    }

    fun stopSong() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        _isPlaying.value = false
        _currentPosition.value = 0
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }

    override fun onDestroy() {
        super.onDestroy()
        proximitySensorManager.stopListening()
        mediaPlayer?.release()
    }
    private fun startPositionUpdater() {
        Thread {
            while (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                _currentPosition.value = mediaPlayer!!.currentPosition
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    return@Thread
                }
            }
        }.start()
    }
}