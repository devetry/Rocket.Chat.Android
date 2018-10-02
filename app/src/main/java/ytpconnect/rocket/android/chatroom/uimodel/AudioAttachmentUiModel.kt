package ytpconnect.rocket.android.chatroom.uimodel

import ytpconnect.rocket.android.R
import ytpconnect.rocket.core.model.Message
import ytpconnect.rocket.core.model.attachment.AudioAttachment

data class AudioAttachmentUiModel(
        override val message: Message,
        override val rawData: AudioAttachment,
        override val messageId: String,
        override val attachmentUrl: String,
        override val attachmentTitle: CharSequence,
        override val id: Long,
        override var reactions: List<ReactionUiModel>,
        override var nextDownStreamMessage: BaseUiModel<*>? = null,
        override var preview: Message? = null,
        override var isTemporary: Boolean = false,
        override var unread: Boolean? = null,
        override var menuItemsToHide: MutableList<Int> = mutableListOf(),
        override var currentDayMarkerText: String,
        override var showDayMarker: Boolean
) : BaseFileAttachmentUiModel<AudioAttachment> {
    override val viewType: Int
        get() = BaseUiModel.ViewType.AUDIO_ATTACHMENT.viewType
    override val layoutId: Int
        get() = R.layout.message_attachment
}