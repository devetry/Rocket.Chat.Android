package ytpconnect.rocket.android.mentions.di

import ytpconnect.rocket.android.dagger.scope.PerFragment
import ytpconnect.rocket.android.mentions.ui.MentionsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MentionsFragmentProvider {

    @ContributesAndroidInjector(modules = [MentionsFragmentModule::class])
    @PerFragment
    abstract fun provideMentionsFragment(): MentionsFragment
}