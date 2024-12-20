package hu.bme.aut.citysee.data.city.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import hu.bme.aut.citysee.data.city.CityService
import hu.bme.aut.citysee.data.sights.firebase.FirebaseSightService
import hu.bme.aut.citysee.domain.model.City
import hu.bme.aut.citysee.domain.model.Sight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirebaseCityService(
    private val firestore: FirebaseFirestore,
    private val sightService: FirebaseSightService,
) : CityService {
    override val cities: Flow<List<City>> = flow {
        val snapshot = firestore.collection(CITY_COLLECTION).get().await()
        val citiesList = snapshot.toObjects<FirebaseCity>()
        emit(citiesList.map {
            it.asCity(it.id, it.name, it.latitude, it.longitude, it.sights)
        })  // Emit the list of cities
    }

    override suspend fun getCity(id: String): City? {
        val firebaseCity = firestore.collection(CITY_COLLECTION).document(id).get().await().toObject<FirebaseCity>()
        return firebaseCity?.asCity(id, firebaseCity.name, firebaseCity.latitude, firebaseCity.longitude, firebaseCity.sights)
    }


    override suspend fun saveCity(city: City) {
        firestore.collection(CITY_COLLECTION).add(city.asFirebaseCity(city.id, city.name,city.latitude, city.longitude, city.sights)).await()
    }

    override suspend fun updateCity(city: City) {
        firestore.collection(CITY_COLLECTION).document(city.id).set(city.asFirebaseCity(city.id, city.name, city.latitude, city.longitude, city.sights)).await()
    }

    override suspend fun deleteCity(id: String) {
        firestore.collection(CITY_COLLECTION).document(id).delete().await()
    }
    companion object {
        private const val CITY_COLLECTION = "cities"
    }
}