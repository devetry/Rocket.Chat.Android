package ytpconnect.rocket.android.preferences.di

import ytpconnect.rocket.android.dagger.scope.PerFragment
import ytpconnect.rocket.android.preferences.ui.PreferencesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PreferencesFragmentProvider {

    @ContributesAndroidInjector(modules = [PreferencesFragmentModule::class])
    @PerFragment
    abstract fun providePreferencesFragment(): PreferencesFragment
}