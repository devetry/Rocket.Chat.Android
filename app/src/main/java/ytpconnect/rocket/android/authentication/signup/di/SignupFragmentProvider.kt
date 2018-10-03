package ytpconnect.rocket.android.authentication.signup.di

import ytpconnect.rocket.android.authentication.signup.ui.SignupFragment
import ytpconnect.rocket.android.dagger.scope.PerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SignupFragmentProvider {

    @ContributesAndroidInjector(modules = [SignupFragmentModule::class])
    @PerFragment
    abstract fun provideSignupFragment(): SignupFragment
}