package hu.bme.aut.citysee.domain.model

import com.google.firebase.firestore.GeoPoint

data class Sight (
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val type: Type = Type.NONE,
    val description: String = "",
    val bonusInfo: String = "",
    val photos: List<String> = emptyList(),
)