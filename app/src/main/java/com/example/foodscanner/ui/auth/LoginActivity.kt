package com.example.foodscanner.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.foodscanner.R
import com.example.foodscanner.data.auth.FirebaseAuthManager
import com.example.foodscanner.databinding.ActivityLoginBinding
import com.example.foodscanner.ui.home.HomeActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authManager = FirebaseAuthManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if already logged in
        if (authManager.isLoggedIn()) {
            navigateToHome()
            return
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            performLogin()
        }

        binding.registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin() {
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString()

        if (!validateInputs(email, password)) {
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.loginButton.isEnabled = false

        lifecycleScope.launch {
            val result = authManager.login(email, password)

            result.onSuccess {
                binding.progressBar.visibility = View.GONE
                navigateToHome()
            }

            result.onFailure { exception ->
                binding.progressBar.visibility = View.GONE
                binding.loginButton.isEnabled = true
                showError(exception.message ?: "Login failed")
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        binding.errorMessage.visibility = View.GONE

        if (email.isEmpty()) {
            showError("Please enter email")
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(getString(R.string.error_invalid_email))
            return false
        }

        if (password.isEmpty()) {
            showError("Please enter password")
            return false
        }

        if (password.length < 6) {
            showError(getString(R.string.error_weak_password))
            return false
        }

        return true
    }

    private fun showError(message: String) {
        binding.errorMessage.visibility = View.VISIBLE
        binding.errorMessage.text = message
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
