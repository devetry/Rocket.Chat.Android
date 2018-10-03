package ytpconnect.rocket.android.main.presentation

import ytpconnect.rocket.android.authentication.server.presentation.VersionCheckView
import ytpconnect.rocket.android.core.behaviours.MessageView
import ytpconnect.rocket.android.main.uimodel.NavHeaderUiModel
import ytpconnect.rocket.android.server.domain.model.Account
import ytpconnect.rocket.common.model.UserStatus

interface MainView : MessageView, VersionCheckView {

    /**
     * Shows the current user status.
     *
     * @see [UserStatus]
     */
    fun showUserStatus(userStatus: UserStatus)

    /**
     * Setups the user account info (displayed in the nav. header)
     *
     * @param uiModel The [NavHeaderUiModel].
     */
    fun setupUserAccountInfo(uiModel: NavHeaderUiModel)

    /**
     * Setups the server account list.
     *
     * @param serverAccountList The list of server accounts.
     */
    fun setupServerAccountList(serverAccountList: List<Account>)

    fun closeServerSelection()

    fun invalidateToken(token: String)

    fun showProgress()

    fun hideProgress()
}