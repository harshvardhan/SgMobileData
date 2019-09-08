package com.android.example.sgmobiledata

import android.app.Application

/**
 * We use a separate App for tests to prevent initializing dependency injection.
 *
 * See [com.android.example.sgmobiledata.util.SgMobileDataTestRunner].
 */
class TestApp : Application()
