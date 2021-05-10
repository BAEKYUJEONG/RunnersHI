package com.A306.runnershi.DB

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.A306.runnershi.Dao.RunDAO
import com.A306.runnershi.Helper.ConvertHelper
import com.A306.runnershi.Model.Run

@Database(
    entities = [Run::class],
    version = 1
)
@TypeConverters(ConvertHelper::class)
abstract class RunningDB: RoomDatabase() {
    abstract fun getRunDao(): RunDAO
}