package ytpconnect.rocket.android.createchannel.presentation

import ytpconnect.rocket.android.core.lifecycle.CancelStrategy
import ytpconnect.rocket.android.main.presentation.MainNavigator
import ytpconnect.rocket.android.members.uimodel.MemberUiModelMapper
import ytpconnect.rocket.android.server.domain.GetCurrentServerInteractor
import ytpconnect.rocket.android.server.infraestructure.RocketChatClientFactory
import ytpconnect.rocket.android.util.extension.launchUI
import ytpconnect.rocket.common.RocketChatException
import ytpconnect.rocket.common.model.RoomType
import ytpconnect.rocket.common.util.ifNull
import ytpconnect.rocket.core.RocketChatClient
import ytpconnect.rocket.core.internal.rest.createChannel
import ytpconnect.rocket.core.internal.rest.spotlight
import javax.inject.Inject

class CreateChannelPresenter @Inject constructor(
    private val view: CreateChannelView,
    private val strategy: CancelStrategy,
    private val mapper: MemberUiModelMapper,
    private val navigator: MainNavigator,
    val serverInteractor: GetCurrentServerInteractor,
    val factory: RocketChatClientFactory
) {
    private val client: RocketChatClient = factory.create(serverInteractor.get()!!)

    fun createChannel(
        roomType: RoomType,
        channelName: String,
        usersList: List<String>,
        readOnly: Boolean
    ) {
        launchUI(strategy) {
            view.showLoading()
            view.disableUserInput()
            try {
                client.createChannel(roomType, channelName, usersList, readOnly)
                view.prepareToShowChatList()
                view.showChannelCreatedSuccessfullyMessage()
                toChatList()
            } catch (exception: RocketChatException) {
                exception.message?.let {
                    view.showMessage(it)
                }.ifNull {
                    view.showGenericErrorMessage()
                }
            } finally {
                view.hideLoading()
                view.enableUserInput()
            }
        }
    }

    fun searchUser(query: String) {
        launchUI(strategy) {
            view.showSuggestionViewInProgress()
            try {
                val users = client.spotlight(query).users
                if (users.isEmpty()) {
                    view.showNoUserSuggestion()
                } else {
                    view.showUserSuggestion(mapper.mapToUiModelList(users))
                }
            } catch (ex: RocketChatException) {
                ex.message?.let {
                    view.showMessage(it)
                }.ifNull {
                    view.showGenericErrorMessage()
                }
            } finally {
                view.hideSuggestionViewInProgress()
            }
        }
    }

    fun toChatList() = navigator.toChatList()
}