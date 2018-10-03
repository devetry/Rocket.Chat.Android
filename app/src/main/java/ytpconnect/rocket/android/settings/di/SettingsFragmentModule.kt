package ytpconnect.rocket.android.settings.di

import androidx.lifecycle.LifecycleOwner
import ytpconnect.rocket.android.core.lifecycle.CancelStrategy
import ytpconnect.rocket.android.dagger.scope.PerFragment
import ytpconnect.rocket.android.settings.presentation.SettingsView
import ytpconnect.rocket.android.settings.ui.SettingsFragment
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
class SettingsFragmentModule {

    @Provides
    @PerFragment
    fun settingsView(frag: SettingsFragment): SettingsView {
        return frag
    }

    @Provides
    @PerFragment
    fun settingsLifecycleOwner(frag: SettingsFragment): LifecycleOwner {
        return frag
    }

    @Provides
    @PerFragment
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
        return CancelStrategy(owner, jobs)
    }
}