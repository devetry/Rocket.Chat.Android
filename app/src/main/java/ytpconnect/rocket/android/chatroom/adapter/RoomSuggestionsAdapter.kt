package ytpconnect.rocket.android.chatroom.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ytpconnect.rocket.android.R
import ytpconnect.rocket.android.chatroom.adapter.RoomSuggestionsAdapter.RoomSuggestionsViewHolder
import ytpconnect.rocket.android.chatroom.uimodel.suggestion.ChatRoomSuggestionUiModel
import ytpconnect.rocket.android.widget.autocompletion.model.SuggestionModel
import ytpconnect.rocket.android.widget.autocompletion.ui.BaseSuggestionViewHolder
import ytpconnect.rocket.android.widget.autocompletion.ui.SuggestionsAdapter

class RoomSuggestionsAdapter : SuggestionsAdapter<RoomSuggestionsViewHolder>("#") {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomSuggestionsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.suggestion_room_item, parent,
                false)
        return RoomSuggestionsViewHolder(view)
    }

    class RoomSuggestionsViewHolder(view: View) : BaseSuggestionViewHolder(view) {

        override fun bind(item: SuggestionModel, itemClickListener: SuggestionsAdapter.ItemClickListener?) {
            item as ChatRoomSuggestionUiModel
            with(itemView) {
                val fullname = itemView.findViewById<TextView>(R.id.text_fullname)
                val name = itemView.findViewById<TextView>(R.id.text_name)
                name.text = item.name
                fullname.text = item.fullName
                setOnClickListener {
                    itemClickListener?.onClick(item)
                }
            }
        }
    }
}