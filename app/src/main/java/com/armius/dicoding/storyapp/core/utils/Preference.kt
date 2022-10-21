package com.armius.dicoding.storyapp.core.utils

import android.content.Context

class Preference(context : Context) {
    private val SP_KEY = "DICODING_STORY_APP"
    private val SP_KEY_TOKEN = "TOKEN_KEY"
    private val SP_KEY_USERID = "USERID_KEY"
    private val SP_KEY_USERNAME = "USERNAME_KEY"
    private var sharedPref = context.getSharedPreferences(SP_KEY, Context.MODE_PRIVATE)
    private val editor = sharedPref.edit()

    private fun saveData(key: String, value: String?) {
        editor.putString(key, value)
        editor.apply()
    }

    fun clearLoginPreference() = editor.clear().apply()

    fun setToken(token: String?) {
        saveData(SP_KEY_TOKEN, token)
    }
    fun getToken() = sharedPref.getString(SP_KEY_TOKEN, null)

    fun setUserID(userid: String?) {
        saveData(SP_KEY_USERID, userid)
    }
    fun getUserID() = sharedPref.getString(SP_KEY_USERID, null)

    fun setUserName(username: String?) {
        saveData(SP_KEY_USERNAME, username)
    }
    fun getUserName() = sharedPref.getString(SP_KEY_USERNAME, null)
}