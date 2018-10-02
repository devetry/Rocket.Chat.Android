package ytpconnect.rocket.android.chatroom.adapter

import android.content.Intent
import android.view.View
import androidx.core.net.toUri
import ytpconnect.rocket.android.chatroom.uimodel.GenericFileAttachmentUiModel
import ytpconnect.rocket.android.emoji.EmojiReactionListener
import ytpconnect.rocket.android.util.extensions.content
import kotlinx.android.synthetic.main.item_file_attachment.view.*

class GenericFileAttachmentViewHolder(itemView: View,
                                      listener: ActionsListener,
                                      reactionListener: EmojiReactionListener? = null)
    : BaseViewHolder<GenericFileAttachmentUiModel>(itemView, listener, reactionListener) {

    init {
        with(itemView) {
            setupActionMenu(file_attachment_container)
        }
    }

    override fun bindViews(data: GenericFileAttachmentUiModel) {
        with(itemView) {
            text_file_name.content = data.attachmentTitle

            text_file_name.setOnClickListener {
                it.context.startActivity(Intent(Intent.ACTION_VIEW, data.attachmentUrl.toUri()))
            }
        }
    }
}