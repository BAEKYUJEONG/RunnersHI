package com.A306.runnershi.Helper

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class HttpConnect {
    var urlAddress:String = ""
    var headers:Map<String, Any>? = null
    var params:Map<String, Any>? = null

    constructor(){}

    constructor(urlAddress: String){
        this.urlAddress = urlAddress
    }

    constructor(urlAddress: String, params: Map<String, Any>){
        this.urlAddress = urlAddress
        this.params = params
    }

    fun post():String{
        // 접속 할 주소
        val url = setServerAddress(urlAddress)
        // 연결 생성
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        // 읽어오기
        var result:String = ""
        runBlocking {
            result = async(Dispatchers.IO){
                readStream(conn)
            }.await()
        }
        conn.disconnect()
        return result
    }

    fun get():String{
        if (this.params != null){
            var paramsString:String = "?"
            this.params!!.keys.forEach{
                paramsString = if (paramsString == "?"){
                    "$paramsString$it="+ this.params!![it]
                }else{
                    "$paramsString&$it="+ this.params!![it]
                }
            }
            this.urlAddress = this.urlAddress + paramsString
        }
        // 접속 할 주소
        val url = setServerAddress(this.urlAddress)
        // 연결 생성
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        // 읽어오기
        var result:String = ""
        runBlocking {
            result = async(Dispatchers.IO){
                readStream(conn)
            }.await()
        }
        conn.disconnect()
        return result
    }

    fun delete():String{
        // 접속 할 주소
        val url = setServerAddress(urlAddress)
        // 연결 생성
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "DELETE"
        // 읽어오기
        var result:String = ""
        runBlocking {
            result = async(Dispatchers.IO){
                readStream(conn)
            }.await()
        }
        conn.disconnect()
        return result
    }

    fun put():String{
        // 접속 할 주소
        val url = setServerAddress(urlAddress)
        // 연결 생성
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "PUT"
        // 읽어오기
        var result:String = ""
        runBlocking {
            result = async(Dispatchers.IO){
                readStream(conn)
            }.await()
        }
        conn.disconnect()
        return result
    }

    suspend fun readStream(stream: HttpURLConnection):String{
        try {
            if (this.params != null && stream.requestMethod != "GET"){
                val outStream = stream.outputStream
                var paramsString = ""
                this.params!!.keys.forEach{
                    paramsString = if (paramsString == ""){
                        "$paramsString$it="+ this.params!![it]
                    }else{
                        "$paramsString&$it="+ this.params!![it]
                    }
                }
                outStream.write(paramsString.toByteArray(StandardCharsets.UTF_8))
                outStream.flush()
                outStream.close()
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
        }catch (e:Exception){
            return e.localizedMessage
        }
    }
}

 private fun setServerAddress(urlAddress: String):URL{
     // 서버 주소가 필요합니다.
     return URL("https://jsonplaceholder.typicode.com" + urlAddress)
 }