package mx.edu.utez.musicp.ui.components.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mx.edu.utez.musicp.data.dao.AppDatabase
import mx.edu.utez.musicp.viewmodel.MusicViewModel

class MusicViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MusicViewModel::class.java)) {

            // 1. Obtener la instancia del SongDao
            // (Asegúrate de que tu AppDatabase tenga un método para obtener la instancia,
            // generalmente usando Room.databaseBuilder)
            val songDao = AppDatabase.getDatabase(context).songDao()

            // 2. 💡 Pasar el SongDao al constructor del MusicViewModel
            return MusicViewModel(songDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}