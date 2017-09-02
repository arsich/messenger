package ru.arsich.messenger.mvp.models


object RepositoryInjector {
    private val dialogsRepository by lazy {
        DialogsRepository()
    }

    private val chatRepository by lazy {
        ChatRepository()
    }

    fun provideDialogsRepository(): DialogsRepository {
        return dialogsRepository
    }

    fun provideChatRepository(): ChatRepository {
        return chatRepository
    }
}