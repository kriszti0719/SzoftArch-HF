package hu.bme.aut.citysee.data.sights.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import hu.bme.aut.citysee.data.auth.AuthService
import hu.bme.aut.citysee.data.sights.SightService
import hu.bme.aut.citysee.domain.model.Sight
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseSightService(
    private val firestore: FirebaseFirestore,
    private val authService: AuthService
) : SightService {

    override val sights: Flow<List<Sight>> = authService.currentUser.flatMapLatest { user ->
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
    }

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

    override suspend fun getSight(id: String): Sight? =
        authService.currentUserId?.let {
            currentCollection(it).document(id).get().await().toObject<FirebaseSight>()?.asSight()
        }

//    override suspend fun getSight(id: String): Sight? =
//        authService.currentUserId?.let { userId ->
//            val document = currentCollection(userId).document(id).get().await()
//            document.data?.let { data ->
//                Json.decodeFromString<FirebaseSight>(data.toString()).asSight()
//            }
//        }

    override suspend fun saveSight(sight: Sight) {
        authService.currentUserId?.let {
            currentCollection(it).add(sight.asFirebaseSight()).await()
        }
    }

    override suspend fun updateSight(sight: Sight) {
        authService.currentUserId?.let {
            currentCollection(it).document(sight.id).set(sight.asFirebaseSight()).await()
        }
    }

    override suspend fun deleteSight(id: String) {
        authService.currentUserId?.let {
            currentCollection(it).document(id).delete().await()
        }
    }

    private fun currentCollection(userId: String) =
        firestore.collection(USER_COLLECTION).document(userId).collection(SIGHT_COLLECTION)

    companion object {
        private const val USER_COLLECTION = "users"
        private const val SIGHT_COLLECTION = "sights"
    }
}