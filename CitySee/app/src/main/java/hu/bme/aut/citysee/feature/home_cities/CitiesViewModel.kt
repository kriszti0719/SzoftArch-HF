package hu.bme.aut.citysee.feature.home_cities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import hu.bme.aut.citysee.CitySeeApplication
import hu.bme.aut.citysee.data.auth.AuthService
import hu.bme.aut.citysee.data.city.CityService
import hu.bme.aut.citysee.data.sights.SightService
import hu.bme.aut.citysee.ui.model.CityUi
import hu.bme.aut.citysee.ui.model.SightUi
import hu.bme.aut.citysee.ui.model.asCityUi
import hu.bme.aut.citysee.ui.model.asSightUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CitiesViewModel constructor(
    private val authService: AuthService,
    private val cityservice: CityService
) : ViewModel() {

    private val _state = MutableStateFlow(CitiesState())
    val state = _state.asStateFlow()

    init {
        loadCities()
    }

    private fun loadCities() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                cityservice.cities.collect {
                    val cities = it.sortedBy { it.name }.map { it.asCityUi() }
                    _state.update { it.copy(isLoading = false, cities = cities) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e) }
            }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val authService = CitySeeApplication.authService
                val cityService = CitySeeApplication.cityService
                CitiesViewModel(
                    authService = authService,
                    cityservice = cityService
                )
            }
        }
    }
}

data class CitiesState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val isError: Boolean = error != null,
    val cities: List<CityUi> = emptyList()
)
