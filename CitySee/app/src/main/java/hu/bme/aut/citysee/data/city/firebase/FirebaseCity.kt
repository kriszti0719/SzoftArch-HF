package hu.bme.aut.citysee.data.city.firebase

import com.google.firebase.firestore.DocumentId
import hu.bme.aut.citysee.domain.model.City
import hu.bme.aut.citysee.domain.model.Sight

data class FirebaseCity(
    @DocumentId val id: String = "",
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val sights: List<String> = emptyList()
)

fun FirebaseCity.asCity(id: String, name: String, latitude: Double, longitude: Double, sights: List<String>) :City{
    return City(id, name, latitude, longitude, sights)
}

fun City.asFirebaseCity(id: String, name: String, latitude: Double, longitude: Double, sights: List<String>) : FirebaseCity{
    return FirebaseCity(id, name, latitude, longitude, sights)
}
