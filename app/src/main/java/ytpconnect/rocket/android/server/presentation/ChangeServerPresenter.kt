package ytpconnect.rocket.android.server.presentation

import ytpconnect.rocket.android.analytics.AnalyticsManager
import ytpconnect.rocket.android.core.lifecycle.CancelStrategy
import ytpconnect.rocket.android.infrastructure.LocalRepository
import ytpconnect.rocket.android.server.domain.GetAccountInteractor
import ytpconnect.rocket.android.server.domain.GetAccountsInteractor
import ytpconnect.rocket.android.server.domain.GetCurrentServerInteractor
import ytpconnect.rocket.android.server.domain.SaveCurrentServerInteractor
import ytpconnect.rocket.android.server.domain.SettingsRepository
import ytpconnect.rocket.android.server.domain.TokenRepository
import ytpconnect.rocket.android.server.infraestructure.ConnectionManagerFactory
import ytpconnect.rocket.android.util.extension.launchUI
import ytpconnect.rocket.common.util.ifNull
import javax.inject.Inject

class ChangeServerPresenter @Inject constructor(
    private val view: ChangeServerView,
    private val navigator: ChangeServerNavigator,
    private val strategy: CancelStrategy,
    private val saveCurrentServerInteractor: SaveCurrentServerInteractor,
    private val getCurrentServerInteractor: GetCurrentServerInteractor,
    private val getAccountInteractor: GetAccountInteractor,
    private val getAccountsInteractor: GetAccountsInteractor,
    private val analyticsManager: AnalyticsManager,
    private val settingsRepository: SettingsRepository,
    private val tokenRepository: TokenRepository,
    private val localRepository: LocalRepository,
    private val connectionManager: ConnectionManagerFactory
) {

    fun loadServer(newUrl: String?, chatRoomId: String? = null) {
        launchUI(strategy) {
            view.showProgress()
            var url = newUrl
            val accounts = getAccountsInteractor.get()
            if (url == null) {
                // Try to load next server on the list...
                url = accounts.firstOrNull()?.serverUrl
            }

            url?.let { serverUrl ->
                val token = tokenRepository.get(serverUrl)
                if (token == null) {
                    view.showInvalidCredentials()
                    view.hideProgress()
                    navigator.toServerScreen()
                    return@launchUI
                }

                val settings = settingsRepository.get(serverUrl)
                if (settings == null) {
                    // TODO - reload settings...
                }

                // Call disconnect on the old url if any...
                getCurrentServerInteractor.get()?.let { url ->
                    connectionManager.get(url)?.disconnect()
                }

                // Save the current username.
                getAccountInteractor.get(serverUrl)?.let { account ->
                    localRepository.save(LocalRepository.CURRENT_USERNAME_KEY, account.userName)
                }

                saveCurrentServerInteractor.save(serverUrl)
                view.hideProgress()
                analyticsManager.logServerSwitch()
                navigator.toChatRooms(chatRoomId)
            }.ifNull {
                view.hideProgress()
                navigator.toServerScreen()
            }
        }
    }
}