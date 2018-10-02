package ytpconnect.rocket.android.chatroom.di

import ytpconnect.rocket.android.chatroom.presentation.ChatRoomNavigator
import ytpconnect.rocket.android.chatroom.ui.ChatRoomActivity
import ytpconnect.rocket.android.dagger.scope.PerActivity
import dagger.Module
import dagger.Provides

@Module
class ChatRoomModule {
    @Provides
    @PerActivity
    fun provideChatRoomNavigator(activity: ChatRoomActivity) = ChatRoomNavigator(activity)
}