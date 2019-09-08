package com.android.example.sgmobiledata.util

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

import com.android.example.sgmobiledata.TestApp

/**
 * Custom runner to disable dependency injection.
 */
class SgMobileDataTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, TestApp::class.java.name, context)
    }
}
