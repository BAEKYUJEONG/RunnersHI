package com.A306.runnershi.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User (
    var token:String? = null,
    var userId:String? = null,
    var userName:String? = null,
    var runningType:Int = 0
){
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
//    var accessToken:String = ""
//    var refreshToken:String = ""
//    var nickname:String = ""
//
//
//    constructor(accessToken:String, refreshToken:String, nickname:String){
//        this.accessToken = accessToken
//        this.refreshToken = refreshToken
//        this.nickname = nickname
//    }