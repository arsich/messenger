package ru.arsich.messenger.ui.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_welcome.*
import ru.arsich.messenger.R
import ru.arsich.messenger.mvp.presenters.WelcomePresenter
import ru.arsich.messenger.mvp.views.WelcomeView
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKError
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback

class WelcomeActivity : AppCompatActivity(), WelcomeView {

    companion object {
        fun startFromExpirationSession(context: Context) {
            val intent = Intent(context, WelcomeActivity::class.java)
            intent.flags - Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
    }

    private lateinit var presenter: WelcomePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        loginBtn.setOnClickListener {
            presenter.login(this)
        }

        presenter = WelcomePresenter(this)
        presenter.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKCallback<VKAccessToken> {
            override fun onResult(res: VKAccessToken) {
                // User passed Authorization
                presenter.start()
            }

            override fun onError(error: VKError) {
                // User didn't pass Authorization
            }
        }

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun showDialogs() {
        DialogsActivity.startFrom(this)
        finish()
    }
}
