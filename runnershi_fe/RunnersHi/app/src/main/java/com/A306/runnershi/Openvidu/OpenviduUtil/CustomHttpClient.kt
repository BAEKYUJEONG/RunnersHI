package com.A306.runnershi.Openvidu.OpenviduUtil

import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class CustomHttpClient(baseUrl: String, basicAuth: String) {
    private var client: OkHttpClient? = null
    private val baseUrl: String
    private val basicAuth: String
    @Throws(IOException::class)
    fun httpCall(
        url: String,
        method: String,
        contentType: String,
        body: RequestBody?,
        callback: Callback?
    ) {
        var url = url
        url = if (url.startsWith("/")) url.substring(1) else url
        val request: Request = Request.Builder()
            .url(baseUrl + url)
            .header("Authorization", basicAuth).header("Content-Type", contentType)
            .method(method, body)
            .build()
        val call = client!!.newCall(request)
        call.enqueue(callback!!)
    }

    fun dispose() {
        client!!.dispatcher.executorService.shutdown()
    }

    init {
        this.baseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        this.basicAuth = basicAuth
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate>? {
                        return null
                    }
                }
            )

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            client = OkHttpClient.Builder().sslSocketFactory(sslSocketFactory, object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }).hostnameVerifier(HostnameVerifier { hostname, session -> true }).build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
