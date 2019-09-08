package com.android.example.sgmobiledata.vo

import com.google.gson.annotations.SerializedName

data class RecordsItem(

	@field:SerializedName("volume_of_mobile_data")
	val volumeOfMobileData: String? = null,

	@field:SerializedName("_id")
	val id: Int? = null,

	@field:SerializedName("quarter")
	val quarter: String? = null
)