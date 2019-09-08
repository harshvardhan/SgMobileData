package com.android.example.sgmobiledata.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.example.sgmobiledata.ui.resource.ResultViewModel
import com.android.example.sgmobiledata.viewmodel.SgMobileDataViewModelFactory

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(ResultViewModel::class)
    abstract fun bindResultViewModel(resultViewModel: ResultViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: SgMobileDataViewModelFactory): ViewModelProvider.Factory
}
