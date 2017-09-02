package ru.arsich.messenger.mvp.presenters

import com.vk.sdk.api.model.VKApiMessage
import ru.arsich.messenger.mvp.models.ChatRepository
import ru.arsich.messenger.mvp.models.RepositoryInjector
import ru.arsich.messenger.mvp.views.ChatView
import ru.arsich.messenger.vk.VKChat
import java.lang.Exception


class ChatPresenter(private val chatView: ChatView, private val dialog: VKChat): BasePresenter, ChatRepository.RequestMessagesListener {

    private lateinit var chatRepository: ChatRepository

    private var offset = 0
    private var startMessageId = -1

    override fun start() {
        chatRepository = RepositoryInjector.provideChatRepository()
        chatRepository.addMessagesSubscriber(this)
        requestMessages()
    }

    override fun close() {
        chatRepository.removeMessagesSubscriber(this)
    }

    fun loadNextMessages(offset: Int) {
        this.offset = offset
        chatView.showListProgress()
        requestMessages()
    }

    private fun requestMessages() {
        chatRepository.requestMessages(dialog.id, offset)
    }

    override fun onMessagesReceived(messages: List<VKApiMessage>) {
        if (startMessageId < 0 && messages.isNotEmpty()) {
            startMessageId = messages[0].id
        }
        chatView.hideListProgress()
        chatView.showMessages(messages)
    }

    override fun onMessagesError(error: Exception) {
        chatView.showError(error)
    }
}