package com.armius.dicoding.storyapp.core.net

import android.content.Context
import androidx.viewbinding.BuildConfig
import com.armius.dicoding.storyapp.core.utils.Preference
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object  ApiConfig {
    private const val BASE_URL = "https://story-api.dicoding.dev/v1/"

    fun getApiService(ctx : Context): ApiService {
        val loggingInterceptor = if(BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        val requestInterceptor = Interceptor { interChain ->
            val original = interChain.request()
            val preference = Preference(ctx)
            val token = preference.getToken()
            val request = if (token != null) {
                original.newBuilder()
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer $token")
                    .method(original.method, original.body)
                    .build()
            } else {
                original.newBuilder()
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .method(original.method, original.body)
                    .build()
            }
            interChain.proceed(request)
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(requestInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }
}