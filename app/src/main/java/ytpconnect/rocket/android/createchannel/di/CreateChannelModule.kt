package ytpconnect.rocket.android.createchannel.di

import androidx.lifecycle.LifecycleOwner
import ytpconnect.rocket.android.createchannel.presentation.CreateChannelView
import ytpconnect.rocket.android.createchannel.ui.CreateChannelFragment
import ytpconnect.rocket.android.dagger.scope.PerFragment
import dagger.Module
import dagger.Provides

@Module
class CreateChannelModule {

    @Provides
    @PerFragment
    fun createChannelView(fragment: CreateChannelFragment): CreateChannelView {
        return fragment
    }

    @Provides
    @PerFragment
    fun provideLifecycleOwner(fragment: CreateChannelFragment): LifecycleOwner {
        return fragment
    }
}