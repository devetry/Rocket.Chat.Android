package ytpconnect.rocket.android.authentication.twofactor.presentation

import ytpconnect.rocket.android.analytics.AnalyticsManager
import ytpconnect.rocket.android.analytics.event.AuthenticationEvent
import ytpconnect.rocket.android.authentication.presentation.AuthenticationNavigator
import ytpconnect.rocket.android.core.lifecycle.CancelStrategy
import ytpconnect.rocket.android.infrastructure.LocalRepository
import ytpconnect.rocket.android.server.domain.GetConnectingServerInteractor
import ytpconnect.rocket.android.server.domain.GetSettingsInteractor
import ytpconnect.rocket.android.server.domain.PublicSettings
import ytpconnect.rocket.android.server.domain.SaveAccountInteractor
import ytpconnect.rocket.android.server.domain.SaveCurrentServerInteractor
import ytpconnect.rocket.android.server.domain.TokenRepository
import ytpconnect.rocket.android.server.domain.favicon
import ytpconnect.rocket.android.server.domain.model.Account
import ytpconnect.rocket.android.server.domain.wideTile
import ytpconnect.rocket.android.server.infraestructure.RocketChatClientFactory
import ytpconnect.rocket.android.util.extension.launchUI
import ytpconnect.rocket.android.util.extensions.avatarUrl
import ytpconnect.rocket.android.util.extensions.serverLogoUrl
import ytpconnect.rocket.android.util.retryIO
import ytpconnect.rocket.common.RocketChatAuthException
import ytpconnect.rocket.common.RocketChatException
import ytpconnect.rocket.common.util.ifNull
import ytpconnect.rocket.core.internal.rest.login
import ytpconnect.rocket.core.internal.rest.me
import ytpconnect.rocket.core.model.Myself
import javax.inject.Inject

class TwoFAPresenter @Inject constructor(
    private val view: TwoFAView,
    private val strategy: CancelStrategy,
    private val navigator: AuthenticationNavigator,
    private val tokenRepository: TokenRepository,
    private val localRepository: LocalRepository,
    private val serverInteractor: GetConnectingServerInteractor,
    private val saveCurrentServerInteractor: SaveCurrentServerInteractor,
    private val analyticsManager: AnalyticsManager,
    private val factory: RocketChatClientFactory,
    private val saveAccountInteractor: SaveAccountInteractor,
    settingsInteractor: GetSettingsInteractor
) {
    private val currentServer = serverInteractor.get()!!
    private var settings: PublicSettings = settingsInteractor.get(serverInteractor.get()!!)

    // TODO: If the usernameOrEmail and password was informed by the user on the previous screen, then we should pass only the pin, like this: fun authenticate(pin: EditText)
    fun authenticate(
        usernameOrEmail: String,
        password: String,
        twoFactorAuthenticationCode: String
    ) {
        val server = serverInteractor.get()
        when {
            server == null -> {
                navigator.toServerScreen()
            }
            twoFactorAuthenticationCode.isBlank() -> {
                view.alertBlankTwoFactorAuthenticationCode()
            }
            else -> {
                launchUI(strategy) {
                    val client = factory.create(server)
                    view.showLoading()
                    try {
                        // The token is saved via the client TokenProvider
                        val token = retryIO("login") {
                            client.login(usernameOrEmail, password, twoFactorAuthenticationCode)
                        }
                        val me = retryIO("me") { client.me() }
                        saveAccount(me)
                        saveCurrentServerInteractor.save(currentServer)
                        tokenRepository.save(server, token)
                        localRepository.save(LocalRepository.CURRENT_USERNAME_KEY, me.username)
                        analyticsManager.logLogin(
                            AuthenticationEvent.AuthenticationWithUserAndPassword,
                            true
                        )
                        navigator.toChatList()
                    } catch (exception: RocketChatException) {
                        if (exception is RocketChatAuthException) {
                            view.alertInvalidTwoFactorAuthenticationCode()
                        } else {
                            analyticsManager.logLogin(
                                AuthenticationEvent.AuthenticationWithUserAndPassword,
                                false
                            )
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

    fun signup() = navigator.toSignUp()

    private suspend fun saveAccount(me: Myself) {
        val icon = settings.favicon()?.let {
            currentServer.serverLogoUrl(it)
        }
        val logo = settings.wideTile()?.let {
            currentServer.serverLogoUrl(it)
        }
        val thumb = currentServer.avatarUrl(me.username!!)
        val account = Account(currentServer, icon, logo, me.username!!, thumb)
        saveAccountInteractor.save(account)
    }
}