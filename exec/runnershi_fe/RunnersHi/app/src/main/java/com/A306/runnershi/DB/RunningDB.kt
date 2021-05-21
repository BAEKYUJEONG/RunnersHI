package com.A306.runnershi.DB

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.A306.runnershi.Dao.RunDAO
import com.A306.runnershi.Dao.UserDAO
import com.A306.runnershi.Helper.ConvertHelper
import com.A306.runnershi.Model.Run
import com.A306.runnershi.Model.User

@Database(
    entities = [Run::class, User::class],
    version = 1
)
@TypeConverters(ConvertHelper::class)
abstract class RunningDB: RoomDatabase() {
    abstract fun getRunDao(): RunDAO
    abstract fun getUserDao(): UserDAO
}