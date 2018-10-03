package ytpconnect.rocket.android.about.di

import ytpconnect.rocket.android.about.ui.AboutFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AboutFragmentProvider {

    @ContributesAndroidInjector()
    abstract fun provideAboutFragment(): AboutFragment
}
