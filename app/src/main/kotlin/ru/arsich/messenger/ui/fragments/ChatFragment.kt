package ru.arsich.messenger.ui.fragments

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vk.sdk.api.model.VKApiMessage
import kotlinx.android.synthetic.main.fragment_chat.*
import ru.arsich.messenger.R
import ru.arsich.messenger.mvp.models.ChatRepository
import ru.arsich.messenger.mvp.presenters.ChatPresenter
import ru.arsich.messenger.mvp.views.ChatView
import ru.arsich.messenger.ui.adapters.ChatAdapter
import ru.arsich.messenger.ui.views.ChatItemDecoration
import ru.arsich.messenger.vk.VKChat


class ChatFragment : Fragment(), ChatView {
    companion object Factory {
        private val VK_CHAT_EXTRA = "vkChat"

        fun create(vkChat: VKChat): ChatFragment {
            val instance = ChatFragment()

            val bundle = Bundle()
            bundle.putParcelable(VK_CHAT_EXTRA, vkChat)
            instance.arguments = bundle

            return instance
        }
    }

    private var presenter: ChatPresenter? = null

    private var loadingNewMessages: Boolean = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loader.indeterminateDrawable.setColorFilter(ContextCompat.getColor(context, android.R.color.white),
                android.graphics.PorterDuff.Mode.SRC_IN)

        val vkChat = arguments.getParcelable<VKChat>(VK_CHAT_EXTRA)

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        messagesList.layoutManager = linearLayoutManager

        val dividerSize = resources.getDimensionPixelSize(R.dimen.chat_divider_big)
        val dividerSizeSmall = resources.getDimensionPixelSize(R.dimen.chat_divider_small)
        val chatItemDecoration = ChatItemDecoration(dividerSize, dividerSizeSmall)
        messagesList.addItemDecoration(chatItemDecoration)

        messagesList.adapter = ChatAdapter(vkChat)

        presenter = ChatPresenter(this, vkChat)
        presenter?.start()

        messagesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerView?.post({
                    val visibleThreshold = ChatRepository.CHAT_PAGE_SIZE / 2
                    val totalItemCount = linearLayoutManager.itemCount
                    val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                    if (!loadingNewMessages && totalItemCount <= (lastVisibleItem + visibleThreshold)
                            && totalItemCount >= ChatRepository.CHAT_PAGE_SIZE) {
                        presenter?.loadNextMessages(totalItemCount)
                    }
                })
            }
        })
    }

    override fun onDetach() {
        super.onDetach()
        presenter?.close()
    }

    override fun showMessages(messages: List<VKApiMessage>) {
        loader.visibility = View.GONE
        (messagesList.adapter as ChatAdapter).addMessages(messages)
    }

    override fun showError(error: Exception) {
        loader.visibility = View.GONE

        error.message?.let {
            Snackbar.make(rootContainer, it, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun showListProgress() {
        loadingNewMessages = true
        (messagesList.adapter as ChatAdapter).showListProgress()
    }

    override fun hideListProgress() {
        loadingNewMessages = false
        (messagesList.adapter as ChatAdapter).hideListProgress()
    }
}