package chat.rocket.android.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class FirebaseAnalyticsHandler {

    /**
     * Variables
     */
    lateinit var firebaseAnalytics: FirebaseAnalytics


    /**
     * Constructor
     */
    constructor(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    }

    /**
     * Logs
     */
    fun logChatAction(params: Bundle) {
        firebaseAnalytics.logEvent("chat_action", params)
    }

    fun logChannelAction(params: Bundle) {
        firebaseAnalytics.logEvent("channel_action", params)
    }


}