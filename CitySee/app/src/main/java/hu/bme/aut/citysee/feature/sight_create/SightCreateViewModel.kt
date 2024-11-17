package hu.bme.aut.citysee.feature.sight_create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import hu.bme.aut.citysee.CitySeeApplication
import hu.bme.aut.citysee.data.sights.SightService
import hu.bme.aut.citysee.ui.model.SightUi
import hu.bme.aut.citysee.ui.model.TypeUi
import hu.bme.aut.citysee.ui.model.asSight
import hu.bme.aut.citysee.ui.model.toUiText
import hu.bme.aut.citysee.util.UiEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SightCreateViewModel constructor(
    private val sightService: SightService
) : ViewModel() {

    private val _state = MutableStateFlow(CreateSightState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: CreateSightEvent) {
        when(event) {
            is CreateSightEvent.ChangeName -> {
                val newValue = event.text
                _state.update { it.copy(
                    sight = it.sight.copy(name = newValue)
                ) }
            }
            is CreateSightEvent.ChangeAddress -> {
                val newValue = event.text
                _state.update { it.copy(
                    sight = it.sight.copy(address = newValue)
                ) }
            }
            is CreateSightEvent.ChangeDescription -> {
                val newValue = event.text
                _state.update { it.copy(
                    sight = it.sight.copy(description = newValue)
                ) }
            }
            is CreateSightEvent.ChangeBonusInfo -> {
                val newValue = event.text
                _state.update { it.copy(
                    sight = it.sight.copy(bonusInfo = newValue)
                ) }
            }
            is CreateSightEvent.SelectType -> {
                val newValue = event.type
                _state.update { it.copy(
                    sight = it.sight.copy(type = newValue)
                ) }
            }
            CreateSightEvent.SaveSight -> {
                onSave()
            }
        }
    }

    private fun onSave() {
        viewModelScope.launch {
            try {
                CoroutineScope(coroutineContext).launch(Dispatchers.IO) {
                    sightService.saveSight(state.value.sight.asSight())
                }
                _uiEvent.send(UiEvent.Success)
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Failure(e.toUiText()))
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val sightService = CitySeeApplication.sightService
                SightCreateViewModel(
                    sightService = sightService,
                )
            }
        }
    }
}

data class CreateSightState(
    val sight: SightUi = SightUi()
)

sealed class CreateSightEvent {
    data class ChangeName(val text: String): CreateSightEvent()
    data class ChangeAddress(val text: String): CreateSightEvent()
    data class ChangeDescription(val text: String): CreateSightEvent()
    data class ChangeBonusInfo(val text: String): CreateSightEvent()
    data class SelectType(val type: TypeUi): CreateSightEvent()
    object SaveSight: CreateSightEvent()
}