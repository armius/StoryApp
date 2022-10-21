package com.armius.dicoding.storyapp

import com.armius.dicoding.storyapp.core.entity.StoryEntity
import com.armius.dicoding.storyapp.core.net.ListStoryItem
import com.armius.dicoding.storyapp.core.net.ResponseAllStories
import com.armius.dicoding.storyapp.core.net.ResponseBasic
import com.armius.dicoding.storyapp.core.net.ResponseLogin
import com.google.gson.Gson

object DataDummy {

    fun generateDummyStoryResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryEntity(
                i.toString(),
                "name + $i",
                "description $i",
                "http://imgdummy.com/photo$i.jpg",
                "today $i",
                1.0,
                1.0,
            )
            items.add(quote)
        }
        return items
    }

    fun generateDummyStoryResponse2(type: String): ResponseAllStories {
        val items: ArrayList<ListStoryItem> = arrayListOf()
        var response: ResponseAllStories = ResponseAllStories(items, false, "")
        when (type) {
            "success" -> {
                for (i in 0..100) {
                    val story = ListStoryItem(
                        "name + $i",
                        "description $i",
                        "http://imgdummy.com/photo$i.jpg",
                        "today $i",
                        1.0,
                        i.toString(),
                        1.0
                    )
                    items.add(story)
                }
                response = ResponseAllStories(items, false, "Stories fetched successfully")
            }
            "nodata" -> {
                val items: ArrayList<ListStoryItem> = arrayListOf()
                response = ResponseAllStories(items, false, "No data")
            }
            "failed" -> {
                val items: ArrayList<ListStoryItem> = arrayListOf()
                response = ResponseAllStories(items, true, "Something error")
            }
        }
        return response
    }

    fun loginResponseSuccess(): ResponseLogin {
        val json = """
            {
                "error": false,
                "message": "success",
                "loginResult": {
                    "userId": "user-6Tg_ouLrltwgGEcI",
                    "name": "ariari77",
                    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLTZUZ19vdUxybHR3Z0dFY0kiLCJpYXQiOjE2NjQ4ODI0MzZ9.DpSKonMH7Mcvd5rN79BQOESvya6d2vO0IHE_bjgptJ0"
                }
            }
        """.trimIndent()
        return Gson().fromJson(json, ResponseLogin::class.java)
    }

    fun loginResponseFailed(): ResponseLogin {
        val json = """
            {
                "error": true,
                "message": "Invalid password/ user not found"
            }
        """.trimIndent()
        return Gson().fromJson(json, ResponseLogin::class.java)
    }

    fun responseBasicSuccess(): ResponseBasic {
        val json = """
            {
                "error": false,
                "message": "Success message"
            }
        """.trimIndent()
        return Gson().fromJson(json, ResponseBasic::class.java)
    }

    fun responseBasicFailed(): ResponseBasic {
        val json = """
            {
                "error": true,
                "message": "Error message"
            }
        """.trimIndent()
        return Gson().fromJson(json, ResponseBasic::class.java)
    }
}