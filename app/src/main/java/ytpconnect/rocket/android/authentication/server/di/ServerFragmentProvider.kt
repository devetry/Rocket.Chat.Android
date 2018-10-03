package ytpconnect.rocket.android.authentication.server.di

import ytpconnect.rocket.android.authentication.server.ui.ServerFragment
import ytpconnect.rocket.android.dagger.scope.PerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServerFragmentProvider {

    @ContributesAndroidInjector(modules = [ServerFragmentModule::class])
    @PerFragment
    abstract fun provideServerFragment(): ServerFragment
}