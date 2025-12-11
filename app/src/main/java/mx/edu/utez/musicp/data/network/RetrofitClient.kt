package mx.edu.utez.musicp.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://192.168.100.89:5000/"

    // Creación "perezosa" (lazy) de la instancia de Retrofit
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Usa Gson
            .build()
    }

    // Instancia pública de tu ApiService
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}