package ytpconnect.rocket.android.chatrooms.adapter.model

import ytpconnect.rocket.common.model.RoomType
import ytpconnect.rocket.common.model.UserStatus

data class RoomUiModel(
    val id: String,
    val type: RoomType,
    val name: CharSequence,
    val avatar: String,
    val open: Boolean = false,
    val date: CharSequence? = null,
    val unread: String? = null,
    val alert: Boolean = false,
    val lastMessage: CharSequence? = null,
    val status: UserStatus? = null,
    val username: String? = null
)