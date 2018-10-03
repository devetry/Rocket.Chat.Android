package ytpconnect.rocket.android.favoritemessages.di

import ytpconnect.rocket.android.dagger.scope.PerFragment
import ytpconnect.rocket.android.favoritemessages.ui.FavoriteMessagesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FavoriteMessagesFragmentProvider {

    @ContributesAndroidInjector(modules = [FavoriteMessagesFragmentModule::class])
    @PerFragment
    abstract fun provideFavoriteMessageFragment(): FavoriteMessagesFragment
}