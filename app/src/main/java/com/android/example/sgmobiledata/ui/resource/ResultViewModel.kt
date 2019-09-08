package com.android.example.sgmobiledata.ui.resource

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.android.example.sgmobiledata.repository.ResultRepository
import com.android.example.sgmobiledata.testing.OpenForTesting
import com.android.example.sgmobiledata.ui.common.CustomMutableLiveDataString
import com.android.example.sgmobiledata.vo.*
import javax.inject.Inject

@OpenForTesting
class ResultViewModel @Inject constructor(repository: ResultRepository) : ViewModel() {
    private val _resourceID: MutableLiveData<CustomMutableLiveDataString> = MutableLiveData()
    val resourceID: LiveData<CustomMutableLiveDataString>
        get() = _resourceID

    val result: LiveData<Resource<Result>> = Transformations
        .switchMap(_resourceID) { input ->
            repository.loadResult(input.customMutableLiveDataString)
        }

    val yearlyResultLiveData: MutableLiveData<Resource<YearlyResult>> = MutableLiveData()

    fun retry() {
        val resourceID = _resourceID.value?.customMutableLiveDataString
        if (resourceID != null) {
            _resourceID.value = CustomMutableLiveDataString(resourceID)
        }
    }

    fun setResourceId(resourceID: String) {
        val update = CustomMutableLiveDataString(resourceID)
        if (_resourceID.value == update) {
            return
        }
        _resourceID.value = update
    }

    fun calculateYearly(resultData: Result){
        val yearlyResultMap = LinkedHashMap<String, LinkedHashMap<String, String>>()
        val yearlyResult = YearlyResult()
        resultData.records?.forEachIndexed { index, recordsItem ->
            run {
                Log.d("ResultVM", "Record Item $index :: " + recordsItem?.quarter)
                //1) Split the quarter into Year and Quarter information

                val yearQuarterList = recordsItem?.quarter?.split("-")
                val year = yearQuarterList?.get(0)
                val quarter= yearQuarterList?.get(1)

                //2) Save the volume in the a quarter -> volume HashMap
                //3) Retrieve quarters HashMap for an year in case data is not on sequence
                var quarters = yearlyResultMap[year]
                //4) check if its null, if yes then initialize it
                if (quarters == null)
                    quarters = LinkedHashMap<String, String>()

                recordsItem?.volumeOfMobileData?.let { quarter?.let { it1 -> quarters.put(it1, it) } }

                //5) Save it back to the Yearly HashMap
                year?.let { yearlyResultMap.put(it, quarters) }
            }
        }

        Log.d("ResultVM", yearlyResultMap.toString())

        //convert this HashMap to List of [com.android.example.sgmobiledata.vo.YearlyRecordItem]
        yearlyResultMap.entries.forEach { entry ->
            run {
                Log.d("ResultVM","Key : " + entry.key + " Value : " + entry.value)
                var yearlySum = 0.0F
                var lastQuarterVolume:Float? = Float.NEGATIVE_INFINITY
                val yearlyRecordItem = YearlyRecordItem()

                yearlyRecordItem.year = entry.key

                entry.value.entries.forEach { quarterEntry -> run{
                    if (lastQuarterVolume != null) {
                        if (lastQuarterVolume!! > quarterEntry.value.toFloat())
                            yearlyRecordItem.hasLossQuarter = true
                    }
                    yearlySum += quarterEntry.value.toFloat()

                    lastQuarterVolume = quarterEntry.value.toFloat()
                }}

                yearlyRecordItem.volumeOfMobileData = yearlySum.toBigDecimal().toPlainString()

                yearlyResult.records.add(yearlyRecordItem)
                Log.d("ResultVM","YearlyRecordItem is $yearlyRecordItem")
            }
        }

        //send an update to the observers with this data
        yearlyResultLiveData.postValue(Resource(Status.SUCCESS, yearlyResult, ""))
    }
}
