package ru.arsich.messenger.vk

import android.app.Activity
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKScope
import com.vk.sdk.VKSdk


class AuthManager {
    companion object {
        fun isAuthorized():Boolean {
            return VKSdk.isLoggedIn()
                    && VKAccessToken.currentToken() != null
                    && !VKAccessToken.currentToken().isExpired
        }

        private val scope = arrayOf(VKScope.FRIENDS, VKScope.MESSAGES)

        fun login(activity: Activity) {
            VKSdk.login(activity, *scope)
        }
    }
}