package ytpconnect.rocket.android.chatrooms.di

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import ytpconnect.rocket.android.chatrooms.adapter.RoomUiModelMapper
import ytpconnect.rocket.android.chatrooms.domain.FetchChatRoomsInteractor
import ytpconnect.rocket.android.chatrooms.presentation.ChatRoomsView
import ytpconnect.rocket.android.chatrooms.ui.ChatRoomsFragment
import ytpconnect.rocket.android.dagger.scope.PerFragment
import ytpconnect.rocket.android.db.ChatRoomDao
import ytpconnect.rocket.android.db.DatabaseManager
import ytpconnect.rocket.android.db.UserDao
import ytpconnect.rocket.android.server.domain.GetCurrentUserInteractor
import ytpconnect.rocket.android.server.domain.PublicSettings
import ytpconnect.rocket.android.server.domain.SettingsRepository
import ytpconnect.rocket.android.server.domain.TokenRepository
import ytpconnect.rocket.android.server.infraestructure.ConnectionManager
import ytpconnect.rocket.android.server.infraestructure.ConnectionManagerFactory
import ytpconnect.rocket.android.server.infraestructure.RocketChatClientFactory
import ytpconnect.rocket.core.RocketChatClient
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ChatRoomsFragmentModule {

    @Provides
    @PerFragment
    fun chatRoomsView(frag: ChatRoomsFragment): ChatRoomsView {
        return frag
    }

    @Provides
    @PerFragment
    fun provideLifecycleOwner(frag: ChatRoomsFragment): LifecycleOwner {
        return frag
    }

    @Provides
    @PerFragment
    fun provideRocketChatClient(
        factory: RocketChatClientFactory,
        @Named("currentServer") currentServer: String
    ): RocketChatClient {
        return factory.create(currentServer)
    }

    @Provides
    @PerFragment
    fun provideChatRoomDao(manager: DatabaseManager): ChatRoomDao = manager.chatRoomDao()

    @Provides
    @PerFragment
    fun provideUserDao(manager: DatabaseManager): UserDao = manager.userDao()

    @Provides
    @PerFragment
    fun provideConnectionManager(
        factory: ConnectionManagerFactory,
        @Named("currentServer") currentServer: String
    ): ConnectionManager {
        return factory.create(currentServer)
    }

    @Provides
    @PerFragment
    fun provideFetchChatRoomsInteractor(
        client: RocketChatClient,
        dbManager: DatabaseManager
    ): FetchChatRoomsInteractor {
        return FetchChatRoomsInteractor(client, dbManager)
    }

    @Provides
    @PerFragment
    fun providePublicSettings(
        repository: SettingsRepository,
        @Named("currentServer") currentServer: String
    ): PublicSettings {
        return repository.get(currentServer)
    }

    @Provides
    @PerFragment
    fun provideRoomMapper(
        context: Application,
        repository: SettingsRepository,
        userInteractor: GetCurrentUserInteractor,
        @Named("currentServer") serverUrl: String
    ): RoomUiModelMapper {
        return RoomUiModelMapper(context, repository.get(serverUrl), userInteractor, serverUrl)
    }

    @Provides
    @PerFragment
    fun provideGetCurrentUserInteractor(
        tokenRepository: TokenRepository,
        @Named("currentServer") serverUrl: String,
        userDao: UserDao
    ): GetCurrentUserInteractor {
        return GetCurrentUserInteractor(tokenRepository, serverUrl, userDao)
    }
}