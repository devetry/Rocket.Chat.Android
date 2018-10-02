package ytpconnect.rocket.android.authentication.registerusername.presentation

import ytpconnect.rocket.android.core.behaviours.LoadingView
import ytpconnect.rocket.android.core.behaviours.MessageView

interface RegisterUsernameView : LoadingView, MessageView {

    /**
     * Alerts the user about a blank username.
     */
    fun alertBlankUsername()
}