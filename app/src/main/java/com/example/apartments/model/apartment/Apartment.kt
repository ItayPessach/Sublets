package com.example.apartments.model.apartment

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.apartments.base.MyApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

enum class Type(type: String) {
    Apartment("Apartment"),
    House("House"),
    Villa("Villa"),
    Penthouse("Penthouse"),
}

@Entity
data class Apartment(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val pricePerNight: Int,
    val description: String,
    val city: String,
    val type: Type,
    val numOfRooms: Int,
    val startDate: Long,
    val endDate: Long,
    val imageUrl: String,
    var liked: Boolean = false,
    var isMine: Boolean = false,
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
        private const val USER_ID = "userId"
        private const val PRICE_PER_NIGHT_KEY = "pricePerNight"
        private const val DESCRIPTION_KEY = "description"
        private const val CITY_KEY = "city"
        private const val TYPE_KEY = "type"
        private const val NUM_OF_ROOMS_KEY = "numOfRooms"
        private const val START_DATE_KEY = "startDate"
        private const val END_DATE_KEY = "endDate"
        private const val IMAGE_URL_KEY = "imageUrl"

        private const val LOCAL_LAST_UPDATED = "get_last_updated"
        const val LAST_UPDATED = "lastUpdated"
        fun fromJson(json: Map<String, Any>, id: String): Apartment {
            val title = json[TITLE_KEY] as? String ?: ""
            val userId = json[USER_ID] as? String ?: ""
            val pricePerNight = (json[PRICE_PER_NIGHT_KEY] as? Long)?.toInt() ?: 0
            val description = json[DESCRIPTION_KEY] as? String ?: ""
            val city = json[CITY_KEY] as? String ?: ""
            val type = json[TYPE_KEY] as? Type ?: Type.House
            val numOfRooms = (json[NUM_OF_ROOMS_KEY] as? Long)?.toInt() ?: 0
            val startDate = json[START_DATE_KEY] as? Long ?: 0
            val endDate = json[END_DATE_KEY] as? Long ?: 0
            val imageUrl = json[IMAGE_URL_KEY] as? String ?: ""

            val apartment = Apartment(id, userId, title, pricePerNight, description, city, type, numOfRooms, startDate, endDate, imageUrl)

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
                USER_ID to userId,
                PRICE_PER_NIGHT_KEY to pricePerNight,
                DESCRIPTION_KEY to description,
                CITY_KEY to city,
                TYPE_KEY to type,
                NUM_OF_ROOMS_KEY to numOfRooms,
                START_DATE_KEY to startDate,
                END_DATE_KEY to endDate,
                IMAGE_URL_KEY to imageUrl,
                LAST_UPDATED to FieldValue.serverTimestamp()
            )
}
