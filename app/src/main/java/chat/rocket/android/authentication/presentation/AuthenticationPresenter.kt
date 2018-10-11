package chat.rocket.android.authentication.presentation

import chat.rocket.android.authentication.ui.YTPOAuth
//import chat.rocket.android.infrastructure.LocalRepository
import chat.rocket.android.server.domain.*
import chat.rocket.android.infrastructure.LocalRepository
import chat.rocket.android.server.domain.GetAccountInteractor
import chat.rocket.android.server.domain.GetCurrentServerInteractor
import chat.rocket.android.server.domain.SettingsRepository
import chat.rocket.android.server.domain.TokenRepository
import chat.rocket.android.core.lifecycle.CancelStrategy
import chat.rocket.android.server.domain.GetConnectingServerInteractor
import chat.rocket.android.util.extension.launchUI
import chat.rocket.android.util.extensions.privacyPolicyUrl
import chat.rocket.android.util.extensions.termsOfServiceUrl
import javax.inject.Inject

class AuthenticationPresenter @Inject constructor(
        private val strategy: CancelStrategy,
        private val navigator: AuthenticationNavigator,
        private val getCurrentServerInteractor: GetCurrentServerInteractor,
        private val getServerInteractor: GetCurrentServerInteractor,
        private val saveServerInteractor: SaveCurrentServerInteractor,
        private val getAccountInteractor: GetAccountInteractor,
        private val settingsRepository: SettingsRepository,
        private val localRepository: LocalRepository,
        private val tokenRepository: TokenRepository
) {
    suspend fun ytpAuth(ytpOAuth: YTPOAuth) {
        saveServerInteractor.save(ytpOAuth.chat_server)
        val account = getAccountInteractor.get(ytpOAuth.chat_server)

        account?.let {
            localRepository.save(LocalRepository.CURRENT_USERNAME_KEY, account.userName)
        }

        if (tokenRepository.get(ytpOAuth.chat_server) == null || account?.userName == null)
            navigator.toYTPLogin(ytpOAuth)
        else
            navigator.toChatList()

    }

    fun loadCredentials(newServer: Boolean, callback: (isAuthenticated: Boolean) -> Unit) {
        launchUI(strategy) {
            val currentServer = getCurrentServerInteractor.get()
            val serverToken = currentServer?.let { tokenRepository.get(currentServer) }
            val settings = currentServer?.let { settingsRepository.get(currentServer) }
            val account = currentServer?.let { getAccountInteractor.get(currentServer) }

            account?.let {
                localRepository.save(LocalRepository.CURRENT_USERNAME_KEY, account.userName)
            }

            if (newServer || currentServer == null ||
                serverToken == null ||
                settings == null ||
                account?.userName == null
            ) {
                callback(false)
            } else {
                callback(true)
            }
        }
    }

    fun termsOfService(toolbarTitle: String) =
            getServerInteractor.get()?.let {
            navigator.toWebPage(it.termsOfServiceUrl(), toolbarTitle)
        }

    fun privacyPolicy(toolbarTitle: String) = getServerInteractor.get()?.let { navigator.toWebPage(it.privacyPolicyUrl(), toolbarTitle) }

    fun toChatList() = navigator.toChatList()
}