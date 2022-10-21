package com.armius.dicoding.storyapp.core.entity

import com.google.gson.annotations.SerializedName

data class ResponseAllStoriesEntity(

    @field:SerializedName("listStory")
    val listStory: List<StoryEntity>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)