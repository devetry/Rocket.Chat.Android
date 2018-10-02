package ytpconnect.rocket.android.authentication.login.di

import ytpconnect.rocket.android.authentication.login.ui.LoginFragment
import ytpconnect.rocket.android.dagger.scope.PerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module abstract class LoginFragmentProvider {

    @ContributesAndroidInjector(modules = [LoginFragmentModule::class])
    @PerFragment
    abstract fun provideLoginFragment(): LoginFragment
}