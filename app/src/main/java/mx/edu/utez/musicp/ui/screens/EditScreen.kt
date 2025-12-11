package mx.edu.utez.musicp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import mx.edu.utez.musicp.R
import mx.edu.utez.musicp.viewmodel.PlaylistViewModel
import mx.edu.utez.peeeli.ui.components.texts.Title
import kotlin.let
import kotlin.text.isNotBlank


@Composable
fun EditScreen(viewModel: PlaylistViewModel, navController: NavController,playlistId: Int,modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isEditing = playlistId > 0
    val scrollState = rememberScrollState()
    var edittitulo by remember { mutableStateOf("") }
    var editdescripcion by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var currentImageUrl by remember { mutableStateOf<String?>(null) }
    val playlistToEdit by viewModel.playlistToEdit.collectAsState()



    // --- 2. LAUNCHER PARA LA GALERÍA ---
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                selectedImageUri = uri
                currentImageUrl = null
            }
        }
    )

    LaunchedEffect(playlistId) {
        if (isEditing) {
            // Cargar datos al ViewModel solo la primera vez que la pantalla se compone
            viewModel.loadPlaylistForEdit(playlistId)
        }
    }


    LaunchedEffect(playlistToEdit) {
        playlistToEdit?.let { p ->
            // Si la playlist cargada es diferente a la que estamos mostrando (o si es la primera carga)
            if (p.id == playlistId) {
                edittitulo = p.titulo
                editdescripcion = p.descripcion
                currentImageUrl = p.imagenUri
                selectedImageUri = null // Resetear la URI local si cargamos datos de la API
            }
        }
    }

    // --- FUNCIÓN DE ACCIÓN (Guardar o Actualizar) ---

    val onSaveOrUpdate: () -> Unit = {
        // 💡 Paso 1: Usamos la validación del botón como la única verificación de validez.
        // Si el botón está habilitado, los campos ya están llenos.

        if (isEditing) {
            // Lógica de ACTUALIZACIÓN (PUT)
            viewModel.updatePlaylist(
                playlistId,
                edittitulo,
                editdescripcion,
                selectedImageUri // Envía la URI local si se seleccionó una imagen nueva
            )
        } else {
            // Lógica de REGISTRO (POST)
            viewModel.addNewPlaylist(edittitulo, editdescripcion, selectedImageUri)
        }

        // Paso 2: Navegar de vuelta SÓLO si la acción se ejecutó
        navController.popBackStack()
    }




    // --- 6. LAYOUT ---
    Column(
        modifier = Modifier
            .fillMaxSize() // Usa fillMaxSize para que el Scroll funcione
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Title("Editar Playlist")

        // --- 7. VISTA PREVIA DE LA IMAGEN ---
        AsyncImage(
            model = selectedImageUri,
            contentDescription = "Foto de la nueva playlist",
            placeholder = painterResource(id = R.drawable.noposter), // Placeholder
            error = painterResource(id = R.drawable.noposter),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(180.dp)
                .height(270.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))

        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- 8. BOTONES PARA SELECCIONAR IMAGEN ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    // Lanzamos el selector de galería
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }, modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC20714) // Tu color rojo
                    // Si quieres cambiar el color del texto, usa: contentColor = Color.White
                )
            ) {
                Text("Seleccionar imagen")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 9. CAMPOS DE TEXTO ---
        TextField(
            value = edittitulo,
            onValueChange = { edittitulo = it },
            label = { Text("Titulo:") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = editdescripcion,
            onValueChange = { editdescripcion = it },
            label = { Text("Descripcion:") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(30.dp))

        // 10. BOTÓN DE GUARDAR/ACTUALIZAR
        Button(
            onClick = onSaveOrUpdate,
            modifier = Modifier.fillMaxWidth(),
            enabled = edittitulo.isNotBlank() && editdescripcion.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC20714))
        ) {
            Text(text = if (isEditing) "Actualizar Playlist" else "Guardar Playlist")
        }

        // 💡 BOTÓN DE ELIMINAR (Solo en modo edición)
        if (isEditing) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.deletePlaylist(playlistId)
                    navController.popBackStack() // Volver a la lista principal después de eliminar
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Eliminar Playlist")
            }
        }
        Spacer(modifier = Modifier.height(90.dp))
    }
}