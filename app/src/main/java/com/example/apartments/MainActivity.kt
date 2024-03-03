package com.example.apartments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.apartments.retrofit.RegionSearchResult
import com.example.apartments.retrofit.RegionsSingelton
import com.example.apartments.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getRegionsFromRemoteApi()
    }

    private fun getRegionsFromRemoteApi() {
        val call = RetrofitInstance.regionsApi.getRegions()

        call.enqueue(object: Callback<RegionSearchResult> {
            override fun onResponse(
                call: Call<RegionSearchResult>,
                response: Response<RegionSearchResult>
            ) {
                if (response.isSuccessful) {
                    RegionsSingelton.regionsSearchResult = response.body()!!.result.records.map { it -> it.regionName }

                    Log.i("TAG", RegionsSingelton.regionsSearchResult.toString())
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