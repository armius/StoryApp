package com.armius.dicoding.storyapp.ui.utils

open class Event<out T>(private val content: T) {

    @Suppress("MemberVisibilityCanBePrivate")
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    companion object {
        const val ON_FAILURE = "callback.onfailure"
        const val REGISTER_SUCCESS = "registersuccess"
        const val NO_DATA = "no_data_result"
    }
}