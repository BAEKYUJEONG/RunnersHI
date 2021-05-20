package com.A306.runnershi.Model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "running_table")
data class Run(
    // 제목
    var title: String? = null,
    // 이미지
    var img: Bitmap? = null,
    // 달리기 날짜
    var timestamp: Long = 0L,
//    var timestamp: Long =
    // 평균 속력
    var avgSpeed: Float = 0f,
    // 거리
    var distance: Int = 0,
    // 시간
    var time: String? = null,
    // 페이스
    var pace: String? = null
){
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
