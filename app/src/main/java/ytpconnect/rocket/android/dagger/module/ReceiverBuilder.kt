package ytpconnect.rocket.android.dagger.module

import ytpconnect.rocket.android.push.DeleteReceiver
import ytpconnect.rocket.android.push.DirectReplyReceiver
import ytpconnect.rocket.android.push.DirectReplyReceiverProvider
import ytpconnect.rocket.android.push.di.DeleteReceiverProvider
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ReceiverBuilder {

    @ContributesAndroidInjector(modules = [DeleteReceiverProvider::class])
    abstract fun bindDeleteReceiver(): DeleteReceiver

    @ContributesAndroidInjector(modules = [DirectReplyReceiverProvider::class])
    abstract fun bindDirectReplyReceiver(): DirectReplyReceiver
}