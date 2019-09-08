package com.android.example.sgmobiledata.vo

import com.google.gson.annotations.SerializedName

data class FieldsItem(

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("type")
	val type: String? = null
)