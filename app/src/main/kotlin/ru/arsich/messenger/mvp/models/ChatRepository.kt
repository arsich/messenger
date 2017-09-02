package ru.arsich.messenger.mvp.models

import com.vk.sdk.api.VKError
import com.vk.sdk.api.VKRequest
import com.vk.sdk.api.VKResponse
import com.vk.sdk.api.model.VKApiGetMessagesResponse
import com.vk.sdk.api.model.VKApiMessage
import ru.arsich.messenger.vk.VKApiMessageHistory
import java.lang.Exception


class ChatRepository {
    companion object {
        val CHAT_PAGE_SIZE = 40
    }

    private val messagesSubscribers: MutableList<RequestMessagesListener> = mutableListOf()

    fun addMessagesSubscriber(subscriber: RequestMessagesListener) {
        messagesSubscribers.add(subscriber)
    }
    fun removeMessagesSubscriber(subscriber: RequestMessagesListener) {
        messagesSubscribers.remove(subscriber)
    }

    // cache only last selected chat, no reload after orientation change
    private var lastChatId: Int = -1
    private var lastMessages: MutableList<VKApiMessage> = ArrayList()

    fun requestMessages(chatId: Int, offset: Int = 0) {
        if (chatId != lastChatId) {
            // clear cache
            lastMessages.clear()
            lastChatId = chatId
        }

        if (offset < lastMessages.size) {
            // return date from cache
            sendMessagesToSubscribers(lastMessages.subList(offset, lastMessages.lastIndex))
            return
        }

        val startMessageId = -1

        val request = VKApiMessageHistory.get(chatId, offset, startMessageId, CHAT_PAGE_SIZE)
        request.attempts = 3
        request.executeWithListener(object: VKRequest.VKRequestListener() {
            override fun onComplete(response: VKResponse?) {
                if (response != null) {
                    val result = response.parsedModel as VKApiGetMessagesResponse

                    val list = result.items.toList()
                    lastMessages.addAll(list)
                    sendMessagesToSubscribers(list)
                }
            }

            override fun onError(error: VKError?) {
                error?.let {
                    sendErrorToSubscribers(error.httpError)
                }
            }
        })
    }

    private fun sendMessagesToSubscribers(messages: List<VKApiMessage>) {
        for (subscriber in messagesSubscribers) {
            subscriber.onMessagesReceived(messages)
        }
    }

    private fun sendErrorToSubscribers(error: Exception) {
        for (subscriber in messagesSubscribers) {
            subscriber.onMessagesError(error)
        }
    }

    interface RequestMessagesListener {
        fun onMessagesReceived(messages: List<VKApiMessage>)
        fun onMessagesError(error: Exception)
    }
}