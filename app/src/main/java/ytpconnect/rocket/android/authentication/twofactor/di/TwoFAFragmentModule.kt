package ytpconnect.rocket.android.authentication.twofactor.di

import androidx.lifecycle.LifecycleOwner
import ytpconnect.rocket.android.authentication.twofactor.presentation.TwoFAView
import ytpconnect.rocket.android.authentication.twofactor.ui.TwoFAFragment
import ytpconnect.rocket.android.core.lifecycle.CancelStrategy
import ytpconnect.rocket.android.dagger.scope.PerFragment
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
class TwoFAFragmentModule {

    @Provides
    @PerFragment
    fun provideJob() = Job()

    @Provides
    @PerFragment
    fun loginView(frag: TwoFAFragment): TwoFAView {
        return frag
    }

    @Provides
    @PerFragment
    fun provideLifecycleOwner(frag: TwoFAFragment): LifecycleOwner {
        return frag
    }

    @Provides
    @PerFragment
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
        return CancelStrategy(owner, jobs)
    }
}
