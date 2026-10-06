package fr.elan.app

import android.app.Application
import fr.elan.app.data.ElanRepository

class ElanApplication : Application() {
    val repository: ElanRepository by lazy { ElanRepository(applicationContext) }
}
