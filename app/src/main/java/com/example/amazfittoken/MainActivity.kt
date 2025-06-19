package com.example.amazfittoken

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.amazfittoken.adapter.DevicesAdapter
import com.example.amazfittoken.databinding.ActivityMainBinding
import com.example.amazfittoken.model.AuthResult
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
        hideDevices()
        
        lifecycleScope.launch {
            try {
                // Step 1: Authenticate user
                val authResult = authRepository.authenticateUser(email, password)
                
                if (authResult.isSuccess) {
                    val auth = authResult.getOrNull()!!
                    
                    // Step 2: Fetch devices
                    val devicesResult = authRepository.getDevices(auth)
                    
                    if (devicesResult.isSuccess) {
                        val devices = devicesResult.getOrNull()!!
                        showDevices(devices)
                        Toast.makeText(this@MainActivity, "Devices retrieved successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        val error = devicesResult.exceptionOrNull()?.message ?: "Failed to get devices"
                        showError(error)
                    }
                } else {
                    val error = authResult.exceptionOrNull()?.message ?: "Authentication failed"
                    showError(error)
                }
            } catch (e: Exception) {
                showError("Network error: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }
    
    private fun showDevices(devices: List<com.example.amazfittoken.model.Device>) {
        if (devices.isEmpty()) {
            showError("No devices found for your account")
            return
        }
        
        binding.devicesHeaderText.visibility = View.VISIBLE
        binding.devicesRecyclerView.visibility = View.VISIBLE
        
        val adapter = DevicesAdapter(devices, this)
        binding.devicesRecyclerView.adapter = adapter
        binding.devicesRecyclerView.layoutManager = LinearLayoutManager(this)
    }
    
    private fun hideDevices() {
        binding.devicesHeaderText.visibility = View.GONE
        binding.devicesRecyclerView.visibility = View.GONE
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
        binding.loginButton.text = if (show) "Retrieving Devices..." else "Get Token"
    }
}