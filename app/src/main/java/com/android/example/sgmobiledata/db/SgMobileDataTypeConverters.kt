package com.android.example.sgmobiledata.db

import androidx.room.TypeConverter
import com.android.example.sgmobiledata.vo.FieldsItem
import com.android.example.sgmobiledata.vo.RecordsItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber

object SgMobileDataTypeConverters {
    @TypeConverter
    @JvmStatic
    fun stringToIntList(data: String?): List<Int>? {
        return data?.let {
            it.split(",").map {
                try {
                    it.toInt()
                } catch (ex: NumberFormatException) {
                    Timber.e(ex, "Cannot convert $it to number")
                    null
                }
            }
        }?.filterNotNull()
    }

    @TypeConverter
    @JvmStatic
    fun intListToString(ints: List<Int>?): String? {
        return ints?.joinToString(",")
    }

    @TypeConverter
    @JvmStatic
    fun fromRecordItemList(value: List<RecordsItem>): String {
        val gson = Gson()
        val type = object : TypeToken<List<RecordsItem>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    @JvmStatic
    fun toRecordsItemList(value: String): List<RecordsItem> {
        val gson = Gson()
        val type = object : TypeToken<List<RecordsItem>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    @JvmStatic
    fun fromFieldsItemList(value: List<FieldsItem>): String {
        val gson = Gson()
        val type = object : TypeToken<List<FieldsItem>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    @JvmStatic
    fun toFieldsItemList(value: String): List<FieldsItem> {
        val gson = Gson()
        val type = object : TypeToken<List<FieldsItem>>() {}.type
        return gson.fromJson(value, type)
    }
}
