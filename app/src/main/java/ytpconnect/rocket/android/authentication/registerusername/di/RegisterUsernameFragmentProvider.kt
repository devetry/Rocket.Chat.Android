package ytpconnect.rocket.android.authentication.registerusername.di

import ytpconnect.rocket.android.authentication.registerusername.ui.RegisterUsernameFragment
import ytpconnect.rocket.android.dagger.scope.PerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class RegisterUsernameFragmentProvider {

    @ContributesAndroidInjector(modules = [RegisterUsernameFragmentModule::class])
    @PerFragment
    abstract fun provideRegisterUsernameFragment(): RegisterUsernameFragment
}