package ytpconnect.rocket.android.dagger.module

import android.content.Context
import android.content.SharedPreferences
import ytpconnect.rocket.android.infrastructure.LocalRepository
import ytpconnect.rocket.android.infrastructure.SharedPreferencesLocalRepository
import ytpconnect.rocket.android.server.domain.CurrentServerRepository
import ytpconnect.rocket.android.server.domain.GetCurrentServerInteractor
import ytpconnect.rocket.android.server.infraestructure.SharedPrefsCurrentServerRepository
import ytpconnect.rocket.android.util.AppJsonAdapterFactory
import ytpconnect.rocket.android.util.TimberLogger
import ytpconnect.rocket.common.internal.FallbackSealedClassJsonAdapter
import ytpconnect.rocket.common.internal.ISO8601Date
import ytpconnect.rocket.common.model.TimestampAdapter
import ytpconnect.rocket.common.util.CalendarISO8601Converter
import ytpconnect.rocket.common.util.Logger
import ytpconnect.rocket.common.util.PlatformLogger
import ytpconnect.rocket.core.internal.AttachmentAdapterFactory
import ytpconnect.rocket.core.internal.ReactionsAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocalModule {

    @Provides
    @Singleton
    fun providePlatformLogger(): PlatformLogger {
        return TimberLogger
    }

    @Provides
    @Singleton
    fun provideCurrentServerRepository(prefs: SharedPreferences): CurrentServerRepository {
        return SharedPrefsCurrentServerRepository(prefs)
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
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("rocket.chat", Context.MODE_PRIVATE)
    }



    @Provides
    @Singleton
    fun provideLocalRepository(sharedPreferences: SharedPreferences, moshi: Moshi): LocalRepository {
        return SharedPreferencesLocalRepository(sharedPreferences, moshi)
    }
}