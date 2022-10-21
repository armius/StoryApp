package com.armius.dicoding.storyapp.ui.addstory

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.armius.dicoding.storyapp.core.net.ApiService
import com.armius.dicoding.storyapp.core.net.ResponseBasic
import com.armius.dicoding.storyapp.ui.utils.Event
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryViewModel(private val client: ApiService) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _infoMsg = MutableLiveData<Event<String>>()
    val infoMsg: LiveData<Event<String>> = _infoMsg

    private val _imageBitmap = MutableLiveData<Bitmap?>()
    val imageBitmap: LiveData<Bitmap?> = _imageBitmap

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri

    private val _selectedFile = MutableLiveData<File>()
    val selectedFile: LiveData<File> = _selectedFile

    fun submitStory(description: RequestBody, imgMP: MultipartBody.Part, lat: RequestBody? = null, lng: RequestBody? = null) {
        _isLoading.value = true
        _isSuccess.value = false
        val call = client.addNewStory(imgMP, description,lat,lng)
        call.enqueue(object : Callback<ResponseBasic> {
            override fun onResponse(call: Call<ResponseBasic>, response: Response<ResponseBasic>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _isSuccess.value = responseBody.error == false
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

    fun setSelectedImage(file: File, bitmap: Bitmap?, uri: Uri?) {
        _imageBitmap.value = bitmap
        _imageUri.value = uri
        _selectedFile.value = file
    }
}