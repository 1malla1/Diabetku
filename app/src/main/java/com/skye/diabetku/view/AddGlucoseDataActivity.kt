package com.skye.diabetku.view

import android.app.TimePickerDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.skye.diabetku.R
import com.skye.diabetku.databinding.ActivityAddGlucoseDataBinding
import com.skye.diabetku.viewmodel.AddGlucoseDataViewModel
import com.skye.diabetku.data.Result
import com.skye.diabetku.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddGlucoseDataActivity : AppCompatActivity() {
    private val addGlucoseDataViewModel: AddGlucoseDataViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityAddGlucoseDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGlucoseDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val drawable: Drawable? = toolbar.navigationIcon
        drawable?.let {
            DrawableCompat.setTint(it, getColor(R.color.white))
            toolbar.navigationIcon = it
        }
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val jenisPemeriksaan: AutoCompleteTextView = findViewById(R.id.edtJenisPemeriksaan)
        setupDropdownJenisPemeriksaan(jenisPemeriksaan)

        addGlucoseDataViewModel.jenisPemeriksaan.observe(this) { jenis ->
            jenisPemeriksaan.setText(jenis)
        }

        addGlucoseDataViewModel.jenisPemeriksaan.observe(this) { jenis ->
            if (!jenis.isNullOrEmpty()) {
                jenisPemeriksaan.setText(jenis, false)
            }
        }
        binding.edtJenisPemeriksaan.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val input = binding.edtJenisPemeriksaan.text.toString()
                val items = resources.getStringArray(R.array.jenis_pemeriksaan)
                if (!items.contains(input)) {
                    binding.edtJenisPemeriksaan.setText("") // Reset input jika tidak valid
                }
            }
        }

        val edtTanggal: TextInputEditText = findViewById(R.id.edtTanggal)
        setupDatePicker(edtTanggal)

        val edtWaktu: TextInputEditText = findViewById(R.id.edtWaktu)
        setupTimePicker(edtWaktu)

        setupButton()
        observeResult()
    }

    private fun observeResult() {
        addGlucoseDataViewModel.result.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.btnSimpan.isEnabled = false
                    binding.btnSimpan.text = getString(R.string.loading)
                }
                is Result.Success -> {
                    binding.btnSimpan.isEnabled = true
                    binding.btnSimpan.text = getString(R.string.simpan)
                    Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is Result.Error -> {
                    binding.btnSimpan.isEnabled = true
                    binding.btnSimpan.text = getString(R.string.simpan)
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupDatePicker(edtTanggal: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val datePicker = android.app.DatePickerDialog(
            this,
            R.style.DatePickerDialogTheme,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }.timeInMillis

                val formattedDate = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(selectedDate)

                edtTanggal.setText(formattedDate)

                addGlucoseDataViewModel.setDate(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        edtTanggal.setOnClickListener {
            if (!datePicker.isShowing) {
                datePicker.show()
            }
        }
    }

    private fun setupTimePicker(edtWaktu: TextInputEditText) {
        edtWaktu.setOnClickListener {
            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                R.style.DatePickerDialogTheme,
                { _, hourOfDay, minute ->
                    addGlucoseDataViewModel.setTime(hourOfDay, minute)
                },
                currentHour, currentMinute, true
            )
            timePickerDialog.show()
        }

        addGlucoseDataViewModel.selectedTime.observe(this) { time ->
            edtWaktu.setText(time)
        }
    }

    private fun setupDropdownJenisPemeriksaan(edtJenisPemeriksaan: AutoCompleteTextView) {
        val items = resources.getStringArray(R.array.jenis_pemeriksaan)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, items)
        edtJenisPemeriksaan.setAdapter(adapter)

        edtJenisPemeriksaan.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = items[position]
            addGlucoseDataViewModel.setJenisPemeriksaan(selectedItem)
        }

    }

    private fun setupButton() {
        binding.btnSimpan.setOnClickListener {
            val glucoseValue = binding.edtGlucose.text.toString()
            val jenisPemeriksaan = binding.edtJenisPemeriksaan.text.toString()
            val tanggal = binding.edtTanggal.text.toString()
            val waktu = binding.edtWaktu.text.toString()

            var isValid = true

            if (glucoseValue.isEmpty()) {
                binding.edtGlucoseLayout.error = "Kadar gula darah tidak boleh kosong"
                isValid = false
            } else {
                try {
                    val value = glucoseValue.toInt()
                    if (value <= 0) {
                        binding.edtGlucoseLayout.error = "Kadar gula darah harus lebih dari 0"
                        isValid = false
                    } else {
                        binding.edtGlucoseLayout.error = null
                    }
                } catch (e: NumberFormatException) {
                    binding.edtGlucoseLayout.error = "Kadar gula darah harus berupa angka"
                    isValid = false
                }
            }

            val items = resources.getStringArray(R.array.jenis_pemeriksaan)
            if (jenisPemeriksaan.trim().isEmpty() || !items.contains(jenisPemeriksaan)) {
                binding.edtJenisPemeriksaanLayout.error = "Jenis pemeriksaan tidak valid atau kosong"
                binding.edtJenisPemeriksaan.setTextColor(ContextCompat.getColor(this, R.color.red_500))
                isValid = false
            } else {
                binding.edtJenisPemeriksaanLayout.error = null
                binding.edtJenisPemeriksaan.setTextColor(ContextCompat.getColor(this, R.color.gray_700))
            }




            if (tanggal.isEmpty()) {
                binding.edtTanggalLayout.error = "Tanggal tidak boleh kosong"
                isValid = false
            } else {
                binding.edtTanggalLayout.error = null
            }

            if (waktu.isEmpty()) {
                binding.edtWaktuLayout.error = "Waktu tidak boleh kosong"
                isValid = false
            } else {
                binding.edtWaktuLayout.error = null
            }

            if (isValid) {
                lifecycleScope.launch {
                    addGlucoseDataViewModel.addGlucoseData(glucoseValue.toInt())
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}