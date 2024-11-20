package hu.bme.aut.citysee.data.sights

import android.net.Uri
import hu.bme.aut.citysee.domain.model.Sight
import hu.bme.aut.citysee.ui.model.SightUi
import kotlinx.coroutines.flow.Flow

interface SightService {
    val sights: Flow<List<Sight>>

    suspend fun getSight(id: String): Sight?

    suspend fun saveSight(sight: Sight,photos: List<String>)

    suspend fun updateSight(sight: Sight)

    suspend fun deleteSight(id: String)

    suspend fun uploadSightPhoto(sight :SightUi,sightCreate: Boolean, uri: Uri?, onComplete: (Boolean) -> Unit)

    suspend fun fetchTempPhotos() : List<String>
}