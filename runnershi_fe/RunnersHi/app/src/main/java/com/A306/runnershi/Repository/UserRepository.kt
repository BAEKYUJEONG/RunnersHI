package com.A306.runnershi.Repository

import com.A306.runnershi.Dao.UserDAO
import com.A306.runnershi.Model.User
import javax.inject.Inject

class UserRepository @Inject constructor(
        val userDAO: UserDAO
) {
    suspend fun insertUser(user:User) = userDAO.insertUser(user)

    suspend fun deleteUser(user:User) = userDAO.deleteUser(user)

    fun getUserInfo() = userDAO.getUserInfo()
}