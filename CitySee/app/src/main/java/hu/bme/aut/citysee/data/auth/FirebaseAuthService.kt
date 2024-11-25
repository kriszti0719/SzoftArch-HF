package hu.bme.aut.citysee.data.auth

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import hu.bme.aut.citysee.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class FirebaseAuthService(private val firebaseAuth: FirebaseAuth) : AuthService {
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    override val currentUserId: String? get() = firebaseAuth.currentUser?.uid
    override val hasUser: Boolean get() = firebaseAuth.currentUser != null
    override val currentUser: Flow<User?>
        get() = callbackFlow {
            this.trySend(currentUserId?.let { User(it) })
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let { User(it.uid) })
                }
            firebaseAuth.addAuthStateListener(listener)
            awaitClose { firebaseAuth.removeAuthStateListener(listener) }
        }


    override suspend fun signUp(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user
                val profileChangeRequest = UserProfileChangeRequest.Builder()
                    .setDisplayName(user?.email?.substringBefore('@'))
                    .setPhotoUri(Uri.parse("https://www.gravatar.com/avatar/00000000000000000000000000000000?d=mp&f=y"))
                    .build()
                user?.updateProfile(profileChangeRequest)
            }.await()
    }

    override suspend fun authenticate(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun sendRecoveryEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    override suspend fun deleteAccount() {
        firebaseAuth.currentUser!!.delete().await()
    }

    override suspend fun getCurrentUser(): User {
        val user = firebaseAuth.currentUser
        if (user != null) {
            return User(
                name = user.displayName ?: "",
                id = user.uid,
                email = user.email ?: "",
                profileImageUrl = FirebaseStorage.getInstance().reference.child("profile_pics/${user.email.hashCode()}.jpg")
                    .toString(),
                points = FirebaseStorage.getInstance().reference.child("points/${user.email.hashCode()}")
                    .toString().toInt()
            )
        } else {
            return User()
        }
    }

    override suspend fun updatePoints(points: Int) {
        val user = firebaseAuth.currentUser
        user?.let {
            var storageReference =
                FirebaseStorage.getInstance().reference.child("points/${user.email.hashCode()}")
            val uploadTask = storageReference.putBytes(points.toString().toByteArray())
            uploadTask.await()
        }
    }

    override suspend fun updateUsername(newUsername: String) {
        val user = firebaseAuth.currentUser

        // Check if user is signed in
        user?.let {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newUsername)
                .build()

            // update the username set in the profile page
            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Profile updated successfully
                        Log.d("ProfileUpdate", "User profile updated.")
                    } else {
                        // Handle failure (e.g., display an error message)
                        Log.e("ProfileUpdate", "Profile update failed", task.exception)
                    }
                }
        }
    }

    override suspend fun updateProfileImage(uri: Uri?, onComplete: (Boolean) -> Unit) {
        // Step 1: Get the current authenticated user's email
        val email = FirebaseAuth.getInstance().currentUser?.email

        // Check if email and uri are valid
        if (email != null && uri != null) {
            // Declare variables outside the try blocks
            val storageReference =
                FirebaseStorage.getInstance().reference.child("profile_pics/${email.hashCode()}.jpg")
            val user = FirebaseAuth.getInstance().currentUser
            val profileChangeRequest: UserProfileChangeRequest
            var downloadUrl: Uri? = null

            // Step 2: Upload the new profile image to Firebase Storage
            val uploadTask = storageReference.putFile(uri)

            // Step 3: Await the upload to complete and get the download URL
            try {
                downloadUrl = uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { exception ->
                            // Log the exception before throwing it
                            Log.e("ProfileImageUpload", "Upload failed: ${exception.message}")
                            throw exception
                        }
                    }
                    // Get the download URL after the upload is complete
                    storageReference.downloadUrl
                }.await() // This will suspend until the download URL is available
            } catch (e: Exception) {
                // Log the exception if something goes wrong
                Log.e("ProfileImageUpload", "Error getting download URL: ${e.message}", e)
                // Handle the error (you can return a failure state or call onComplete(false), etc.)
                onComplete(false)
                return
            }

            // Step 4: Update the user's profile with the new image URL
            if (downloadUrl != null && user != null) {
                profileChangeRequest = UserProfileChangeRequest.Builder()
                    .setPhotoUri(downloadUrl)  // Set the new profile image URL here
                    .build()

                // Step 5: Update the user's profile
                try {
                    user.updateProfile(profileChangeRequest)
                        ?.await() // Use await() to suspend until profile is updated
                    // Successfully updated the profile
                    onComplete(true)
                } catch (e: Exception) {
                    // Failed to update the profile
                    Log.e("ProfileImageUpload", "Error updating profile: ${e.message}", e)
                    onComplete(false)
                }
            } else {
                onComplete(false)
            }
        } else {
            // If the user is not logged in or URI is null
            Log.e("ProfileImageUpload", "Email or URI is null")
            onComplete(false)
        }
    }

    // Upload profile image to Firebase Storage and store its URL in Firestore
    override suspend fun uploadProfileImageToFirebase(uri: Uri, onComplete: (String?) -> Unit) {
        val email = FirebaseAuth.getInstance().currentUser?.email
        val storageReference =
            FirebaseStorage.getInstance().reference.child("profile_pics/${email.hashCode()}.jpg")
        val uploadTask = storageReference.putFile(uri)

        // Await the upload to complete and get the download URL
        val downloadUrl = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            // Get the download URL after the upload is complete
            storageReference.downloadUrl
        }.await()
    }

    override suspend fun updateUserProfile(imageUrl: String, onComplete: (Boolean) -> Unit) {
        val currentUserId = firebaseAuth.currentUser?.uid
        if (currentUserId != null) {
            val user = User(
                id = currentUserId,
                name = firebaseAuth.currentUser?.displayName ?: "",
                email = firebaseAuth.currentUser?.email ?: "",
                profileImageUrl = imageUrl
            )

            firestore.collection("users").document(currentUserId).set(user)
                .addOnSuccessListener {
                    Log.d("Firestore", "User profile updated successfully")
                    onComplete(true)
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error updating profile", e)
                    onComplete(false)
                }
        } else {
            onComplete(false)
        }
    }

    override suspend fun fetchUserProfile(onComplete: (User?) -> Unit) {
        val currentUserId = firebaseAuth.currentUser?.uid
        if (currentUserId != null) {
            firestore.collection("users").document(currentUserId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val user = document.toObject(User::class.java)
                        onComplete(user)
                    } else {
                        Log.w("Firestore", "No such document")
                        onComplete(null)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error fetching profile", e)
                    onComplete(null)
                }
        } else {
            onComplete(null)
        }
    }

    override suspend fun signOut() {
        Log.e("FirebaseAuthService", "Signing out...")
        firebaseAuth.signOut()
    }
}