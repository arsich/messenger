package ru.arsich.messenger.mvp.presenters

import android.app.Activity
import ru.arsich.messenger.mvp.views.WelcomeView
import ru.arsich.messenger.vk.AuthManager


class WelcomePresenter(private val view: WelcomeView): BasePresenter {
    override fun start() {
        if (AuthManager.isAuthorized()) {
            view.showDialogs()
        }
    }

    fun login(activity: Activity) {
        AuthManager.login(activity)
    }
}