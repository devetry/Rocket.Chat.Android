package ytpconnect.rocket.android.settings.password.di

import ytpconnect.rocket.android.dagger.scope.PerFragment
import ytpconnect.rocket.android.settings.password.ui.PasswordFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PasswordFragmentProvider {
    @ContributesAndroidInjector(modules = [PasswordFragmentModule::class])
    @PerFragment
    abstract fun providePasswordFragment(): PasswordFragment
}