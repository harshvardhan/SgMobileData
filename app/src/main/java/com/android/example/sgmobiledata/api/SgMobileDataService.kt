package com.android.example.sgmobiledata.api

import androidx.lifecycle.LiveData
import com.android.example.sgmobiledata.vo.ResourceData
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * REST API access points
 */
interface SgMobileDataService {
    //https://api.myjson.com/bins/ii6zb
    @GET("action/datastore_search")
    fun getResourceData(@Query("resource_id") resourceID: String): LiveData<ApiResponse<ResourceData>>
}
