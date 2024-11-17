package hu.bme.aut.citysee.data.sights.firebase

import com.google.firebase.firestore.DocumentId
import hu.bme.aut.citysee.domain.model.Sight
import hu.bme.aut.citysee.domain.model.Type

data class FirebaseSight(
    @DocumentId val id: String = "",
    val name: String = "",
    val address: String = "",
    val type: Type = Type.NONE,
    val description: String = "",
    val bonusInfo: String = ""
)

fun FirebaseSight.asSight() = Sight(
    id = id,
    name = name,
    address = address,
    type = type,
    description = description,
    bonusInfo = bonusInfo,
)

fun Sight.asFirebaseSight() = FirebaseSight(
    id = id,
    name = name,
    address = address,
    type = type,
    description = description,
    bonusInfo = bonusInfo,
)