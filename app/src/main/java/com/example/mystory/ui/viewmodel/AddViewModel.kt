package com.example.mystory.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mystory.data.repository.StoryRepository
import java.io.File

class AddViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun addStory(description: String, photo: File, lat: String?, lon: String?)  = storyRepository.addStory(description, photo, lat, lon)
}