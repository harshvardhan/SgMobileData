package com.android.example.sgmobiledata.api

import okhttp3.MediaType
import okhttp3.ResponseBody
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@RunWith(JUnit4::class)
class ApiResponseTest {
    @Test
    fun exception() {
        val exception = Exception("foo")
        val (errorMessage) = ApiResponse.create<String>(exception)
        assertThat<String>(errorMessage, `is`("foo"))
    }

    @Test
    fun success() {
        val apiResponse: ApiSuccessResponse<String> = ApiResponse
            .create<String>(Response.success("foo")) as ApiSuccessResponse<String>
        assertThat<String>(apiResponse.body, `is`("foo"))
        assertThat<Int>(apiResponse.nextPage, `is`(nullValue()))
    }

    @Test
    fun link() {
        val link =
            "https://data.gov.sg/api/action/datastore_search?resource_id=a807b7ab-6cad-4aa6-87d0-e283a7353a0f"
        val headers = okhttp3.Headers.of("link", link)
        val response = ApiResponse.create<String>(Response.success("foo", headers))
        assertThat<Any>((response as ApiSuccessResponse).body, notNullValue())
    }

    @Test
    fun badLinkHeader() {
        val link = "https://data.sg/api/action/datastore_search?resource_id=a807b7ab-6cad-4aa6-87d0-e283a7353a0f"
        val headers = okhttp3.Headers.of("link", link)
        val response = ApiResponse.create<String>(Response.success("foo", headers))
        assertThat<Int>((response as ApiSuccessResponse).nextPage, nullValue())
    }

    @Test
    fun error() {
        val errorResponse = Response.error<String>(
            400,
            ResponseBody.create(MediaType.parse("application/txt"), "blah")
        )
        val (errorMessage) = ApiResponse.create<String>(errorResponse) as ApiErrorResponse<String>
        assertThat<String>(errorMessage, `is`("blah"))
    }
}