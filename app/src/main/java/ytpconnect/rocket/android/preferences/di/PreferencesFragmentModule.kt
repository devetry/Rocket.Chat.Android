package ytpconnect.rocket.android.preferences.di

import ytpconnect.rocket.android.dagger.scope.PerFragment
import ytpconnect.rocket.android.preferences.presentation.PreferencesView
import ytpconnect.rocket.android.preferences.ui.PreferencesFragment
import dagger.Module
import dagger.Provides

@Module
class PreferencesFragmentModule {

    @Provides
    @PerFragment
    fun preferencesView(frag: PreferencesFragment): PreferencesView {
        return frag
    }
}