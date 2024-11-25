package hu.bme.aut.citysee.feature.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import hu.bme.aut.citysee.CitySeeApplication
import hu.bme.aut.citysee.data.auth.AuthService
import hu.bme.aut.citysee.domain.usecases.IsEmailValidUseCase
import hu.bme.aut.citysee.domain.usecases.PasswordsMatchUseCase
import hu.bme.aut.citysee.ui.model.UiText
import hu.bme.aut.citysee.ui.model.toUiText
import hu.bme.aut.citysee.util.UiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import hu.bme.aut.citysee.R.string as StringResources

class RegisterUserViewModel constructor(
    private val authService: AuthService,
    private val isEmailValid: IsEmailValidUseCase,
    private val passwordsMatch: PasswordsMatchUseCase
): ViewModel() {
    
    private val _state = MutableStateFlow(RegisterUserState())
    val state = _state.asStateFlow()

    private val email get() = state.value.email

    private val password get() = state.value.password

    private val confirmPassword get() = state.value.confirmPassword

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: RegisterUserEvent) {
        when(event) {
            is RegisterUserEvent.EmailChanged -> {
                val newEmail = event.email.trim()
                _state.update { it.copy(email = newEmail) }
            }
            is RegisterUserEvent.PasswordChanged -> {
                val newPassword = event.password.trim()
                _state.update { it.copy(password = newPassword) }
            }
            is RegisterUserEvent.ConfirmPasswordChanged -> {
                val newConfirmPassword = event.password.trim()
                _state.update { it.copy(confirmPassword = newConfirmPassword) }
            }
            RegisterUserEvent.PasswordVisibilityChanged -> {
                _state.update { it.copy(passwordVisibility = !state.value.passwordVisibility) }
            }
            RegisterUserEvent.ConfirmPasswordVisibilityChanged -> {
                _state.update { it.copy(confirmPasswordVisibility = !state.value.confirmPasswordVisibility) }
            }
            RegisterUserEvent.SignUp -> {
                onSignUp()
            }
        }
    }

    private fun onSignUp() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                when (isEmailValid.validate(email)) {
                    IsEmailValidUseCase.ValidationResult.EmptyEmail -> {
                        _uiEvent.send(
                            UiEvent.Failure(UiText.StringResource(StringResources.error_empty_email))
                        )
                    }
                    IsEmailValidUseCase.ValidationResult.InvalidEmail -> {
                        _uiEvent.send(
                            UiEvent.Failure(UiText.StringResource(StringResources.error_invalid_email))
                        )
                    }
                    IsEmailValidUseCase.ValidationResult.Valid -> {
                        if (password.isBlank()) {
                            _uiEvent.send(
                                UiEvent.Failure(UiText.StringResource(StringResources.error_empty_password))
                            )
                        } else {
                            authService.authenticate(email, password)
                            _uiEvent.send(UiEvent.Success)
                        }
                    }
                }
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Failure(e.toUiText()))
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val authService = CitySeeApplication.authService
                val isEmailValidUseCase = IsEmailValidUseCase()
                val passwordsMatchUseCase = PasswordsMatchUseCase()
                RegisterUserViewModel(
                    authService,
                    isEmailValidUseCase,
                    passwordsMatchUseCase
                )
            }
        }
    }
}


data class RegisterUserState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisibility: Boolean = false,
    val confirmPasswordVisibility: Boolean = false
)

sealed class RegisterUserEvent {
    data class EmailChanged(val email: String): RegisterUserEvent()
    data class PasswordChanged(val password: String): RegisterUserEvent()
    data class ConfirmPasswordChanged(val password: String): RegisterUserEvent()
    object PasswordVisibilityChanged: RegisterUserEvent()
    object ConfirmPasswordVisibilityChanged: RegisterUserEvent()
    object SignUp: RegisterUserEvent()
}

