package ytpconnect.rocket.android.analytics

import ytpconnect.rocket.android.analytics.event.AuthenticationEvent
import ytpconnect.rocket.android.analytics.event.ScreenViewEvent
import ytpconnect.rocket.android.analytics.event.SubscriptionTypeEvent
import ytpconnect.rocket.android.server.domain.AnalyticsTrackingInteractor
import ytpconnect.rocket.android.server.domain.GetAccountsInteractor
import ytpconnect.rocket.android.server.domain.GetCurrentServerInteractor
import javax.inject.Inject

class AnalyticsManager @Inject constructor(
    private val analyticsTrackingInteractor: AnalyticsTrackingInteractor,
    getCurrentServerInteractor: GetCurrentServerInteractor,
    getAccountsInteractor: GetAccountsInteractor,
    private val analytics: List<Analytics>
) {
    val serverUrl = getCurrentServerInteractor.get()
    val accounts = getAccountsInteractor.get()

    fun logLogin(
        event: AuthenticationEvent,
        loginSucceeded: Boolean
    ) {
        if (analyticsTrackingInteractor.get()) {
            analytics.forEach { it.logLogin(event, loginSucceeded) }
        }
    }

    fun logSignUp(
        event: AuthenticationEvent,
        signUpSucceeded: Boolean
    ) {
        if (analyticsTrackingInteractor.get()) {
            analytics.forEach { it.logSignUp(event, signUpSucceeded) }
        }
    }

    fun logScreenView(event: ScreenViewEvent) {
        if (analyticsTrackingInteractor.get()) {
            analytics.forEach { it.logScreenView(event) }
        }
    }

    fun logMessageSent(event: SubscriptionTypeEvent) {
        if (analyticsTrackingInteractor.get() && serverUrl != null) {
            analytics.forEach { it.logMessageSent(event, serverUrl) }
        }
    }

    fun logMediaUploaded(event: SubscriptionTypeEvent, mimeType: String) {
        if (analyticsTrackingInteractor.get()) {
            analytics.forEach { it.logMediaUploaded(event, mimeType) }
        }
    }

    fun logReaction(event: SubscriptionTypeEvent) {
        if (analyticsTrackingInteractor.get()) {
            analytics.forEach { it.logReaction(event) }
        }
    }

    fun logServerSwitch() {
        if (analyticsTrackingInteractor.get() && serverUrl != null) {
            analytics.forEach { it.logServerSwitch(serverUrl, accounts.size) }
        }
    }
}
