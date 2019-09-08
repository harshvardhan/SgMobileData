package com.android.example.sgmobiledata.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.runner.AndroidJUnit4
import com.android.example.sgmobiledata.util.LiveDataTestUtil.getValue
import com.android.example.sgmobiledata.util.TestUtil
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResultDaoTest : DbTest() {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun insertAndRead() {
        val resourceData = TestUtil.createResourceData("a807b7ab-6cad-4aa6-87d0-e283a7353a0f")
        resourceData.result?.let { db.resultDao().insert(it) }
        val loaded = getValue(db.resultDao().load("a807b7ab-6cad-4aa6-87d0-e283a7353a0f"))
        assertThat(loaded, notNullValue())
        assertThat(loaded.id, `is`(1))
        assertThat(loaded.resourceId, `is`("a807b7ab-6cad-4aa6-87d0-e283a7353a0f"))
    }
}
