package chat.rocket.android.members.adapter

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import chat.rocket.android.R
import chat.rocket.android.members.uimodel.MemberUiModel
import chat.rocket.android.util.extensions.content
import chat.rocket.android.util.extensions.inflate
import kotlinx.android.synthetic.main.avatar.view.*
import kotlinx.android.synthetic.main.avatar_profile.*
import kotlinx.android.synthetic.main.item_member.view.*
import java.util.*

class MembersAdapter(
    private val listener: (MemberUiModel) -> Unit
) : RecyclerView.Adapter<MembersAdapter.ViewHolder>() {
    private var dataSet: List<MemberUiModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersAdapter.ViewHolder =
        ViewHolder(parent.inflate(R.layout.item_member))

    override fun onBindViewHolder(holder: MembersAdapter.ViewHolder, position: Int) =
        holder.bind(dataSet[position], listener)

    override fun getItemCount(): Int = dataSet.size

    fun clearData() {
        dataSet = emptyList()
        notifyDataSetChanged()
    }

    fun prependData(dataSet: List<MemberUiModel>) {
        this.dataSet = dataSet
        notifyItemRangeInserted(0, dataSet.size)
    }

    fun appendData(dataSet: List<MemberUiModel>) {
        val previousDataSetSize = this.dataSet.size
        this.dataSet += dataSet
        notifyItemRangeInserted(previousDataSetSize, dataSet.size)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(memberUiModel: MemberUiModel, listener: (MemberUiModel) -> Unit) = with(itemView) {
            if (memberUiModel.avatarUri!!.contains("avatar/louis?format=jpeg") || memberUiModel.avatarUri.contains("avatar/Advisor_Nada?")) {
                image_avatar.visibility = View.VISIBLE
                image_avatar_text_view.visibility = View.GONE
                image_avatar.setImageURI(memberUiModel.avatarUri)
            } else {
                image_avatar.visibility = View.GONE
                image_avatar_text_view.visibility = View.VISIBLE
                image_avatar_text_view.text = memberUiModel.displayName.substring(0, 2).toUpperCase()
                val rnd = Random()
                val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                image_avatar_text_view.setBackgroundColor(color)
            }
            text_member.content = memberUiModel.displayName
            text_member.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    DrawableHelper.getUserStatusDrawable(memberUiModel.status, context), null, null, null)
            setOnClickListener { listener(memberUiModel) }
        }
    }
}
