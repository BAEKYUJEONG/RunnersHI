package com.A306.runnershi.Helper

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitClient {
    private var instance: RetrofitService? = null
    private val gson = GsonBuilder().setLenient().create()
    // 서버 주소
    private const val BASE_URL = "https://k4a306.p.ssafy.io:8081"

    fun getInstance(): RetrofitService {
        if (instance == null) {
            instance = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(RetrofitService::class.java)
        }

        return instance!!
    }

    // 임시로 받아올 티캔 서버
//    private const val BASE_URL_TEMP = "https://skeldtcan.iptime.org:8081"
//
//    fun getInstanceTemp(): RetrofitService {
//        if (instance == null) {
//            instance = Retrofit.Builder()
//                .baseUrl(BASE_URL_TEMP)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build()
//                .create(RetrofitService::class.java)
//        }
//
//        return instance!!
//    }
}