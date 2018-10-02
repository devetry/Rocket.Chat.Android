package ytpconnect.rocket.android.profile.di

import ytpconnect.rocket.android.dagger.scope.PerFragment
import ytpconnect.rocket.android.profile.ui.ProfileFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ProfileFragmentProvider {

    @ContributesAndroidInjector(modules = [ProfileFragmentModule::class])
    @PerFragment
    abstract fun provideProfileFragment(): ProfileFragment
}