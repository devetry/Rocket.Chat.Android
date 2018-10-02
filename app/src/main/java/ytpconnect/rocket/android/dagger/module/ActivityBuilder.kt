package ytpconnect.rocket.android.dagger.module

import ytpconnect.rocket.android.about.di.AboutFragmentProvider
import ytpconnect.rocket.android.authentication.di.AuthenticationModule
import ytpconnect.rocket.android.authentication.login.di.LoginFragmentProvider
import ytpconnect.rocket.android.authentication.registerusername.di.RegisterUsernameFragmentProvider
import ytpconnect.rocket.android.authentication.resetpassword.di.ResetPasswordFragmentProvider
import ytpconnect.rocket.android.authentication.server.di.ServerFragmentProvider
import ytpconnect.rocket.android.authentication.signup.di.SignupFragmentProvider
import ytpconnect.rocket.android.authentication.twofactor.di.TwoFAFragmentProvider
import ytpconnect.rocket.android.authentication.ui.AuthenticationActivity
import ytpconnect.rocket.android.chatinformation.di.MessageInfoFragmentProvider
import ytpconnect.rocket.android.chatinformation.ui.MessageInfoActivity
import ytpconnect.rocket.android.chatroom.di.ChatRoomFragmentProvider
import ytpconnect.rocket.android.chatroom.di.ChatRoomModule
import ytpconnect.rocket.android.chatroom.ui.ChatRoomActivity
import ytpconnect.rocket.android.chatrooms.di.ChatRoomsFragmentProvider
import ytpconnect.rocket.android.createchannel.di.CreateChannelProvider
import ytpconnect.rocket.android.dagger.scope.PerActivity
import ytpconnect.rocket.android.draw.main.di.DrawModule
import ytpconnect.rocket.android.draw.main.ui.DrawingActivity
import ytpconnect.rocket.android.favoritemessages.di.FavoriteMessagesFragmentProvider
import ytpconnect.rocket.android.files.di.FilesFragmentProvider
import ytpconnect.rocket.android.main.di.MainModule
import ytpconnect.rocket.android.main.ui.MainActivity
import ytpconnect.rocket.android.members.di.MembersFragmentProvider
import ytpconnect.rocket.android.mentions.di.MentionsFragmentProvider
import ytpconnect.rocket.android.pinnedmessages.di.PinnedMessagesFragmentProvider
import ytpconnect.rocket.android.preferences.di.PreferencesFragmentProvider
import ytpconnect.rocket.android.profile.di.ProfileFragmentProvider
import ytpconnect.rocket.android.server.di.ChangeServerModule
import ytpconnect.rocket.android.server.ui.ChangeServerActivity
import ytpconnect.rocket.android.settings.di.SettingsFragmentProvider
import ytpconnect.rocket.android.settings.password.di.PasswordFragmentProvider
import ytpconnect.rocket.android.settings.password.ui.PasswordActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @PerActivity
    @ContributesAndroidInjector(
        modules = [AuthenticationModule::class,
            ServerFragmentProvider::class,
            LoginFragmentProvider::class,
            RegisterUsernameFragmentProvider::class,
            ResetPasswordFragmentProvider::class,
            SignupFragmentProvider::class,
            TwoFAFragmentProvider::class
        ]
    )
    abstract fun bindAuthenticationActivity(): AuthenticationActivity

    @PerActivity
    @ContributesAndroidInjector(
        modules = [MainModule::class,
            ChatRoomsFragmentProvider::class,
            CreateChannelProvider::class,
            ProfileFragmentProvider::class,
            SettingsFragmentProvider::class,
            AboutFragmentProvider::class,
            PreferencesFragmentProvider::class
        ]
    )
    abstract fun bindMainActivity(): MainActivity

    @PerActivity
    @ContributesAndroidInjector(
        modules = [
            ChatRoomModule::class,
            ChatRoomFragmentProvider::class,
            MembersFragmentProvider::class,
            MentionsFragmentProvider::class,
            PinnedMessagesFragmentProvider::class,
            FavoriteMessagesFragmentProvider::class,
            FilesFragmentProvider::class
        ]
    )
    abstract fun bindChatRoomActivity(): ChatRoomActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [PasswordFragmentProvider::class])
    abstract fun bindPasswordActivity(): PasswordActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [ChangeServerModule::class])
    abstract fun bindChangeServerActivity(): ChangeServerActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [MessageInfoFragmentProvider::class])
    abstract fun bindMessageInfoActiviy(): MessageInfoActivity
    @ContributesAndroidInjector(modules = [DrawModule::class])
    abstract fun bindDrawingActivity(): DrawingActivity
}
