package hu.bme.aut.citysee.data.sights.firebase

import android.net.Uri
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.storage.FirebaseStorage
import hu.bme.aut.citysee.data.auth.AuthService
import hu.bme.aut.citysee.data.sights.SightService
import hu.bme.aut.citysee.domain.model.Sight
import hu.bme.aut.citysee.ui.model.SightUi
import hu.bme.aut.citysee.ui.model.asSightUi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import kotlin.text.get

class FirebaseSightService(
    private val firestore: FirebaseFirestore,
    private val authService: AuthService
) : SightService {

    // Switch to global sights collection that is user independent
    override val sights: Flow<List<Sight>> = flow {
        val snapshot = firestore.collection(GLOBAL_SIGHT_COLLECTION).get().await()  // Fetch data once from the global "sights" collection
        val sightsList = snapshot.toObjects<FirebaseSight>().map { it.asSight() }
        emit(sightsList)  // Emit the list of sights
    }

/*    override val sights: Flow<List<Sight>> = authService.currentUser.flatMapLatest { user ->
        if (user == null) flow { emit(emptyList()) }
        else currentCollection(user.id)
            .snapshots()
            .map { snapshot ->
                snapshot
                    .toObjects<FirebaseSight>()
                    .map {
                        it.asSight()
                    }
            }
    }*/

//    override val sights: Flow<List<Sight>> = authService.currentUser.flatMapLatest { user ->
//        if (user == null) flow { emit(emptyList()) }
//        else callbackFlow {
//            val listener = currentCollection(user.id)
//                .addSnapshotListener { snapshot, error ->
//                    if (error != null) {
//                        close(error) // Ha hiba van, lezárjuk a flow-t
//                        return@addSnapshotListener
//                    }
//                    if (snapshot != null) {
//                        trySend(
//                            snapshot.toObjects<FirebaseSight>().map { it.asSight() }
//                        )
//                    }
//                }
//            awaitClose { listener.remove() } // Eltávolítja a listener-t, amikor nincs több gyűjtő
//        }
//    }

    override suspend fun getSight(id: String): Sight? {
        return firestore.collection(GLOBAL_SIGHT_COLLECTION)
            .document(id)
            .get()
            .await()
            .toObject<FirebaseSight>()
            ?.asSight()
    }

//    override suspend fun getSight(id: String): Sight? =
//        authService.currentUserId?.let { userId ->
//            val document = currentCollection(userId).document(id).get().await()
//            document.data?.let { data ->
//                Json.decodeFromString<FirebaseSight>(data.toString()).asSight()
//            }
//        }

    override suspend fun saveSight(sight: Sight, photos: List<String>) {
        try {
            val sightCollection = firestore.collection(GLOBAL_SIGHT_COLLECTION)

            // Add sight to the collection, Firestore will generate an ID for the document
            val documentReference = sightCollection.add(sight.asFirebaseSight()).await()

            // Get the generated document ID
            val sightId = documentReference.id

            val updatedSight = sight.copy(id = sightId)

            Log.d("FirebaseSightService", "photos are: $photos")
            // Process each local photo file from the sight's photos list
            for (photoPath in photos) {
                //val photoUri = Uri.fromFile(File(photoPath))
                val photoUri = photoPath.toUri()

                uploadSightPhoto(updatedSight.asSightUi(), false, photoUri) { success ->}
            }
        } catch (e: Exception) {
            Log.e("FirebaseSightService", "Error saving sight", e)
            throw e
        }
    }



    // Update an existing sight in the global collection
    override suspend fun updateSight(sight: Sight) {
        firestore.collection(GLOBAL_SIGHT_COLLECTION)
            .document(sight.id)
            .set(sight.asFirebaseSight())
            .await()
    }

    override suspend fun fetchTempPhotos() : List<String>{
        val storageReference = FirebaseStorage.getInstance().reference.child("sight_photos/temp")

        return try {
            // List all files in the "temp" folder
            val result = storageReference.listAll().await()

            // Map the files to their download URLs (or you can use paths, depending on your need)
            result.items.map { it.downloadUrl.await().toString() }
        } catch (e: Exception) {
            // Handle error if any
            emptyList()
        }
    }

    override suspend fun deleteSight(id: String) {
        try {
            // Reference to the sight's folder in Firebase Storage
            val sightFolderRef = FirebaseStorage.getInstance().reference.child("sight_photos/$id")

            // List all files in the sight's folder
            val files = sightFolderRef.listAll().await()

            // Delete all files in the sight's folder
            for (fileRef in files.items) {
                fileRef.delete().await()
            }

            // After deleting all files, delete the sight's document in Firestore
            firestore.collection(GLOBAL_SIGHT_COLLECTION)
                .document(id)
                .delete()
                .await()

        } catch (e: Exception) {
            // Handle errors (log them, rethrow, etc.)
            throw e
        }
    }

    override suspend fun uploadSightPhoto(sight: SightUi, sightCreate: Boolean, uri: Uri?, onComplete: (Boolean) -> Unit) {
        val email = FirebaseAuth.getInstance().currentUser?.email
        val sightDoc = FirebaseFirestore.getInstance().collection("sight")
            .document(sight.id)  // Assuming sight.id is the Firestore document ID
            .get()
            .await()

        // Convert Firestore document to Sight
        val sightFromDb = sightDoc.toObject(Sight::class.java)?.asSightUi()
        Log.d("FirebaseSightService", "Sight retrieved: $sightFromDb")

        Log.d("FirebaseSightService", "PHOTO URI: $uri")
        Log.d("FirebaseSightService", "Uploading photo for sight: ${sight.id}, sightCreate: $sightCreate")

        // Check if current user's email is valid
        if (email != null && uri != null && sightFromDb != null) {
            Log.d("FirebaseSightService", "User email: $email is valid")

            val storageReference = if (!sightCreate) {
                // If not creating a new sight, upload it as usual
                val uniqueNumber = System.currentTimeMillis()
                FirebaseStorage.getInstance().reference.child("sight_photos/${sight.id}/${uniqueNumber}.jpg")
            } else {
                // If creating a new sight, upload it to a temp folder with a unique number
                val uniqueNumber = System.currentTimeMillis()
                FirebaseStorage.getInstance().reference.child("sight_photos/temp/$uniqueNumber.jpg")
            }

            try {
                if (sightCreate) {
                    Log.d("FirebaseSightService", "Uploading photo to temp folder")

                    // Upload the file
                    storageReference.putFile(uri).await()

                    Log.d("FirebaseSightService", "Photo uploaded successfully to temp folder")
                    onComplete(true)
                } else {
                    Log.d("FirebaseSightService", "Uploading photo to permanent folder for sight ID: ${sight.id}")

                    // Upload the file to Firebase Storage
                    storageReference.putFile(uri).await()
                    Log.d("FirebaseSightService", "Photo uploaded successfully to permanent folder")

                    // Get the download URL from storage
                    val downloadUri = storageReference.downloadUrl.await()
                    Log.d("FirebaseSightService", "Download URL obtained: $downloadUri")

                    // Add the URL to the sight's photos list
                    val updatedPhotos = sightFromDb.photos.toMutableList()
                    updatedPhotos.add(downloadUri.toString())
                    Log.d("FirebaseSightService", "Updating Firestore with new photos list for sight ID: ${sightFromDb.id}")
                    Log.d("FirebaseSightService", "Updated photos list: $updatedPhotos")

                    // Update Firestore
                    updateSightPhotos(sight.id, updatedPhotos)
                    Log.d("FirebaseSightService", "Firestore updated with new photos list for sight ID: ${sight.id}")

                    // Notify success
                    onComplete(true)
                }
            } catch (e: Exception) {
                Log.e("FirebaseSightService", "Error during photo upload", e)
                onComplete(false)
            }
        } else {
            if (email == null) Log.e("FirebaseSightService", "Error: User email is null")
            if (uri == null) Log.e("FirebaseSightService", "Error: URI is null")

            onComplete(false)
        }
    }

    // Helper function to update the sight's photos list in Firestore
    private suspend fun updateSightPhotos(sightId: String, updatedPhotos: List<String>) {
        firestore.collection("sight")
            .document(sightId)
            .update("photos", updatedPhotos)
            .await()
    }

    override suspend fun getAllSights(): List<Sight> {
        return try {
            // Fetch all documents from the global sights collection
            val snapshot = firestore.collection(GLOBAL_SIGHT_COLLECTION)
                .get()
                .await()

            // Convert each document to a Sight object
            snapshot.toObjects<FirebaseSight>().map { it.asSight() }
        } catch (e: Exception) {
            Log.e("FirebaseSightService", "Error fetching all sights", e)
            emptyList() // Return an empty list if an error occurs
        }
    }

    companion object {
        private const val GLOBAL_SIGHT_COLLECTION = "sight"
    }
}