package com.example.films.model

import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface FilmsApi {
    @GET("films.json")
    suspend fun getFilms(): FilmsResponse

    companion object {
        fun create(): FilmsApi {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://s3-eu-west-1.amazonaws.com/sequeniatesttask/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(FilmsApi::class.java)
        }
    }
}
