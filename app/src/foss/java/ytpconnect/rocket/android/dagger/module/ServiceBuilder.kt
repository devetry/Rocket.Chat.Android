package ytpconnect.rocket.android.dagger.module

import ytpconnect.rocket.android.chatroom.di.MessageServiceProvider
import ytpconnect.rocket.android.chatroom.service.MessageService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module abstract class ServiceBuilder {
    @ContributesAndroidInjector(modules = [MessageServiceProvider::class])
    abstract fun bindMessageService(): MessageService
}
