package chat.rocket.android.authentication.login.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.view.ViewTreeObserver
import android.widget.*

import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isGone

import chat.rocket.android.authentication.loginoptions.presentation.LoginOptionsPresenter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import chat.rocket.android.R
import chat.rocket.android.analytics.AnalyticsManager
import chat.rocket.android.analytics.event.AuthenticationEvent
import chat.rocket.android.analytics.event.ScreenViewEvent
import chat.rocket.android.authentication.domain.model.LoginDeepLinkInfo
import chat.rocket.android.authentication.login.presentation.LoginPresenter
import chat.rocket.android.authentication.login.presentation.LoginView
//import chat.rocket.android.authentication.loginoptions.presentation.
import chat.rocket.android.authentication.loginoptions.ui.REQUEST_CODE_FOR_CAS
import chat.rocket.android.authentication.loginoptions.ui.REQUEST_CODE_FOR_OAUTH
import chat.rocket.android.authentication.loginoptions.ui.REQUEST_CODE_FOR_SAML

import chat.rocket.android.authentication.ui.YTPOAuth
import chat.rocket.android.helper.KeyboardHelper
import chat.rocket.android.helper.TextHelper

import chat.rocket.android.authentication.ui.AuthenticationActivity

import chat.rocket.android.helper.getCredentials
import chat.rocket.android.helper.hasCredentialsSupport
import chat.rocket.android.helper.requestStoredCredentials
import chat.rocket.android.helper.saveCredentials
import chat.rocket.android.util.extension.asObservable
import chat.rocket.android.util.extensions.clearLightStatusBar
import chat.rocket.android.util.extensions.inflate
import chat.rocket.android.util.extensions.showToast
import chat.rocket.android.util.extensions.textContent
import chat.rocket.android.util.extensions.ui

//import chat.rocket.android.util.extensions.vibrateSmartPhone
import chat.rocket.android.webview.oauth.ui.*
import chat.rocket.android.webview.sso.ui.INTENT_SSO_TOKEN
import chat.rocket.android.webview.sso.ui.ssoWebViewIntent
import chat.rocket.common.util.ifNull

import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_authentication_log_in.*
import javax.inject.Inject

private const val SERVER_NAME = "server_name"

internal const val REQUEST_CODE_FOR_SIGN_IN_REQUIRED = 1
internal const val REQUEST_CODE_FOR_MULTIPLE_ACCOUNTS_RESOLUTION = 2
internal const val REQUEST_CODE_FOR_SAVE_RESOLUTION = 3

fun newInstance(serverName: String): Fragment = LoginFragment().apply {
    arguments = Bundle(1).apply {
        putString(SERVER_NAME, serverName)
    }
}

class LoginFragment : Fragment(), LoginView {
    @Inject
    lateinit var presenter: LoginPresenter
    @Inject
    lateinit var analyticsManager: AnalyticsManager


    private var serverName: String? = null
    private val editTextsDisposable = CompositeDisposable()

//    private var isOauthViewEnable = false
//    private val layoutListener = ViewTreeObserver.OnGlobalLayoutListener {
//        areLoginOptionsNeeded()
//    }
//    private var isGlobalLayoutListenerSetUp = false
//    private var deepLinkInfo: LoginDeepLinkInfo? = null

    companion object {
        const val ARG_YTP_OAUTH = "ytpOAuth"
        fun newInstance() = LoginFragment()

        fun newInstance(ytpOAuth: YTPOAuth) : LoginFragment {
            val fragment = LoginFragment()
            fragment.arguments = Bundle().apply { putSerializable(ARG_YTP_OAUTH, ytpOAuth) }
            return fragment
        }

        private const val DEEP_LINK_INFO = "DeepLinkInfo"

        fun newInstance(deepLinkInfo: LoginDeepLinkInfo? = null) = LoginFragment().apply {
            arguments = Bundle().apply {
                putParcelable(DEEP_LINK_INFO, deepLinkInfo)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)

// CONFLICT: HEAD
//        val ytpAuthData = (arguments?.getSerializable(ARG_YTP_OAUTH) as YTPOAuth)
//
//        startActivityForResult(activity?.ytpoauthWebViewIntent(ytpAuthData), REQUEST_CODE_FOR_OAUTH)
//
//        val bundle = arguments
//        if (bundle != null) {
//            serverName = bundle.getString(SERVER_NAME)
// CONFLICT: MERGE
        arguments?.run {
            serverName = getString(SERVER_NAME)
// CONFLICT: END
        }

    }

    fun Context.makeToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = container?.inflate(R.layout.fragment_authentication_log_in)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        presenter.setupView()
        subscribeEditTexts()
        setupOnClickListener()
        analyticsManager.logScreenView(ScreenViewEvent.Login)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
// CONFLICT: HEAD
//        if (resultCode == Activity.RESULT_OK && data != null) {
//            when (requestCode) {
//                REQUEST_CODE_FOR_OAUTH -> {
//                    presenter.authenticateWithOauth(
//                            data.getStringExtra(INTENT_OAUTH_CREDENTIAL_TOKEN),
//                            data.getStringExtra(INTENT_OAUTH_CREDENTIAL_SECRET)
//                    )
// CONFLICT: MERGE
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                when (requestCode) {
                    REQUEST_CODE_FOR_MULTIPLE_ACCOUNTS_RESOLUTION ->
                        getCredentials(data)?.let {
                            onCredentialRetrieved(it.first, it.second)
                        }
                    REQUEST_CODE_FOR_SIGN_IN_REQUIRED ->
                        getCredentials(data)?.let { credential ->
                            text_username_or_email.setText(credential.first)
                            text_password.setText(credential.second)
                        }
                    REQUEST_CODE_FOR_SAVE_RESOLUTION -> showMessage(getString(R.string.msg_credentials_saved_successfully))
// CONFLICT: END
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasCredentialsSupport()) {
            // NOTE: Disabling the GLS feature for now. Needs to improve its behaviour on the app.
//            requestStoredCredentials()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unsubscribeEditTexts()
    }

    private fun setupToolbar() {
        with(activity as AuthenticationActivity) {
            this.clearLightStatusBar()
            toolbar.isVisible = true
            toolbar.title = serverName?.replace(getString(R.string.default_protocol), "")
        }
    }


    override fun showLoading() {
        ui {
            disableUserInput()
            view_loading.isVisible = true
        }
    }

    override fun hideLoading() {
        ui {
            view_loading.isVisible = false
            enableUserInput()
        }
    }

    override fun showMessage(resId: Int) {
        ui {
            showToast(resId)
        }
    }

    override fun showMessage(message: String) {
        ui {
            showToast(message)
        }
    }

    override fun showGenericErrorMessage() = showMessage(R.string.msg_generic_error)

    private fun setupOnClickListener() =
        ui {
            button_log_in.setOnClickListener {
                presenter.authenticateWithUserAndPassword(
                    text_username_or_email.textContent,
                    text_password.textContent
                )
            }
        }

    override fun showForgotPasswordView() {
// CONFLICT: HEAD
//        ui { _ ->
//            button_forgot_your_password.isGone = true
// CONFLICT: MERGE
        ui {
            button_forgot_your_password.isVisible = true
// CONFLICT: END
            button_forgot_your_password.setOnClickListener { presenter.forgotPassword() }

        }
    }

    override fun enableButtonLogin() {
        context?.let {
            ViewCompat.setBackgroundTintList(
                button_log_in, ContextCompat.getColorStateList(it, R.color.colorAccent)
            )
            button_log_in.isEnabled = true
        }

    }

    override fun disableButtonLogin() {
        context?.let {
            ViewCompat.setBackgroundTintList(
                button_log_in,
                ContextCompat.getColorStateList(it, R.color.colorAuthenticationButtonDisabled)
            )
            button_log_in.isEnabled = false
        }
    }

    override fun enableButtonForgetPassword() {
        context?.let {
            button_forgot_your_password.isEnabled = false
            button_forgot_your_password.setTextColor(
                ContextCompat.getColorStateList(it, R.color.colorAccent)
            )
        }
    }

    override fun disableButtonForgetPassword() {
        context?.let {
            button_forgot_your_password.isEnabled = false
            button_forgot_your_password.setTextColor(
                ContextCompat.getColorStateList(it, R.color.colorAuthenticationButtonDisabled)
            )
        }
    }

    private fun requestStoredCredentials() {
        activity?.requestStoredCredentials()?.let { credentials ->
            onCredentialRetrieved(credentials.first, credentials.second)
        }
    }

    private fun onCredentialRetrieved(id: String, password: String) {
        presenter.authenticateWithUserAndPassword(id, password)
    }

    override fun saveSmartLockCredentials(id: String, password: String) {
        activity?.saveCredentials(id, password)
    }

    private fun subscribeEditTexts() {
        editTextsDisposable.add(
            Observables.combineLatest(
                text_username_or_email.asObservable(),
                text_password.asObservable()
            ) { text_username_or_email, text_password ->
                return@combineLatest (
                        text_username_or_email.isNotBlank() && text_password.isNotBlank()
                        )
            }.subscribe { isValid ->
                if (isValid) {
                    enableButtonLogin()
                } else {
                    disableButtonLogin()
                }
            })
    }

    private fun unsubscribeEditTexts() = editTextsDisposable.clear()

    private fun enableUserInput() {
        ui {
            enableButtonLogin()
            enableButtonForgetPassword()
            text_username_or_email.isEnabled = true
            text_password.isEnabled = true
        }
    }

    private fun disableUserInput() {
        ui {
            disableButtonLogin()
            disableButtonForgetPassword()
            text_username_or_email.isEnabled = false
            text_password.isEnabled = false
        }
    }
}
