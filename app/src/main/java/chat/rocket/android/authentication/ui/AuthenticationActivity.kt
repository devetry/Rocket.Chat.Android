package chat.rocket.android.authentication.ui

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import chat.rocket.android.R
import chat.rocket.android.analytics.event.ScreenViewEvent
import chat.rocket.android.authentication.domain.model.LoginDeepLinkInfo
import chat.rocket.android.authentication.domain.model.getLoginDeepLinkInfo
import chat.rocket.android.authentication.presentation.AuthenticationPresenter
import chat.rocket.android.core.lifecycle.CancelStrategy
import chat.rocket.android.util.extension.launchUI
import chat.rocket.android.util.extensions.addFragment
import chat.rocket.android.util.extensions.encodeToBase64
import chat.rocket.android.util.extensions.generateRandomString
//import chat.rocket.android.util.extensions.setVisible
import chat.rocket.common.util.ifNull
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.app_bar.*
// CONFLICT: HEAD
//import kotlinx.coroutines.experimental.Job
//import kotlinx.coroutines.experimental.android.UI
//import kotlinx.coroutines.experimental.launch
// CONFLICT: MERGE
// CONFLICT: END
import javax.inject.Inject
import java.io.Serializable
import java.util.*

class YTPOAuth constructor(var chat_server: String,
                           var drupal_idp: String,
                           var session_cookie: String,
                           var auth_cookie: String,
                           var auth_user_cookie: String,
                           var state: String) : Serializable {
    companion object {
        const val INTENT_BASE_URL = "base_url"
        const val INTENT_AUTH_URL = "auth_url"
        const val INTENT_SESSION_COOKIE = "sessionCookie"
        const val INTENT_AUTH_COOKIE = "authCookie"
        const val INTENT_AUTH_USER_COOKIE = "authUserCookie"

        operator fun invoke(intent: Intent): YTPOAuth? = with(intent) {
            val state = "{\"loginStyle\":\"popup\",\"credentialToken\":\"${generateRandomString(40)}\",\"isCordova\":true}".encodeToBase64()
            YTPOAuth(chat_server = getStringExtra(INTENT_BASE_URL),
                    drupal_idp = getStringExtra(INTENT_AUTH_URL) + state,
                    session_cookie = getStringExtra(INTENT_SESSION_COOKIE),
                    auth_cookie = getStringExtra(INTENT_AUTH_COOKIE),
                    auth_user_cookie = getStringExtra(INTENT_AUTH_USER_COOKIE),
                    state = state)
        }

//        operator fun invoke(intent: Intent): YTPOAuth? = with(intent) {
//            val state = "{\"loginStyle\":\"popup\",\"credentialToken\":\"${generateRandomString(40)}\",\"isCordova\":true}".encodeToBase64()
//            YTPOAuth(chat_server = "http://yt-portal.raccoongang.com:3000/",
//                    drupal_idp = "http://yt-portal.raccoongang.com/ar/oauth2/authorize?destination=oauth2/authorize&client_id=F9D848A32AA9C6552E1AB7F90C03B749FBF1300B&redirect_uri=http://yt-portal.raccoongang.com:3000/_oauth/drupal?close&response_type=code&scope=gender%20email%20openid%20profile%20offline_access&state=" + state,
//                    session_cookie = "SESSbe80cad36eaa53a63ac1dcf71b5f6448=KBesgNR6QeGUmS_JtvRGfUz-kreg1BLgnpCKeE7KUiI; expires=Mon, 12-Nov-2018 20:51:09 GMT; Max-Age=2000000; path=/; domain=.yt-portal.raccoongang.com; HttpOnly",
//                    auth_cookie = "authenticated=1; expires=Sun, 20-Oct-2019 17:17:49 GMT; Max-Age=31536000; path=/; domain=raccoongang.com",
//                    auth_user_cookie = "authenticated_user=TannerJuby; expires=Sun, 20-Oct-2019 17:17:49 GMT; Max-Age=31536000; path=/; domain=raccoongang.com",
//                    state = state)
//        }

//        operator fun invoke(intent: Intent): YTPOAuth? = with(intent) {
//            val state = "{\"loginStyle\":\"popup\",\"credentialToken\":\"${generateRandomString(40)}\",\"isCordova\":true}".encodeToBase64()
//            YTPOAuth(chat_server = "https://chat.youngthinker.org",
//                    drupal_idp = "http://chat.youngthinker.org/en/oauth2/authorize?destination=oauth2/authorize&client_id=F9D848A32AA9C6552E1AB7F90C03B749FBF1300B&redirect_uri=http://chat.youngthinker.org/_oauth/drupal?close&response_type=code&scope=gender%20email%20openid%20profile%20offline_access&state=" + state,
//                    session_cookie = "SSESS5b2b596e3c8f92c08fa6d8668d02934b=0VEvzKJ8rWVcOx9QoluzRyCnhvA3O3vfFbU8bSrq9xU; expires=Mon, 12-Nov-2018 19:54:06 GMT; Max-Age=2000000; path=/; domain=.youngthinker.org; secure; HttpOnly",
//                    auth_cookie = "authenticated=1; expires=Sun, 20-Oct-2019 16:20:46 GMT; Max-Age=31536000; path=/; domain=youngthinker.org",
//                    auth_user_cookie = "authenticated_user=TannerJuby; expires=Sun, 20-Oct-2019 16:20:46 GMT; Max-Age=31536000; path=/; domain=youngthinker.org",
//                    state = state)
//        }
    }
}

// YTP UPDATE
// NEW:
class AuthenticationActivity @Inject constructor(
        private val strategy: CancelStrategy
) : AppCompatActivity(), HasSupportFragmentInjector {
// OLD:
//class AuthenticationActivity : AppCompatActivity(), HasSupportFragmentInjector {
// END
    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var presenter: AuthenticationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        setupToolbar()
//        loadCredentials()
    }

    override fun onStart() {
        super.onStart()

        if (intent.getStringExtra(YTPOAuth.INTENT_BASE_URL) == null ||
                intent.getStringExtra(YTPOAuth.INTENT_AUTH_URL) == null ||
                intent.getStringExtra(YTPOAuth.INTENT_SESSION_COOKIE) == null ||
                intent.getStringExtra(YTPOAuth.INTENT_AUTH_COOKIE) == null ||
                intent.getStringExtra(YTPOAuth.INTENT_AUTH_USER_COOKIE) == null) {

            val ytpIntent = Intent("com.devetry.ytp.START")

            if (ytpIntent.resolveActivity(packageManager) != null) {
                ytpIntent.putExtra("tab", "careers")
                ytpIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(ytpIntent)
                finish()
            } else {
                toolbar.visibility = View.INVISIBLE
                addFragment(
                        ScreenViewEvent.OnBoarding.screenName,
                        R.id.fragment_container,
                        allowStateLoss = true
                ) {
                    chat.rocket.android.authentication.installytp.ui.newInstance()
                }
            }

        } else {
            // YTP UPDATE
            // NEW:
            launchUI(strategy) {
                YTPOAuth(intent)?.let {
                    presenter.ytpAuth(it)
                }
            }
            // OLD:
//            launch(UI + job) {
//                YTPOAuth(intent)?.let {
//                    presenter.ytpAuth(it)
//                }
//            }
            // END
        }
    }

    private fun setupToolbar() {
        with(toolbar) {
            setSupportActionBar(this)
            if (Locale.getDefault().language == "ar") setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_ar)
            else setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
            setNavigationOnClickListener { onBackPressed() }
        }
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        currentFragment?.onActivityResult(requestCode, resultCode, data)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> =
        fragmentDispatchingAndroidInjector

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.legal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_terms_of_Service -> presenter.termsOfService(getString(R.string.action_terms_of_service))
            R.id.action_privacy_policy -> presenter.privacyPolicy(getString(R.string.action_privacy_policy))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadCredentials() {
        intent.getLoginDeepLinkInfo()?.let {
            // YTP UPDATE
            // NEW:
            launchUI(strategy) {
                YTPOAuth(intent)?.let {
                    presenter.ytpAuth(it)
                }
            }
            // OLD
//            launch(UI + job) {
//                YTPOAuth(intent)?.let {
//                    presenter.ytpAuth(it)
//                }
//            }
            // END
            showServerFragment(it)
        }.ifNull {
            val newServer = intent.getBooleanExtra(INTENT_ADD_NEW_SERVER, false)
            presenter.loadCredentials(newServer) { isAuthenticated ->
                if (isAuthenticated) {
                    showChatList()
                } else {
                    showOnBoardingFragment()
                }
            }
        }
    }

    private fun showOnBoardingFragment() {
        addFragment(
            ScreenViewEvent.OnBoarding.screenName,
            R.id.fragment_container,
            allowStateLoss = true
        ) {
            chat.rocket.android.authentication.onboarding.ui.newInstance()
        }
    }

    private fun showServerFragment(deepLinkInfo: LoginDeepLinkInfo) {
        addFragment(
            ScreenViewEvent.Server.screenName,
            R.id.fragment_container,
            allowStateLoss = true
        ) {
            chat.rocket.android.authentication.server.ui.newInstance()
        }
    }

    private fun showChatList() = presenter.toChatList()
}

const val INTENT_ADD_NEW_SERVER = "INTENT_ADD_NEW_SERVER"

fun Context.newServerIntent(): Intent {
    return Intent(this, AuthenticationActivity::class.java).apply {
        putExtra(INTENT_ADD_NEW_SERVER, true)
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}