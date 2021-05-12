package com.A306.runnershi.Helper

import com.A306.runnershi.Model.Token
import com.A306.runnershi.Model.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface RetrofitService {
    @Headers("Content-Type: application/json")
    @POST("/user/signin/kakao")
    fun kakaoLogin(@Body body:Token) : Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("/user/signup/social")
    fun socialRegister(
        @Query("userId") userId:String,
        @Query("userName") userName: String
    ): Call<ResponseBody>

    @GET("/user/emailchk/{email}")
    fun userEmailCheck(@Path("email") email:String): Call<ResponseBody>

    @GET("/user/namechk/{name}")
    fun userNicknameCheck(@Path("name") name:String): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("/user/signup/runhi")
    fun normalRegister(
        @Query("email") email:String,
        @Query("pwd") pwd: String,
        @Query("userName") userName: String
    ): Call<ResponseBody>
}