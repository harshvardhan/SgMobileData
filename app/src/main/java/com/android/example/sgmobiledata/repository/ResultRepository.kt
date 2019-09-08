package com.android.example.sgmobiledata.repository

import androidx.lifecycle.LiveData
import com.android.example.sgmobiledata.AppExecutors
import com.android.example.sgmobiledata.api.ApiResponse
import com.android.example.sgmobiledata.api.SgMobileDataService
import com.android.example.sgmobiledata.db.ResultDao
import com.android.example.sgmobiledata.db.SgMobileDataDB
import com.android.example.sgmobiledata.testing.OpenForTesting
import com.android.example.sgmobiledata.util.RateLimiter
import com.android.example.sgmobiledata.vo.Resource
import com.android.example.sgmobiledata.vo.ResourceData
import com.android.example.sgmobiledata.vo.Result
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that handles Results instances.
 */
@Singleton
@OpenForTesting
class ResultRepository @Inject constructor(
        private val appExecutors: AppExecutors,
        private val db: SgMobileDataDB,
        private val resultDao: ResultDao,
        private val sgMobileDataService: SgMobileDataService
) {

    private val repoListRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

    fun loadResult(resourceID: String): LiveData<Resource<Result>> {
        return object : NetworkBoundResource<Result, ResourceData>(appExecutors) {
            override fun shouldFetch(data: Result?): Boolean {
                //don't fetch from API if data and more than 0 records
                if (data != null){
                    if (data.records?.size!! > 0)
                        return false
                }
                return true
            }

            override fun saveCallResult(item: ResourceData) {
                item.result?.let { resultDao.insert(it) }
            }

            override fun loadFromDb():LiveData<Result>{
                return resultDao.load(resourceID)
            }

            override fun createCall():LiveData<ApiResponse<ResourceData>>{
                return sgMobileDataService.getResourceData(resourceID)
            }

            override fun onFetchFailed() {
                repoListRateLimit.reset(resourceID)
            }
        }.asLiveData()
    }
}
