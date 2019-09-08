package com.android.example.sgmobiledata.vo

import androidx.room.Entity
import androidx.room.TypeConverters
import com.android.example.sgmobiledata.db.SgMobileDataTypeConverters

@Entity(primaryKeys = ["query"])
@TypeConverters(SgMobileDataTypeConverters::class)
data class RepoSearchResult(
    val query: String,
    val repoIds: List<Int>,
    val totalCount: Int,
    val next: Int?
)
