package com.android.example.sgmobiledata.ui.repo

import android.content.Context
import androidx.annotation.StringRes
import androidx.databinding.DataBindingComponent
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.android.example.sgmobiledata.R
import com.android.example.sgmobiledata.binding.FragmentBindingAdapters
import com.android.example.sgmobiledata.testing.SingleFragmentActivity
import com.android.example.sgmobiledata.ui.resource.ResultFragment
import com.android.example.sgmobiledata.ui.resource.ResultViewModel
import com.android.example.sgmobiledata.util.*
import com.android.example.sgmobiledata.vo.Resource
import com.android.example.sgmobiledata.vo.Result
import com.android.example.sgmobiledata.vo.YearlyResult
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class ResultFragmentTest {
    @Rule
    @JvmField
    val activityRule = ActivityTestRule(SingleFragmentActivity::class.java, true, true)
    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResourceRule()
    @Rule
    @JvmField
    val countingAppExecutors = CountingAppExecutorsRule()
    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityRule)

    private val resultLiveData = MutableLiveData<Resource<Result>>()
    private val yearlyResultLiveData = MutableLiveData<Resource<YearlyResult>>()
    private lateinit var viewModel: ResultViewModel
    private lateinit var mockBindingAdapter: FragmentBindingAdapters

    private val resultFragment = TestResultFragment()

    @Before
    fun init() {
        viewModel = mock(ResultViewModel::class.java)
        mockBindingAdapter = mock(FragmentBindingAdapters::class.java)
        doNothing().`when`(viewModel).setResourceId("a807b7ab-6cad-4aa6-87d0-e283a7353a0f")
        `when`(viewModel.result).thenReturn(resultLiveData)
        `when`(viewModel.yearlyResultLiveData).thenReturn(yearlyResultLiveData)
        resultFragment.appExecutors = countingAppExecutors.appExecutors
        resultFragment.viewModelFactory = ViewModelUtil.createFor(viewModel)
        resultFragment.dataBindingComponent = object : DataBindingComponent {
            override fun getFragmentBindingAdapters(): FragmentBindingAdapters {
                return mockBindingAdapter
            }
        }
        activityRule.activity.setFragment(resultFragment)
        EspressoTestUtil.disableProgressBarAnimations(activityRule)
    }

    @Test
    fun testLoading() {
        resultLiveData.postValue(Resource.loading(null))
        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
        onView(withId(R.id.retry)).check(matches(not(isDisplayed())))
    }

    @Test
    fun testValueWhileLoading() {
        val resourceData = TestUtil.createResourceData("a807b7ab-6cad-4aa6-87d0-e283a7353a0f")
        resultLiveData.postValue(Resource.loading(resourceData.result))
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("2004"))))
    }

    private fun listMatcher(): RecyclerViewMatcher {
        return RecyclerViewMatcher(R.id.result_list)
    }

    private fun setResult(vararg names: String) {
        val resourceData = TestUtil.createResourceData("a807b7ab-6cad-4aa6-87d0-e283a7353a0f")
        val yearlyResult = resourceData.result?.let { TestUtil.calculateYearly(it) }
        yearlyResultLiveData.postValue(Resource.success(yearlyResult))
    }

    private fun getString(@StringRes id: Int, vararg args: Any): String {
        return ApplicationProvider.getApplicationContext<Context>().getString(id, *args)
    }

    class TestResultFragment : ResultFragment() {
        val navController = mock<NavController>()
        override fun navController() = navController
    }
}