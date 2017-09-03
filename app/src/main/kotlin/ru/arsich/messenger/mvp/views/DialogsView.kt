package ru.arsich.messenger.mvp.views

import ru.arsich.messenger.vk.VKChat


interface DialogsView {
    fun showDialogs(list: List<VKChat>)
    fun showError(error: Exception)

    fun showRefreshing()
    fun hideRefreshing()
}