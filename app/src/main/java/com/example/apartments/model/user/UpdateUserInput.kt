package com.example.apartments.model.user

data class UpdateUserInput(
    var name: String,
    val phoneNumber: String,
    var avatarUrl: String
) {
    companion object {
        private const val NAME_KEY = "name"
        private const val PHONE_NUMBER_KEY = "phoneNumber"
        private const val AVATAR_URL_KEY = "avatarUrl"
    }

    val json: HashMap<String, Any>
        get() = hashMapOf(
            NAME_KEY to name,
            PHONE_NUMBER_KEY to phoneNumber,
            AVATAR_URL_KEY to avatarUrl,
        )
}
