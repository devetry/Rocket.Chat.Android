package ytpconnect.rocket.android.authentication.registerusername.di

import androidx.lifecycle.LifecycleOwner
import ytpconnect.rocket.android.authentication.registerusername.presentation.RegisterUsernameView
import ytpconnect.rocket.android.authentication.registerusername.ui.RegisterUsernameFragment
import ytpconnect.rocket.android.core.lifecycle.CancelStrategy
import ytpconnect.rocket.android.dagger.scope.PerFragment
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
class RegisterUsernameFragmentModule {

    @Provides
    @PerFragment
    fun provideJob() = Job()

    @Provides
    @PerFragment
    fun registerUsernameView(frag: RegisterUsernameFragment): RegisterUsernameView {
        return frag
    }

    @Provides
    @PerFragment
    fun provideLifecycleOwner(frag: RegisterUsernameFragment): LifecycleOwner {
        return frag
    }

    @Provides
    @PerFragment
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
        return CancelStrategy(owner, jobs)
    }
}