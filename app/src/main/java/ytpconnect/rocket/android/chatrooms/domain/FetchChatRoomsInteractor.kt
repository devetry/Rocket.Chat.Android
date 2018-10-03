package ytpconnect.rocket.android.chatrooms.domain

import ytpconnect.rocket.android.db.DatabaseManager
import ytpconnect.rocket.android.util.retryIO
import ytpconnect.rocket.core.RocketChatClient
import ytpconnect.rocket.core.internal.rest.chatRooms
import ytpconnect.rocket.core.model.ChatRoom
import ytpconnect.rocket.core.model.userId
import timber.log.Timber

class FetchChatRoomsInteractor(
    private val client: RocketChatClient,
    private val dbManager: DatabaseManager
) {

    suspend fun refreshChatRooms() {
        val rooms = retryIO("fetch chatRooms", times = 10,
            initialDelay = 200, maxDelay = 2000) {
            client.chatRooms().update
        }

        Timber.d("Refreshing rooms: $rooms")
        dbManager.processRooms(rooms)
    }
}