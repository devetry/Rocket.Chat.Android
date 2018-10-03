package ytpconnect.rocket.android.authentication.registerusername.presentation

import ytpconnect.rocket.android.analytics.AnalyticsManager
import ytpconnect.rocket.android.analytics.event.AuthenticationEvent
import ytpconnect.rocket.android.authentication.presentation.AuthenticationNavigator
import ytpconnect.rocket.android.core.lifecycle.CancelStrategy
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
import ytpconnect.rocket.common.RocketChatException
import ytpconnect.rocket.common.model.Token
import ytpconnect.rocket.common.util.ifNull
import ytpconnect.rocket.core.RocketChatClient
import ytpconnect.rocket.core.internal.rest.updateOwnBasicInformation
import javax.inject.Inject

class RegisterUsernamePresenter @Inject constructor(
    private val view: RegisterUsernameView,
    private val strategy: CancelStrategy,
    private val navigator: AuthenticationNavigator,
    private val tokenRepository: TokenRepository,
    factory: RocketChatClientFactory,
    private val saveAccountInteractor: SaveAccountInteractor,
    private val analyticsManager: AnalyticsManager,
    serverInteractor: GetConnectingServerInteractor,
    private val saveCurrentServer: SaveCurrentServerInteractor,
    settingsInteractor: GetSettingsInteractor
) {
    private val currentServer = serverInteractor.get()!!
    private val client: RocketChatClient = factory.create(currentServer)
    private var settings: PublicSettings = settingsInteractor.get(serverInteractor.get()!!)

    fun registerUsername(username: String, userId: String, authToken: String) {
        if (username.isBlank()) {
            view.alertBlankUsername()
        } else {
            launchUI(strategy) {
                view.showLoading()
                try {
                    val me = retryIO("updateOwnBasicInformation(username = $username)") {
                        client.updateOwnBasicInformation(username = username)
                    }
                    val registeredUsername = me.username
                    if (registeredUsername != null) {
                        saveAccount(registeredUsername)
                        saveCurrentServer.save(currentServer)
                        tokenRepository.save(currentServer, Token(userId, authToken))
                        analyticsManager.logSignUp(
                            AuthenticationEvent.AuthenticationWithOauth,
                            true
                        )
                        navigator.toChatList()
                    }
                } catch (exception: RocketChatException) {
                    analyticsManager.logSignUp(AuthenticationEvent.AuthenticationWithOauth, false)
                    exception.message?.let {
                        view.showMessage(it)
                    }.ifNull {
                        view.showGenericErrorMessage()
                    }
                } finally {
                    view.hideLoading()
                }
            }
        }
    }

    private suspend fun saveAccount(username: String) {
        val icon = settings.favicon()?.let {
            currentServer.serverLogoUrl(it)
        }
        val logo = settings.wideTile()?.let {
            currentServer.serverLogoUrl(it)
        }
        val thumb = currentServer.avatarUrl(username)
        val account = Account(currentServer, icon, logo, username, thumb)
        saveAccountInteractor.save(account)
    }
}