package com.github.antonkonyshev.stepic.di

import androidx.room.Room
import com.github.antonkonyshev.stepic.data.database.StepicDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    single<StepicDatabase> {
        Room.databaseBuilder(
            androidApplication(), StepicDatabase::class.java, "stepic"
        ).build()
    }

    single {
        get<StepicDatabase>().courseDao()
    }

    single {
        get<StepicDatabase>().authorDao()
    }
}