package ru.arsich.messenger.ui.adapters

import android.graphics.Bitmap
import android.graphics.Rect
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.vk.sdk.api.model.VKApiMessage
import com.vk.sdk.api.model.VKApiPhoto
import kotlinx.android.synthetic.main.list_chat_item.view.*
import ru.arsich.messenger.R
import ru.arsich.messenger.utils.CommonUtils
import ru.arsich.messenger.utils.images.*
import ru.arsich.messenger.vk.AuthManager
import ru.arsich.messenger.vk.VKChat
import java.util.*


class ChatAdapter(private val vkChat: VKChat): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val messages: MutableList<VKApiMessage> = ArrayList()
    private var showProgress: Boolean = false

    private val VIEW_ITEM = 1
    private val VIEW_PROGRESS = 0

    private var currentLocale: Locale? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        if (currentLocale == null && parent != null) {
            currentLocale = CommonUtils.getLocale(parent.context)
        }

        var vh: RecyclerView.ViewHolder? = null
        if (viewType == VIEW_ITEM) {
            vh = MessageViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_chat_item, parent, false))
        } else if (viewType == VIEW_PROGRESS) {
            vh = ProgressViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_chat_progress_item, parent, false))
        }
        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is MessageViewHolder) {
            holder.bindMessage(messages[position], vkChat, isLastPosition(position), currentLocale)
        }
    }

    fun isLastPosition(position: Int): Boolean {
        var isLast = true
        if (messages.size > position + 1 && position >= 0) {
            isLast = messages[position].user_id != messages[position + 1].user_id
        }
        return isLast
    }

    fun showListProgress() {
        if (!showProgress) {
            showProgress = true
            notifyItemInserted(itemCount)
        }
    }

    fun hideListProgress() {
        if (showProgress) {
            showProgress = false
            notifyItemRemoved(itemCount)
        }
    }

    fun addMessages(newTraders: List<VKApiMessage>) {
        val lastPosition = itemCount
        messages.addAll(newTraders)
        notifyItemRangeInserted(lastPosition, itemCount - lastPosition)
    }

    override fun getItemCount(): Int {
        var lastElement = 0
        if (showProgress) {
            lastElement ++
        }
        return messages.size + lastElement
    }

    override fun getItemViewType(position: Int): Int {
        if (position == messages.size && showProgress) {
            return VIEW_PROGRESS
        } else {
            return VIEW_ITEM
        }
    }

    class ProgressViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            val loader = view.findViewById<ProgressBar>(R.id.loader)
            loader.indeterminateDrawable.setColorFilter(ContextCompat.getColor(view.context, android.R.color.white),
                    android.graphics.PorterDuff.Mode.SRC_IN)
        }
    }

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view), SingleImageReceiver, MultiImageReceiver {
        private var lastImageLoader: ImageLoader? = null
        private var attachmentsLoader: ImageLoader? = null

        fun bindMessage(message: VKApiMessage, chat: VKChat, lastMessageFromUser: Boolean, locale: Locale?) {
            val isMine = message.user_id == AuthManager.getUserId()

            itemView.messageView.clearAvatar()
            itemView.messageView.clearAttachments()

            itemView.messageView.setIsIncomingMessage(!isMine)
            itemView.messageView.setIsLastMessage(lastMessageFromUser)
            if (message.body.isNotEmpty()) {
                itemView.messageView.setMessageText(message.body)
            } else {
                itemView.messageView.setMessageText(CommonUtils.getMessageAttachmentName(message, itemView.context))
            }
            itemView.messageView.setDateText(CommonUtils.getFormattedDate(message.date.toInt(), locale))

            handleAttachments(message)

            itemView.messageView.requestLayout()
            itemView.messageView.invalidate()

            lastImageLoader?.interrupt()
            chat.getAvatarByUserId(message.user_id)?.let {
                lastImageLoader = SingleImageLoader(it, this)
                lastImageLoader?.load(itemView.context.applicationContext)
            }
        }

        private fun handleAttachments(message: VKApiMessage) {
            attachmentsLoader?.interrupt()
            if (message.attachments.size == 0) {
                return
            }

            val rects: MutableList<Rect> = mutableListOf()
            val urls: MutableList<String> = mutableListOf()

            val MAX_PHOTO_SIZE = 604

            message.attachments.forEach {
                if (it is VKApiPhoto) {
                    if (it.width >= it.height) {
                        val newHeight = (MAX_PHOTO_SIZE / it.width.toFloat() * it.height).toInt()
                        rects.add(Rect(0,0, MAX_PHOTO_SIZE, newHeight))
                    } else {
                        val newWidth = (MAX_PHOTO_SIZE / it.height.toFloat() * it.width).toInt()
                        rects.add(Rect(0,0, newWidth, MAX_PHOTO_SIZE))
                    }
                    urls.add(it.photo_604)
                }
            }

            if (rects.size > 0) {
                itemView.messageView.addAttachmentsRects(rects)
                attachmentsLoader = MultiImageLoader(urls.toTypedArray(), this)
                attachmentsLoader?.load(itemView.context.applicationContext)
            }
        }

        override fun onImageReceive(bitmap: Bitmap, url: String) {
            itemView.messageView.addAvatar(bitmap)
            lastImageLoader = null
        }

        override fun onImagesReceive(bitmaps: List<Bitmap>) {
            itemView.messageView.addAttachmentsBitmaps(bitmaps)
            attachmentsLoader = null
        }
    }
}