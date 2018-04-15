package chat.rocket.android.authentication.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import chat.rocket.android.R
import chat.rocket.android.authentication.presentation.AuthenticationPresenter
import chat.rocket.android.authentication.server.ui.ServerFragment
import chat.rocket.android.util.extensions.addFragment
import chat.rocket.android.util.extensions.launchUI
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.Serializable
import javax.inject.Inject

class YTPOAuth private constructor(var chat_server: String,
                                   var drupal_idp: String,
                                   var session_cookie: String,
                                   var state: String) : Serializable {

    companion object {
        const val INTENT_BASE_URL = "base_url"
        const val INTENT_AUTH_URL = "auth_url"
        const val INTENT_SESSION_COOKIE = "cookie"
        const val INTENT_STATE = "state"

        operator fun invoke(intent: Intent): YTPOAuth? =
                with(intent) {
                    YTPOAuth(chat_server = getStringExtra(INTENT_BASE_URL) ?: return null,
                            drupal_idp = getStringExtra(INTENT_AUTH_URL) ?: return null,
                            session_cookie = getStringExtra(INTENT_SESSION_COOKIE) ?: return null,
                            state = getStringExtra(INTENT_STATE) ?: return null)
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



        launch(UI + job) {

            YTPOAuth(intent)?.let { presenter.ytpAuth(it) }
//            val newServer = intent.getBooleanExtra(INTENT_ADD_NEW_SERVER, false)
//            presenter.loadCredentials(newServer) { authenticated ->
//                if (!authenticated) {
//                    showServerInput(savedInstanceState)
//                }
//            }
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    fun showServerInput(savedInstanceState: Bundle?) {
        addFragment("ServerFragment", R.id.fragment_container) {
            ServerFragment.newInstance()
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