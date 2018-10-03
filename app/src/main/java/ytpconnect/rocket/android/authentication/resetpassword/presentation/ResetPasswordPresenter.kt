package ytpconnect.rocket.android.authentication.resetpassword.presentation

import ytpconnect.rocket.android.authentication.presentation.AuthenticationNavigator
import ytpconnect.rocket.android.core.lifecycle.CancelStrategy
import ytpconnect.rocket.android.server.domain.GetConnectingServerInteractor
import ytpconnect.rocket.android.server.infraestructure.RocketChatClientFactory
import ytpconnect.rocket.android.util.extensions.isEmail
import ytpconnect.rocket.android.util.extension.launchUI
import ytpconnect.rocket.android.util.retryIO
import ytpconnect.rocket.common.RocketChatException
import ytpconnect.rocket.common.RocketChatInvalidResponseException
import ytpconnect.rocket.common.util.ifNull
import ytpconnect.rocket.core.RocketChatClient
import ytpconnect.rocket.core.internal.rest.forgotPassword
import javax.inject.Inject

class ResetPasswordPresenter @Inject constructor(
    private val view: ResetPasswordView,
    private val strategy: CancelStrategy,
    private val navigator: AuthenticationNavigator,
    factory: RocketChatClientFactory,
    serverInteractor: GetConnectingServerInteractor
) {
    private val currentServer = serverInteractor.get()!!
    private val client: RocketChatClient = factory.create(currentServer)

    fun resetPassword(email: String) {
        when {
            email.isBlank() -> view.alertBlankEmail()
            !email.isEmail() -> view.alertInvalidEmail()
            else -> launchUI(strategy) {
                view.showLoading()
                try {
                    retryIO("forgotPassword(email = $email)") {
                        client.forgotPassword(email)
                    }
                    navigator.toPreviousView()
                    view.emailSent()
                } catch (exception: RocketChatException) {
                    if (exception is RocketChatInvalidResponseException) {
                        view.updateYourServerVersion()
                    } else {
                        exception.message?.let {
                            view.showMessage(it)
                        }.ifNull {
                            view.showGenericErrorMessage()
                        }
                    }
                } finally {
                    view.hideLoading()
                }
            }
        }
    }
}