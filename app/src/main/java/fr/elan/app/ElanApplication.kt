package fr.elan.app

import android.app.Application
import fr.elan.app.data.ElanRepository
import fr.elan.app.platform.UpdateScheduler

class ElanApplication : Application() {
    val repository: ElanRepository by lazy { ElanRepository(applicationContext) }

    override fun onCreate() {
        super.onCreate()
        UpdateScheduler.schedule(applicationContext)
    }
}
