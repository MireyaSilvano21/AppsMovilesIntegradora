package mx.edu.utez.musicp.ui.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun uriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val tempFile = File.createTempFile("playlist_img", ".jpg", context.cacheDir)
    val outputStream = FileOutputStream(tempFile)

    inputStream?.copyTo(outputStream)

    inputStream?.close()
    outputStream.close()

    return tempFile
}
