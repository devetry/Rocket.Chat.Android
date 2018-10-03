package ytpconnect.rocket.android.app

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import ytpconnect.rocket.android.server.domain.GetCurrentServerInteractor
import ytpconnect.rocket.android.server.infraestructure.ConnectionManagerFactory
import ytpconnect.rocket.common.RocketChatException
import ytpconnect.rocket.common.model.UserStatus
import ytpconnect.rocket.core.internal.realtime.setTemporaryStatus
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class AppLifecycleObserver @Inject constructor(
    private val serverInteractor: GetCurrentServerInteractor,
    private val factory: ConnectionManagerFactory
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onEnterForeground() {
        changeTemporaryStatus(UserStatus.Online())
        serverInteractor.get()?.let { currentServer ->
            factory.create(currentServer).resetReconnectionTimer()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onEnterBackground() {
        changeTemporaryStatus(UserStatus.Away())
    }

    private fun changeTemporaryStatus(userStatus: UserStatus) {
        launch {
            serverInteractor.get()?.let { currentServer ->
                factory.create(currentServer).setTemporaryStatus(userStatus)
            }
        }
    }
}