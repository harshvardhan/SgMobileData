package com.android.example.sgmobiledata.ui.resource

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.android.example.sgmobiledata.AppExecutors
import com.android.example.sgmobiledata.R
import com.android.example.sgmobiledata.binding.FragmentDataBindingComponent
import com.android.example.sgmobiledata.databinding.ResultFragmentBinding
import com.android.example.sgmobiledata.di.Injectable
import com.android.example.sgmobiledata.testing.OpenForTesting
import com.android.example.sgmobiledata.ui.common.RetryCallback
import com.android.example.sgmobiledata.util.autoCleared
import com.android.example.sgmobiledata.vo.Result
import com.android.example.sgmobiledata.vo.Status
import javax.inject.Inject

/**
 * The UI Controller for displaying a Mobile Consumption Data for each Quarter.
 */
@OpenForTesting
class ResultFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val resultViewModel: ResultViewModel by viewModels {
        viewModelFactory
    }

    @Inject
    lateinit var appExecutors: AppExecutors

    // mutable for testing
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<ResultFragmentBinding>()
    var result : Result? = null

    private var adapter by autoCleared<YearlyResultAdapter>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("ResultFragment", "onActivityCreated")

        resultViewModel.yearlyResultLiveData.observe(this, Observer { resource ->
            val yearlyResultData = resource?.data
            if (yearlyResultData != null && resource.status == Status.SUCCESS){
                Log.d("ResultFragment", "YearlyResultData Loaded")
                adapter.submitList(yearlyResultData.records)
            }
            else
                adapter.submitList(emptyList())
        })

        resultViewModel.result.observe(this, Observer { resource ->
            result = resource?.data
            if (result != null && resource?.status == Status.SUCCESS){
                Log.d("ResultFragment", "Result Loaded")
                resultViewModel.calculateYearly(result!!)
            }
        })

        val yearlyResultAdapter = YearlyResultAdapter(
                dataBindingComponent = dataBindingComponent,
                appExecutors = appExecutors
        ){
            yearlyRecordItem -> run{
                Toast.makeText(this.context, "The year ${yearlyRecordItem.year} has a loss quarter", Toast.LENGTH_LONG).show()
            }
        }

        binding.resultList.adapter = yearlyResultAdapter
        adapter = yearlyResultAdapter

        resultViewModel.setResourceId("a807b7ab-6cad-4aa6-87d0-e283a7353a0f")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("ResultFragment", "onCreateView")
        val dataBinding = DataBindingUtil.inflate<ResultFragmentBinding>(
            inflater,
            R.layout.result_fragment,
            container,
            false
        )
        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                resultViewModel.retry()
            }
        }
        binding = dataBinding
        return dataBinding.root
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
