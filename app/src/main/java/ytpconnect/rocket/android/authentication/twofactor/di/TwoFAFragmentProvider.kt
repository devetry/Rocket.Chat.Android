package ytpconnect.rocket.android.authentication.twofactor.di

import ytpconnect.rocket.android.authentication.twofactor.ui.TwoFAFragment
import ytpconnect.rocket.android.dagger.scope.PerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module abstract class TwoFAFragmentProvider {

    @ContributesAndroidInjector(modules = [TwoFAFragmentModule::class])
    @PerFragment
    abstract fun provideTwoFAFragment(): TwoFAFragment
}
