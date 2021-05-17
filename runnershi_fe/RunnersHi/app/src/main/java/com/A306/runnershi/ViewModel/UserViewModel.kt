package com.A306.runnershi.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.A306.runnershi.Model.User
import com.A306.runnershi.Repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
        val userRepository: UserRepository
) : ViewModel(){
    fun insertUser(user:User) = viewModelScope.launch {
        userRepository.insertUser(user)
    }

    fun deleteUser(user:User) = viewModelScope.launch {
        userRepository.deleteUser(user)
    }

    fun deleteAllUser() = viewModelScope.launch {
        userRepository.deleteAllUser()
    }

    val userInfo = userRepository.getUserInfo()
}