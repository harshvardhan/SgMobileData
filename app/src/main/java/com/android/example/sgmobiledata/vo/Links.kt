package com.android.example.sgmobiledata.vo

import com.google.gson.annotations.SerializedName

data class Links(

	@field:SerializedName("next")
	val next: String? = null,

	@field:SerializedName("start")
	val start: String? = null
)