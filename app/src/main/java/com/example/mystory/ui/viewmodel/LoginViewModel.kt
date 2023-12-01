package com.example.mystory.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mystory.data.repository.UserRepository

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    fun login(email: String, password: String) = repository.login(email, password)
}