package ytpconnect.rocket.android.members.di

import androidx.lifecycle.LifecycleOwner
import ytpconnect.rocket.android.chatroom.ui.ChatRoomActivity
import ytpconnect.rocket.android.core.lifecycle.CancelStrategy
import ytpconnect.rocket.android.dagger.scope.PerFragment
import ytpconnect.rocket.android.members.presentation.MembersNavigator
import ytpconnect.rocket.android.members.presentation.MembersView
import ytpconnect.rocket.android.members.ui.MembersFragment
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
class MembersFragmentModule {

    @Provides
    @PerFragment
    fun membersView(frag: MembersFragment): MembersView {
        return frag
    }

    @Provides
    @PerFragment
    fun provideChatRoomNavigator(activity: ChatRoomActivity) = MembersNavigator(activity)

    @Provides
    @PerFragment
    fun provideJob() = Job()

    @Provides
    @PerFragment
    fun provideLifecycleOwner(frag: MembersFragment): LifecycleOwner {
        return frag
    }

    @Provides
    @PerFragment
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
        return CancelStrategy(owner, jobs)
    }
}