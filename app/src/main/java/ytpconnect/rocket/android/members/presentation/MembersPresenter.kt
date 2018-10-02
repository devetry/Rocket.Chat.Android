package ytpconnect.rocket.android.members.presentation

import ytpconnect.rocket.android.core.lifecycle.CancelStrategy
import ytpconnect.rocket.android.db.DatabaseManager
import ytpconnect.rocket.android.members.uimodel.MemberUiModel
import ytpconnect.rocket.android.members.uimodel.MemberUiModelMapper
import ytpconnect.rocket.android.server.infraestructure.RocketChatClientFactory
import ytpconnect.rocket.android.util.extension.launchUI
import ytpconnect.rocket.common.RocketChatException
import ytpconnect.rocket.common.model.roomTypeOf
import ytpconnect.rocket.common.util.ifNull
import ytpconnect.rocket.core.RocketChatClient
import ytpconnect.rocket.core.internal.rest.getMembers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class MembersPresenter @Inject constructor(
    private val view: MembersView,
    private val navigator: MembersNavigator,
    private val dbManager: DatabaseManager,
    @Named("currentServer") private val currentServer: String,
    private val strategy: CancelStrategy,
    private val mapper: MemberUiModelMapper,
    val factory: RocketChatClientFactory
) {
    private val client: RocketChatClient = factory.create(currentServer)
    private var offset: Long = 0

    /**
     * Loads all the chat room members for the given room id.
     *
     * @param roomId The id of the room to get chat room members from.
     */
    fun loadChatRoomsMembers(roomId: String) {
        launchUI(strategy) {
            try {
                view.showLoading()
                dbManager.getRoom(roomId)?.let {
                    val members =
                        client.getMembers(roomId, roomTypeOf(it.chatRoom.type), offset, 60)
                    val memberUiModels = mapper.mapToUiModelList(members.result)
                    view.showMembers(memberUiModels, members.total)
                    offset += 1 * 60L
                }.ifNull {
                    Timber.e("Couldn't find a room with id: $roomId at current server.")
                }
            } catch (exception: RocketChatException) {
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

    fun toMemberDetails(memberUiModel: MemberUiModel) {
        navigator.toMemberDetails(
            memberUiModel.avatarUri.toString(),
            memberUiModel.realName.toString(),
            "@${memberUiModel.username}",
            memberUiModel.email ?: "",
            memberUiModel.utcOffset.toString()
        )
    }
}