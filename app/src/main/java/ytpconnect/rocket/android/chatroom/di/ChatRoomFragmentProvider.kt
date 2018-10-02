package ytpconnect.rocket.android.chatroom.di

import ytpconnect.rocket.android.chatroom.ui.ChatRoomFragment
import ytpconnect.rocket.android.dagger.scope.PerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ChatRoomFragmentProvider {

    @ContributesAndroidInjector(modules = [ChatRoomFragmentModule::class])
    @PerFragment
    abstract fun provideChatRoomFragment(): ChatRoomFragment
}