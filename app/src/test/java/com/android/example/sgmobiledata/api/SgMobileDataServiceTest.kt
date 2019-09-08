package com.android.example.sgmobiledata.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android.example.sgmobiledata.util.LiveDataCallAdapterFactory
import com.android.example.sgmobiledata.util.LiveDataTestUtil.getValue
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.hamcrest.CoreMatchers
import org.hamcrest.core.IsNull
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(JUnit4::class)
class SgMobileDataServiceTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: SgMobileDataService

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun createService() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(SgMobileDataService::class.java)
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun getResourceData() {
        enqueueResponse("results.json")
        val resourceData = (getValue(service.getResourceData("a807b7ab-6cad-4aa6-87d0-e283a7353a0f")) as ApiSuccessResponse).body

        val request = mockWebServer.takeRequest()
        Assert.assertThat(request.path, CoreMatchers.`is`("/action/datastore_search?resource_id=a807b7ab-6cad-4aa6-87d0-e283a7353a0f"))

        Assert.assertThat(resourceData.success, CoreMatchers.`is`(true))

        val resourceID = resourceData.result?.resourceId
        Assert.assertThat(resourceID, CoreMatchers.`is`("a807b7ab-6cad-4aa6-87d0-e283a7353a0f"))

        val recordsItem = resourceData.result?.records?.get(0)
        Assert.assertThat(recordsItem, IsNull.notNullValue())
        Assert.assertThat(recordsItem?.quarter, CoreMatchers.`is`("2004-Q3"))
        Assert.assertThat(recordsItem?.id, CoreMatchers.`is`(1))
    }

    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val inputStream = javaClass.classLoader
                .getResourceAsStream("api-response/$fileName")
        val source = Okio.buffer(Okio.source(inputStream))
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(
                mockResponse
                        .setBody(source.readString(Charsets.UTF_8))
        )
    }

}
