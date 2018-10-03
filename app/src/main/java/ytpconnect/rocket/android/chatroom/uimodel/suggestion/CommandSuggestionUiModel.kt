package ytpconnect.rocket.android.chatroom.uimodel.suggestion

import ytpconnect.rocket.android.widget.autocompletion.model.SuggestionModel

class CommandSuggestionUiModel(text: String,
                               val description: String,
                               searchList: List<String>) : SuggestionModel(text, searchList)