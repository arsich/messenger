package ru.arsich.messenger.ui.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.toolbar_chat.*
import ru.arsich.messenger.R
import ru.arsich.messenger.ui.views.CenterCropDrawable
import ru.arsich.messenger.ui.views.MultiImageView
import ru.arsich.messenger.utils.ImageReceiver
import ru.arsich.messenger.utils.MultiImageLoader
import ru.arsich.messenger.vk.VKChat


class ChatActivity: AppCompatActivity(), ImageReceiver {
    companion object {
        val EXTRA_VK_CHAT = "vkChat"

        fun startWith(context: Context, vkChat: VKChat) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(EXTRA_VK_CHAT, vkChat)
            context.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(CenterCropDrawable(resources.getDrawable(R.drawable.chat_background)))
        setContentView(R.layout.activity_chat)

        val vkChat = intent.getParcelableExtra<VKChat>(EXTRA_VK_CHAT)

        val loader = MultiImageLoader(vkChat.getAvatars(), this)
        loader.load(applicationContext)

        setupToolbar(vkChat.title, vkChat.users.size)
    }

    override fun onReceive(bitmaps: List<Bitmap>) {
        photoView.shape = MultiImageView.Shape.CIRCLE
        photoView.dividerWidth = resources.getDimension(R.dimen.avatar_divider)
        photoView.addImages(bitmaps)
    }

    private fun setupToolbar(title: String, membersNumber: Int) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        titleView.text = title
        subtitleView.text = resources.getQuantityString(R.plurals.members_count, membersNumber, membersNumber)
    }
}