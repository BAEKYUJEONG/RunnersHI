package com.A306.runnershi.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User (
    var token:String? = null,
    var userId:String? = null,
    var userName:String? = null,
    var runningType:String? = null
){
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}