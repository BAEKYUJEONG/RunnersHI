package com.A306.runnershi.Model

data class Room(
    val room_id: Int = 0,
    val title: String? = null,
    val type: Int? = null,
    val count: Int? = null,
    val roomToken:String = "",
    val roomSession:String = ""
)