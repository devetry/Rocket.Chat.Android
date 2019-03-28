package chat.rocket.android.chatrooms.adapter

import android.app.Activity
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import chat.rocket.android.R
import chat.rocket.android.chatrooms.adapter.model.RoomUiModel
import chat.rocket.common.model.RoomType
import chat.rocket.common.model.UserStatus
import kotlinx.android.synthetic.main.item_chat.view.*
import kotlinx.android.synthetic.main.unread_messages_badge.view.*
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.Color
import android.widget.ImageView
import java.io.*
import java.net.MalformedURLException
import java.net.URL
import android.graphics.drawable.PictureDrawable
import android.opengl.Visibility
import com.bumptech.glide.Glide
import androidx.core.view.ViewCompat.animate
import chat.rocket.android.util.Utils
import kotlinx.android.synthetic.main.avatar_profile.*
import java.util.*


class RoomViewHolder(itemView: View, private val listener: (RoomUiModel) -> Unit) : ViewHolder<RoomItemHolder>(itemView) {

    private val resources: Resources = itemView.resources
    private val channelUnread: Drawable = resources.getDrawable(R.drawable.ic_hashtag_black_12dp)
    private val channel: Drawable = resources.getDrawable(R.drawable.ic_hashtag_12dp)
    private val groupUnread: Drawable = resources.getDrawable(R.drawable.ic_lock_black_12_dp)
    private val group: Drawable = resources.getDrawable(R.drawable.ic_lock_12_dp)
    private val online: Drawable = resources.getDrawable(R.drawable.ic_status_online_12dp)
    private val away: Drawable = resources.getDrawable(R.drawable.ic_status_away_12dp)
    private val busy: Drawable = resources.getDrawable(R.drawable.ic_status_busy_12dp)
    private val offline: Drawable = resources.getDrawable(R.drawable.ic_status_invisible_12dp)



    override fun bindViews(data: RoomItemHolder) {
        val room = data.data
        with(itemView) {

            if (room.avatar.contains("avatar/louis?") || room.avatar.contains("avatar/Advisor_Nada?")) {
                image_avatar.visibility = View.VISIBLE
                image_avatar_text_view.visibility = View.INVISIBLE
                image_avatar.setImageURI(room.avatar)
            } else {
                image_avatar.visibility = View.INVISIBLE
                image_avatar_text_view.visibility = View.VISIBLE
                image_avatar_text_view.text = room.name.substring(0, 2).toUpperCase()
                val rnd = Random()
                val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                image_avatar_text_view.setBackgroundColor(color)
            }

            text_chat_name.text = room.name

            text_last_message.isGone = true
//            if (room.lastMessage != null) {
//                text_last_message.isVisible = true
//                text_last_message.text = room.lastMessage
//            } else {
//                text_last_message.isGone = true
//            }

            if (room.date != null) {
                text_last_message_date_time.isVisible = true
                text_last_message_date_time.text = room.date
            } else {
                text_last_message_date_time.isGone = true
            }

            if (room.unread != null) {
                text_total_unread_messages.isVisible = true
                text_total_unread_messages.text = room.unread
            } else {
                text_total_unread_messages.isGone = true
            }

            if (room.status != null && room.type is RoomType.DirectMessage) {
                image_chat_icon.setImageDrawable(getStatusDrawable(room.status))
            } else {
                image_chat_icon.setImageDrawable(getRoomDrawable(room.type, room.alert))
            }

            setOnClickListener {
                listener(room)
            }
        }
    }

    private fun getRoomDrawable(type: RoomType, alert: Boolean): Drawable? {
        return when(type) {
            is RoomType.Channel -> if (alert) channelUnread else channel
            is RoomType.PrivateGroup -> if (alert) groupUnread else group
            else -> null
        }
    }

    private fun getStatusDrawable(status: UserStatus): Drawable {
        return when(status) {
            is UserStatus.Online -> online
            is UserStatus.Away -> away
            is UserStatus.Busy -> busy
            else -> offline
        }
    }
}