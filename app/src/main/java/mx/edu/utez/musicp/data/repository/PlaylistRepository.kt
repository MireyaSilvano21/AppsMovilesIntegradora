package mx.edu.utez.musicp.data.repository

import android.content.Context
import android.net.Uri
import mx.edu.utez.musicp.data.model.Playlist
import mx.edu.utez.musicp.data.network.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream


class PlaylistRepository(
    private val apiService: ApiService,
    // Necesitamos el Context para leer el archivo de la imagen
    private val context: Context
) {

    suspend fun getPlaylist(): List<Playlist> {
        return try {
            apiService.getPlaylist()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Cambiamos la firma para aceptar la Uri de la imagen
    suspend fun insertPlaylist(titulo: String, descripcion : String, imageUri: Uri?) {

        try {
            // 1. Convertir los Strings a RequestBody
            val tituloBody = titulo.toRequestBody("text/plain".toMediaTypeOrNull())
            val descripcionBody = descripcion.toRequestBody("text/plain".toMediaTypeOrNull())


            // 2. Convertir la Uri de la imagen a MultipartBody.Part
            var imagePart: MultipartBody.Part? = null
            if (imageUri != null) {
                // Obtenemos el tipo de contenido (ej. "image/jpeg")
                val type = context.contentResolver.getType(imageUri)
                val stream: InputStream? = context.contentResolver.openInputStream(imageUri)
                val bytes = stream?.readBytes()
                stream?.close()

                if (bytes != null && type != null) {
                    val requestFile = bytes.toRequestBody(type.toMediaTypeOrNull())
                    // 'image' debe coincidir con el nombre en el backend (request.files.get('image'))
                    imagePart = MultipartBody.Part.createFormData("imagen", "image.jpg", requestFile)
                }
            }

            // 3. Llamar a la API
            apiService.addPlaylist(
                titulo = tituloBody,
                descripcion = descripcionBody,

                imagen = imagePart
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    suspend fun deletePlaylist(playlistId: Int) {
        // La API devuelve Response<Unit>, verificamos el éxito
        val response = apiService.deletePlaylist(playlistId)
        if (!response.isSuccessful) {
            throw Exception("Error al eliminar playlist: ${response.code()}")
        }
    }

    suspend fun updatePlaylist(
        playlistId: Int,
        titulo: String,
        descripcion: String,
        imageUri: Uri?
    ): Playlist {
        // 1. Convertir campos de texto a RequestBody
        val tituloPart = titulo.toRequestBody("text/plain".toMediaTypeOrNull())
        val descripcionPart = descripcion.toRequestBody("text/plain".toMediaTypeOrNull())

        var imagePart: MultipartBody.Part? = null
        if (imageUri != null) {
            // 2. Convertir Uri a MultipartBody.Part
            imagePart = uriToMultipartPart(imageUri, "imagen") // Debes crear esta función auxiliar
        }

        // 3. Llamar a la API
        return apiService.updatePlaylist(
            playlistId,
            tituloPart,
            descripcionPart,
            imagePart
        )
    }

    // 💡 NECESITAS ESTA FUNCIÓN AUXILIAR (o similar, asumiendo que ya la tienes para addPlaylist)
    fun uriToMultipartPart(uri: Uri, name: String): MultipartBody.Part? {
        // ... (Tu implementación para convertir la Uri en un archivo temporal y luego en MultipartBody.Part)
        // Usualmente requiere Context y ContentResolver.
        // Usamos el ContentResolver para leer el archivo de la URI
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val mediaType = context.contentResolver.getType(uri)?.toMediaTypeOrNull() ?: "image/*".toMediaTypeOrNull()
        val requestFile = inputStream.readBytes().toRequestBody(mediaType)

        // Usamos un nombre de archivo temporal para la parte del formulario
        return MultipartBody.Part.createFormData(name, "temp_image.jpg", requestFile)
    }
}