package com.A306.runnershi.Openvidu

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OpenviduSessionRetrofitClient {
    private var instance: OpenviduSessionRetrofitService? = null
    private val gson = GsonBuilder().setLenient().create()
    // 서버 주소
    private const val BASE_URL = "https://k4a3061.p.ssafy.io"

    fun getInstance(): OpenviduSessionRetrofitService {
        if (instance == null) {
            instance = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(OkHttpClient().newBuilder().addInterceptor(BasicAuthInterceptor("OPENVIDUAPP", "MY_SECRET")).build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(OpenviduSessionRetrofitService::class.java)
        }

        return instance!!
    }
}