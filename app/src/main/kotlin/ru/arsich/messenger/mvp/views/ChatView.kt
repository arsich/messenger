package ru.arsich.messenger.mvp.views

import com.vk.sdk.api.model.VKApiMessage


interface ChatView {
    fun showMessages(messages: List<VKApiMessage>)
    fun showError(error: Exception)

    fun showListProgress()
    fun hideListProgress()
}