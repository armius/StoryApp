package com.armius.dicoding.storyapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.armius.dicoding.storyapp.core.net.ApiService
import com.armius.dicoding.storyapp.core.net.ResponseLogin
import com.armius.dicoding.storyapp.ui.utils.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val client: ApiService) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _isLoginSuccess = MutableLiveData<Boolean>()
    val isLoginSuccess: LiveData<Boolean> = _isLoginSuccess

    private val _userID = MutableLiveData<String>()
    val userID: LiveData<String> = _userID
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName
    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    private val _errMsg = MutableLiveData<Event<String>>()
    val errMsg: LiveData<Event<String>> = _errMsg



    fun loginUser(email: String, password: String) {
        _isLoading.value = true
        _isLoginSuccess.value = false
        val call = client.loginUser(email, password)
        call.enqueue(object : Callback<ResponseLogin> {
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        setSession(
                            responseBody.loginResult.userId,
                            responseBody.loginResult.name,
                            responseBody.loginResult.token
                        )
                        _isLoginSuccess.value = responseBody.error == false
                    }
                } else {
                    val responseBody = response.errorBody()
                    if (responseBody != null) {
                        _errMsg.value = Event(responseBody.string())
                    }
                }
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                _isLoading.value = false
                _errMsg.value = Event(Event.ON_FAILURE)
            }
        })
    }

    fun setSession(userid:String, username: String, token: String) {
        _userID.value = userid
        _userName.value = username
        _token.value = token
    }

}