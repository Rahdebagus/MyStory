package com.example.mystory.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.mystory.ui.activity.CameraActivity.Companion.CAMERAX_RESULT
import com.example.mystory.R
import com.example.mystory.data.Utils.reduceFileImage
import com.example.mystory.data.Utils.uriToFile
import com.example.mystory.databinding.ActivityAddStoryBinding
import com.example.mystory.ui.viewmodel.AddViewModel
import com.google.android.material.snackbar.Snackbar
import com.example.mystory.data.repository.Result
import com.example.mystory.ui.viewmodel.ViewModelFactory


class AddStory : AppCompatActivity() {

    private var currentImageUri: Uri? = null

    private val binding: ActivityAddStoryBinding by lazy {
        ActivityAddStoryBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<AddViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri =
                it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            currentImageUri?.let { uri ->
                binding.previewImageView.setImageURI(uri)
            }
        }
    }

    private val launchCamera = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            currentImageUri?.let {
                Log.d("Image URI", "showImage: $it")
                binding.previewImageView.setImageURI(it)
            }
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setupView()
        setupAction()
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
    }

    private fun setupAction() {
        binding.cameraButton.setOnClickListener {
            launcherGallery.launch(Intent(this, CameraActivity
            ::class.java))
        }

        binding.galleryButton.setOnClickListener {
            launchCamera.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.uploadButton.setOnClickListener {
            currentImageUri?.let { uri ->
                val imageFile = uriToFile(uri, this).reduceFileImage()

                viewModel.addStory(
                    binding.edAddDescription.text.toString(),
                    imageFile,
                    null,
                    null,
                ).observe(this) {
                    if (it != null) {
                        when (it) {
                            is Result.Loading -> {
                                showLoading(true)
                            }

                            is Result.Success -> {
                                val intent = Intent(this, MainActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                startActivity(intent)
                                finish()
                            }

                            is Result.Error -> {
                                Snackbar.make(binding.root, it.error, Snackbar.LENGTH_SHORT).show()
                                showLoading(false)
                            }
                        }
                    }
                }
            } ?: Snackbar.make(
                binding.root,
                getString(R.string.empty_img),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.uploadButton.isEnabled = !isLoading
        binding.uploadButton.text = if (isLoading) {
            getString(R.string.loading)
        } else {
            getString(R.string.upload)
        }
    }
}