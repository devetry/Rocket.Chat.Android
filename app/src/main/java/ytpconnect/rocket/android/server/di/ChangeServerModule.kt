package ytpconnect.rocket.android.server.di

import androidx.lifecycle.LifecycleOwner
import ytpconnect.rocket.android.core.lifecycle.CancelStrategy
import ytpconnect.rocket.android.dagger.scope.PerActivity
import ytpconnect.rocket.android.server.presentation.ChangeServerNavigator
import ytpconnect.rocket.android.server.presentation.ChangeServerView
import ytpconnect.rocket.android.server.ui.ChangeServerActivity
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
class ChangeServerModule {

    @Provides
    @PerActivity
    fun provideJob() = Job()

    @Provides
    @PerActivity
    fun provideChangeServerNavigator(activity: ChangeServerActivity) = ChangeServerNavigator(activity)

    @Provides
    @PerActivity
    fun ChangeServerView(activity: ChangeServerActivity): ChangeServerView {
        return activity
    }

    @Provides
    fun provideLifecycleOwner(activity: ChangeServerActivity): LifecycleOwner = activity

    @Provides
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy = CancelStrategy(owner, jobs)
}