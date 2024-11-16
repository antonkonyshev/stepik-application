package com.github.antonkonyshev.stepic.di

import com.github.antonkonyshev.stepic.data.repository.CourseRepositoryImpl
import com.github.antonkonyshev.stepic.data.network.StepicApi
import com.github.antonkonyshev.stepic.data.repository.AuthorRepositoryImpl
import com.github.antonkonyshev.stepic.domain.repository.AuthorRepository
import com.github.antonkonyshev.stepic.domain.repository.CourseRepository
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

    single<AuthorRepository> {
        AuthorRepositoryImpl(get())
    }
}