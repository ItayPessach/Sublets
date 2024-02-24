package com.example.apartments.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.apartments.model.apartment.Apartment

@Dao
interface ApartmentDao {
    @Query("SELECT * FROM Apartment")
    fun getAll(): LiveData<MutableList<Apartment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg apartments: Apartment)

    @Delete
    fun delete(apartment: Apartment)

    @Query("SELECT * FROM Apartment WHERE id = :id")
    fun getApartmentById(id: String): LiveData<Apartment>
}