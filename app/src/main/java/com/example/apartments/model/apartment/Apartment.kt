package com.example.apartments.model.apartment

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.apartments.base.MyApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

enum class ApartmentType(type: String) {
    Building("building"),
    Private("private"),
}

@Entity
data class Apartment(
    @PrimaryKey val id: String,
    val title: String,
    val pricePerNight: Int,
    val description: String,
    val city: String,
    val apartmentType: ApartmentType,
    val numOfRooms: Int,
    val startDate: Long,
    val endDate: Long,
    var liked: Boolean = false,
    var lastUpdated: Long? = null
) {

    companion object {
        var lastUpdated: Long
            get() {
                return MyApplication.Globals
                    .appContext?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.getLong(LOCAL_LAST_UPDATED, 0) ?: 0
            }
            set(value) {
                MyApplication.Globals
                    ?.appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)?.edit()
                    ?.putLong(LOCAL_LAST_UPDATED, value)?.apply()
            }

        private const val TITLE_KEY = "title"
        private const val PRICE_PER_NIGHT_KEY = "pricePerNight"
        private const val DESCRIPTION_KEY = "description"
        private const val CITY_KEY = "city"
        private const val APARTMENT_TYPE_KEY = "apartmentType"
        private const val NUM_OF_ROOMS_KEY = "numOfRooms"
        private const val START_DATE_KEY = "startDate"
        private const val END_DATE_KEY = "endDate"
        private const val LIKED_KEY = "liked"

        private const val LOCAL_LAST_UPDATED = "get_last_updated"
        const val LAST_UPDATED = "lastUpdated"
        fun fromJson(json: Map<String, Any>, id: String): Apartment {
            val title = json[TITLE_KEY] as? String ?: ""
            val pricePerNight = (json[PRICE_PER_NIGHT_KEY] as? Long)?.toInt() ?: 0
            val description = json[DESCRIPTION_KEY] as? String ?: ""
            val city = json[CITY_KEY] as? String ?: ""
            val apartmentType = json[APARTMENT_TYPE_KEY] as? ApartmentType ?: ApartmentType.Private
            val numOfRooms = (json[NUM_OF_ROOMS_KEY] as? Long)?.toInt() ?: 0
            val startDate = (json[START_DATE_KEY] as? Timestamp)?.toDate()?.time ?: 0
            val endDate = (json[END_DATE_KEY] as? Timestamp)?.toDate()?.time ?: 0
            val liked = json[LIKED_KEY] as? Boolean ?: false

            val apartment = Apartment(id, title, pricePerNight, description, city, apartmentType, numOfRooms, startDate, endDate, liked)

            val timestamp: Timestamp? = json[LAST_UPDATED] as? Timestamp
            timestamp?.let {
                apartment.lastUpdated = it.seconds
            }

            return apartment
        }
    }
    val json: Map<String, Any>
        get() = hashMapOf(
                TITLE_KEY to title,
                PRICE_PER_NIGHT_KEY to pricePerNight,
                DESCRIPTION_KEY to description,
                CITY_KEY to city,
                APARTMENT_TYPE_KEY to apartmentType,
                NUM_OF_ROOMS_KEY to numOfRooms,
                START_DATE_KEY to startDate,
                END_DATE_KEY to endDate,
                LIKED_KEY to liked,
                LAST_UPDATED to FieldValue.serverTimestamp()
            )
}
