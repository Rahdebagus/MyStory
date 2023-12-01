package com.example.mystory.ui.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystory.databinding.ActivityMainBinding
import com.example.mystory.ui.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.example.mystory.data.repository.Result
import com.example.mystory.ui.adapter.StoryAdapter
import com.example.mystory.ui.viewmodel.ViewModelFactory


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        viewModel.getSession().observe(this) { user ->
            if (user.token == "") {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        setupView()
        actionAdd()
        actionLogout()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStory.addItemDecoration(itemDecoration)

        viewModel.getStories().observe(this) { storyResponse ->
            when (storyResponse) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    binding.rvStory.adapter = StoryAdapter().apply {
                        submitList(storyResponse.data.listStory)
                    }
                }
                is Result.Error -> handleError(storyResponse.error)
                }
            }
        }


    private fun actionLogout() {
        binding.fabLogout.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun actionAdd() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddStory::class.java))
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun handleError(errorMessage: String) {
        showLoading(false)
        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
    }
    }


