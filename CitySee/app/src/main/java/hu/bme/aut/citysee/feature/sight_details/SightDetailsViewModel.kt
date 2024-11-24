package hu.bme.aut.citysee.feature.sight_details

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import hu.bme.aut.citysee.CitySeeApplication
import hu.bme.aut.citysee.data.sights.SightService
import hu.bme.aut.citysee.feature.sight_create.CreateSightEvent
import hu.bme.aut.citysee.ui.model.SightUi
import hu.bme.aut.citysee.ui.model.TypeUi
import hu.bme.aut.citysee.ui.model.asSight
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

class SightDetailsViewModel  constructor(
    private val savedState: SavedStateHandle,
    private val sightService: SightService
) : ViewModel() {

    private val _state = MutableStateFlow(CheckSightState())
    val state: StateFlow<CheckSightState> = _state

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: CheckSightEvent) {
        when(event) {
            CheckSightEvent.EditingSight -> {
                _state.update { it.copy(
                    isEditingSight = true
                ) }
            }
            CheckSightEvent.StopEditingSight -> {
                _state.update { it.copy(
                    isEditingSight = false
                ) }
            }
            is CheckSightEvent.ChangeName -> {
                val newValue = event.text
                _state.update { it.copy(
                    sight = it.sight?.copy(name = newValue)
                ) }
            }
            is CheckSightEvent.ChangeAddress -> {
                val newValue = event.text
                _state.update { it.copy(
                    sight = it.sight?.copy(address = newValue)
                ) }
            }
            is CheckSightEvent.ChangeBonusInfo -> {
                val newValue = event.text
                _state.update { it.copy(
                    sight = it.sight?.copy(bonusInfo = newValue)
                ) }
            }
            is CheckSightEvent.ChangeDescription -> {
                val newValue = event.text
                _state.update { it.copy(
                    sight = it.sight?.copy(description = newValue)
                ) }
            }
            is CheckSightEvent.SelectType -> {
                val newValue = event.type
                _state.update { it.copy(
                    sight = it.sight?.copy(type = newValue)
                ) }
            }
            is CheckSightEvent.UpdateCoordinates -> {
                val newLatitude = event.latitude
                val newLongitude = event.longitude
                _state.update { it.copy(
                    sight = it.sight?.copy(latitude = newLatitude, longitude = newLongitude)
                ) }
            }
            // TODO: Ez később kiszedhető, de most debugra szerintem gyorsabb megoldás lenne, benne hagynám egy időre
            CheckSightEvent.DeleteSight -> {
                onDelete()
            }
            CheckSightEvent.UpdateSight -> {
                onUpdate()
            }
        }
    }

    init {
        load()
    }

    fun uploadSightPhoto(uri: Uri?, onComplete: (Boolean) -> Unit) {
        val currentSight = state.value.sight
        if (currentSight != null && uri != null) { // Ensure sight and URI are not null
            viewModelScope.launch {
                sightService.uploadSightPhoto(currentSight,false, uri) { success ->
                    if (success) {
                        val updatedSight = currentSight.copy(
                            photos = currentSight.photos + uri.toString() // Add the new photo to the list
                        )
                        _state.update { it.copy(sight = updatedSight) } // Update sight in state
                    }
                    onComplete(success)
                }
            }
        } else {
            onComplete(false) // Handle the case where sight or URI is null
        }
    }

    private fun load() {
        val sightId = checkNotNull<String>(savedState["id"])
        viewModelScope.launch {
            _state.update { it.copy(isLoadingSight = true) }
            try {
                CoroutineScope(coroutineContext).launch(Dispatchers.IO) {
                    val sight = sightService.getSight(sightId)!!.asSightUi()
                    _state.update { it.copy(isLoadingSight = false, sight = sight) }
                }
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Failure(e.toUiText()))
            }
        }
    }

    private fun onUpdate() {
        viewModelScope.launch {
            try {
                CoroutineScope(coroutineContext).launch(Dispatchers.IO) {
                    sightService.updateSight(state.value.sight!!.asSight())
                }
                _uiEvent.send(UiEvent.Success)
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Failure(e.toUiText()))
            }
        }
    }

    private fun onDelete() {
        viewModelScope.launch {
            try {
                CoroutineScope(coroutineContext).launch(Dispatchers.IO) {
                    sightService.deleteSight(state.value.sight!!.id)
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
                val savedState = createSavedStateHandle()
                SightDetailsViewModel(
                    sightService = sightService,
                    savedState = savedState
                )
            }
        }
    }
}

data class CheckSightState(
    var sight: SightUi? = null,
    val isLoadingSight: Boolean = false,
    val isEditingSight: Boolean = false,
    val error: Throwable? = null,
)

sealed class CheckSightEvent {
    object EditingSight: CheckSightEvent()
    object StopEditingSight: CheckSightEvent()
    data class ChangeName(val text: String): CheckSightEvent()
    data class ChangeAddress(val text: String): CheckSightEvent()
    data class ChangeBonusInfo(val text: String): CheckSightEvent()
    data class ChangeDescription(val text: String): CheckSightEvent()
    data class SelectType(val type: TypeUi): CheckSightEvent()
    data class UpdateCoordinates(val latitude: Double, val longitude: Double) : CheckSightEvent() // New event
    object DeleteSight: CheckSightEvent()
    object UpdateSight: CheckSightEvent()
}