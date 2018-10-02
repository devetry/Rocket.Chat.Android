package ytpconnect.rocket.android.chatinformation.presentation

import ytpconnect.rocket.android.chatroom.uimodel.UiModelMapper
import ytpconnect.rocket.android.core.lifecycle.CancelStrategy
import ytpconnect.rocket.android.server.domain.GetCurrentServerInteractor
import ytpconnect.rocket.android.server.infraestructure.ConnectionManagerFactory
import ytpconnect.rocket.android.util.extension.launchUI
import ytpconnect.rocket.android.util.retryIO
import ytpconnect.rocket.common.RocketChatException
import ytpconnect.rocket.core.internal.rest.getMessageReadReceipts
import timber.log.Timber
import javax.inject.Inject

class MessageInfoPresenter @Inject constructor(
    private val view: MessageInfoView,
    private val strategy: CancelStrategy,
    private val mapper: UiModelMapper,
    serverInteractor: GetCurrentServerInteractor,
    factory: ConnectionManagerFactory
) {

    private val currentServer = serverInteractor.get()!!
    private val manager = factory.create(currentServer)
    private val client = manager.client

    fun loadReadReceipts(messageId: String) {
        launchUI(strategy) {
            try {
                view.showLoading()
                val readReceipts = retryIO(description = "getMessageReadReceipts") {
                    client.getMessageReadReceipts(messageId = messageId).result
                }
                view.showReadReceipts(mapper.map(readReceipts))
            } catch (ex: RocketChatException) {
                Timber.e(ex)
                view.showGenericErrorMessage()
            } finally {
                view.hideLoading()
            }
        }
    }
}
