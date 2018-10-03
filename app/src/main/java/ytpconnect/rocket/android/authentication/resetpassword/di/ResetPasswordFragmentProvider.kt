package ytpconnect.rocket.android.authentication.resetpassword.di

import ytpconnect.rocket.android.authentication.resetpassword.ui.ResetPasswordFragment
import ytpconnect.rocket.android.dagger.scope.PerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ResetPasswordFragmentProvider {

    @ContributesAndroidInjector(modules = [ResetPasswordFragmentModule::class])
    @PerFragment
    abstract fun provideResetPasswordFragment(): ResetPasswordFragment
}