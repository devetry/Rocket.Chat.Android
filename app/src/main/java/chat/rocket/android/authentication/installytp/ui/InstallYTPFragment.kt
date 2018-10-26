package chat.rocket.android.authentication.installytp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import chat.rocket.android.R
import kotlinx.android.synthetic.main.fragment_install_young_thinker.*

fun newInstance() = InstallYTPFragment()

class InstallYTPFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_install_young_thinker, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        installButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + "com.devetry.ytp"))
            startActivity(intent)
        }
    }

}