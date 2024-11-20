package hu.bme.aut.citysee

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hu.bme.aut.citysee.data.auth.AuthService
import hu.bme.aut.citysee.data.auth.FirebaseAuthService
import hu.bme.aut.citysee.data.sights.SightService
import hu.bme.aut.citysee.data.sights.firebase.FirebaseSightService

class CitySeeApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        authService = FirebaseAuthService(FirebaseAuth.getInstance())
        sightService = FirebaseSightService(FirebaseFirestore.getInstance(), authService)
    }

    companion object{
        lateinit var authService: AuthService
        lateinit var sightService: SightService
    }
}