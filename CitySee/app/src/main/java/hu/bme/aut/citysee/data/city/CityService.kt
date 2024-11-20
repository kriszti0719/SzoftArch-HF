package hu.bme.aut.citysee.data.city

import hu.bme.aut.citysee.domain.model.City
import kotlinx.coroutines.flow.Flow

interface CityService {
    val cities: Flow<List<City>>

    suspend fun getCity(id: String): City?

    suspend fun saveCity(city: City)

    suspend fun updateCity(city: City)

    suspend fun deleteCity(id: String)
}