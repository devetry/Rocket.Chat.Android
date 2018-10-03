package ytpconnect.rocket.android.settings.password.di

import androidx.lifecycle.LifecycleOwner
import ytpconnect.rocket.android.core.lifecycle.CancelStrategy
import ytpconnect.rocket.android.dagger.scope.PerFragment
import ytpconnect.rocket.android.settings.password.presentation.PasswordView
import ytpconnect.rocket.android.settings.password.ui.PasswordFragment
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
class PasswordFragmentModule {

    @Provides
    @PerFragment
    fun provideJob() = Job()

    @Provides
    @PerFragment
    fun passwordView(frag: PasswordFragment): PasswordView {
        return frag
    }

    @Provides
    @PerFragment
    fun settingsLifecycleOwner(frag: PasswordFragment): LifecycleOwner {
        return frag
    }

    @Provides
    @PerFragment
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
        return CancelStrategy(owner, jobs)
    }
}
