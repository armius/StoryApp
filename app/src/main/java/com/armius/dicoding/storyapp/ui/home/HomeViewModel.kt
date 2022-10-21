package com.armius.dicoding.storyapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.armius.dicoding.storyapp.core.entity.StoryEntity
import com.armius.dicoding.storyapp.core.paging.StoryRepository

class HomeViewModel(storyRepository: StoryRepository) : ViewModel() {
    val story: LiveData<PagingData<StoryEntity>> =
        storyRepository.getStory().cachedIn(viewModelScope)
}