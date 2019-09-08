package com.android.example.sgmobiledata.ui.result

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.android.example.sgmobiledata.repository.ResultRepository
import com.android.example.sgmobiledata.ui.resource.ResultViewModel
import com.android.example.sgmobiledata.util.mock
import com.android.example.sgmobiledata.vo.Resource
import com.android.example.sgmobiledata.vo.Result
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*

@RunWith(JUnit4::class)
class ResultViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository = mock(ResultRepository::class.java)
    private var resultViewModel = ResultViewModel(repository)

    @Test
    fun testNull() {
        assertThat(resultViewModel.result, notNullValue())
        verify(repository, never()).loadResult(anyString())
    }

    @Test
    fun dontFetchWithoutObservers() {
        resultViewModel.setResourceId("a807b7ab-6cad-4aa6-87d0-e283a7353a0f")
        verify(repository, never()).loadResult(anyString())
    }

    @Test
    fun fetchWhenObserved() {
        resultViewModel.setResourceId("a807b7ab-6cad-4aa6-87d0-e283a7353a0f")
        resultViewModel.result.observeForever(mock())
        verify(repository).loadResult("a807b7ab-6cad-4aa6-87d0-e283a7353a0f")
    }

    @Test
    fun changeWhileObserved() {
        resultViewModel.result.observeForever(mock())

        resultViewModel.setResourceId("a807b7ab-6cad-4aa6-87d0-e283a7353a0f")
        resultViewModel.setResourceId("e283a7353a0f-6cad-4aa6-87d0-a807b7ab")

        verify(repository).loadResult("a807b7ab-6cad-4aa6-87d0-e283a7353a0f")
        verify(repository).loadResult("e283a7353a0f-6cad-4aa6-87d0-a807b7ab")
    }

    @Test
    fun retry() {
        resultViewModel.retry()
        verifyNoMoreInteractions(repository)
        resultViewModel.setResourceId("foo")
        verifyNoMoreInteractions(repository)
        val observer = mock<Observer<Resource<Result>>>()
        resultViewModel.result.observeForever(observer)
        verify(repository).loadResult("foo")
        reset(repository)
        resultViewModel.retry()
        verify(repository).loadResult("foo")
    }
}