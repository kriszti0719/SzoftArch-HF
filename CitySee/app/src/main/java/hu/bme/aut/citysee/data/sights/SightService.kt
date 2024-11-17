package hu.bme.aut.citysee.data.sights

import hu.bme.aut.citysee.domain.model.Sight
import kotlinx.coroutines.flow.Flow

interface SightService {
    val sights: Flow<List<Sight>>

    suspend fun getSight(id: String): Sight?

    suspend fun saveSight(sight: Sight)

    suspend fun updateSight(sight: Sight)

    suspend fun deleteSight(id: String)
}