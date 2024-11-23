package hu.bme.aut.citysee.feature.map

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import hu.bme.aut.citysee.CitySeeApplication
import hu.bme.aut.citysee.data.city.CityService
import hu.bme.aut.citysee.data.sights.SightService
import hu.bme.aut.citysee.domain.model.Sight
import hu.bme.aut.citysee.ui.model.CityUi
import hu.bme.aut.citysee.ui.model.SightUi
import hu.bme.aut.citysee.ui.model.asCityUi
import hu.bme.aut.citysee.ui.model.asSightUi
import hu.bme.aut.citysee.ui.model.toUiText
import hu.bme.aut.citysee.util.UiEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CityMapViewModel constructor(
    private val savedState: SavedStateHandle,
    private val cityService: CityService,
    private val sightService: SightService
) : ViewModel() {

    private val _state = MutableStateFlow(CheckCitiesState())
    val state: StateFlow<CheckCitiesState> = _state

    private var sightIds = emptyList<String>()
    private var currentCity : CityUi = CityUi()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadCity()
        loadSights()
    }

    private fun loadCity() {
        val cityId = checkNotNull<String>(savedState["id"])
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val city = cityService.getCity(cityId)
                if (city != null) {
                    sightIds = city.sights
                    currentCity = city.asCityUi()
                }
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Failure(e.toUiText()))
            }
        }
    }
    private fun loadSights() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                var citySights = emptyList<SightUi>()
                for(id in sightIds){
                    sightService.getSight(id)?.let { citySights.plus(it.asSightUi()) }
                }
                currentCity.sights = citySights
                _state.update {it.copy(isLoading = false, city = currentCity) }
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Failure(e.toUiText()))
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val cityService = CitySeeApplication.cityService
                val sightService = CitySeeApplication.sightService
                val savedState = createSavedStateHandle()
                CityMapViewModel(
                    cityService = cityService,
                    sightService = sightService,
                    savedState = savedState
                )
            }
        }
    }
}

data class CheckCitiesState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val isError: Boolean = error != null,
    val city: CityUi? = null
)