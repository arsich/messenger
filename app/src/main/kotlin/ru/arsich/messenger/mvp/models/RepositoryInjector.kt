package ru.arsich.messenger.mvp.models


object RepositoryInjector {
    private val dialogsRepository by lazy {
        DialogsRepository()
    }

    fun provideDialogsRepository(): DialogsRepository {
        return dialogsRepository
    }
}