package com.example.apartments.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.apartments.base.MyApplication
import com.example.apartments.model.apartment.Apartment


@Database(entities = [Apartment:: class], version = 8)
abstract class AppLocalDbRepository: RoomDatabase() {
    abstract fun apartmentDao(): ApartmentDao
}
object AppLocalDatabase {
    val db: AppLocalDbRepository by lazy {

        val context = MyApplication.Globals.appContext
            ?: throw IllegalStateException("Application context not available")

        Room.databaseBuilder(
            context,
            AppLocalDbRepository::class.java,
            "dbFileName.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}