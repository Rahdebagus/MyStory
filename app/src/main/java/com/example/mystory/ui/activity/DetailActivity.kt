package com.example.mystory.ui.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.mystory.data.Utils.formatDate
import com.example.mystory.databinding.ActivityDetailBinding
import com.example.mystory.ui.viewmodel.DetailViewModel
import com.google.android.material.snackbar.Snackbar
import com.example.mystory.data.repository.Result
import com.example.mystory.ui.viewmodel.ViewModelFactory


class DetailActivity : AppCompatActivity() {

    private val binding: ActivityDetailBinding by lazy {
        ActivityDetailBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
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

        val storyId = intent.getStringExtra(STORY_ID) as String

        viewModel.getDetailStory(storyId).observe(this) { story ->
            when (story) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    with(story.data.story) {
                        Glide.with(this@DetailActivity)
                            .load(photoUrl)
                            .into(binding.storyPhoto)

                        binding.createdAt.text = createdAt.formatDate()
                        binding.storyName.text = name
                        binding.storyDescription.text = description
                    }
                }

                is Result.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.root, story.error, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val STORY_ID = "STORY_ID"
    }
}