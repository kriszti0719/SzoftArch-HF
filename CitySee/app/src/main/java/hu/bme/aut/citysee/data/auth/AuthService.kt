package hu.bme.aut.citysee.data.auth

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import hu.bme.aut.citysee.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthService {
    val currentUserId: String?

    val hasUser: Boolean

    val currentUser: Flow<User?>

    suspend fun signUp(
        email: String, password: String,
    )

    suspend fun authenticate(
        email: String,
        password: String
    )

    suspend fun sendRecoveryEmail(email: String)

    suspend fun deleteAccount()

    suspend fun getCurrentUser(): User

    suspend fun updateUsername(newUsername: String)

    suspend fun uploadProfileImageToFirebase(uri: Uri, onComplete: (String?) -> Unit)

    suspend fun fetchUserProfile(onComplete: (User?) -> Unit)

    suspend fun updateUserProfile(imageUrl: String, onComplete: (Boolean) -> Unit)

    suspend fun updateProfileImage(uri: Uri?, onComplete: (Boolean) -> Unit)

    suspend fun signOut()
}