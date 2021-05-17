package com.A306.runnershi.Openvidu

import com.A306.runnershi.Helper.RetrofitService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OpenviduRetrofitClient {
    private var okHttpClient = UnsafeOpenvidu().getUnsafeClient()

    private var instance: OpenviduRetrofitService? = null
    private val gson = GsonBuilder().setLenient().create()
    // 서버 주소
    private const val BASE_URL = "https://skeldtcan.iptime.org:8081"

    fun getInstance(): OpenviduRetrofitService {
        if (instance == null) {
            instance = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(OpenviduRetrofitService::class.java)
        }

        return instance!!
    }
}