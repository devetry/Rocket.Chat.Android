package ytpconnect.rocket.android.authentication.signup.presentation

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
import ytpconnect.rocket.android.server.domain.favicon
import ytpconnect.rocket.android.server.domain.model.Account
import ytpconnect.rocket.android.server.domain.wideTile
import ytpconnect.rocket.android.server.infraestructure.RocketChatClientFactory
import ytpconnect.rocket.android.util.extension.launchUI
import ytpconnect.rocket.android.util.extensions.avatarUrl
import ytpconnect.rocket.android.util.extensions.privacyPolicyUrl
import ytpconnect.rocket.android.util.extensions.serverLogoUrl
import ytpconnect.rocket.android.util.extensions.termsOfServiceUrl
import ytpconnect.rocket.android.util.retryIO
import ytpconnect.rocket.common.RocketChatException
import ytpconnect.rocket.common.util.ifNull
import ytpconnect.rocket.core.internal.rest.login
import ytpconnect.rocket.core.internal.rest.me
import ytpconnect.rocket.core.internal.rest.signup
import ytpconnect.rocket.core.model.Myself
import javax.inject.Inject

class SignupPresenter @Inject constructor(
    private val view: SignupView,
    private val strategy: CancelStrategy,
    private val navigator: AuthenticationNavigator,
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

    fun signup(name: String, username: String, password: String, email: String) {
        val server = serverInteractor.get()
        when {
            server == null -> {
                navigator.toServerScreen()
            }
            name.isBlank() -> {
                view.alertBlankName()
            }
            username.isBlank() -> {
                view.alertBlankUsername()
            }
            password.isEmpty() -> {
                view.alertEmptyPassword()
            }
            email.isBlank() -> {
                view.alertBlankEmail()
            }
            else -> {
                val client = factory.create(server)
                launchUI(strategy) {
                    view.showLoading()
                    try {
                        // TODO This function returns a user so should we save it?
                        retryIO("signup") { client.signup(email, name, username, password) }
                        // TODO This function returns a user token so should we save it?
                        retryIO("login") { client.login(username, password) }
                        val me = retryIO("me") { client.me() }
                        saveCurrentServerInteractor.save(currentServer)
                        localRepository.save(LocalRepository.CURRENT_USERNAME_KEY, me.username)
                        saveAccount(me)
                        analyticsManager.logSignUp(
                            AuthenticationEvent.AuthenticationWithUserAndPassword,
                            true
                        )
                        view.saveSmartLockCredentials(username, password)
                        navigator.toChatList()
                    } catch (exception: RocketChatException) {
                        analyticsManager.logSignUp(
                            AuthenticationEvent.AuthenticationWithUserAndPassword,
                            false
                        )
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
    }

    fun termsOfService() {
        serverInteractor.get()?.let {
            navigator.toWebPage(it.termsOfServiceUrl())
        }
    }

    fun privacyPolicy() {
        serverInteractor.get()?.let {
            navigator.toWebPage(it.privacyPolicyUrl())
        }
    }

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