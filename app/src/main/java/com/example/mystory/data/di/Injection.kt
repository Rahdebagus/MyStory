package com.example.mystory.data.di

import android.content.Context
import com.example.mystory.data.api.Api
import com.example.mystory.data.pref.UserPreference
import com.example.mystory.data.pref.dataStore
import com.example.mystory.data.repository.StoryRepository
import com.example.mystory.data.repository.UserRepository

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)

        val apiService = Api.getApiService()
        return UserRepository.getInstance(pref, apiService)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = Api.getApiService()
        return StoryRepository.getInstance(pref, apiService)
    }

}