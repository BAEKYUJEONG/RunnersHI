package com.A306.runnershi.Model

import androidx.room.Entity

@Entity(tableName = "user_table")
data class User (
        var accessToken:String? = null,
        var refreshToken:String? = null,
        var nickname:String? = null
)
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