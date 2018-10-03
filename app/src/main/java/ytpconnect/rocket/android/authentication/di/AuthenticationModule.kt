package ytpconnect.rocket.android.authentication.di

import ytpconnect.rocket.android.authentication.presentation.AuthenticationNavigator
import ytpconnect.rocket.android.authentication.ui.AuthenticationActivity
import ytpconnect.rocket.android.dagger.scope.PerActivity
import dagger.Module
import dagger.Provides

@Module
class AuthenticationModule {

    @Provides
    @PerActivity
    fun provideAuthenticationNavigator(activity: AuthenticationActivity) = AuthenticationNavigator(activity)
}