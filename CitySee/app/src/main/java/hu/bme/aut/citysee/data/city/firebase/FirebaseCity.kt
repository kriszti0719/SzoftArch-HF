package hu.bme.aut.citysee.data.city.firebase

import hu.bme.aut.citysee.data.sights.firebase.FirebaseSightService
import com.google.firebase.firestore.DocumentId
import hu.bme.aut.citysee.domain.model.City
import hu.bme.aut.citysee.domain.model.Sight

data class FirebaseCity(
    @DocumentId val id: String = "",
    val name: String = "",
    val sights: List<String>
)

fun FirebaseCity.asCity(id: String, name: String, sights: List<Sight>) :City{
    return City(id, name, sights)
}

fun City.asFirebaseCity(id: String, name: String, sights: List<Sight>) : FirebaseCity{
    val sightIds : List<String> = emptyList()
    for (sight in sights){
        sightIds.plus(sight.id)
    }
    return FirebaseCity(id, name, sightIds)
}