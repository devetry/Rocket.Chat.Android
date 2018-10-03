package ytpconnect.rocket.android.authentication.presentation

import android.content.Intent
import ytpconnect.rocket.android.R
import ytpconnect.rocket.android.authentication.domain.model.LoginDeepLinkInfo
import ytpconnect.rocket.android.authentication.login.ui.LoginFragment
import ytpconnect.rocket.android.authentication.login.ui.TAG_LOGIN_FRAGMENT
import ytpconnect.rocket.android.authentication.registerusername.ui.RegisterUsernameFragment
import ytpconnect.rocket.android.authentication.registerusername.ui.TAG_REGISTER_USERNAME_FRAGMENT
import ytpconnect.rocket.android.authentication.resetpassword.ui.ResetPasswordFragment
import ytpconnect.rocket.android.authentication.resetpassword.ui.TAG_RESET_PASSWORD_FRAGMENT
import ytpconnect.rocket.android.authentication.signup.ui.SignupFragment
import ytpconnect.rocket.android.authentication.signup.ui.TAG_SIGNUP_FRAGMENT
import ytpconnect.rocket.android.authentication.twofactor.ui.TAG_TWO_FA_FRAGMENT
import ytpconnect.rocket.android.authentication.twofactor.ui.TwoFAFragment
import ytpconnect.rocket.android.authentication.ui.AuthenticationActivity
import ytpconnect.rocket.android.authentication.ui.YTPOAuth
import ytpconnect.rocket.android.authentication.ui.newServerIntent
import ytpconnect.rocket.android.main.ui.MainActivity
import ytpconnect.rocket.android.server.ui.changeServerIntent
import ytpconnect.rocket.android.util.extensions.addFragmentBackStack
import ytpconnect.rocket.android.util.extensions.toPreviousView
import ytpconnect.rocket.android.webview.ui.webViewIntent

class AuthenticationNavigator(internal val activity: AuthenticationActivity) {
    fun toYTPLogin(ytpOAuth: YTPOAuth) {
        activity.addFragmentBackStack("LoginFragment", R.id.fragment_container) {
            LoginFragment.newInstance(ytpOAuth)
        }
    }
    fun toLogin() {
        activity.addFragmentBackStack(TAG_LOGIN_FRAGMENT, R.id.fragment_container) {
            LoginFragment.newInstance()
        }
    }

    fun toLogin(deepLinkInfo: LoginDeepLinkInfo) {
        activity.addFragmentBackStack(TAG_LOGIN_FRAGMENT, R.id.fragment_container) {
            LoginFragment.newInstance(deepLinkInfo)
        }
    }

    fun toPreviousView() {
        activity.toPreviousView()
    }

    fun toTwoFA(username: String, password: String) {
        activity.addFragmentBackStack(TAG_TWO_FA_FRAGMENT, R.id.fragment_container) {
            TwoFAFragment.newInstance(username, password)
        }
    }

    fun toSignUp() {
        activity.addFragmentBackStack(TAG_SIGNUP_FRAGMENT, R.id.fragment_container) {
            SignupFragment.newInstance()
        }
    }

    fun toForgotPassword() {
        activity.addFragmentBackStack(TAG_RESET_PASSWORD_FRAGMENT, R.id.fragment_container) {
            ResetPasswordFragment.newInstance()
        }
    }

    fun toWebPage(url: String) {
        activity.startActivity(activity.webViewIntent(url))
        activity.overridePendingTransition(R.anim.slide_up, R.anim.hold)
    }

    fun toRegisterUsername(userId: String, authToken: String) {
        activity.addFragmentBackStack(TAG_REGISTER_USERNAME_FRAGMENT, R.id.fragment_container) {
            RegisterUsernameFragment.newInstance(userId, authToken)
        }
    }

    fun toChatList() {
        activity.startActivity(Intent(activity, MainActivity::class.java))
        activity.finish()
    }

    fun toChatList(serverUrl: String) {
        activity.startActivity(activity.changeServerIntent(serverUrl))
        activity.finish()
    }

    fun toServerScreen() {
        activity.startActivity(activity.newServerIntent())
        activity.finish()
    }
}