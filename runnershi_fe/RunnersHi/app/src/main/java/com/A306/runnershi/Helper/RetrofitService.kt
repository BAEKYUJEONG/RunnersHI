package com.A306.runnershi.Helper

import com.A306.runnershi.Model.Token
import com.A306.runnershi.Model.User
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    @Headers("Content-Type: application/json")
    @POST("/user/signin/kakao")
    fun kakaoLogin(@Body body:Token) : Call<User>
}