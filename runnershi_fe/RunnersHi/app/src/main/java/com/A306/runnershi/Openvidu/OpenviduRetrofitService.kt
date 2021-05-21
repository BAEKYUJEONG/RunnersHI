package com.A306.runnershi.Openvidu

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface OpenviduRetrofitService {
    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @POST("/room/create")
    fun createRoom(
            @Header("token") token:String,
            @Body body:Map<String, Any>
    ): Call<ResponseBody>

    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @POST("/room/join")
    fun joinRoom(
            @Header("token") token:String,
            @Body roomId:Int
    ): Call<ResponseBody>
}