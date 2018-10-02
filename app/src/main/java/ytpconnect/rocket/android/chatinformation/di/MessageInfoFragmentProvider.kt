package ytpconnect.rocket.android.chatinformation.di

import ytpconnect.rocket.android.chatinformation.ui.MessageInfoFragment
import ytpconnect.rocket.android.dagger.scope.PerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MessageInfoFragmentProvider {

    @ContributesAndroidInjector(modules = [MessageInfoFragmentModule::class])
    @PerFragment
    abstract fun provideMessageInfoFragment(): MessageInfoFragment
}
