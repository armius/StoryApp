package com.armius.dicoding.storyapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.armius.dicoding.storyapp.R
import com.armius.dicoding.storyapp.core.net.ApiConfig
import com.armius.dicoding.storyapp.databinding.ActivityRegisterBinding
import com.armius.dicoding.storyapp.ui.customcomponents.CustomEditText
import com.armius.dicoding.storyapp.ui.login.LoginActivity
import com.armius.dicoding.storyapp.ui.utils.Event
import com.armius.dicoding.storyapp.ui.viewmodel.ViewModelFactory2
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import org.json.JSONTokener

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()

        registerViewModel = ViewModelProvider(this, ViewModelFactory2(ApiConfig.getApiService(this.applicationContext)))[RegisterViewModel::class.java]
        with(binding) {
            etName.liveChangeEditText()
            etEmail.liveChangeEditText()
            etPassword.liveChangeEditText()
            btnRegister.setOnClickListener{
                registerViewModel.registerUser(etName.text.toString(), etEmail.text.toString(), etPassword.text.toString())
                setEnabledInput(false)
            }
            fabToLoginPage.setOnClickListener{
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        registerViewModel.isLoading.observe(this, {
            showLoading(it)
        })

        playAnimation()
    }

    private fun showLoading(isLoading: Boolean) = if (isLoading) {
        binding.loadingView.root.visibility = View.VISIBLE
    } else {
        binding.loadingView.root.visibility = View.GONE
        setEnabledInput(true)
        registerViewModel.infoMsg.observe(this, {
            it.getContentIfNotHandled()?.let { infoMsg ->
                var msg = ""
                if(infoMsg==Event.ON_FAILURE) {
                    msg = this.getString(R.string.connection_error)
                } else if (infoMsg==Event.REGISTER_SUCCESS) {
                    msg = this.getString(R.string.register_successful)
                } else {
                    val json = JSONTokener(infoMsg).nextValue() as JSONObject
                    msg = json.getString("message")
                }
                Snackbar.make(binding.tilEmail, msg, Snackbar.LENGTH_INDEFINITE).setAction(this.getString(
                    R.string.ok)) { }.show()
            }
        })
    }

    private fun CustomEditText.liveChangeEditText() {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.btnRegister.isEnabled = binding.etName.validInput && binding.etEmail.validInput && binding.etPassword.validInput
            }

        })
    }

    private fun setEnabledInput(isEnabled: Boolean) {
        with(binding) {
            btnRegister.isEnabled = isEnabled
            etName.isEnabled = isEnabled
            etEmail.isEnabled = isEnabled
            etPassword.isEnabled = isEnabled
        }
    }

    private fun playAnimation() {
        with(binding) {
            val image = ObjectAnimator.ofFloat(ivApplogo, View.ALPHA, 1f).setDuration(800)
            val image2 = ObjectAnimator.ofFloat(ivApplogo, View.TRANSLATION_Y, -300f, 0f).setDuration(800)
            image2.interpolator = AccelerateDecelerateInterpolator()
            val imageAS = AnimatorSet()
            imageAS.playTogether(image, image2)

            val etName = ObjectAnimator.ofFloat(tilName, View.ALPHA, 1f).setDuration(800)
            val etName2 = ObjectAnimator.ofFloat(tilName, View.TRANSLATION_X, -300f, 0f).setDuration(800)
            etName2.interpolator = AccelerateDecelerateInterpolator()
            val etNameAS = AnimatorSet()
            etNameAS.playTogether(etName, etName2)

            val etEmail = ObjectAnimator.ofFloat(tilEmail, View.ALPHA, 1f).setDuration(800)
            val etEmail2 = ObjectAnimator.ofFloat(tilEmail, View.TRANSLATION_X, getScreenX(),0f).setDuration(800)
            etEmail2.interpolator = AccelerateDecelerateInterpolator()
            val etEmailAS = AnimatorSet()
            etEmailAS.playTogether(etEmail, etEmail2)

            val etPass = ObjectAnimator.ofFloat(tilPassword, View.ALPHA, 1f).setDuration(800)
            val etPass2 = ObjectAnimator.ofFloat(tilPassword, View.TRANSLATION_X, -300f, 0f).setDuration(800)
            etPass2.interpolator = AccelerateDecelerateInterpolator()
            val etPassAS = AnimatorSet()
            etPassAS.playTogether(etPass, etPass2)

            val btnRegister = ObjectAnimator.ofFloat(btnRegister, View.TRANSLATION_Y, getScreenY()+200f, 0f).setDuration(700)
            btnRegister.interpolator = AccelerateDecelerateInterpolator()

            AnimatorSet().apply {
                playTogether(imageAS, etNameAS, etEmailAS, etPassAS, btnRegister)
                start()
            }
        }
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

    companion object{
        fun getScreenX(): Float = (Resources.getSystem().displayMetrics.widthPixels).toFloat()
        fun getScreenY(): Float = (Resources.getSystem().displayMetrics.heightPixels).toFloat()
    }
}