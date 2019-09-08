package com.android.example.sgmobiledata.vo

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.android.example.sgmobiledata.db.SgMobileDataTypeConverters
import com.google.gson.annotations.SerializedName

@Entity
@TypeConverters(SgMobileDataTypeConverters::class)
data class Result(

	@PrimaryKey(autoGenerate = true)
	@NonNull
	val id: Int,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("records")
	val records: List<RecordsItem?>? = null,

	@field:SerializedName("resource_id")
	val resourceId: String? = null,

	@field:SerializedName("fields")
	val fields: List<FieldsItem?>? = null
)