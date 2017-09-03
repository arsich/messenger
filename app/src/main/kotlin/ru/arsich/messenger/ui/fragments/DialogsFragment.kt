package ru.arsich.messenger.ui.fragments

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_dialogs.*
import ru.arsich.messenger.R
import ru.arsich.messenger.mvp.presenters.DialogsPresenter
import ru.arsich.messenger.mvp.views.DialogsView
import ru.arsich.messenger.ui.adapters.DialogsAdapter
import ru.arsich.messenger.vk.VKChat


class DialogsFragment : Fragment(), DialogsView {
    companion object Factory {
        fun create(): DialogsFragment = DialogsFragment()
    }

    private var presenter: DialogsPresenter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_dialogs, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loader.indeterminateDrawable.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary),
                android.graphics.PorterDuff.Mode.SRC_IN)
        refreshLayout.setColorSchemeResources(R.color.colorPrimary)

        val linearLayoutManager = LinearLayoutManager(context)
        dialogsList.layoutManager = linearLayoutManager

        dialogsList.adapter = DialogsAdapter()

        presenter = DialogsPresenter(this)
        presenter?.start()

        refreshLayout.setOnRefreshListener {
            presenter?.refreshDialogs()
        }
    }

    override fun onDetach() {
        super.onDetach()
        presenter?.close()
    }

    override fun showDialogs(list: List<VKChat>) {
        loader.visibility = View.GONE
        (dialogsList.adapter as DialogsAdapter).addDialogs(list)
    }

    override fun showError(error: Exception) {
        loader.visibility = View.GONE

        error.message?.let {
            Snackbar.make(rootContainer, it, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun showRefreshing() {
        refreshLayout.isRefreshing = true
    }

    override fun hideRefreshing() {
        refreshLayout.isRefreshing = false
    }
}