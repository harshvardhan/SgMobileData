package com.android.example.sgmobiledata.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.example.sgmobiledata.api.SgMobileDataService
import com.android.example.sgmobiledata.db.ResultDao
import com.android.example.sgmobiledata.db.SgMobileDataDB
import com.android.example.sgmobiledata.util.ApiUtil.successCall
import com.android.example.sgmobiledata.util.InstantAppExecutors
import com.android.example.sgmobiledata.util.TestUtil
import com.android.example.sgmobiledata.util.mock
import com.android.example.sgmobiledata.vo.Resource
import com.android.example.sgmobiledata.vo.Result
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*

@RunWith(JUnit4::class)
class ResultRepositoryTest {
    private lateinit var repository: ResultRepository
    private val dao = mock(ResultDao::class.java)
    private val service = mock(SgMobileDataService::class.java)
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun init() {
        mockWebServer = MockWebServer()

        val db = mock(SgMobileDataDB::class.java)
        `when`(db.resultDao()).thenReturn(dao)
        `when`(db.runInTransaction(ArgumentMatchers.any())).thenCallRealMethod()
        repository = ResultRepository(InstantAppExecutors(), db, dao, service)
    }

    @Test
    fun loadResourceDataFromNetwork() {
        val dbData = MutableLiveData<Result>()
        `when`(dao.load("a807b7ab-6cad-4aa6-87d0-e283a7353a0f")).thenReturn(dbData)

        val resourceData = TestUtil.createResourceData("a807b7ab-6cad-4aa6-87d0-e283a7353a0f")
        val call = successCall(resourceData)
        `when`(service.getResourceData("a807b7ab-6cad-4aa6-87d0-e283a7353a0f")).thenReturn(call)

        val data = repository.loadResult("a807b7ab-6cad-4aa6-87d0-e283a7353a0f")
        verify(dao).load("a807b7ab-6cad-4aa6-87d0-e283a7353a0f")
        verifyNoMoreInteractions(service)

        val observer = mock<Observer<Resource<Result>>>()
        data.observeForever(observer)
        verifyNoMoreInteractions(service)
        verify(observer).onChanged(Resource.loading(null))
        val updatedDbData = MutableLiveData<Result>()
        `when`(dao.load("a807b7ab-6cad-4aa6-87d0-e283a7353a0f")).thenReturn(updatedDbData)

        dbData.postValue(null)
        verify(service).getResourceData("a807b7ab-6cad-4aa6-87d0-e283a7353a0f")
        resourceData.result?.let { verify(dao).insert(it) }

        updatedDbData.postValue(resourceData.result)
        verify(observer).onChanged(Resource.success(resourceData.result))
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
    /*

    @Test
    fun loadContributors() {
        val dbData = MutableLiveData<List<Contributor>>()
        `when`(dao.loadContributors("foo", "bar")).thenReturn(dbData)

        val data = repository.loadContributors(
            "foo",
            "bar"
        )
        verify(dao).loadContributors("foo", "bar")

        verify(service, never()).getContributors(anyString(), anyString())

        val repo = TestUtil.createRepo("foo", "bar", "desc")
        val contributor = TestUtil.createContributor(repo, "log", 3)
        // network does not send these
        val contributors = listOf(contributor)
        val call = successCall(contributors)
        `when`(service.getContributors("foo", "bar"))
            .thenReturn(call)

        val observer = mock<Observer<Resource<List<Contributor>>>>()
        data.observeForever(observer)

        verify(observer).onChanged(Resource.loading(null))

        val updatedDbData = MutableLiveData<List<Contributor>>()
        `when`(dao.loadContributors("foo", "bar")).thenReturn(updatedDbData)
        dbData.value = emptyList()

        verify(service).getContributors("foo", "bar")
        val inserted = argumentCaptor<List<Contributor>>()
        // empty list is a workaround for null capture return
        verify(dao).insertContributors(inserted.capture() ?: emptyList())


        assertThat(inserted.value.size, `is`(1))
        val first = inserted.value[0]
        assertThat(first.repoName, `is`("bar"))
        assertThat(first.repoOwner, `is`("foo"))

        updatedDbData.value = contributors
        verify(observer).onChanged(Resource.success(contributors))
    }

    @Test
    fun searchNextPage_null() {
        `when`(dao.findSearchResult("foo")).thenReturn(null)
        val observer = mock<Observer<Resource<Boolean>>>()
        repository.searchNextPage("foo").observeForever(observer)
        verify(observer).onChanged(null)
    }

    @Test
    fun search_fromDb() {
        val ids = arrayListOf(1, 2)

        val observer = mock<Observer<Resource<List<Repo>>>>()
        val dbSearchResult = MutableLiveData<RepoSearchResult>()
        val repositories = MutableLiveData<List<Repo>>()

        `when`(dao.search("foo")).thenReturn(dbSearchResult)

        repository.search("foo").observeForever(observer)

        verify(observer).onChanged(Resource.loading(null))
        verifyNoMoreInteractions(service)
        reset(observer)

        val dbResult = RepoSearchResult("foo", ids, 2, null)
        `when`(dao.loadOrdered(ids)).thenReturn(repositories)

        dbSearchResult.postValue(dbResult)

        val repoList = arrayListOf<Repo>()
        repositories.postValue(repoList)
        verify(observer).onChanged(Resource.success(repoList))
        verifyNoMoreInteractions(service)
    }

    @Test
    fun search_fromServer() {
        val ids = arrayListOf(1, 2)
        val repo1 = TestUtil.createRepo(1, "owner", "repo 1", "desc 1")
        val repo2 = TestUtil.createRepo(2, "owner", "repo 2", "desc 2")

        val observer = mock<Observer<Resource<List<Repo>>>>()
        val dbSearchResult = MutableLiveData<RepoSearchResult>()
        val repositories = MutableLiveData<List<Repo>>()

        val repoList = arrayListOf(repo1, repo2)
        val apiResponse = RepoSearchResponse(2, repoList)

        val callLiveData = MutableLiveData<ApiResponse<RepoSearchResponse>>()
        `when`(service.searchRepos("foo")).thenReturn(callLiveData)

        `when`(dao.search("foo")).thenReturn(dbSearchResult)

        repository.search("foo").observeForever(observer)

        verify(observer).onChanged(Resource.loading(null))
        verifyNoMoreInteractions(service)
        reset(observer)

        `when`(dao.loadOrdered(ids)).thenReturn(repositories)
        dbSearchResult.postValue(null)
        verify(dao, never()).loadOrdered(anyList())

        verify(service).searchRepos("foo")
        val updatedResult = MutableLiveData<RepoSearchResult>()
        `when`(dao.search("foo")).thenReturn(updatedResult)
        updatedResult.postValue(RepoSearchResult("foo", ids, 2, null))

        callLiveData.postValue(ApiResponse.create(Response.success(apiResponse)))
        verify(dao).insertRepos(repoList)
        repositories.postValue(repoList)
        verify(observer).onChanged(Resource.success(repoList))
        verifyNoMoreInteractions(service)
    }

    @Test
    fun search_fromServer_error() {
        `when`(dao.search("foo")).thenReturn(AbsentLiveData.create())
        val apiResponse = MutableLiveData<ApiResponse<RepoSearchResponse>>()
        `when`(service.searchRepos("foo")).thenReturn(apiResponse)

        val observer = mock<Observer<Resource<List<Repo>>>>()
        repository.search("foo").observeForever(observer)
        verify(observer).onChanged(Resource.loading(null))

        apiResponse.postValue(ApiResponse.create(Exception("idk")))
        verify(observer).onChanged(Resource.error("idk", null))
    }*/
}