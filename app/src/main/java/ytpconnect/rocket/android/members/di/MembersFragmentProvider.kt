package ytpconnect.rocket.android.members.di

import ytpconnect.rocket.android.members.ui.MembersFragment
import ytpconnect.rocket.android.dagger.scope.PerFragment
import ytpconnect.rocket.android.members.ui.MemberBottomSheetFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MembersFragmentProvider {

    @ContributesAndroidInjector(modules = [MembersFragmentModule::class])
    @PerFragment
    abstract fun provideMembersFragment(): MembersFragment

    @ContributesAndroidInjector()
    @PerFragment
    abstract fun provideMemberBottomSheetFragment(): MemberBottomSheetFragment

}