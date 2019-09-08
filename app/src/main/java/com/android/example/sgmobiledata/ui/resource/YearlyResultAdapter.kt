package com.android.example.sgmobiledata.ui.resource

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.android.example.sgmobiledata.AppExecutors
import com.android.example.sgmobiledata.R
import com.android.example.sgmobiledata.databinding.YearlyResultItemBinding
import com.android.example.sgmobiledata.ui.common.DataBoundListAdapter
import com.android.example.sgmobiledata.vo.YearlyRecordItem

class YearlyResultAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val callback: ((YearlyRecordItem) -> Unit)?
) : DataBoundListAdapter<YearlyRecordItem, YearlyResultItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<YearlyRecordItem>() {
        override fun areItemsTheSame(oldItem: YearlyRecordItem, newItem: YearlyRecordItem): Boolean {
            return oldItem.year == newItem.year
        }

        override fun areContentsTheSame(oldItem: YearlyRecordItem, newItem: YearlyRecordItem): Boolean {
            return oldItem.year == newItem.year
                    && oldItem.volumeOfMobileData == newItem.volumeOfMobileData
        }
    }
) {

    override fun createBinding(parent: ViewGroup): YearlyResultItemBinding {
        val binding = DataBindingUtil
            .inflate<YearlyResultItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.yearly_result_item,
                parent,
                false,
                dataBindingComponent
            )
        binding.downIcon.setOnClickListener {
            binding.yearlyRecordItem?.let {
                callback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: YearlyResultItemBinding, item: YearlyRecordItem) {
        binding.yearlyRecordItem = item
    }
}
