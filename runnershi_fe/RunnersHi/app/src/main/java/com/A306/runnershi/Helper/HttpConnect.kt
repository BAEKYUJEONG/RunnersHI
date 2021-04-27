package com.A306.runnershi.Helper

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.concurrent.thread

class HttpConnect {
    var urlAddress:String = ""
    var headers:Map<String, Any>? = null

    constructor(){}

    constructor(urlAddress:String){
        this.urlAddress = urlAddress
    }

    constructor(urlAddress:String, headers:Map<String, Any>){
        this.urlAddress = urlAddress
        this.headers = headers
    }

    // 접속 할 주소
    val url = setServerAddress(urlAddress)
    // 연결 생성
    val conn = url.openConnection() as HttpURLConnection

    fun post(params:Objects){
        conn.requestMethod = "POST"
    }

    fun get():String{
        conn.requestMethod = "GET"
        // 읽어오기
        val inputStream = InputStreamReader(conn.inputStream, "UTF-8")
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

    fun delete(){
        conn.requestMethod = "DELETE"
    }

    fun put(){
        conn.requestMethod = "PUT"
    }
}

 private fun setServerAddress(urlAddress:String?):URL{
     return URL("https://jsonplaceholder.typicode.com/todos")
 }