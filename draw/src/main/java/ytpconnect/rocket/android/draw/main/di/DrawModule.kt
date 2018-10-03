package ytpconnect.rocket.android.draw.main.di

import androidx.lifecycle.LifecycleOwner
import ytpconnect.rocket.android.core.lifecycle.CancelStrategy
import ytpconnect.rocket.android.draw.main.presenter.DrawView
import ytpconnect.rocket.android.draw.main.ui.DrawingActivity
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
class DrawModule {

    @Provides
    fun provideMainView(activity: DrawingActivity): DrawView = activity

    @Provides
    fun provideJob() = Job()

    @Provides
    fun provideLifecycleOwner(activity: DrawingActivity): LifecycleOwner = activity

    @Provides
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy =
        CancelStrategy(owner, jobs)
}