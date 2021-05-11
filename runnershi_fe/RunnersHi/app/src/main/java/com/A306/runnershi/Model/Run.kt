package com.A306.runnershi.Model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_table")
data class Run(
    // 이미지
    var img: Bitmap? = null,
    // 달리기 날짜
    var timestamp: Long = 0L,
    // 평균 속력
    var avgSpeed: Float = 0f,
    // 거리
    var distance: Int = 0,
    // 시간
    var time: String? = null,
    // 페이스
    var pace: String? = null
    // 칼로리 필요없음
){
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
