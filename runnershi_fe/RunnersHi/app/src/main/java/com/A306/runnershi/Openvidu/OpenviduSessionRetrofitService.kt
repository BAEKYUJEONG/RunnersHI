package com.A306.runnershi.Openvidu

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface OpenviduSessionRetrofitService {
    @Headers("Content-Type: application/json")
    @GET("/api/sessions")
    fun getRoomList(): Call<ResponseBody>
}