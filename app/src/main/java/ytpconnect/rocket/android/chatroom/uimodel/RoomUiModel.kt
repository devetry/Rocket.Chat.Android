package ytpconnect.rocket.android.chatroom.uimodel

import ytpconnect.rocket.core.model.ChatRoomRole

data class RoomUiModel(
    val roles: List<ChatRoomRole>,
    val isBroadcast: Boolean = false,
    val isRoom: Boolean = false
)