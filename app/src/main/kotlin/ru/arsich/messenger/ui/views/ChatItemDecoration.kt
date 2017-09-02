package ru.arsich.messenger.ui.views

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import ru.arsich.messenger.ui.adapters.ChatAdapter


class ChatItemDecoration(private val verticalSpaceHeight: Int, private val smallVerticalSpaceHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                       state: RecyclerView.State) {
        if ((parent.adapter as ChatAdapter).isLastPosition(parent.getChildAdapterPosition(view))) {
            outRect.top = verticalSpaceHeight
        } else {
            outRect.top = smallVerticalSpaceHeight
        }
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.bottom = verticalSpaceHeight
        }
    }
}