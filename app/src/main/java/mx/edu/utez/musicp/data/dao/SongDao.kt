package mx.edu.utez.musicp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import mx.edu.utez.musicp.data.model.Song

@Dao
interface SongDao {

    // 💡 Función clave: Filtrar canciones por playlistId
    @Query("SELECT * FROM songs WHERE playlistId = :id")
    fun getSongsByPlaylistId(id: Int): Flow<List<Song>>

    // Insertar todas las canciones iniciales (solo se hace una vez)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(songs: List<Song>)
}