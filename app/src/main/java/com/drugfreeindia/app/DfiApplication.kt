package com.drugfreeindia.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DfiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
