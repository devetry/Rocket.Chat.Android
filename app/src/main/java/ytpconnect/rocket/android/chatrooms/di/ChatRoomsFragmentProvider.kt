package ytpconnect.rocket.android.chatrooms.di

import ytpconnect.rocket.android.chatrooms.ui.ChatRoomsFragment
import ytpconnect.rocket.android.dagger.scope.PerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ChatRoomsFragmentProvider {

    @ContributesAndroidInjector(modules = [ChatRoomsFragmentModule::class])
    @PerFragment
    abstract fun provideChatRoomsFragment(): ChatRoomsFragment
}