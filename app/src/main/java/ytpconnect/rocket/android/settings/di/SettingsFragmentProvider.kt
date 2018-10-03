package ytpconnect.rocket.android.settings.di

import ytpconnect.rocket.android.settings.ui.SettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SettingsFragmentProvider {

    @ContributesAndroidInjector(modules = [SettingsFragmentModule::class])
    abstract fun provideSettingsFragment(): SettingsFragment
}