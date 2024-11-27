package com.github.antonkonyshev.stepic

import android.app.Application
import com.github.antonkonyshev.stepic.di.authModule
import com.github.antonkonyshev.stepic.di.databaseModule
import com.github.antonkonyshev.stepic.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin

class StepicApplication : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidContext(this@StepicApplication)
            modules(databaseModule, networkModule, authModule)
        }
    }

    companion object {
        @Volatile
        lateinit var instance: StepicApplication
            private set
    }
}