package ru.arsich.messenger.vk

import com.vk.sdk.api.VKRequest
import com.vk.sdk.api.model.VKApiGetMessagesResponse
import com.vk.sdk.util.VKUtil

object VKApiMessageHistory {
    fun get(chatId: Int, offset: Int = 0, startMessageId: Int = -1, count: Int = 10): VKRequest {
        val params = VKUtil.paramsFrom("chat_id", chatId, "offset", offset, "start_message_id", startMessageId, "count", count)
        return VKRequest("messages.getHistory", params, VKApiGetMessagesResponse::class.java)
    }
}