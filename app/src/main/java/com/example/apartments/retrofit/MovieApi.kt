package com.example.apartments.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {
    @GET("/?apikey=f8f48246")
    fun getMovieByTitle(@Query("s") title: String): Call<MovieSearchResult>
}