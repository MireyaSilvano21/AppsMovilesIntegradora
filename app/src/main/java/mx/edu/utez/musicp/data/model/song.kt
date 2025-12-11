package mx.edu.utez.musicp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.net.Uri // Importante para manejar rutas de archivos

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey(autoGenerate = true)
    val songDbId: Int = 0,

    val id: Int = 0, // Id de la canción (si lo necesitas)

    val title: String,
    val artist: String,
    val duration: String = "0:00", // La obtendremos al cargar el archivo

    // 💡 NUEVO: URI del archivo local. Se guarda como String en Room.
    val fileUri: String,

    // El enlace a la playlist de Flask
    val playlistId: Int
)