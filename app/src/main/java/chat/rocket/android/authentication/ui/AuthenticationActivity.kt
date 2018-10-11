package chat.rocket.android.authentication.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import chat.rocket.android.R
import chat.rocket.android.authentication.domain.model.LoginDeepLinkInfo
import chat.rocket.android.authentication.domain.model.getLoginDeepLinkInfo
import chat.rocket.android.authentication.presentation.AuthenticationPresenter
import chat.rocket.android.authentication.server.ui.ServerFragment
import chat.rocket.android.authentication.server.ui.TAG_SERVER_FRAGMENT
import chat.rocket.android.util.extensions.addFragment
import chat.rocket.android.util.extensions.encodeToBase64
import chat.rocket.android.util.extensions.generateRandomString
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject
import java.io.Serializable

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

//        operator fun invoke(intent: Intent): YTPOAuth? = with(intent) {
//            val state = "{\"loginStyle\":\"popup\",\"credentialToken\":\"${generateRandomString(40)}\",\"isCordova\":true}".encodeToBase64()
//            YTPOAuth(chat_server = getStringExtra(INTENT_BASE_URL),
//                    drupal_idp = getStringExtra(INTENT_AUTH_URL) + state,
//                    session_cookie = getStringExtra(INTENT_SESSION_COOKIE),
//                    auth_cookie = getStringExtra(INTENT_AUTH_COOKIE),
//                    auth_user_cookie = getStringExtra(INTENT_AUTH_USER_COOKIE),
//                    state = state)
//        }

        operator fun invoke(intent: Intent): YTPOAuth? = with(intent) {
            val state = "{\"loginStyle\":\"popup\",\"credentialToken\":\"${generateRandomString(40)}\",\"isCordova\":true}".encodeToBase64()
            YTPOAuth(chat_server = "http://yt-portal.raccoongang.com:3000/",
                    drupal_idp = "http://yt-portal.raccoongang.com/en/oauth2/authorize?destination=oauth2/authorize&client_id=F9D848A32AA9C6552E1AB7F90C03B749FBF1300B&redirect_uri=http://yt-portal.raccoongang.com:3000/_oauth/drupal?close&response_type=code&scope=gender%20email%20openid%20profile%20offline_access&state=" + state,
                    session_cookie = "SESSbe80cad36eaa53a63ac1dcf71b5f6448=qcwezobiEFD7w7t3VJs0PUaNFenNWI5SzpMc1xZJJoY",
                    auth_cookie = "authenticated=1; expires=Thu, 10-Oct-2019 19:53:26 GMT; Max-Age=31536000; path=/; domain=raccoongang.com",
                    auth_user_cookie = "authenticated_user=TannerJuby; expires=Thu, 10-Oct-2019 19:53:26 GMT; Max-Age=31536000; path=/; domain=raccoongang.com",
                    state = state)
        }
    }
}

class AuthenticationActivity : AppCompatActivity(), HasSupportFragmentInjector {
    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var presenter: AuthenticationPresenter
    val job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_authentication)
        setTheme(R.style.AuthenticationTheme)
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        launch(UI + job) {
            YTPOAuth(intent)?.let {
                presenter.ytpAuth(it)
            }
        }
    }

    override fun onStop() {
        job.cancel()
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        currentFragment?.onActivityResult(requestCode, resultCode, data)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    fun showServerInput(deepLinkInfo: LoginDeepLinkInfo?) {
        addFragment(TAG_SERVER_FRAGMENT, R.id.fragment_container, allowStateLoss = true) {
            ServerFragment.newInstance(deepLinkInfo)
        }
    }
}

const val INTENT_ADD_NEW_SERVER = "INTENT_ADD_NEW_SERVER"

fun Context.newServerIntent(): Intent {
    return Intent(this, AuthenticationActivity::class.java).apply {
        putExtra(INTENT_ADD_NEW_SERVER, true)
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}