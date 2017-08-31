package ru.arsich.messenger.mvp.presenters

import ru.arsich.messenger.mvp.models.DialogsRepository
import ru.arsich.messenger.mvp.models.RepositoryInjector
import ru.arsich.messenger.mvp.views.DialogsView
import ru.arsich.messenger.vk.VKChat
import java.lang.Exception

class DialogsPresenter(private val view: DialogsView): BasePresenter, DialogsRepository.RequestDialogsListener {
    private lateinit var repository: DialogsRepository

    override fun start() {
        repository = RepositoryInjector.provideDialogsRepository()
        repository.addDialogsSubscriber(this)
        repository.requestChats()
    }

    fun close() {
        repository.removeDialogsSubscriber(this)
    }

    override fun onDialogsReceived(list: List<VKChat>) {
        view.showDialogs(list)
    }

    override fun onDialogsError(error: Exception) {
        view.showError(error)
    }
}