package com.example.mystory.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mystory.data.di.Injection
import com.example.mystory.data.repository.StoryRepository
import com.example.mystory.data.repository.UserRepository


class ViewModelFactory private constructor(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = with(modelClass) {
        when {
            isAssignableFrom(MainViewModel::class.java) -> MainViewModel(userRepository , storyRepository )
            isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(userRepository)
            isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(userRepository)
            isAssignableFrom(DetailViewModel::class.java) -> DetailViewModel(storyRepository)
            isAssignableFrom(AddViewModel::class.java) -> AddViewModel(storyRepository)

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: ViewModelFactory(
                Injection.provideUserRepository(context),
                Injection.provideStoryRepository(context)
            )
        }.also { instance = it }
    }
}