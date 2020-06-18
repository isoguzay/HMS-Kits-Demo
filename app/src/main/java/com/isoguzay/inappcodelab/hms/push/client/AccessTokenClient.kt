package com.isoguzay.inappcodelab.hms.push.client

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AccessTokenClient {

    companion object{

        private const val accessTokenBaseUrl = "https://oauth-login.cloud.huawei.com/"
        private const val TIMEOUT: Long = 500000

        fun getClient() : Retrofit {

            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.MICROSECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor).build()

            return Retrofit.Builder()
                .baseUrl(accessTokenBaseUrl).client(client)
                .addConverterFactory(GsonConverterFactory.create()).build()
        }
    }
}