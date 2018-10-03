package ytpconnect.rocket.android.push.di

import ytpconnect.rocket.android.dagger.module.AppModule
import ytpconnect.rocket.android.push.DeleteReceiver
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class DeleteReceiverProvider {
    @ContributesAndroidInjector(modules = [AppModule::class])
    abstract fun provideDeleteReceiver(): DeleteReceiver
}