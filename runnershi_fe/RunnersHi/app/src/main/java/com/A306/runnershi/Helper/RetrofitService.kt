package com.A306.runnershi.Helper

import com.A306.runnershi.Model.Token
import com.A306.runnershi.Model.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface RetrofitService {
    // 카카오 로그인
    @Headers("Content-Type: application/json")
    @POST("/user/signin/kakao")
    fun kakaoLogin(@Body body:Token) : Call<ResponseBody>

    // 카카오 가입
    @Headers("Content-Type: application/json")
    @POST("/user/signup/social")
    fun socialRegister(
        @Query("userId") userId:String,
        @Query("userName") userName: String
    ): Call<ResponseBody>

    // 이메일 확인
    @GET("/user/emailchk/{email}")
    fun userEmailCheck(@Path("email") email:String): Call<ResponseBody>

    // 닉네임 확인
    @GET("/user/namechk/{name}")
    fun userNicknameCheck(@Path("name") name:String): Call<ResponseBody>

    // 일반 회원가입
    @Headers("Content-Type: application/json")
    @POST("/user/signup/runhi")
    fun normalRegister(
        @Query("email") email:String,
        @Query("pwd") pwd: String,
        @Query("userName") userName: String
    ): Call<ResponseBody>

    // 일반 로그인
    @Headers("Content-Type: application/json")
    @POST("/user/signin/runhi")
    fun normalLogin(
        @Query("email") email:String,
        @Query("pwd") pwd:String
    ): Call<ResponseBody>
}