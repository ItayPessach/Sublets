package com.example.apartments.model.apartment

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Apartment(
    @PrimaryKey val id: Int,
    val title: String,
    val subtitle: String
)
