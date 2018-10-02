package ytpconnect.rocket.android.createchannel.di

import ytpconnect.rocket.android.createchannel.ui.CreateChannelFragment
import ytpconnect.rocket.android.dagger.scope.PerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class CreateChannelProvider {

    @ContributesAndroidInjector(modules = [CreateChannelModule::class])
    @PerFragment
    abstract fun provideCreateChannelFragment(): CreateChannelFragment
}