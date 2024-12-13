package com.skye.diabetku.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import com.skye.diabetku.data.Result
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.skye.diabetku.R
import com.skye.diabetku.databinding.ActivityRegisterBinding
import com.skye.diabetku.viewmodel.RegisterViewModel
import com.skye.diabetku.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegisterActivity : AppCompatActivity() {
    private val registerViewModel : RegisterViewModel by viewModels { ViewModelFactory.getInstance(this) }
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val loginTextView = findViewById<TextView>(R.id.tvSignIn)
        loginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val edtTanggalLahir: TextInputEditText = findViewById(R.id.edtBirthDate)
        setupDatePicker(edtTanggalLahir)

        registerViewModel.selectedDate.observe(this) { selectedDate ->
            selectedDate?.let {
                edtTanggalLahir.setText(it)
            }
        }

        lifecycleScope.launch {
            registerViewModel.registerResult.collect { result ->
                when (result) {
                    is Result.Loading -> {
                    }
                    is Result.Success -> {
                        showToast("Register successful!")
                        navigateToLogin()
                    }
                    is Result.Error -> {
                        result.message?.let { showToast(it) }
                    }
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            val retypePassword = binding.edtRetypePassword.text.toString()
            val dateOfBirth = binding.edtBirthDate.text.toString()

            var isValid = true

            if (name.isEmpty()) {
                binding.edtNameLayout.error = "Nama tidak boleh kosong"
                isValid = false
            } else {
                binding.edtNameLayout.error = null
            }

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

            if (retypePassword.isEmpty()) {
                binding.edtRetypePasswordLayout.error = "Konfirmasi kata sandi tidak boleh kosong"
                isValid = false
            } else {
                binding.edtRetypePasswordLayout.error = null
            }

            if (password != retypePassword) {
                binding.edtRetypePasswordLayout.error = "Password tidak cocok"
                isValid = false
            }

            if (dateOfBirth.isEmpty()) {
                binding.edtBirthDateLayout.error = "Tanggal lahir tidak boleh kosong"
                isValid = false
            } else {
                binding.edtBirthDateLayout.error = null
            }

            if (isValid) {
                registerViewModel.register(name, email, password, dateOfBirth)
            } else {
                Toast.makeText(this, "Periksa kembali input Anda", Toast.LENGTH_SHORT).show()
            }
        }

        binding.edtRetypePassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = binding.edtPassword.text.toString()
                val retypePassword = binding.edtRetypePassword.text.toString()

                if (retypePassword.isNotEmpty() && retypePassword != password) {
                    binding.edtRetypePasswordLayout.error = "Password tidak cocok"
                } else {
                    binding.edtRetypePasswordLayout.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupDatePicker(edtTanggalLahir: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val datePicker = android.app.DatePickerDialog(
            this,
            R.style.DatePickerDialogTheme,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }.timeInMillis

                val formattedDate = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(selectedDate)

                edtTanggalLahir.setText(formattedDate)

                registerViewModel.setDate(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        edtTanggalLahir.setOnClickListener {
            if (!datePicker.isShowing) {
                datePicker.show()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}