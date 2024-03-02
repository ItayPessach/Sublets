package com.example.apartments.modules.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apartments.model.user.User
import com.example.apartments.model.user.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel: ViewModel() {
    var user: MutableLiveData<User> = MutableLiveData<User>()

    fun getUser(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
                val fetchedUser = UserModel.instance.fetchUser(userId)
                user.postValue(fetchedUser!!)
            }
        }
}