package ytpconnect.rocket.android.mentions.presentention

import ytpconnect.rocket.android.chatroom.uimodel.BaseUiModel
import ytpconnect.rocket.android.core.behaviours.LoadingView
import ytpconnect.rocket.android.core.behaviours.MessageView

interface MentionsView : MessageView, LoadingView {

    /**
     * Shows the list of mentions for the current room.
     *
     * @param mentions The list of mentions.
     */
    fun showMentions(mentions: List<BaseUiModel<*>>)
}