package ytpconnect.rocket.android.server.presentation

import android.content.Intent
import ytpconnect.rocket.android.authentication.ui.newServerIntent
import ytpconnect.rocket.android.main.ui.MainActivity
import ytpconnect.rocket.android.server.ui.ChangeServerActivity
import ytpconnect.rocket.android.server.ui.INTENT_CHAT_ROOM_ID

class ChangeServerNavigator (internal val activity: ChangeServerActivity) {

    fun toServerScreen() {
        activity.startActivity(activity.newServerIntent())
        activity.finish()
    }

    fun toChatRooms(chatRoomId: String? = null) {
        activity.startActivity(Intent(activity, MainActivity::class.java).also {
            it.putExtra(INTENT_CHAT_ROOM_ID, chatRoomId)
        })
        activity.finish()
    }

}