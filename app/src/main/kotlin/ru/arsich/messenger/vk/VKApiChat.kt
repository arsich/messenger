package ru.arsich.messenger.vk

import com.vk.sdk.api.VKParameters
import com.vk.sdk.api.VKParser
import com.vk.sdk.api.VKRequest
import com.vk.sdk.api.model.VKList
import org.json.JSONObject


object VKApiChat {
    fun get():VKRequest {
        val request = VKRequest("execute", VKParameters.from("code", code))
        request.setResponseParser(parser)
        return request
    }

    private val parser = object: VKParser() {
        override fun createModel(json: JSONObject?): VKList<VKChat> = VKList(json, VKChat::class.java)
    }

    private val code = """
        var chats = API.messages.getChat({"chat_ids": API.messages.getDialogs({"preview_length":50, "count":100}).items@.message@.chat_id, "fields": "photo_100"});
        var messages = API.messages.getDialogs({"preview_length":50, "count":100}).items@.message;

        var b = 0;
        var messagesLength = messages.length;
        var messagesProcessed = [];
        while (b < messagesLength) {
            var info = messages[b];
            if (info.chat_id) {
                messagesProcessed.push({"date": info.date, "body": info.body, "id": info.chat_id});
            }
            b = b + 1;
        }

        var a = 0;
        var chatsLength = chats.length;
        var messagesProcessedLength = messagesProcessed.length;

        var result = [];
        while (a < chatsLength) {
            if (chats[a].kicked < 1 && chats[a].left < 1) {
                var b = 0;
                var messageInfo = {};
                while (b < messagesProcessedLength) {
                    if (chats[a].id == messagesProcessed[b].id) {
                        messageInfo = messagesProcessed[b];
                        b = messagesProcessedLength;
                    }
                    b = b + 1;
                }
                var resultItem = chats[a] + messageInfo;
                result.push(resultItem);
            }
            a = a + 1;
        }

        return result;
    """.trimIndent()
}