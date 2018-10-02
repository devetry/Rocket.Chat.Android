package ytpconnect.rocket.android.dagger.module

import android.app.Application
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import ytpconnect.rocket.android.BuildConfig
import ytpconnect.rocket.android.R
import ytpconnect.rocket.android.analytics.AnalyticsManager
import ytpconnect.rocket.android.analytics.AnswersAnalytics
import ytpconnect.rocket.android.analytics.GoogleAnalyticsForFirebase
import ytpconnect.rocket.android.authentication.infraestructure.SharedPreferencesMultiServerTokenRepository
import ytpconnect.rocket.android.authentication.infraestructure.SharedPreferencesTokenRepository
import ytpconnect.rocket.android.chatroom.service.MessageService
import ytpconnect.rocket.android.dagger.qualifier.ForAuthentication
import ytpconnect.rocket.android.dagger.qualifier.ForMessages
import ytpconnect.rocket.android.db.DatabaseManager
import ytpconnect.rocket.android.db.DatabaseManagerFactory
import ytpconnect.rocket.android.helper.MessageParser
import ytpconnect.rocket.android.infrastructure.LocalRepository
import ytpconnect.rocket.android.infrastructure.SharedPreferencesLocalRepository
import ytpconnect.rocket.android.push.GroupedPush
import ytpconnect.rocket.android.push.PushManager
import ytpconnect.rocket.android.server.domain.AccountsRepository
import ytpconnect.rocket.android.server.domain.ActiveUsersRepository
import ytpconnect.rocket.android.server.domain.AnalyticsTrackingInteractor
import ytpconnect.rocket.android.server.domain.AnalyticsTrackingRepository
import ytpconnect.rocket.android.server.domain.ChatRoomsRepository
import ytpconnect.rocket.android.server.domain.CurrentServerRepository
import ytpconnect.rocket.android.server.domain.GetAccountInteractor
import ytpconnect.rocket.android.server.domain.GetAccountsInteractor
import ytpconnect.rocket.android.server.domain.GetCurrentServerInteractor
import ytpconnect.rocket.android.server.domain.GetSettingsInteractor
import ytpconnect.rocket.android.server.domain.JobSchedulerInteractor
import ytpconnect.rocket.android.server.domain.MessagesRepository
import ytpconnect.rocket.android.server.domain.MultiServerTokenRepository
import ytpconnect.rocket.android.server.domain.PermissionsRepository
import ytpconnect.rocket.android.server.domain.RoomRepository
import ytpconnect.rocket.android.server.domain.SettingsRepository
import ytpconnect.rocket.android.server.domain.TokenRepository
import ytpconnect.rocket.android.server.domain.UsersRepository
import ytpconnect.rocket.android.server.infraestructure.JobSchedulerInteractorImpl
import ytpconnect.rocket.android.server.infraestructure.MemoryActiveUsersRepository
import ytpconnect.rocket.android.server.infraestructure.MemoryChatRoomsRepository
import ytpconnect.rocket.android.server.infraestructure.MemoryRoomRepository
import ytpconnect.rocket.android.server.infraestructure.MemoryUsersRepository
import ytpconnect.rocket.android.server.infraestructure.SharedPreferencesAccountsRepository
import ytpconnect.rocket.android.server.infraestructure.SharedPreferencesMessagesRepository
import ytpconnect.rocket.android.server.infraestructure.SharedPreferencesPermissionsRepository
import ytpconnect.rocket.android.server.infraestructure.SharedPreferencesSettingsRepository
import ytpconnect.rocket.android.server.infraestructure.SharedPrefsAnalyticsTrackingRepository
import ytpconnect.rocket.android.server.infraestructure.SharedPrefsConnectingServerRepository
import ytpconnect.rocket.android.server.infraestructure.SharedPrefsCurrentServerRepository
import ytpconnect.rocket.android.util.AppJsonAdapterFactory
import ytpconnect.rocket.android.util.HttpLoggingInterceptor
import ytpconnect.rocket.android.util.TimberLogger
import ytpconnect.rocket.common.internal.FallbackSealedClassJsonAdapter
import ytpconnect.rocket.common.internal.ISO8601Date
import ytpconnect.rocket.common.model.TimestampAdapter
import ytpconnect.rocket.common.util.CalendarISO8601Converter
import ytpconnect.rocket.common.util.Logger
import ytpconnect.rocket.common.util.PlatformLogger
import ytpconnect.rocket.core.internal.AttachmentAdapterFactory
import ytpconnect.rocket.core.internal.ReactionsAdapter
import com.facebook.drawee.backends.pipeline.DraweeConfig
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.listener.RequestLoggingListener
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import ru.noties.markwon.SpannableConfiguration
import ru.noties.markwon.spans.SpannableTheme
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Timber.d(message)
            }
        })
        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            // TODO - change to HEADERS on production...
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        }

        return interceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(logger: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideImagePipelineConfig(
        context: Context,
        okHttpClient: OkHttpClient
    ): ImagePipelineConfig {
        val listeners = setOf(RequestLoggingListener())

        return OkHttpImagePipelineConfigFactory.newBuilder(context, okHttpClient)
            .setRequestListeners(listeners)
            .setDownsampleEnabled(true)
            .experiment().setPartialImageCachingEnabled(true).build()
    }

    @Provides
    @Singleton
    fun provideDraweeConfig(): DraweeConfig {
        return DraweeConfig.newBuilder().build()
    }

    @Provides
    @Singleton
    fun provideTokenRepository(prefs: SharedPreferences, moshi: Moshi): TokenRepository {
        return SharedPreferencesTokenRepository(prefs, moshi)
    }

    @Provides
    @Singleton
    fun providePlatformLogger(): PlatformLogger {
        return TimberLogger
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Application) =
        context.getSharedPreferences("rocket.chat", Context.MODE_PRIVATE)


    @Provides
    @ForMessages
    fun provideMessagesSharedPreferences(context: Application) =
        context.getSharedPreferences("messages", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideLocalRepository(prefs: SharedPreferences, moshi: Moshi): LocalRepository {
        return SharedPreferencesLocalRepository(prefs, moshi)
    }

    @Provides
    @Singleton
    fun provideCurrentServerRepository(prefs: SharedPreferences): CurrentServerRepository {
        return SharedPrefsCurrentServerRepository(prefs)
    }

    @Provides
    @Singleton
    fun provideAnalyticsTrackingRepository(prefs: SharedPreferences): AnalyticsTrackingRepository {
        return SharedPrefsAnalyticsTrackingRepository(prefs)
    }

    @Provides
    @ForAuthentication
    fun provideConnectingServerRepository(prefs: SharedPreferences): CurrentServerRepository {
        return SharedPrefsConnectingServerRepository(prefs)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(localRepository: LocalRepository): SettingsRepository {
        return SharedPreferencesSettingsRepository(localRepository)
    }

    @Provides
    @Singleton
    fun providePermissionsRepository(
        localRepository: LocalRepository,
        moshi: Moshi
    ): PermissionsRepository {
        return SharedPreferencesPermissionsRepository(localRepository, moshi)
    }

    @Provides
    @Singleton
    fun provideRoomRepository(): RoomRepository {
        return MemoryRoomRepository()
    }

    @Provides
    @Singleton
    fun provideChatRoomRepository(): ChatRoomsRepository {
        return MemoryChatRoomsRepository()
    }

    @Provides
    @Singleton
    fun provideActiveUsersRepository(): ActiveUsersRepository {
        return MemoryActiveUsersRepository()
    }

    @Provides
    @Singleton
    fun provideMoshi(
        logger: PlatformLogger,
        currentServerInteractor: GetCurrentServerInteractor
    ): Moshi {
        val url = currentServerInteractor.get() ?: ""
        return Moshi.Builder()
            .add(FallbackSealedClassJsonAdapter.ADAPTER_FACTORY)
            .add(AppJsonAdapterFactory.INSTANCE)
            .add(AttachmentAdapterFactory(Logger(logger, url)))
            .add(
                java.lang.Long::class.java,
                ISO8601Date::class.java,
                TimestampAdapter(CalendarISO8601Converter())
            )
            .add(
                Long::class.java,
                ISO8601Date::class.java,
                TimestampAdapter(CalendarISO8601Converter())
            )
            .add(ReactionsAdapter())
            .build()
    }

    @Provides
    @Singleton
    fun provideMultiServerTokenRepository(
        repository: LocalRepository,
        moshi: Moshi
    ): MultiServerTokenRepository {
        return SharedPreferencesMultiServerTokenRepository(repository, moshi)
    }

    @Provides
    @Singleton
    fun provideMessageRepository(
        @ForMessages preferences: SharedPreferences,
        moshi: Moshi,
        currentServerInteractor: GetCurrentServerInteractor
    ): MessagesRepository {
        return SharedPreferencesMessagesRepository(preferences, moshi, currentServerInteractor)
    }

    @Provides
    @Singleton
    fun provideUserRepository(): UsersRepository {
        return MemoryUsersRepository()
    }

    @Provides
    @Singleton
    fun provideConfiguration(context: Application): SpannableConfiguration {
        val res = context.resources
        return SpannableConfiguration.builder(context)
            .theme(
                SpannableTheme.builder()
                    .blockMargin(0)
                    .linkColor(res.getColor(R.color.colorAccent))
                    .build()
            )
            .build()
    }

    @Provides
    fun provideMessageParser(
        context: Application,
        configuration: SpannableConfiguration,
        serverInteractor: GetCurrentServerInteractor,
        settingsInteractor: GetSettingsInteractor
    ): MessageParser {
        val url = serverInteractor.get()!!
        return MessageParser(context, configuration, settingsInteractor.get(url))
    }

    @Provides
    @Singleton
    fun provideAccountsRepository(
        preferences: SharedPreferences,
        moshi: Moshi
    ): AccountsRepository =
        SharedPreferencesAccountsRepository(preferences, moshi)

    @Provides
    fun provideNotificationManager(context: Application) =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    @Singleton
    fun provideGroupedPush() = GroupedPush()

    @Provides
    @Singleton
    fun providePushManager(
        context: Application,
        groupedPushes: GroupedPush,
        manager: NotificationManager,
        moshi: Moshi,
        getAccountInteractor: GetAccountInteractor,
        getSettingsInteractor: GetSettingsInteractor
    ): PushManager {
        return PushManager(
            groupedPushes,
            manager,
            moshi,
            getAccountInteractor,
            getSettingsInteractor,
            context
        )
    }

    @Provides
    fun provideJobScheduler(context: Application): JobScheduler {
        return context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
    }

    @Provides
    fun provideSendMessageJob(context: Application): JobInfo {
        return JobInfo.Builder(
            MessageService.RETRY_SEND_MESSAGE_ID,
            ComponentName(context, MessageService::class.java)
        )
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .build()
    }

    @Provides
    fun provideJobSchedulerInteractor(
        jobScheduler: JobScheduler,
        jobInfo: JobInfo
    ): JobSchedulerInteractor {
        return JobSchedulerInteractorImpl(jobScheduler, jobInfo)
    }

    @Provides
    @Named("currentServer")
    fun provideCurrentServer(currentServerInteractor: GetCurrentServerInteractor): String {
        return currentServerInteractor.get()!!
    }

    @Provides
    fun provideDatabaseManager(
        factory: DatabaseManagerFactory,
        @Named("currentServer") currentServer: String
    ): DatabaseManager {
        return factory.create(currentServer)
    }

    @Provides
    @Singleton
    fun provideAnswersAnalytics(): AnswersAnalytics {
        return AnswersAnalytics()
    }

    @Provides
    @Singleton
    fun provideGoogleAnalyticsForFirebase(context: Application): GoogleAnalyticsForFirebase {
        return GoogleAnalyticsForFirebase(context)
    }

    @Provides
    @Singleton
    fun provideAnalyticsManager(
        analyticsTrackingInteractor: AnalyticsTrackingInteractor,
        getCurrentServerInteractor: GetCurrentServerInteractor,
        getAccountsInteractor: GetAccountsInteractor,
        answersAnalytics: AnswersAnalytics,
        googleAnalyticsForFirebase: GoogleAnalyticsForFirebase
    ): AnalyticsManager {
        return AnalyticsManager(
            analyticsTrackingInteractor,
            getCurrentServerInteractor,
            getAccountsInteractor,
            listOf(answersAnalytics, googleAnalyticsForFirebase)
        )
    }
}
