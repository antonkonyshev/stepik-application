package com.github.antonkonyshev.stepic.di

import com.github.antonkonyshev.stepic.data.repository.AuthRepositoryImpl
import com.github.antonkonyshev.stepic.data.repository.AuthorRepositoryImpl
import com.github.antonkonyshev.stepic.domain.repository.AuthRepository
import org.koin.dsl.module

val authModule = module {
    single<AuthRepository> {
        AuthRepositoryImpl()
    }
}