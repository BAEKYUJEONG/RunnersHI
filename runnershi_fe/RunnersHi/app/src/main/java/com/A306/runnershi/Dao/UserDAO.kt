package com.A306.runnershi.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.A306.runnershi.Model.User

@Dao
interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Delete
    suspend fun deleteUser(user:User)

    @Query("SELECT * FROM user_table LIMIT 0, 1")
    fun getUserInfo(): User

}