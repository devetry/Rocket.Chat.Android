package ytpconnect.rocket.android.files.di

import ytpconnect.rocket.android.dagger.scope.PerFragment
import ytpconnect.rocket.android.files.ui.FilesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FilesFragmentProvider {

    @ContributesAndroidInjector(modules = [FilesFragmentModule::class])
    @PerFragment
    abstract fun provideFilesFragment(): FilesFragment
}