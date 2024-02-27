package com.example.apartments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.apartments.retrofit.MovieSearchResult
import com.example.apartments.retrofit.MoviesSingelton
import com.example.apartments.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val call = RetrofitInstance.moviesApi.getMovieByTitle("Guardians of the Galaxy Vol. 2")

        call.enqueue(object: Callback<MovieSearchResult> {
            override fun onResponse(
                call: Call<MovieSearchResult>,
                response: Response<MovieSearchResult>
            ) {
                if (response.isSuccessful) {
                    MoviesSingelton.movieSearchResult = response.body()
                    Log.i("TAG", MoviesSingelton.movieSearchResult.toString())
                } else {
                    Log.e("TAG", response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<MovieSearchResult>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }
}