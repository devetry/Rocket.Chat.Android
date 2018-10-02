package ytpconnect.rocket.android.chatroom.uimodel

import ytpconnect.rocket.android.R
import ytpconnect.rocket.core.model.Message
import ytpconnect.rocket.core.model.url.Url

data class UrlPreviewUiModel(
    override val message: Message,
    override val rawData: Url,
    override val messageId: String,
    val title: CharSequence?,
    val hostname: String,
    val description: CharSequence?,
    val thumbUrl: String?,
    override var reactions: List<ReactionUiModel>,
    override var nextDownStreamMessage: BaseUiModel<*>? = null,
    override var preview: Message? = null,
    override var isTemporary: Boolean = false,
    override var unread: Boolean? = null,
    override var menuItemsToHide: MutableList<Int> = mutableListOf(),
    override var currentDayMarkerText: String,
    override var showDayMarker: Boolean
) : BaseUiModel<Url> {
    override val viewType: Int
        get() = BaseUiModel.ViewType.URL_PREVIEW.viewType
    override val layoutId: Int
        get() = R.layout.message_url_preview
}