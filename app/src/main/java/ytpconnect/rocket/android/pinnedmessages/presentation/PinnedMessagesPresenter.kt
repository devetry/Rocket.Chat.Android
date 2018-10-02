package ytpconnect.rocket.android.pinnedmessages.presentation

import ytpconnect.rocket.android.chatroom.uimodel.UiModelMapper
import ytpconnect.rocket.android.core.lifecycle.CancelStrategy
import ytpconnect.rocket.android.db.DatabaseManager
import ytpconnect.rocket.android.server.infraestructure.RocketChatClientFactory
import ytpconnect.rocket.android.util.extension.launchUI
import ytpconnect.rocket.common.RocketChatException
import ytpconnect.rocket.common.model.roomTypeOf
import ytpconnect.rocket.common.util.ifNull
import ytpconnect.rocket.core.RocketChatClient
import ytpconnect.rocket.core.internal.rest.getPinnedMessages
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class PinnedMessagesPresenter @Inject constructor(
    private val view: PinnedMessagesView,
    private val strategy: CancelStrategy,
    private val dbManager: DatabaseManager,
    @Named("currentServer") private val currentServer: String,
    private val mapper: UiModelMapper,
    val factory: RocketChatClientFactory
) {
    private val client: RocketChatClient = factory.create(currentServer)
    private var offset: Int = 0

    /**
     * Loads all pinned messages for the given room id.
     *
     * @param roomId The id of the room to get pinned messages from.
     */
    fun loadPinnedMessages(roomId: String) {
        launchUI(strategy) {
            try {
                view.showLoading()
                dbManager.getRoom(roomId)?.let {
                    val pinnedMessages =
                        client.getPinnedMessages(roomId, roomTypeOf(it.chatRoom.type), offset)
                    val messageList = mapper.map(pinnedMessages.result, asNotReversed = true)
                    view.showPinnedMessages(messageList)
                    offset += 1 * 30
                }.ifNull {
                    Timber.e("Couldn't find a room with id: $roomId at current server.")
                }
            } catch (exception: RocketChatException) {
                Timber.e(exception)
            } finally {
                view.hideLoading()
            }
        }
    }
}