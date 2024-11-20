package hu.bme.aut.citysee

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hu.bme.aut.citysee.data.auth.AuthService
import hu.bme.aut.citysee.data.auth.FirebaseAuthService
import hu.bme.aut.citysee.data.city.CityService
import hu.bme.aut.citysee.data.city.firebase.FirebaseCity
import hu.bme.aut.citysee.data.city.firebase.FirebaseCityService
import hu.bme.aut.citysee.data.sights.SightService
import hu.bme.aut.citysee.data.sights.firebase.FirebaseSightService

class CitySeeApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        authService = FirebaseAuthService(FirebaseAuth.getInstance())
        sightService = FirebaseSightService(FirebaseFirestore.getInstance(), authService)
        cityService = FirebaseCityService(FirebaseFirestore.getInstance(),
            sightService as FirebaseSightService
        )
    }

    companion object{
        lateinit var authService: AuthService
        lateinit var sightService: SightService
        lateinit var cityService: CityService
    }
}