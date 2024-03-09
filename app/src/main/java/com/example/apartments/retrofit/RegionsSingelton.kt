package com.example.apartments.retrofit

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object RegionsSingelton {
    private var _regionsSearchResult: List<String>? = null

    val regionsList: List<String> get() = _regionsSearchResult ?: listOf()


    fun getRegionsFromRemoteApi() {
        val call = RetrofitInstance.regionsApi.getRegions()

        call.enqueue(object: Callback<RegionSearchResult> {
            override fun onResponse(
                call: Call<RegionSearchResult>,
                response: Response<RegionSearchResult>
            ) {
                if (response.isSuccessful) {
                   _regionsSearchResult = response.body()!!.result.records.map { it.regionName }

                    Log.i("TAG", regionsList.toString())
                } else {
                    Log.e("TAG", response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<RegionSearchResult>, t: Throwable) {
                Log.e("TAG", "$t.printStackTrace()")
            }
        })
    }
}