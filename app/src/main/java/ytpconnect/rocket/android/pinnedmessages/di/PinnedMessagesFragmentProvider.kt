package ytpconnect.rocket.android.pinnedmessages.di

import ytpconnect.rocket.android.dagger.scope.PerFragment
import ytpconnect.rocket.android.pinnedmessages.ui.PinnedMessagesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PinnedMessagesFragmentProvider {

    @ContributesAndroidInjector(modules = [PinnedMessagesFragmentModule::class])
    @PerFragment
    abstract fun providePinnedMessageFragment(): PinnedMessagesFragment
}