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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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


@Composable
fun AñadirScreen(viewModel: PlaylistViewModel, navController: NavController,modifier: Modifier = Modifier) {
    val context = LocalContext.current
    // --- 1. ESTADO PARA GUARDAR LA URI DE LA IMAGEN ---
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val scrollState = rememberScrollState()


    // --- 2. LAUNCHER PARA LA GALERÍA ---
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                selectedImageUri = uri
            }
        }
    )


    // --- 5. ESTADO PARA LOS CAMPOS DE TEXTO ---
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }


    // --- 6. LAYOUT ---
    Column(
        modifier = Modifier
            .fillMaxSize() // Usa fillMaxSize para que el Scroll funcione
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Title("Registrar Nueva PLaylist")

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
                Text("Galería")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 9. CAMPOS DE TEXTO ---
        TextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Nombre:") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("descripcion:") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(30.dp))

        // --- 10. BOTÓN DE GUARDAR ACTUALIZADO ---
        Button(
            onClick = {

                // Llamamos a la nueva función del ViewModel
                viewModel.addNewPlaylist(titulo, descripcion, selectedImageUri)

                // Reseteamos los campos
                titulo = ""
                descripcion = ""

                selectedImageUri = null
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC20714) // Tu color rojo
                // Si quieres cambiar el color del texto, usa: contentColor = Color.White
            )
        ) {
            Text(text = "Guardar Playlist")
        }
        Spacer(modifier = Modifier.height(90.dp))
    }
}