package com.example.mystory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mystory.data.repository.StoryRepository
import com.example.mystory.data.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel (private val userRepository: UserRepository, private val storyRepository: StoryRepository) : ViewModel() {
    fun getSession() = userRepository.getSession().asLiveData()

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    fun getStories() = storyRepository.getStories()
}