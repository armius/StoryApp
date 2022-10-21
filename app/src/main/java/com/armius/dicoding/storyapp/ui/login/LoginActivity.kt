package com.armius.dicoding.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.lifecycle.ViewModelProvider
import com.armius.dicoding.storyapp.R
import com.armius.dicoding.storyapp.core.net.ApiConfig
import com.armius.dicoding.storyapp.core.utils.Preference
import com.armius.dicoding.storyapp.databinding.ActivityLoginBinding
import com.armius.dicoding.storyapp.ui.customcomponents.CustomEditText
import com.armius.dicoding.storyapp.ui.home.HomeActivity
import com.armius.dicoding.storyapp.ui.register.RegisterActivity
import com.armius.dicoding.storyapp.ui.register.RegisterActivity.Companion.getScreenX
import com.armius.dicoding.storyapp.ui.register.RegisterActivity.Companion.getScreenY
import com.armius.dicoding.storyapp.ui.utils.Event
import com.armius.dicoding.storyapp.ui.viewmodel.ViewModelFactory2
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import org.json.JSONTokener

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        val preference = Preference(this.applicationContext)
        if(preference.getToken() != null) {
            goToHome()
        }

        loginViewModel = ViewModelProvider(this, ViewModelFactory2(ApiConfig.getApiService(this.applicationContext)))[LoginViewModel::class.java]
        with(binding) {
            etEmailLogin.liveChangeEditText()
            etPasswordLogin.liveChangeEditText()
            btnLogin.setOnClickListener {
                loginViewModel.loginUser(etEmailLogin.text.toString(), etPasswordLogin.text.toString())
                setEnabledInput(false)
            }
            fabToRegisterPage.setOnClickListener{
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        loginViewModel.isLoading.observe(this, {
            showLoading(it)
        })
        loginViewModel.isLoginSuccess.observe(this, {
            if(it==true) {
                val preference = Preference(this@LoginActivity)
                with(preference) {
                    setToken(loginViewModel.token.value)
                    setUserID(loginViewModel.userID.value)
                    setUserName(loginViewModel.userName.value)
                }
                goToHome()
            } else {
                loginViewModel.errMsg.observe(this@LoginActivity, {
                    it.getContentIfNotHandled()?.let { errMsg ->
                        var msg = ""
                        if (errMsg==Event.ON_FAILURE) {
                            msg = this@LoginActivity.getString(R.string.connection_error)
                        } else {
                            val json = JSONTokener(errMsg).nextValue() as JSONObject
                            msg = json.getString("message")
                        }
                        Snackbar.make(binding.tilEmail, msg, Snackbar.LENGTH_INDEFINITE)
                            .setAction(this@LoginActivity.getString(R.string.ok)) { }.show()
                    }
                })
                setEnabledInput(true)
            }
        })
        playAnimation()
    }

    private fun goToHome() {
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) = if (isLoading) {
        binding.loadingView.root.visibility = View.VISIBLE
    } else {
        binding.loadingView.root.visibility = View.GONE
        setEnabledInput(true)
    }

    private fun CustomEditText.liveChangeEditText() {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.btnLogin.isEnabled = binding.etEmailLogin.validInput && binding.etPasswordLogin.validInput
            }

        })
    }

    private fun setEnabledInput(isEnabled: Boolean) {
        with(binding) {
            btnLogin.isEnabled = isEnabled
            etEmailLogin.isEnabled = isEnabled
            etPasswordLogin.isEnabled = isEnabled
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

    private fun playAnimation() {
        with(binding) {
            val image = ObjectAnimator.ofFloat(ivApplogo, View.ALPHA, 1f).setDuration(800)
            val image2 = ObjectAnimator.ofFloat(ivApplogo, View.TRANSLATION_Y, -300f, 0f).setDuration(800)
            image2.interpolator = AccelerateDecelerateInterpolator()
            val imageAS = AnimatorSet()
            imageAS.playTogether(image, image2)

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

            val btnLogin = ObjectAnimator.ofFloat(btnLogin, View.TRANSLATION_Y, getScreenY()+200f, 0f).setDuration(700)
            btnLogin.interpolator = AccelerateDecelerateInterpolator()

            AnimatorSet().apply {
                playTogether(imageAS, etEmailAS, etPassAS, btnLogin)
                start()
            }
        }
    }
}