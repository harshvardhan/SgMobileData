package com.android.example.sgmobiledata.di

import com.android.example.sgmobiledata.ui.resource.ResultFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeResultFragment(): ResultFragment
}
