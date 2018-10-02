package ytpconnect.rocket.android.chatrooms.presentation

import ytpconnect.rocket.android.core.behaviours.LoadingView
import ytpconnect.rocket.android.core.behaviours.MessageView

interface ChatRoomsView : LoadingView, MessageView {
    fun showLoadingRoom(name: CharSequence)

    fun hideLoadingRoom()
}