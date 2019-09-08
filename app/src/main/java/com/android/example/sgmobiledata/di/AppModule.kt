package com.android.example.sgmobiledata.di

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.android.example.sgmobiledata.api.SgMobileDataService
import com.android.example.sgmobiledata.db.RepoDao
import com.android.example.sgmobiledata.db.ResultDao
import com.android.example.sgmobiledata.db.SgMobileDataDB
import com.android.example.sgmobiledata.db.UserDao
import com.android.example.sgmobiledata.util.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {
    @Singleton
    @Provides
    fun provideSgMobileDataService(): SgMobileDataService {
        val httpClient = OkHttpClient.Builder()

        httpClient.addInterceptor { chain ->
            val original = chain.request()
            Log.d("AppModule", "URL :: ${original.url()}")
            chain.proceed(original)
        }

        val client = httpClient.build()

        return Retrofit.Builder()
            .baseUrl("https://data.gov.sg/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(SgMobileDataService::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): SgMobileDataDB {
        return Room
            .databaseBuilder(app, SgMobileDataDB::class.java, "sgmobiledatadb.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideUserDao(db: SgMobileDataDB): UserDao {
        return db.userDao()
    }

    @Singleton
    @Provides
    fun provideRepoDao(db: SgMobileDataDB): RepoDao {
        return db.repoDao()
    }

    @Singleton
    @Provides
    fun provideResultDao(db: SgMobileDataDB): ResultDao {
        return db.resultDao()
    }
}
