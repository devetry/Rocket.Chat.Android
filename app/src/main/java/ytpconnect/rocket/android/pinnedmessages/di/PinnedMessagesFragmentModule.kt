package ytpconnect.rocket.android.pinnedmessages.di

import androidx.lifecycle.LifecycleOwner
import ytpconnect.rocket.android.core.lifecycle.CancelStrategy
import ytpconnect.rocket.android.dagger.scope.PerFragment
import ytpconnect.rocket.android.pinnedmessages.presentation.PinnedMessagesView
import ytpconnect.rocket.android.pinnedmessages.ui.PinnedMessagesFragment
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
class PinnedMessagesFragmentModule {

    @Provides
    @PerFragment
    fun providePinnedMessagesView(frag: PinnedMessagesFragment): PinnedMessagesView {
        return frag
    }

    @Provides
    @PerFragment
    fun provideJob() = Job()

    @Provides
    @PerFragment
    fun provideLifecycleOwner(frag: PinnedMessagesFragment): LifecycleOwner {
        return frag
    }

    @Provides
    @PerFragment
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
        return CancelStrategy(owner, jobs)
    }
}