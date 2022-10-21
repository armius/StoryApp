package com.armius.dicoding.storyapp.core.net

import com.armius.dicoding.storyapp.core.entity.ResponseAllStoriesEntity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun registerUser(
        @Field("name") name : String,
        @Field("email") email : String,
        @Field("password") password : String
    ) : Call<ResponseBasic>

    @FormUrlEncoded
    @POST("login")
    fun loginUser(
        @Field("email") email : String,
        @Field("password") password : String
    ) : Call<ResponseLogin>

    @GET("stories")
    fun getAllStories(
        @Query("page") page : Int? = null,
        @Query("size") size : Int? = null,
        @Query("location") location : Int? = null
    ) : Call<ResponseAllStories>

    @GET("stories")
    suspend fun getAllStories2(
        @Query("page") page : Int? = null,
        @Query("size") size : Int? = null,
        @Query("location") location : Int? = null
    ) : ResponseAllStoriesEntity

    @Multipart
    @POST("stories")
    fun addNewStory(
        @Part file: MultipartBody.Part,
        @Part("description") description : RequestBody,
        @Part("lat") lat : RequestBody?,
        @Part("lon") lon : RequestBody?
    ) : Call<ResponseBasic>
}