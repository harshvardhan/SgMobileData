package com.android.example.sgmobiledata.vo

import com.google.gson.annotations.SerializedName

data class ResourceData(

	@field:SerializedName("result")
	var result: Result? = null,

	@field:SerializedName("help")
	var help: String? = null,

	@field:SerializedName("success")
	var success: Boolean? = null
)