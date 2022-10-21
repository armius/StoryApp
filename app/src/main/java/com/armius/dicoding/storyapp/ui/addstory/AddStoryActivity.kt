package com.armius.dicoding.storyapp.ui.addstory

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.armius.dicoding.storyapp.R
import com.armius.dicoding.storyapp.core.net.ApiConfig
import com.armius.dicoding.storyapp.databinding.ActivityAddStoryBinding
import com.armius.dicoding.storyapp.ui.customcomponents.CustomEditText
import com.armius.dicoding.storyapp.ui.home.HomeActivity
import com.armius.dicoding.storyapp.ui.login.LoginViewModel
import com.armius.dicoding.storyapp.ui.utils.Event
import com.armius.dicoding.storyapp.ui.utils.ImageUtils.createCustomTempFile
import com.armius.dicoding.storyapp.ui.utils.ImageUtils.reduceFileImage
import com.armius.dicoding.storyapp.ui.utils.ImageUtils.uriToFile
import com.armius.dicoding.storyapp.ui.viewmodel.ViewModelFactory2
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.json.JSONTokener
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var currentPhotoPath: String
    private var selectedPictureFile: File? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val tempFile = File(currentPhotoPath)
            selectedPictureFile = if(tempFile.length()>1000000) {
                reduceFileImage(File(currentPhotoPath))
            } else {
                File(currentPhotoPath)
            }
            val result = BitmapFactory.decodeFile(tempFile.path)
            binding.ivAddstoryPicture.setImageBitmap(result)
            addStoryViewModel.setSelectedImage(selectedPictureFile!!, result, null)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            selectedPictureFile = myFile
            binding.ivAddstoryPicture.setImageURI(selectedImg)
            addStoryViewModel.setSelectedImage(selectedPictureFile!!, null, selectedImg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        addStoryViewModel = ViewModelProvider(this, ViewModelFactory2(ApiConfig.getApiService(this.applicationContext)))[AddStoryViewModel::class.java]
        with(binding) {
            etAddstoryDescription.liveChangeEditText()
            btnOpenCamera.setOnClickListener {
                startTakePhoto()
            }
            btnOpenGallery.setOnClickListener {
                startGallery()
            }
            btnAddstorySubmit.setOnClickListener {
                val requestImageFile = selectedPictureFile!!.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData("photo", selectedPictureFile!!.name, requestImageFile)
                val description = etAddstoryDescription.text.toString().toRequestBody("text/plain".toMediaType())
                var lat : RequestBody? = null
                var lng : RequestBody? = null
                if(currentLocation != null) {
                    lat = currentLocation?.latitude.toString().toRequestBody("text/plain".toMediaType())
                    lng = currentLocation?.longitude.toString().toRequestBody("text/plain".toMediaType())
                }
                addStoryViewModel.submitStory(
                    description,
                    imageMultipart,
                    lat,
                    lng
                )
                setEnabledInput(false)
            }
        }
        with(addStoryViewModel) {
            isLoading.observe(this@AddStoryActivity, {
                showLoading(it)
            })
            isSuccess.observe(this@AddStoryActivity, {
                if (it) {
                    showSuccessAlert()
                } else {
                    addStoryViewModel.infoMsg.observe(this@AddStoryActivity, {
                        it.getContentIfNotHandled()?.let { infoMsg ->
                            var msg = ""
                            if(infoMsg==Event.ON_FAILURE) {
                                msg = this@AddStoryActivity.getString(R.string.connection_error)
                            } else {
                                val json = JSONTokener(infoMsg).nextValue() as JSONObject
                                msg = json.getString("message")
                            }
                            Snackbar.make(binding.cvAddstoryPicture, msg, Snackbar.LENGTH_INDEFINITE)
                                .setAction(this@AddStoryActivity.getString(R.string.ok)) { }.show()
                        }
                    })
                }
            })
            imageBitmap.observe(this@AddStoryActivity, {
                if (it!= null) {
                    binding.ivAddstoryPicture.setImageBitmap(it)
                }
            })
            imageUri.observe(this@AddStoryActivity, {
                if (it!= null) {
                    binding.ivAddstoryPicture.setImageURI(it)
                }
            })
            selectedFile.observe(this@AddStoryActivity, {
                if(it!=null) {
                    selectedPictureFile = it
                }
            })
        }
    }

    override fun onStart() {
        super.onStart()
        getCurrentLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.permission_not_granted),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.armius.dicoding.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun CustomEditText.liveChangeEditText() {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.btnAddstorySubmit.isEnabled = binding.etAddstoryDescription.validInput && selectedPictureFile != null
            }

        })
    }

    private fun showLoading(isLoading: Boolean) = if (isLoading) {
        binding.loadingView.root.visibility = View.VISIBLE
    } else {
        binding.loadingView.root.visibility = View.GONE
        setEnabledInput(true)
    }

    private fun setEnabledInput(isEnabled: Boolean) {
        with(binding) {
            btnAddstorySubmit.isEnabled = isEnabled
            btnOpenCamera.isEnabled = isEnabled
            btnOpenGallery.isEnabled = isEnabled
            etAddstoryDescription.isEnabled = isEnabled
        }
    }

    private fun showSuccessAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.confirmation))
        builder.setMessage(getString(R.string.posting_successful))
        builder.setCancelable(false)
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
            val intent = Intent(this@AddStoryActivity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        })
        builder.show()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getCurrentLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getCurrentLocation()
                }
                else -> {
                    // No location access granted.
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getCurrentLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    this.currentLocation = location
                } else {
                    Toast.makeText(
                        this@AddStoryActivity,
                        getString(R.string.location_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}