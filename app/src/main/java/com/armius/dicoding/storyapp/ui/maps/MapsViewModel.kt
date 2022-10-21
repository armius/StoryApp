package com.armius.dicoding.storyapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.armius.dicoding.storyapp.core.net.ApiService
import com.armius.dicoding.storyapp.core.net.ListStoryItem
import com.armius.dicoding.storyapp.core.net.ResponseAllStories
import com.armius.dicoding.storyapp.ui.utils.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val client: ApiService) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _infoMsg = MutableLiveData<Event<String>>()
    val infoMsg: LiveData<Event<String>> = _infoMsg
    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = _listStory

    fun getStoryList(page: Int, size: Int) {
        _isLoading.value = true
        val call = client.getAllStories(page, size, 1)
        call.enqueue(object : Callback<ResponseAllStories> {
            override fun onResponse(call: Call<ResponseAllStories>, response: Response<ResponseAllStories>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _listStory.value = responseBody.listStory
                        if (responseBody.listStory.isEmpty()) {
                            _infoMsg.value = Event(Event.NO_DATA)
                        } else {
                            _infoMsg.value = Event("")
                        }
                    }
                } else {
                    val responseBody = response.errorBody()
                    if (responseBody != null) {
                        _infoMsg.value = Event(responseBody.string())
                    }
                }
            }

            override fun onFailure(call: Call<ResponseAllStories>, t: Throwable) {
                _isLoading.value = false
                _infoMsg.value = Event(Event.ON_FAILURE)
            }
        })
    }
}