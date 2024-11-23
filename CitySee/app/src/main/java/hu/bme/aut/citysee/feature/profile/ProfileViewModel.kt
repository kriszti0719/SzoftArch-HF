package hu.bme.aut.citysee.feature.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import hu.bme.aut.citysee.CitySeeApplication
import hu.bme.aut.citysee.data.auth.AuthService
import hu.bme.aut.citysee.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel constructor(
    private val authService: AuthService,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val user = authService.getCurrentUser()
                // Update state with user data
                _state.update {
                    it.copy(
                        isLoading = false,
                        name = user.name,
                        id = user.id,
                        email = user.email
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e) }
            }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            val user = authService.getCurrentUser()
            _state.update {
                it.copy(profileImageUrl = user.profileImageUrl)
                it.copy(points = user.points)
            }
        }
    }

    fun updateUsername(newUsername: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                // Update the username via the user service
                authService.updateUsername(newUsername)

                // Optionally update the local state or show a success message
                _state.update { it.copy(name = newUsername) }

                callback(true)
            } catch (e: Exception) {
                // Handle any errors (e.g., show error message)
                callback(false)
            }
        }
    }

    fun updateProfileImage(uri: Uri?, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            authService.updateProfileImage(uri) { success ->
                onComplete(success)
            }
        }
    }

    fun fetchUserProfile(onComplete: (User?) -> Unit) {
        viewModelScope.launch {
            authService.fetchUserProfile { user ->
                onComplete(user)
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
                ProfileViewModel(
                    authService = authService
                )
            }
        }
    }

}
data class ProfileState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val isError: Boolean = error != null,
    val email : String = "",
    val name: String = "",
    val id: String = "",
    val profileImageUrl: String? = null,
    var points: Int? = 0
)