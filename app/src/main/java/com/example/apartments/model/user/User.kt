package com.example.apartments.model.user

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.apartments.base.MyApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
@Entity
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val phoneNumber: String,
    val avatarUrl: String,
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

        private const val NAME_KEY = "name"
        private const val PHONE_NUMBER_KEY = "phoneNumber"
        private const val AVATAR_URL_KEY = "avatarUrl"

        private const val LOCAL_LAST_UPDATED = "get_last_updated"
        const val LAST_UPDATED = "lastUpdated"
        fun fromJson(json: Map<String, Any>, id: String): User {
            val name = json[NAME_KEY] as? String ?: ""
            val phoneNumber = json[PHONE_NUMBER_KEY] as? String ?: ""
            val avatarUrl = json[AVATAR_URL_KEY] as? String ?: ""

            val user = User(id, name, phoneNumber, avatarUrl)

            val timestamp: Timestamp? = json[LAST_UPDATED] as? Timestamp
            timestamp?.let {
                user.lastUpdated = it.seconds
            }

            return user
        }
    }
    val json: Map<String, Any>
        get() = hashMapOf(
            NAME_KEY to name,
            PHONE_NUMBER_KEY to phoneNumber,
            AVATAR_URL_KEY to avatarUrl,
            LAST_UPDATED to FieldValue.serverTimestamp()
        )
}