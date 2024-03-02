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
    // add user id
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val city: String,
    val apartmentType: ApartmentType,
    val numOfRooms: Int,
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
        private const val DESCRIPTION_KEY = "description"
        private const val CITY_KEY = "city"
        private const val APARTMENT_TYPE_KEY = "apartmentType"
        private const val NUM_OF_ROOMS_KEY = "numOfRooms"

        private const val LOCAL_LAST_UPDATED = "get_last_updated"
        const val LAST_UPDATED = "lastUpdated"
        fun fromJson(json: Map<String, Any>, id: String): Apartment {
            val title = json[TITLE_KEY] as? String ?: ""
            val description = json[DESCRIPTION_KEY] as? String ?: ""
            val city = json[CITY_KEY] as? String ?: ""
            val apartmentType = json[APARTMENT_TYPE_KEY] as? ApartmentType ?: ApartmentType.Private
            val numOfRooms = json[NUM_OF_ROOMS_KEY] as? Int ?: 0

            val apartment = Apartment(id, title, description, city, apartmentType, numOfRooms)

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
                DESCRIPTION_KEY to description,
                CITY_KEY to city,
                APARTMENT_TYPE_KEY to apartmentType,
                NUM_OF_ROOMS_KEY to numOfRooms,
                LAST_UPDATED to FieldValue.serverTimestamp()
            )
}
