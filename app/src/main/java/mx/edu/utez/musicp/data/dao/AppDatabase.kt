package mx.edu.utez.musicp.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import mx.edu.utez.musicp.data.model.Song

import android.content.Context
import androidx.room.Room


@Database(entities = [Song::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun songDao(): SongDao

    // 💡 CLAVE: Implementar el patrón Singleton
    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Si INSTANCE no es nulo, la devuelve
            return INSTANCE ?: synchronized(this) {
                // Si INSTANCE es nulo, construye la base de datos
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "music_database" // 💡 Nombre del archivo de la DB
                )
                    // Esto permite ejecutar operaciones de DB en el hilo principal (solo en modo debug)
                    // .allowMainThreadQueries()
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}