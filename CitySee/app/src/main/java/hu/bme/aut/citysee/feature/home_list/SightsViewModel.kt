package hu.bme.aut.citysee.feature.home_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import hu.bme.aut.citysee.CitySeeApplication
import hu.bme.aut.citysee.data.auth.AuthService
import hu.bme.aut.citysee.data.sights.SightService
import hu.bme.aut.citysee.ui.model.SightUi
import hu.bme.aut.citysee.ui.model.asSightUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SightsViewModel constructor(
    private val authService: AuthService,
    private val sightService: SightService
) : ViewModel() {

    private val _state = MutableStateFlow(SightsState())
    val state = _state.asStateFlow()

    init {
        loadSights()
    }

    private fun loadSights() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                sightService.sights.collect {
                    val sights = it.sortedBy { it.name }.map { it.asSightUi() }
                    _state.update { it.copy(isLoading = false, sights = sights) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e) }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authService.signOut()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val authService = CitySeeApplication.authService
                val sightService = CitySeeApplication.sightService
                SightsViewModel(
                    authService = authService,
                    sightService = sightService
                )
            }
        }
    }
}

data class SightsState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val isError: Boolean = error != null,
    val sights: List<SightUi> = emptyList()
)
