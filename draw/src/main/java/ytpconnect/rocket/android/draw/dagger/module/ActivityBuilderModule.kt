package ytpconnect.rocket.android.draw.dagger.module

import ytpconnect.rocket.android.draw.main.di.DrawModule
import ytpconnect.rocket.android.draw.main.ui.DrawingActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = [DrawModule::class])
    abstract fun contributeDrawingActivityInjector(): DrawingActivity
}