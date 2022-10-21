package com.armius.dicoding.storyapp.core.net

import com.google.gson.annotations.SerializedName

data class ResponseBasic(
	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
