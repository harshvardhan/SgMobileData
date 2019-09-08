package com.android.example.sgmobiledata.vo

data class YearlyRecordItem(var volumeOfMobileData: String? = null){
	var hasLossQuarter: Boolean = false

	var year: String? = null

	var quarters:HashMap<String, String> = HashMap<String, String>()
}