package com.A306.runnershi.Model

import java.time.LocalDate

data class Detail (
    val userName: String? = null,
    val runningDate: String? = null,
    val endTime: String? = null,
    var runningTime:Int = 0,
    var title: String? = null,
    var distance: Double = 0.0
)