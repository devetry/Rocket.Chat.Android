package ytpconnect.rocket.android.server.domain

import ytpconnect.rocket.core.model.ChatRoom
import javax.inject.Inject

class SaveChatRoomsInteractor @Inject constructor(private val repository: ChatRoomsRepository) {

    fun save(url: String, chatRooms: List<ChatRoom>) = repository.save(url, chatRooms)
}