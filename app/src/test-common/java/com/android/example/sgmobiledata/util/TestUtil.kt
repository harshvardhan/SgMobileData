package com.android.example.sgmobiledata.util

import android.util.Log
import com.android.example.sgmobiledata.vo.*

object TestUtil {

    fun createResourceData(resourceID:String):ResourceData{
        val resourceData = ResourceData()
        var recordsItem: ArrayList<RecordsItem> = ArrayList()

        var recordsItemOne = RecordsItem("0.018787", 1, "2004-Q1")
        var recordsItemTwo = RecordsItem("0.019090", 2, "2004-Q2")
        var recordsItemThree = RecordsItem("0.039090", 3, "2004-Q3")
        var recordsItemFour = RecordsItem("0.049090", 4, "2004-Q4")

        recordsItem.add(recordsItemOne)
        recordsItem.add(recordsItemTwo)
        recordsItem.add(recordsItemThree)
        recordsItem.add(recordsItemFour)

        recordsItemOne = RecordsItem("0.908787", 1, "2005-Q1")
        recordsItemTwo = RecordsItem("1.019090", 2, "2005-Q2")
        recordsItemThree = RecordsItem("1.239090", 3, "2005-Q3")
        recordsItemFour = RecordsItem("2.849090", 4, "2005-Q4")

        recordsItem.add(recordsItemOne)
        recordsItem.add(recordsItemTwo)
        recordsItem.add(recordsItemThree)
        recordsItem.add(recordsItemFour)

        recordsItemOne = RecordsItem("4.908787", 1, "2006-Q1")
        recordsItemTwo = RecordsItem("4.019090", 2, "2006-Q2")
        recordsItemThree = RecordsItem("4.239090", 3, "2006-Q3")
        recordsItemFour = RecordsItem("4.849090", 4, "2006-Q4")

        recordsItem.add(recordsItemOne)
        recordsItem.add(recordsItemTwo)
        recordsItem.add(recordsItemThree)
        recordsItem.add(recordsItemFour)

        val result = Result(1, recordsItem.size, recordsItem.toList(), resourceID, null)

        resourceData.result = result
        resourceData.success = false
        resourceData.help = "https://data.gov.sg/api/3/action/help_show?name=datastore_search"

        return resourceData
    }

    fun calculateYearly(resultData: Result): YearlyResult {
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

        //return the yearlyResult data
        return yearlyResult
    }
}
