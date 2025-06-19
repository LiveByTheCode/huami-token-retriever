package com.example.amazfittoken

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.amazfittoken.databinding.ActivityMainBinding
import com.example.amazfittoken.repository.AuthRepository
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var authRepository: AuthRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        authRepository = AuthRepository()
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            
            if (validateInput(email, password)) {
                authenticateUser(email, password)
            }
        }
        
        binding.copyButton.setOnClickListener {
            copyTokenToClipboard()
        }
    }
    
    private fun validateInput(email: String, password: String): Boolean {
        binding.emailInputLayout.error = null
        binding.passwordInputLayout.error = null
        
        if (email.isEmpty()) {
            binding.emailInputLayout.error = "Email is required"
            return false
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInputLayout.error = "Please enter a valid email address"
            return false
        }
        
        if (password.isEmpty()) {
            binding.passwordInputLayout.error = "Password is required"
            return false
        }
        
        return true
    }
    
    private fun authenticateUser(email: String, password: String) {
        showLoading(true)
        hideError()
        hideTokenCard()
        
        lifecycleScope.launch {
            try {
                val result = authRepository.authenticateUser(email, password)
                
                if (result.isSuccess) {
                    val token = result.getOrNull()!!
                    showToken(token)
                    Toast.makeText(this@MainActivity, "Token retrieved successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Unknown error occurred"
                    showError(error)
                }
            } catch (e: Exception) {
                showError("Network error: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }
    
    private fun showToken(token: String) {
        binding.tokenTextView.text = token
        binding.tokenCardView.visibility = View.VISIBLE
    }
    
    private fun hideTokenCard() {
        binding.tokenCardView.visibility = View.GONE
    }
    
    private fun showError(message: String) {
        binding.errorTextView.text = message
        binding.errorTextView.visibility = View.VISIBLE
    }
    
    private fun hideError() {
        binding.errorTextView.visibility = View.GONE
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.loginButton.isEnabled = !show
        binding.loginButton.text = if (show) "Retrieving Token..." else "Get Token"
    }
    
    private fun copyTokenToClipboard() {
        val token = binding.tokenTextView.text.toString()
        if (token.isNotEmpty()) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Amazfit Token", token)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Token copied to clipboard!", Toast.LENGTH_SHORT).show()
        }
    }
}