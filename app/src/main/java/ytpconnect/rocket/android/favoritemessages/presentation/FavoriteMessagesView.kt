package ytpconnect.rocket.android.favoritemessages.presentation

import ytpconnect.rocket.android.chatroom.uimodel.BaseUiModel
import ytpconnect.rocket.android.core.behaviours.LoadingView
import ytpconnect.rocket.android.core.behaviours.MessageView

interface FavoriteMessagesView : MessageView, LoadingView {

    /**
     * Shows the list of favorite messages for the current room.
     *
     * @param favoriteMessages The list of favorite messages to show.
     */
    fun showFavoriteMessages(favoriteMessages: List<BaseUiModel<*>>)
}