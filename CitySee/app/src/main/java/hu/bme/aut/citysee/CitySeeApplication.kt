package hu.bme.aut.citysee

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import hu.bme.aut.citysee.data.auth.AuthService
import hu.bme.aut.citysee.data.auth.FirebaseAuthService

class CitySeeApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        authService = FirebaseAuthService(FirebaseAuth.getInstance())
    }

    companion object{
        lateinit var authService: AuthService
    }
}