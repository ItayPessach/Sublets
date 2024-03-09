package com.example.apartments.modules.profile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apartments.model.FirebaseStorageModel
import com.example.apartments.model.user.UpdateUserInput
import com.example.apartments.model.user.User
import com.example.apartments.model.user.UserModel
import kotlinx.coroutines.launch

class ProfileViewModel: ViewModel() {
    var user: MutableLiveData<User> = MutableLiveData<User>()

    fun getCurrentUser() {
        user.value = UserModel.instance.currentUser
    }

    fun updateCurrentUser(updateUserInput: UpdateUserInput, avatarUri: Uri?) {
        viewModelScope.launch {

            if (avatarUri != null) {
                updateUserInput.avatarUrl = FirebaseStorageModel.instance.addImageToFirebaseStorage(avatarUri, FirebaseStorageModel.USERS_PATH)
            }

            UserModel.instance.updateMe(updateUserInput)
            user.postValue(UserModel.instance.currentUser)

        }
    }
}