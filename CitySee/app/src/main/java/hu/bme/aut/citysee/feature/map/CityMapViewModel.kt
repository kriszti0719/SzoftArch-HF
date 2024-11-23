package hu.bme.aut.citysee.feature.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import hu.bme.aut.citysee.CitySeeApplication
import hu.bme.aut.citysee.data.city.CityService
import hu.bme.aut.citysee.ui.model.CityUi
import hu.bme.aut.citysee.ui.model.asCityUi
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
    private val cityService: CityService
) : ViewModel() {

    private val _state = MutableStateFlow(CheckCitiesState())
    val state: StateFlow<CheckCitiesState> = _state

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadCity()
    }

    private fun loadCity() {
        val cityId = checkNotNull<String>(savedState["id"])
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                CoroutineScope(coroutineContext).launch(Dispatchers.IO) {
                    val city = cityService.getCity(cityId)!!.asCityUi()
                    _state.update { it.copy(isLoading = false, city = city) }
                }
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Failure(e.toUiText()))
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val cityService = CitySeeApplication.cityService
                val savedState = createSavedStateHandle()
                CityMapViewModel(
                    cityService = cityService,
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