package ru.lavafrai.study.android3

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.*
import org.koin.core.context.startKoin
import org.koin.dsl.module
import ru.lavafrai.study.android3.data.local.AppDatabase
import ru.lavafrai.study.android3.data.repository.TimerRepository
import ru.lavafrai.study.android3.data.repository.TimerRepositoryImpl
import ru.lavafrai.study.android3.viewmodels.MainViewModel

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        val appModule = module {
            single { AppDatabase.getInstance(get()) }
            single { get<AppDatabase>().timerDao() }
            single<TimerRepository> { TimerRepositoryImpl(get()) }

            viewModel { MainViewModel(get()) }
        }

        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}

