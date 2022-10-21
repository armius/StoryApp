package com.armius.dicoding.storyapp.core.di

import android.content.Context
import com.armius.dicoding.storyapp.core.database.StoryDatabase
import com.armius.dicoding.storyapp.core.net.ApiConfig
import com.armius.dicoding.storyapp.core.paging.StoryRepository

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService(context)
        return StoryRepository(database, apiService)
    }
}