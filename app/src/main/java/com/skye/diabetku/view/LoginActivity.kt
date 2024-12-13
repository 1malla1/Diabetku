package com.skye.diabetku.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.skye.diabetku.R
import com.skye.diabetku.data.Result
import com.skye.diabetku.databinding.ActivityLoginBinding
import com.skye.diabetku.viewmodel.LoginViewModel
import com.skye.diabetku.viewmodel.RegisterViewModel
import com.skye.diabetku.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val loginViewModel : LoginViewModel by viewModels { ViewModelFactory.getInstance(this) }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val registerTextView = findViewById<TextView>(R.id.tvSignUp)

        registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        lifecycleScope.launch {
            loginViewModel.loginResult.collect { result ->
                when (result) {
                    is Result.Loading -> {
                    }
                    is Result.Success -> {
                        AlertDialog.Builder(this@LoginActivity).apply {
                            setTitle("Login Berhasil")
                            setMessage("Anda berhasil login Selamat datang kembali!")
                            setPositiveButton("OK") { _, _ ->
                                navigateToMain()
                            }
                            create()
                            show()
                        }
                    }
                    is Result.Error -> {
                        showToast("Login Gagal! Periksa kembali email dan password anda")
                    }
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            var isValid = true

            if (email.isEmpty()) {
                binding.edtEmailLayout.error = "Email tidak boleh kosong"
                isValid = false
            }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtEmail.error = "Format email tidak valid"
                isValid = false
            } else {
                binding.edtEmailLayout.error = null
            }

            if (password.isEmpty()) {
                binding.edtPassword.error = "Kata sandi tidak boleh kosong"
                isValid = false
            } else if (password.length < 8) {
                binding.edtPasswordLayout.error = "Password harus terdiri dari minimal 8 karakter"
                isValid = false
            } else {
                binding.edtPasswordLayout.error = null
            }

            if (isValid) {
                loginViewModel.login(email, password)
            } else {
                Toast.makeText(this, "Periksa kembali input Anda", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("FROM_LOGIN", true)
        }
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}