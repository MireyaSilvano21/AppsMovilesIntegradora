package mx.edu.utez.musicp.data.network

import mx.edu.utez.musicp.data.model.Playlist
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @GET("api/playlists")
    suspend fun getPlaylist(): List<Playlist>

    @Multipart
    @POST("api/playlists")
    suspend fun addPlaylist(
        @Part("titulo") titulo: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): Playlist
    // 💡 NUEVO: Endpoint para la ELIMINACIÓN
    @DELETE("api/playlists/{playlist_id}")
    suspend fun deletePlaylist(@Path("playlist_id") id: Int): Response<Unit>

    // 💡 NUEVO: Endpoint para la ACTUALIZACIÓN (PUT)
    @Multipart
    @PUT("api/playlists/{playlist_id}")
    suspend fun updatePlaylist(
        @Path("playlist_id") id: Int,
        @Part("titulo") titulo: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): Playlist
}
