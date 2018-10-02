package ytpconnect.rocket.android.authentication.server.di

import androidx.lifecycle.LifecycleOwner
import ytpconnect.rocket.android.authentication.server.presentation.ServerView
import ytpconnect.rocket.android.authentication.server.ui.ServerFragment
import ytpconnect.rocket.android.core.lifecycle.CancelStrategy
import ytpconnect.rocket.android.dagger.scope.PerFragment
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
class ServerFragmentModule {

    @Provides
    @PerFragment
    fun provideJob() = Job()

    @Provides
    @PerFragment
    fun serverView(frag: ServerFragment): ServerView {
        return frag
    }

    @Provides
    @PerFragment
    fun provideLifecycleOwner(frag: ServerFragment): LifecycleOwner {
        return frag
    }

    @Provides
    @PerFragment
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
        return CancelStrategy(owner, jobs)
    }
}