package com.android.example.sgmobiledata.db


import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.example.sgmobiledata.vo.*

/**
 * Main database description.
 */
@Database(
    entities = [
        User::class,
        Repo::class,
        Contributor::class,
        RepoSearchResult::class,
        Result::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SgMobileDataDB : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun repoDao(): RepoDao

    abstract fun resultDao(): ResultDao
}
