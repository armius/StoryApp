package com.armius.dicoding.storyapp.core.paging

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.armius.dicoding.storyapp.core.database.StoryDatabase
import com.armius.dicoding.storyapp.core.entity.StoryEntity
import com.armius.dicoding.storyapp.core.net.ApiService

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService) {

    fun getStory(): LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).liveData
    }
}