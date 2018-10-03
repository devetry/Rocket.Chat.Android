package ytpconnect.rocket.android.files.di

import androidx.lifecycle.LifecycleOwner
import ytpconnect.rocket.android.core.lifecycle.CancelStrategy
import ytpconnect.rocket.android.dagger.scope.PerFragment
import ytpconnect.rocket.android.files.presentation.FilesView
import ytpconnect.rocket.android.files.ui.FilesFragment
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
class FilesFragmentModule {

    @Provides
    @PerFragment
    fun provideFilesView(frag: FilesFragment): FilesView {
        return frag
    }

    @Provides
    @PerFragment
    fun provideJob() = Job()

    @Provides
    @PerFragment
    fun provideLifecycleOwner(frag: FilesFragment): LifecycleOwner {
        return frag
    }

    @Provides
    @PerFragment
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
        return CancelStrategy(owner, jobs)
    }
}