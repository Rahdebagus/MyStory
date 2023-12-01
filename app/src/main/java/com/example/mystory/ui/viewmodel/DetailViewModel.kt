package com.example.mystory.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mystory.data.repository.StoryRepository

class DetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getDetailStory(id: String) = storyRepository.getDetailStory(id)
}