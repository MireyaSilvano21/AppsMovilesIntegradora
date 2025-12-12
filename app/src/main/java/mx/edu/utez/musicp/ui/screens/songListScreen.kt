package mx.edu.utez.musicp.ui.screens

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.IBinder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.utez.musicp.data.model.Song
import kotlinx.coroutines.flow.collectLatest
import mx.edu.utez.musicp.service.MusicService
import mx.edu.utez.musicp.ui.components.factory.MusicViewModelFactory
import mx.edu.utez.musicp.viewmodel.MusicViewModel
import kotlin.jvm.java
import kotlin.let
import kotlin.ranges.rangeTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsListScreen(playlistId:Int) {

    val context = LocalContext.current
    val viewModel: MusicViewModel = viewModel(factory = MusicViewModelFactory(context))
    val songs by viewModel.songs.collectAsState()
    //LAUNCHER PARA AÑADIR ARCHIVOS LOCALEs
    val songPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent() // Contrato para obtener contenido (archivos)
    ) { uri: Uri? ->
        // Este bloque se ejecuta cuando el usuario selecciona un archivo
        uri?.let {
            // 💡 2. Llamar al ViewModel para procesar la URI y guardar la metadata
            // Le pasamos el ID de la playlist actual
            viewModel.saveLocalSong(it, playlistId, context)
        }
    }

    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()

    var musicService by remember { mutableStateOf<MusicService?>(null) }
    var isBound by remember { mutableStateOf(false) }
    var hasProximitySensor by remember { mutableStateOf(false) }
    var isProximityNear by remember { mutableStateOf(false) }

    val connection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                val binder = service as MusicService.MusicBinder
                musicService = binder.getService()
                isBound = true

                // Verificar si el dispositivo tiene sensor de proximidad
                hasProximitySensor =
                    musicService?.getProximitySensorManager()?.hasProximitySensor() ?: false
            }

            override fun onServiceDisconnected(arg0: ComponentName) {
                isBound = false
                musicService = null
            }
        }
    }

    DisposableEffect(Unit) {
        val intent = Intent(context, MusicService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)

        onDispose {
            if (isBound) {
                context.unbindService(connection)
                isBound = false
            }
        }
    }

    // Observar cambios en el servicio de música
    LaunchedEffect(musicService) {
        musicService?.isPlaying?.collectLatest { playing ->
            viewModel.setPlayingState(playing)
        }
    }

    LaunchedEffect(musicService) {
        musicService?.currentPosition?.collectLatest { position ->
            viewModel.setCurrentPosition(position)
        }
    }

    // Observar cambios en el sensor de proximidad
    LaunchedEffect(musicService) {
        musicService?.getProximitySensorManager()?.isNear?.collectLatest { near ->
            isProximityNear = near
            if (near) {
                val current = viewModel.currentSong.value
                if (current != null) {
                    val next = viewModel.getNextSong(current.id)
                    if (next != null) {
                        viewModel.setCurrentSong(next)
                        musicService?.playSong(next)

                    } else{
                        musicService?.stopSong()
                    }
                }
            }

        }
    }
    LaunchedEffect(key1 = playlistId) {
        viewModel.loadSongsForPlaylist(playlistId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Todas las Canciones de la playlist ") }
                ,
                actions = {
                    if (hasProximitySensor) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = "Sensor de Proximidad Activo",
                            tint = if (isProximityNear) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(24.dp)
                        )
                    }
                }
            )
        } ,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // 💡 3. Disparar el selector de archivos (solo archivos de audio)
                    songPickerLauncher.launch("audio/mpeg") // Filtro para MP3
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir Canción")
            }
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            // Indicador del sensor de proximidad
            if (hasProximitySensor) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = "Sensor de Proximidad",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Control por Proximidad Activado",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "Acerca tu mano al sensor para pausar/reanudar",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Badge(
                            containerColor = if (isProximityNear) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (isProximityNear) "Cerca" else "Lejos",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }



            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(songs) { song ->
                    val isCurrent = currentSong?.songDbId == song.songDbId
                    SongItem(
                        song = song,
                        isCurrent = isCurrent,
                        isPlaying = isPlaying && isCurrent,
                        onPlayPause = {
                            if (isCurrent && isPlaying) {
                                musicService?.pauseSong()
                            } else {
                                viewModel.setCurrentSong(song)
                                musicService?.playSong(song)

                            }
                        }
                    )
                }
            }

            currentSong?.let { song ->
                CurrentPlayer(
                    song = song,
                    isPlaying = isPlaying,
                    currentPosition = currentPosition,
                    duration = musicService?.getDuration() ?: 0,
                    onPlayPause = {
                        if (isPlaying) {
                            musicService?.pauseSong()
                        } else {
                            musicService?.resumeSong()
                        }
                    },
                    onSeek = { position ->
                        musicService?.seekTo(position)
                    },
                    onStop = {
                        musicService?.stopSong()
                        viewModel.setCurrentSong(null)
                    }
                )
            }
        }
    }
}

// Los composables SongItem y CurrentPlayer se mantienen igual...
@Composable
fun SongItem(
    song:Song,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onPlayPause: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${song.artist} • ${song.duration}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onPlayPause,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying)
                        Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun CurrentPlayer(
    song: Song,
    isPlaying: Boolean,
    currentPosition: Int,
    duration: Int,
    onPlayPause: () -> Unit,
    onSeek: (Int) -> Unit,
    onStop: () -> Unit
) {
    val viewModel: MusicViewModel = viewModel()
    val safeDuration = if (duration > 0) duration else 1

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Reproduciendo ahora",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = song.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = viewModel.formatDuration(currentPosition),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.width(50.dp)
                )

                Slider(
                    value = currentPosition.toFloat(),
                    onValueChange = { onSeek(it.toInt()) },
                    // 🚨 APLICAMOS LA DURACIÓN SEGURA AQUÍ
                    valueRange = 0f..safeDuration.toFloat(),
                    modifier = Modifier.weight(1f),
                    // Deshabilitamos el Slider si la duración real es 0 (ej. archivo fallido)
                    enabled = duration > 0
                )

                Text(
                    text = viewModel.formatDuration(duration),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.width(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onPlayPause) {
                    Icon(
                        imageVector = if (isPlaying)
                            Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onStop) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Detener",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}