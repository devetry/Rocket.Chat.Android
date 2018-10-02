package ytpconnect.rocket.android.chatroom.uimodel.suggestion

import ytpconnect.rocket.android.widget.autocompletion.model.SuggestionModel

class ChatRoomSuggestionUiModel(text: String,
                                val fullName: String,
                                val name: String,
                                searchList: List<String>) : SuggestionModel(text, searchList, false) {
}