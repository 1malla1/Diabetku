package com.skye.diabetku.view

import android.app.TimePickerDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputEditText
import com.skye.diabetku.R
import com.skye.diabetku.data.Result
import com.skye.diabetku.databinding.ActivityAddGlucoseDataBinding
import com.skye.diabetku.databinding.ActivityEditGlucoseDataBinding
import com.skye.diabetku.viewmodel.AddGlucoseDataViewModel
import com.skye.diabetku.viewmodel.EditGlucoseDataViewModel
import com.skye.diabetku.viewmodel.PemantauanGulaDarahViewModel
import com.skye.diabetku.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditGlucoseDataActivity : AppCompatActivity() {
    private val editGlucoseDataViewModel : EditGlucoseDataViewModel by viewModels() {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityEditGlucoseDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditGlucoseDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bloodGlucoseId = intent.getIntExtra("BLOOD_GLUCOSE_ID", -1)
        if (bloodGlucoseId != -1) {
            editGlucoseDataViewModel.getBloodGlucoseById(bloodGlucoseId)
        } else {
            Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
        }

        setupToolbar()
        setupAutoCompleteTextView(binding.edtJenisPemeriksaan)
        setupDatePicker(binding.edtTanggal)
        setupTimePicker(binding.edtWaktu)
        observeBloodGlucoseData()
        binding.btnSimpan.setOnClickListener {updateData()}

    }
    private fun observeBloodGlucoseData() {
        editGlucoseDataViewModel.bloodGlucoseDataById.observe(this, Observer { data ->
            data?.let {
                val glucoseValue = it.data?.glucoseValue ?: ""
                val testTime = formatTime(it.data?.testTime ?: "")
                val testDate = formatDate(it.data?.testDate ?: "")
                val testType = it.data?.testType ?: ""

                setText(glucoseValue, testTime, testDate, testType)
            } ?: run {
                showToast("Data tidak ditemukan")
            }
        })

        editGlucoseDataViewModel.updateBloodGlucose.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.btnSimpan.isEnabled = false
                    binding.btnSimpan.text = getString(R.string.loading)
                }
                is Result.Success -> {
                    binding.btnSimpan.isEnabled = true
                    binding.btnSimpan.text = getString(R.string.simpan)
                    showToast("Data berhasil diupdate!")
                    finish()
                }
                is Result.Error -> {
                    binding.btnSimpan.isEnabled = true
                    binding.btnSimpan.text = getString(R.string.simpan)
                    showToast(result.error)
                }
            }
        }
    }
    private fun setText(glucoseValue: String, testTime: String, testDate: String, testType: String) {
        binding.edtGlucose.setText(glucoseValue)
        binding.edtWaktu.setText(testTime)
        binding.edtTanggal.setText(testDate)
        binding.edtJenisPemeriksaan.setText(testType, false)
        editGlucoseDataViewModel.setJenisPemeriksaan(testType)
    }
    private fun updateData() {
        if (!validasiInput()) return

        val waktu = binding.edtWaktu.text.toString()
        val tanggal = binding.edtTanggal.text.toString()
        val glucose = binding.edtGlucose.text.toString().toDoubleOrNull() ?: 0.0
        val jenisPemeriksaan = binding.edtJenisPemeriksaan.text.toString()
        val bloodGlucoseId = intent.getIntExtra("BLOOD_GLUCOSE_ID", -1)

        if (bloodGlucoseId == -1) {
            showToast("Gagal update data, data tidak ditemukan")
            return
        }


        editGlucoseDataViewModel.updateBloodGlucose(glucose, tanggal, waktu, jenisPemeriksaan, bloodGlucoseId)

    }
    private fun validasiInput(): Boolean {
        return when {
            binding.edtGlucose.text.isNullOrEmpty() -> {
                binding.edtGlucoseLayout.error ="Kadar Glukosa tidak boleh kosong"
                false
            }
            binding.edtWaktu.text.isNullOrEmpty() -> {
                binding.edtWaktuLayout.error = "Waktu tidak boleh kosong"
                false
            }
            binding.edtTanggal.text.isNullOrEmpty() -> {
                binding.edtTanggalLayout.error = "Tanggal tidak boleh kosong"
                false
            }
            binding.edtJenisPemeriksaan.text.isNullOrEmpty() -> {
                binding.edtJenisPemeriksaanLayout.error = "Jenis pemeriksaan tidak boleh kosong"
                false
            }
            else -> true
        }
    }


    private fun setupToolbar() {
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

    }
    private fun formatDate(dateString: String): String {
        try {
            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val date = originalFormat.parse(dateString)
            val targetFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return targetFormat.format(date ?: Date())
        } catch (e: Exception) {
            e.printStackTrace()
            return dateString
        }
    }

    private fun formatTime(timeString: String): String {
        try {
            val originalFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val time = originalFormat.parse(timeString)
            val targetFormat = SimpleDateFormat("HH mm", Locale.getDefault())
            return targetFormat.format(time ?: Date())
        } catch (e: Exception) {
            e.printStackTrace()
            return timeString
        }
    }
    private fun setupAutoCompleteTextView(autoCompleteTextView: AutoCompleteTextView) {
        val items = resources.getStringArray(R.array.jenis_pemeriksaan)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, items)
        autoCompleteTextView.setAdapter(adapter)
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
                    editGlucoseDataViewModel.setTime(hourOfDay, minute)
                },
                currentHour, currentMinute, true
            )
            timePickerDialog.show()
        }

        editGlucoseDataViewModel.selectedTime.observe(this) { time ->
            edtWaktu.setText(time)
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
                editGlucoseDataViewModel.setDate(selectedDate)
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

    private fun deleteData(bloodGlucoseId: Int) {
        editGlucoseDataViewModel.deleteBloodGlucose(bloodGlucoseId)

        editGlucoseDataViewModel.deleteBloodGlucose.observe(this, Observer { result ->
            when (result) {
                is Result.Loading -> {
                }
                is Result.Success -> {
                    showToast("Data berhasil dihapus!")
                    finish()
                }
                is Result.Error -> {
                    showToast("Gagal menghapus data: ${result.error}")
                }
            }
        })
    }
    private fun showDeleteConfirmationDialog() {
        val bloodGlucoseId = intent.getIntExtra("BLOOD_GLUCOSE_ID", -1)

        if (bloodGlucoseId == -1) {
            showToast("Data tidak ditemukan")
            return
        }

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Hapus Data")
            .setMessage("Apakah Anda yakin ingin menghapus data ini?")
            .setPositiveButton("Ya") { dialog, _ ->
                deleteData(bloodGlucoseId)
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete, menu)
        val menuItem = menu?.findItem(R.id.action_delete)
        menuItem?.icon?.let {
            it.setTint(ContextCompat.getColor(this, R.color.white))
        }
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
               showDeleteConfirmationDialog()
            true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}