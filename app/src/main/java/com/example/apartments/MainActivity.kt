package com.example.apartments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.apartments.retrofit.RegionsSingelton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RegionsSingelton.getRegionsFromRemoteApi()
    }
}