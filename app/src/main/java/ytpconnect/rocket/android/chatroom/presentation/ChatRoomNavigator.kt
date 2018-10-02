package ytpconnect.rocket.android.chatroom.presentation

import ytpconnect.rocket.android.R
import ytpconnect.rocket.android.chatinformation.ui.messageInformationIntent
import ytpconnect.rocket.android.chatroom.ui.ChatRoomActivity
import ytpconnect.rocket.android.chatroom.ui.chatRoomIntent
import ytpconnect.rocket.android.favoritemessages.ui.TAG_FAVORITE_MESSAGES_FRAGMENT
import ytpconnect.rocket.android.files.ui.TAG_FILES_FRAGMENT
import ytpconnect.rocket.android.members.ui.TAG_MEMBERS_FRAGMENT
import ytpconnect.rocket.android.mentions.ui.TAG_MENTIONS_FRAGMENT
import ytpconnect.rocket.android.pinnedmessages.ui.TAG_PINNED_MESSAGES_FRAGMENT
import ytpconnect.rocket.android.server.ui.changeServerIntent
import ytpconnect.rocket.android.util.extensions.addFragmentBackStack

class ChatRoomNavigator(internal val activity: ChatRoomActivity) {

    fun toMembersList(chatRoomId: String) {
        activity.addFragmentBackStack(TAG_MEMBERS_FRAGMENT, R.id.fragment_container) {
            ytpconnect.rocket.android.members.ui.newInstance(chatRoomId)
        }
    }

    fun toMentions(chatRoomId: String) {
        activity.addFragmentBackStack(TAG_MENTIONS_FRAGMENT, R.id.fragment_container) {
            ytpconnect.rocket.android.mentions.ui.newInstance(chatRoomId)
        }
    }

    fun toPinnedMessageList(chatRoomId: String) {
        activity.addFragmentBackStack(TAG_PINNED_MESSAGES_FRAGMENT, R.id.fragment_container) {
            ytpconnect.rocket.android.pinnedmessages.ui.newInstance(chatRoomId)
        }
    }

    fun toFavoriteMessageList(chatRoomId: String) {
        activity.addFragmentBackStack(TAG_FAVORITE_MESSAGES_FRAGMENT, R.id.fragment_container) {
            ytpconnect.rocket.android.favoritemessages.ui.newInstance(chatRoomId)
        }
    }

    fun toFileList(chatRoomId: String) {
        activity.addFragmentBackStack(TAG_FILES_FRAGMENT, R.id.fragment_container) {
            ytpconnect.rocket.android.files.ui.newInstance(chatRoomId)
        }
    }

    fun toNewServer() {
        activity.startActivity(activity.changeServerIntent())
        activity.finish()
    }

    fun toDirectMessage(
        chatRoomId: String,
        chatRoomName: String,
        chatRoomType: String,
        isChatRoomReadOnly: Boolean,
        chatRoomLastSeen: Long,
        isChatRoomSubscribed: Boolean,
        isChatRoomCreator: Boolean,
        isChatRoomFavorite: Boolean,
        chatRoomMessage: String
    ) {
        activity.startActivity(
            activity.chatRoomIntent(
                chatRoomId,
                chatRoomName,
                chatRoomType,
                isChatRoomReadOnly,
                chatRoomLastSeen,
                isChatRoomSubscribed,
                isChatRoomCreator,
                isChatRoomFavorite,
                chatRoomMessage
            )
        )
        activity.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
    }

    fun toMessageInformation(messageId: String) {
        activity.startActivity(activity.messageInformationIntent(messageId = messageId))
        activity.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
    }
}