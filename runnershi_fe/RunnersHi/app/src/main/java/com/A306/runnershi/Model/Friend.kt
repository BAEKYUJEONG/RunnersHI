package com.A306.runnershi.Model

data class Friend (
    val userName: String? = null,
    // 달리기 날짜
    var timestamp: Long = 0L,
    // 거리
    var distance: Int = 0,
    // 시간
    var time: String? = null
)