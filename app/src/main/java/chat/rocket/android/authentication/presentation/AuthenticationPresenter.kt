package chat.rocket.android.authentication.presentation

import chat.rocket.android.authentication.ui.YTPOAuth
import chat.rocket.android.infrastructure.LocalRepository
import chat.rocket.android.server.domain.*
import javax.inject.Inject

class AuthenticationPresenter @Inject constructor(
        private val navigator: AuthenticationNavigator,
        private val getCurrentServerInteractor: GetCurrentServerInteractor,
        private val serverInteractor: SaveCurrentServerInteractor,
        private val getAccountInteractor: GetAccountInteractor,
        private val settingsRepository: SettingsRepository,
        private val localRepository: LocalRepository,
        private val tokenRepository: TokenRepository
) {
    suspend fun loadCredentials(newServer: Boolean, callback: (authenticated: Boolean) -> Unit) {
        val currentServer = getCurrentServerInteractor.get()
        val serverToken = currentServer?.let { tokenRepository.get(currentServer) }
        val settings = currentServer?.let { settingsRepository.get(currentServer) }
        val account = currentServer?.let { getAccountInteractor.get(currentServer) }

        account?.let {
            localRepository.save(LocalRepository.CURRENT_USERNAME_KEY, account.userName)
        }

        if (newServer || currentServer == null || serverToken == null || settings == null || account?.userName == null) {
            callback(false)
        } else {
            callback(true)
            navigator.toChatList()
        }
    }

    suspend fun ytpAuth(ytpOAuth: YTPOAuth) {
        serverInteractor.save(ytpOAuth.chat_server)
        val account = getAccountInteractor.get(ytpOAuth.chat_server)

        account?.let {
            localRepository.save(LocalRepository.CURRENT_USERNAME_KEY, account.userName)
        }

        if (tokenRepository.get(ytpOAuth.chat_server) == null || account?.userName == null)
            navigator.toYTPLogin(ytpOAuth)
        else
            navigator.toChatList()

    }
}