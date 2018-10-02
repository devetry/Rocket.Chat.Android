package ytpconnect.rocket.android.util.extensions

import ytpconnect.rocket.android.server.domain.model.Account
import ytpconnect.rocket.android.server.infraestructure.RocketChatClientFactory
import ytpconnect.rocket.android.util.retryIO
import ytpconnect.rocket.core.RocketChatClient
import ytpconnect.rocket.core.internal.rest.registerPushToken
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import timber.log.Timber

suspend fun RocketChatClientFactory.registerPushToken(
    token: String,
    accounts: List<Account>
) {
    withContext(CommonPool) {
        accounts.forEach { account ->
            try {
                retryIO(description = "register push token: ${account.serverUrl}") {
                    create(account.serverUrl).registerPushToken(token)
                }
            } catch (ex: Exception) {
                Timber.d(ex, "Error registering Push token for ${account.serverUrl}")
                ex.printStackTrace()
            }
        }
    }
}