package hu.bme.aut.citysee.util

import hu.bme.aut.citysee.ui.model.UiText

sealed class UiEvent {
    object Success: UiEvent()

    data class Failure(val message: UiText): UiEvent()
}

