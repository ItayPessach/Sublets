package com.example.apartments.retrofit

import com.google.gson.annotations.SerializedName

data class Movie(
    val Title: String,
)

data class MovieSearchResult(@SerializedName("Search") val search: List<Movie>)


