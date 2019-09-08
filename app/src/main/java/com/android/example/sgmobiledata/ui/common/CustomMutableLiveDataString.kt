package com.android.example.sgmobiledata.ui.common

import androidx.lifecycle.LiveData
import com.android.example.sgmobiledata.util.AbsentLiveData

data class CustomMutableLiveDataString(val customMutableLiveDataString: String) {
    public fun <T> ifExists(f: (String) -> LiveData<T>): LiveData<T> {
        return if (customMutableLiveDataString.isBlank()) {
            AbsentLiveData.create()
        } else {
            f(customMutableLiveDataString)
        }
    }
}
