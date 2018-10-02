package ytpconnect.rocket.android.main.presentation

import android.content.Context
import ytpconnect.rocket.android.core.lifecycle.CancelStrategy
import ytpconnect.rocket.android.db.DatabaseManagerFactory
import ytpconnect.rocket.android.emoji.Emoji
import ytpconnect.rocket.android.emoji.EmojiRepository
import ytpconnect.rocket.android.emoji.Fitzpatrick
import ytpconnect.rocket.android.emoji.internal.EmojiCategory
import ytpconnect.rocket.android.infrastructure.LocalRepository
import ytpconnect.rocket.android.main.uimodel.NavHeaderUiModel
import ytpconnect.rocket.android.main.uimodel.NavHeaderUiModelMapper
import ytpconnect.rocket.android.push.GroupedPush
import ytpconnect.rocket.android.server.domain.GetAccountsInteractor
import ytpconnect.rocket.android.server.domain.GetCurrentServerInteractor
import ytpconnect.rocket.android.server.domain.GetSettingsInteractor
import ytpconnect.rocket.android.server.domain.PublicSettings
import ytpconnect.rocket.android.server.domain.RefreshSettingsInteractor
import ytpconnect.rocket.android.server.domain.RefreshPermissionsInteractor
import ytpconnect.rocket.android.server.domain.RemoveAccountInteractor
import ytpconnect.rocket.android.server.domain.SaveAccountInteractor
import ytpconnect.rocket.android.server.domain.TokenRepository
import ytpconnect.rocket.android.server.domain.favicon
import ytpconnect.rocket.android.server.domain.model.Account
import ytpconnect.rocket.android.server.infraestructure.ConnectionManagerFactory
import ytpconnect.rocket.android.server.infraestructure.RocketChatClientFactory
import ytpconnect.rocket.android.server.presentation.CheckServerPresenter
import ytpconnect.rocket.android.util.extension.launchUI
import ytpconnect.rocket.android.util.extensions.adminPanelUrl
import ytpconnect.rocket.android.util.extensions.serverLogoUrl
import ytpconnect.rocket.android.util.retryIO
import ytpconnect.rocket.common.RocketChatAuthException
import ytpconnect.rocket.common.RocketChatException
import ytpconnect.rocket.common.model.UserStatus
import ytpconnect.rocket.common.util.ifNull
import ytpconnect.rocket.core.RocketChatClient
import ytpconnect.rocket.core.internal.rest.getCustomEmojis
import ytpconnect.rocket.core.internal.rest.logout
import ytpconnect.rocket.core.internal.rest.me
import ytpconnect.rocket.core.internal.rest.unregisterPushToken
import ytpconnect.rocket.core.model.Myself
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.withContext
import timber.log.Timber
import javax.inject.Inject

class MainPresenter @Inject constructor(
    private val view: MainView,
    private val strategy: CancelStrategy,
    private val navigator: MainNavigator,
    private val tokenRepository: TokenRepository,
    private val serverInteractor: GetCurrentServerInteractor,
    private val refreshSettingsInteractor: RefreshSettingsInteractor,
    private val refreshPermissionsInteractor: RefreshPermissionsInteractor,
    private val localRepository: LocalRepository,
    private val navHeaderMapper: NavHeaderUiModelMapper,
    private val saveAccountInteractor: SaveAccountInteractor,
    private val getAccountsInteractor: GetAccountsInteractor,
    private val removeAccountInteractor: RemoveAccountInteractor,
    factory: RocketChatClientFactory,
    private val groupedPush: GroupedPush,
    dbManagerFactory: DatabaseManagerFactory,
    getSettingsInteractor: GetSettingsInteractor,
    managerFactory: ConnectionManagerFactory
) : CheckServerPresenter(strategy, factory, view = view) {
    private val currentServer = serverInteractor.get()!!
    private val manager = managerFactory.create(currentServer)
    private val dbManager = dbManagerFactory.create(currentServer)
    private val client: RocketChatClient = factory.create(currentServer)
    private var settings: PublicSettings = getSettingsInteractor.get(serverInteractor.get()!!)
    private val userDataChannel = Channel<Myself>()

    fun toChatList(chatRoomId: String? = null) = navigator.toChatList(chatRoomId)

    fun toUserProfile() = navigator.toUserProfile()

    fun toSettings() = navigator.toSettings()

    fun toAdminPanel() = tokenRepository.get(currentServer)?.let {
        navigator.toAdminPanel(currentServer.adminPanelUrl(), it.authToken)
    }

    fun toCreateChannel() = navigator.toCreateChannel()

    fun loadServerAccounts() {
        launchUI(strategy) {
            try {
                view.setupServerAccountList(getAccountsInteractor.get())
            } catch (ex: Exception) {
                when (ex) {
                    is RocketChatAuthException -> logout()
                    else -> {
                        Timber.d(ex, "Error loading serve accounts")
                        ex.message?.let {
                            view.showMessage(it)
                        }.ifNull {
                            view.showGenericErrorMessage()
                        }
                    }
                }
            }
        }
    }

    fun loadCurrentInfo() {
        checkServerInfo(currentServer)
        launchUI(strategy) {
            try {
                val me = retryIO("me") { client.me() }
                val model = navHeaderMapper.mapToUiModel(me)
                saveAccount(model)
                view.setupUserAccountInfo(model)
            } catch (ex: Exception) {
                when (ex) {
                    is RocketChatAuthException -> {
                        logout()
                    }
                    else -> {
                        Timber.d(ex, "Error loading my information for navheader")
                        ex.message?.let {
                            view.showMessage(it)
                        }.ifNull {
                            view.showGenericErrorMessage()
                        }
                    }
                }
            }
            subscribeMyselfUpdates()
        }
    }

    /**
     * Load all emojis for the current server. Simple emojis are always the same for every server,
     * but custom emojis vary according to the its url.
     */
    fun loadEmojis() {
        launchUI(strategy) {
            EmojiRepository.setCurrentServerUrl(currentServer)
            val customEmojiList = mutableListOf<Emoji>()
            try {
                for (customEmoji in retryIO("getCustomEmojis()") { client.getCustomEmojis() }) {
                    customEmojiList.add(Emoji(
                        shortname = ":${customEmoji.name}:",
                        category = EmojiCategory.CUSTOM.name,
                        url = "$currentServer/emoji-custom/${customEmoji.name}.${customEmoji.extension}",
                        count = 0,
                        fitzpatrick = Fitzpatrick.Default.type,
                        keywords = customEmoji.aliases,
                        shortnameAlternates = customEmoji.aliases,
                        siblings = mutableListOf(),
                        unicode = "",
                        isDefault = true
                    ))
                }

                EmojiRepository.load(view as Context, customEmojis = customEmojiList)
            } catch (ex: RocketChatException) {
                Timber.e(ex)
                EmojiRepository.load(view as Context)
            }
        }
    }

    /**
     * Logout from current server.
     */
    fun logout() {
        launchUI(strategy) {
            view.showProgress()
            try {
                clearTokens()
                retryIO("logout") { client.logout() }
            } catch (exception: RocketChatException) {
                Timber.d(exception, "Error calling logout")
                exception.message?.let {
                    view.showMessage(it)
                }.ifNull {
                    view.showGenericErrorMessage()
                }
            }

            try {
                disconnect()
                removeAccountInteractor.remove(currentServer)
                tokenRepository.remove(currentServer)

                withContext(CommonPool) { dbManager.logout() }
                navigator.switchOrAddNewServer()
            } catch (ex: Exception) {
                Timber.d(ex, "Error cleaning up the session...")
            }
            view.hideProgress()
        }
    }

    fun connect() {
        refreshSettingsInteractor.refreshAsync(currentServer)
        refreshPermissionsInteractor.refreshAsync(currentServer)
        manager.connect()
    }

    fun disconnect() {
        manager.removeUserDataChannel(userDataChannel)
        manager.disconnect()
    }

    fun changeServer(serverUrl: String) {
        if (currentServer != serverUrl) {
            navigator.switchOrAddNewServer(serverUrl)
        } else {
            view.closeServerSelection()
        }
    }

    fun addNewServer() {
        navigator.toServerScreen()
    }

    fun changeDefaultStatus(userStatus: UserStatus) {
        launchUI(strategy) {
            try {
                manager.setDefaultStatus(userStatus)
                view.showUserStatus(userStatus)
            } catch (ex: RocketChatException) {
                ex.message?.let {
                    view.showMessage(it)
                }.ifNull {
                    view.showGenericErrorMessage()
                }
            }
        }
    }

    private suspend fun saveAccount(uiModel: NavHeaderUiModel) {
        val icon = settings.favicon()?.let {
            currentServer.serverLogoUrl(it)
        }
        val account = Account(
            currentServer,
            icon,
            uiModel.serverLogo,
            uiModel.userDisplayName!!,
            uiModel.userAvatar
        )
        saveAccountInteractor.save(account)
    }

    private suspend fun clearTokens() {
        serverInteractor.clear()
        val pushToken = localRepository.get(LocalRepository.KEY_PUSH_TOKEN)
        if (pushToken != null) {
            try {
                retryIO("unregisterPushToken") { client.unregisterPushToken(pushToken) }
                view.invalidateToken(pushToken)
            } catch (ex: Exception) {
                Timber.d(ex, "Error unregistering push token")
            }
        }
        localRepository.clearAllFromServer(currentServer)
    }

    private suspend fun subscribeMyselfUpdates() {
        manager.addUserDataChannel(userDataChannel)
        for (myself in userDataChannel) {
            updateMyself(myself)
        }
    }

    private fun updateMyself(myself: Myself) =
        view.setupUserAccountInfo(navHeaderMapper.mapToUiModel(myself))

    fun clearNotificationsForChatroom(chatRoomId: String?) {
        if (chatRoomId == null) return

        groupedPush.hostToPushMessageList[currentServer]?.let { list ->
            list.removeAll { it.info.roomId == chatRoomId }
        }
    }
}
