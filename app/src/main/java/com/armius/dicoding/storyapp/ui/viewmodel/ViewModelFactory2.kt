package com.armius.dicoding.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.armius.dicoding.storyapp.core.net.ApiService
import com.armius.dicoding.storyapp.ui.addstory.AddStoryViewModel
import com.armius.dicoding.storyapp.ui.login.LoginViewModel
import com.armius.dicoding.storyapp.ui.maps.MapsViewModel
import com.armius.dicoding.storyapp.ui.register.RegisterViewModel

class ViewModelFactory2(private val client: ApiService) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(client) as T
        } else if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(client) as T
        } else if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(client) as T
        } else if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(client) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}