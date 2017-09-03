package ru.arsich.messenger.ui.adapters

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_dialogs_item.view.*
import ru.arsich.messenger.R
import ru.arsich.messenger.ui.activities.ChatActivity
import ru.arsich.messenger.ui.views.MultiImageView
import ru.arsich.messenger.utils.CommonUtils
import ru.arsich.messenger.utils.images.ImageLoader
import ru.arsich.messenger.utils.images.MultiImageReceiver
import ru.arsich.messenger.utils.images.MultiImageLoader
import ru.arsich.messenger.vk.VKChat
import java.util.*


class DialogsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var currentLocale: Locale? = null

    private var dialogs: MutableList<VKChat> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        if (currentLocale == null && parent != null) {
            currentLocale = CommonUtils.getLocale(parent.context)
        }
        return DialogViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_dialogs_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is DialogViewHolder) {
            holder.bindDialog(dialogs[position], currentLocale)
        }
    }

    fun addDialogs(newDialogs: List<VKChat>) {
        if (dialogs.isEmpty()) {
            dialogs.addAll(newDialogs)
            notifyItemRangeInserted(0, newDialogs.size - 1)
        } else {
            // add new items
            val firstDialog = dialogs[0]
            var newElementIndex = 0
            while (newElementIndex < newDialogs.size && newDialogs[newElementIndex].date > firstDialog.date) {
                val newDialog = newDialogs[newElementIndex]
                var dialogJustChanged = false
                val previousDialog = dialogs.find { it.id == newDialog.id }
                if (previousDialog != null) {
                    // remove old dialog
                    val oldIndex = dialogs.indexOf(previousDialog)
                    dialogs.removeAt(oldIndex)
                    if (oldIndex == newElementIndex) {
                        dialogJustChanged = true
                    } else {
                        notifyItemRemoved(oldIndex)
                    }
                }

                // new dialogs
                dialogs.add(newElementIndex, newDialog)
                if (dialogJustChanged) {
                    notifyItemChanged(newElementIndex)
                } else {
                    notifyItemInserted(newElementIndex)
                }
                newElementIndex++
            }
        }
    }

    override fun getItemCount(): Int = dialogs.size

    class DialogViewHolder(view: View) : RecyclerView.ViewHolder(view), MultiImageReceiver {
        init {
            itemView.setOnClickListener {
                lastDialog?.let {
                    ChatActivity.startWith(itemView.context, it)
                }
            }
        }

        private var lastDialog: VKChat? = null

        private var lastImageLoader: ImageLoader? = null

        fun bindDialog(dialog: VKChat, locale: Locale?) {
            lastDialog = dialog

            itemView.titleView.text = dialog.title
            itemView.messageView.text = dialog.body
            val date = CommonUtils.getFormattedDate(dialog.date, locale)
            itemView.dialogContainer.setDateText(date)

            itemView.avatarView.shape = MultiImageView.Shape.CIRCLE
            itemView.avatarView.dividerWidth = itemView.context.resources.getDimension(R.dimen.avatar_divider)

            lastImageLoader?.interrupt()
            lastImageLoader = MultiImageLoader(dialog.getAvatars(), this)
            lastImageLoader?.load(itemView.context.applicationContext)
        }

        override fun onImagesReceive(bitmaps: List<Bitmap>) {
            itemView.avatarView.addImages(bitmaps)
            lastImageLoader = null
        }
    }
}