package ytpconnect.rocket.android.pinnedmessages.presentation

import ytpconnect.rocket.android.chatroom.uimodel.BaseUiModel
import ytpconnect.rocket.android.core.behaviours.LoadingView
import ytpconnect.rocket.android.core.behaviours.MessageView

interface PinnedMessagesView : MessageView, LoadingView {

    /**
     * Show list of pinned messages for the current room.
     *
     * @param pinnedMessages The list of pinned messages.
     */
    fun showPinnedMessages(pinnedMessages: List<BaseUiModel<*>>)
}