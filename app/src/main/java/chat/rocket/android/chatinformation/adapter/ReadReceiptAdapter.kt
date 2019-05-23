package chat.rocket.android.chatinformation.adapter

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import chat.rocket.android.R
import chat.rocket.android.chatinformation.adapter.ReadReceiptAdapter.ReadReceiptViewHolder
import chat.rocket.android.chatinformation.viewmodel.ReadReceiptViewModel
import chat.rocket.android.helper.AvatarHelper
import chat.rocket.android.util.extensions.inflate
import kotlinx.android.synthetic.main.avatar.view.*
import kotlinx.android.synthetic.main.avatar_profile.*
import kotlinx.android.synthetic.main.item_read_receipt.view.*
import java.util.*

class ReadReceiptAdapter : RecyclerView.Adapter<ReadReceiptViewHolder>() {
    private val data = ArrayList<ReadReceiptViewModel>()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadReceiptViewHolder {
        return ReadReceiptViewHolder(parent.inflate(R.layout.item_read_receipt, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ReadReceiptViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun addAll(items: List<ReadReceiptViewModel>) {
        data.clear()
        data.addAll(items)
        notifyItemRangeInserted(0, items.size)
    }

    class ReadReceiptViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(readReceipt: ReadReceiptViewModel) {
            with(itemView) {
                if (readReceipt.avatar.contains("avatar/louis?format=jpeg") || readReceipt.avatar.contains("avatar/Advisor_Nada?") || readReceipt.avatar.contains("avatar/Young_Thinkers_")) {
                    image_avatar.visibility = View.VISIBLE
                    image_avatar_text_view.visibility = View.GONE
                    image_avatar.setImageURI(readReceipt.avatar)
                } else {
                    image_avatar.visibility = View.GONE
                    image_avatar_text_view.visibility = View.VISIBLE
                    image_avatar_text_view.text = readReceipt.name.substring(0, 2).toUpperCase()
                    val color = AvatarHelper().getAvatarBackground(readReceipt.name)
                    image_avatar_text_view.setBackgroundColor(resources.getColor(color))
                }
                receipt_name.text = readReceipt.name
                receipt_time.text = readReceipt.time
            }
        }
    }
}
