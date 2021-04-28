package com.A306.runnershi.Helper

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.util.*
import kotlin.concurrent.thread

class HttpConnect {
    var urlAddress:String = ""
    var headers:Map<String, Any>? = null
    var params:Map<String, Any>? = null

    constructor(){}

    constructor(urlAddress:String){
        this.urlAddress = urlAddress
    }

    constructor(urlAddress:String, headers:Map<String, Any>){
        this.urlAddress = urlAddress
        this.headers = headers
    }

    constructor(urlAddress:String, headers:Map<String, Any>, params:Map<String, Any>){
        this.urlAddress = urlAddress
        this.headers = headers
        this.params = params
    }

    // 접속 할 주소
    val url = setServerAddress(urlAddress)
    // 연결 생성
    val conn = url.openConnection() as HttpURLConnection

    fun post(params:Objects){
        conn.requestMethod = "POST"
    }

    fun get():String?{
        conn.requestMethod = "GET"
        // 읽어오기
        var result:String? = ""
        runBlocking {
            result = async (Dispatchers.IO){
                readStream(conn)
            }.await()
        }
        return result
    }

    fun delete(){
        conn.requestMethod = "DELETE"
    }

    fun put(){
        conn.requestMethod = "PUT"
    }

    suspend fun readStream(stream:HttpURLConnection):String{
        if (this.params != null){
            val outStream = stream.outputStream
            var receivedParams:Map<String, Any>? = this.params

        }

        val inputStream = InputStreamReader(stream.inputStream, "UTF-8")
        val bufferedRead = BufferedReader(inputStream)
        var stringResult:String? = null
        val buf = StringBuffer()
        do{
            stringResult = bufferedRead.readLine()
            if (stringResult != null){
                buf.append("$stringResult\n")
            }
        }while(stringResult != null)

        return buf.toString()
    }
}

 private fun setServerAddress(urlAddress:String?):URL{
     return URL("https://jsonplaceholder.typicode.com/todos/1")
 }