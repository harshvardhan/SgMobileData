package com.android.example.sgmobiledata.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.example.sgmobiledata.testing.OpenForTesting
import com.android.example.sgmobiledata.vo.Result

/**
 * Interface for database access on Repo related operations.
 */
@Dao
@OpenForTesting
abstract class ResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg result: Result)

    @Query("SELECT * FROM result WHERE resourceId = :resourceId")
    abstract fun load(resourceId: String): LiveData<Result>
}
