package ru.arsich.messenger.mvp.models

import com.vk.sdk.api.VKError
import com.vk.sdk.api.VKRequest
import com.vk.sdk.api.VKResponse
import com.vk.sdk.api.model.VKList
import ru.arsich.messenger.vk.VKApiChat
import ru.arsich.messenger.vk.VKChat
import java.lang.Exception


class DialogsRepository {

    private val dialogsSubscribers: MutableList<RequestDialogsListener> = mutableListOf()

    private var dialogsRequest: VKRequest? = null

    private var dialogsList: List<VKChat> = listOf()

    fun addDialogsSubscriber(subscriber: RequestDialogsListener) {
        dialogsSubscribers.add(subscriber)
    }
    fun removeDialogsSubscriber(subscriber: RequestDialogsListener) {
        dialogsSubscribers.remove(subscriber)
    }

    fun requestDialogs() {
        if (dialogsList.isNotEmpty()) {
            sendDialogsListToSubscribers()
            return
        }
        if (dialogsRequest != null) {
            return
        }

        dialogsRequest = VKApiChat.get()
        dialogsRequest?.attempts = 3
        dialogsRequest?.executeWithListener(object: VKRequest.VKRequestListener() {
            override fun onComplete(response: VKResponse?) {
                if (response != null) {
                    dialogsList = (response.parsedModel as VKList<VKChat>).toList()
                    sendDialogsListToSubscribers()
                }
                dialogsRequest = null
            }

            override fun onError(error: VKError?) {
                error?.let {
                    if (it.httpError != null) {
                        sendErrorToDialogsSubscribers(it.httpError)
                    } else if (it.apiError != null) {
                        sendErrorToDialogsSubscribers(Exception(it.apiError.errorMessage))
                    } else {
                        sendErrorToDialogsSubscribers(Exception(it.errorMessage))
                    }
                }
                dialogsRequest = null
            }
        })
    }

    fun clearCache() {
        dialogsList = listOf()
    }

    private fun sendDialogsListToSubscribers() {
        for (subscriber in dialogsSubscribers) {
            subscriber.onDialogsReceived(dialogsList)
        }
    }

    private fun sendErrorToDialogsSubscribers(error: Exception) {
        for (subscriber in dialogsSubscribers) {
            subscriber.onDialogsError(error)
        }
    }

    interface RequestDialogsListener {
        fun onDialogsReceived(list: List<VKChat>)
        fun onDialogsError(error: Exception)
    }
}