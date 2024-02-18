package com.example.apartments.model.apartment

import android.os.Looper
import androidx.core.os.HandlerCompat
import com.example.apartments.dao.AppLocalDatabase
import java.util.concurrent.Executors

class ApartmentModel private constructor() {
    private val database = AppLocalDatabase.db
    private var executor = Executors.newSingleThreadExecutor()
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())

    companion object {
        val instance: ApartmentModel = ApartmentModel()
    }

   fun getAllApartments(callback: (List<Apartment>) -> Unit) {
        executor.execute {

            Thread.sleep(5000)

            val apartments = database.apartmentDao().getAll()
            mainHandler.post {
                callback(apartments)
            }
        }
   }

    fun addApartment(apartment: Apartment, callback: () -> Unit) {
        executor.execute {
            database.apartmentDao().insert(apartment)
            mainHandler.post {
                callback()
            }
        }
    }
}