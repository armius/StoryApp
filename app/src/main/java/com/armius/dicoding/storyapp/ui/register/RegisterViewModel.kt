package com.armius.dicoding.storyapp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.armius.dicoding.storyapp.core.net.ApiService
import com.armius.dicoding.storyapp.core.net.ResponseBasic
import com.armius.dicoding.storyapp.ui.utils.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val client: ApiService) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _infoMsg = MutableLiveData<Event<String>>()
    val infoMsg: LiveData<Event<String>> = _infoMsg

    fun registerUser(name: String, email: String, password: String) {
        _isLoading.value = true
        val call = client.registerUser(name, email, password)
        call.enqueue(object : Callback<ResponseBasic> {
            override fun onResponse(call: Call<ResponseBasic>, response: Response<ResponseBasic>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _infoMsg.value = Event(Event.REGISTER_SUCCESS)
                    }
                } else {
                    val responseBody = response.errorBody()
                    if (responseBody != null) {
                        _infoMsg.value = Event(responseBody.string())
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBasic>, t: Throwable) {
                _isLoading.value = false
                _infoMsg.value = Event(Event.ON_FAILURE)
            }
        })
    }
}