package com.armius.dicoding.storyapp.core.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.armius.dicoding.storyapp.core.entity.StoryEntity

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(storyEntity: List<StoryEntity>)

    @Query("SELECT * FROM table_story")
    fun getAllStories(): PagingSource<Int, StoryEntity>

    @Query("DELETE FROM table_story")
    suspend fun deleteAll()
}