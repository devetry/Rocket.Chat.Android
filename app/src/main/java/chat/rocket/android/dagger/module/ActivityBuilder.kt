package chat.rocket.android.dagger.module

import chat.rocket.android.app.MainActivity
import chat.rocket.android.authentication.di.*
import chat.rocket.android.authentication.login.di.LoginFragmentProvider
import chat.rocket.android.authentication.server.di.ServerFragmentProvider
import chat.rocket.android.authentication.signup.di.SignupFragmentProvider
import chat.rocket.android.authentication.twofactor.di.TwoFAFragmentProvider
import chat.rocket.android.authentication.ui.AuthenticationActivity
import chat.rocket.android.dagger.scope.PerActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module abstract class ActivityBuilder {

    @PerActivity
    @ContributesAndroidInjector(modules = [AuthenticationModule::class,
        LoginFragmentProvider::class,
        ServerFragmentProvider::class,
        SignupFragmentProvider::class,
        TwoFAFragmentProvider::class
    ])

    abstract fun bindAuthenticationActivity(): AuthenticationActivity

    @ContributesAndroidInjector abstract fun bindMainActivity(): MainActivity
}