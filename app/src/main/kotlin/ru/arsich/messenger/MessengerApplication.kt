package ru.arsich.messenger

import com.vk.sdk.VKSdk
import android.app.Application
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKAccessTokenTracker
import ru.arsich.messenger.ui.activities.WelcomeActivity

class MessengerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        vkAccessTokenTracker.startTracking()
        VKSdk.initialize(applicationContext)
    }

    private val vkAccessTokenTracker = object: VKAccessTokenTracker() {
        override fun onVKAccessTokenChanged(oldToken: VKAccessToken?, newToken: VKAccessToken?) {
            if (newToken == null) {
                WelcomeActivity.startFromExpirationSession(this@MessengerApplication)
            }
        }
    }

}