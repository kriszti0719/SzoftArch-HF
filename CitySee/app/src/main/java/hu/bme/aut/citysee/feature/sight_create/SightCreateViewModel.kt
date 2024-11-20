package hu.bme.aut.citysee.feature.sight_create

import android.net.Uri
import android.util.Log
import androidx.compose.ui.platform.LocalContext
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
import java.io.File
import java.io.FileOutputStream

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

    fun storeImagesDuringSightCreation(uri: Uri?, context: android.content.Context) {
        // Check if the Uri is not null
        if (uri != null) {
            val uniqueNumber = System.currentTimeMillis()

            // Ensure the temp directory exists
            val tempDir = File(context.cacheDir, "temp")
            if (!tempDir.exists()) {
                tempDir.mkdirs()  // Create the directory if it doesn't exist
            }

            // Save the image locally
            val localPath = tempDir.absolutePath + "/${uniqueNumber}.jpg"
            val localFile = File(localPath)

            try {
                // Store the file locally in the app's cache directory
                val inputStream = context.contentResolver.openInputStream(uri)
                val outputStream = FileOutputStream(localFile)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()


                var updatedPhotos = _state.value.currentPhotos + uri.toString()
                viewModelScope.launch {
                    _state.update { currentState ->
                        currentState.copy(
                            currentPhotos = updatedPhotos // Add to temp photos
                        )
                    }
/*                    _state.update { it.copy(
                        sight = it.sight.copy(
                            photos = updatedPhotos // Add the temp images to the sight's photos list
                        )
                    ) }*/
                }
                Log.d("FirebaseSightService", "Photo stored locally at: $localPath")

            } catch (e: Exception) {
                Log.e("FirebaseSightService", "Error storing photo locally", e)
            }
        } else {
            // Handle the case when uri is null
            Log.e("FirebaseSightService", "Error: URI is null")
        }
    }


    private fun onSave() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                sightService.saveSight(state.value.sight.asSight(), state.value.currentPhotos)
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
    val sight: SightUi = SightUi(),
    val currentPhotos: List<String> = listOf(),
)

sealed class CreateSightEvent {
    data class ChangeName(val text: String): CreateSightEvent()
    data class ChangeAddress(val text: String): CreateSightEvent()
    data class ChangeDescription(val text: String): CreateSightEvent()
    data class ChangeBonusInfo(val text: String): CreateSightEvent()
    data class SelectType(val type: TypeUi): CreateSightEvent()
    object SaveSight: CreateSightEvent()
}