package com.github.antonkonyshev.stepic.di

import com.github.antonkonyshev.stepic.data.network.StepicApi
import com.github.antonkonyshev.stepic.data.network.StepicHeaderInterceptor
import com.github.antonkonyshev.stepic.data.repository.AuthorRepositoryImpl
import com.github.antonkonyshev.stepic.data.repository.CourseRepositoryImpl
import com.github.antonkonyshev.stepic.domain.repository.AuthorRepository
import com.github.antonkonyshev.stepic.domain.repository.CourseRepository
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.java.net.cookiejar.JavaNetCookieJar
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy

val networkModule = module {
    single<StepicApi> {
        Retrofit.Builder()
            .baseUrl(StepicApi.BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create()
                )
            ).client(
                OkHttpClient.Builder()
                    .cookieJar(JavaNetCookieJar(CookieManager().apply {
                        setCookiePolicy(CookiePolicy.ACCEPT_ALL)
                    }))
                    .addNetworkInterceptor(StepicHeaderInterceptor())
                    .addNetworkInterceptor(HttpLoggingInterceptor().apply {
                        setLevel(HttpLoggingInterceptor.Level.HEADERS)
                    })
                    .build()
            ).build()
            .create(StepicApi::class.java)
    }

    single<CourseRepository> {
        CourseRepositoryImpl()
    }

    single<AuthorRepository> {
        AuthorRepositoryImpl()
    }

    single<AuthorRepository> {
        AuthorRepositoryImpl()
    }
}