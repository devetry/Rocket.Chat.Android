package ytpconnect.rocket.android.authentication.server.presentation

import ytpconnect.rocket.android.core.behaviours.LoadingView
import ytpconnect.rocket.android.core.behaviours.MessageView

interface ServerView : LoadingView, MessageView, VersionCheckView {

    /**
     * Shows an invalid server URL message.
     */
    fun showInvalidServerUrlMessage()
}