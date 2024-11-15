package com.github.antonkonyshev.stepic.di

import com.github.antonkonyshev.stepic.data.CourseRepositoryImpl
import com.github.antonkonyshev.stepic.data.StepicApi
import com.github.antonkonyshev.stepic.domain.CourseRepository
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single {
        Retrofit.Builder()
            .baseUrl(StepicApi.BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create()
                )
            ).client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    }).build()
            ).build()
            .create(StepicApi::class.java)
    }

    single<CourseRepository> {
        CourseRepositoryImpl(get())
    }
}