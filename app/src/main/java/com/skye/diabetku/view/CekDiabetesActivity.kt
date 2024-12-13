package com.skye.diabetku.view

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skye.diabetku.R
import com.skye.diabetku.data.Result
import com.skye.diabetku.data.remote.response.GetData
import com.skye.diabetku.databinding.ActivityCekDiabetesBinding
import com.skye.diabetku.databinding.ActivityPemantauanGulaDarahBinding
import com.skye.diabetku.databinding.DialogLayoutBinding
import com.skye.diabetku.viewmodel.CekDiabetesViewModel
import com.skye.diabetku.viewmodel.PemantauanGulaDarahViewModel
import com.skye.diabetku.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class CekDiabetesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCekDiabetesBinding
    private val cekDiabetesViewModel: CekDiabetesViewModel by viewModels() {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCekDiabetesBinding.inflate(layoutInflater)
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

        cekDiabetesViewModel.getDiabetesCheckData()
        clickButton()
        observeResult()
    }
    private fun observeResult() {
        cekDiabetesViewModel.result.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.btnCek.isEnabled = false
                    binding.btnCek.text = getString(R.string.loading)
                }
                is Result.Success -> {
                    binding.btnCek.isEnabled = true
                    binding.btnCek.text = getString(R.string.simpan)

                    val diagnosis = result.data.data?.result
                    val message = if (diagnosis == "Diabetes") {
                        "Hasil Anda: Diabetes"
                    } else {
                        "Hasil Anda: No Diabetes"
                    }
                    showResultDialog(message)
                }
                is Result.Error -> {
                    binding.btnCek.isEnabled = true
                    binding.btnCek.text = getString(R.string.simpan)
                    showToast(result.error)
                }
            }
        }

        cekDiabetesViewModel.diabetesResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                }
                is Result.Success -> {
                    displayDiabetesResult(result.data.data)
                }
                is Result.Error -> {
                    showToast(result.error)
                }
            }
        }
    }

    private fun showResultDialog(message: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_layout, null)
        val dialogBinding = DialogLayoutBinding.bind(dialogView)

        dialogBinding.tvTitle.text = getString(R.string.hasil_diagnosis)
        dialogBinding.tvMessage.text = message

        val dialog = MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3_Animation)
            .setView(dialogView)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
            setTextColor(ContextCompat.getColor(context, R.color.blue_500))
        }
    }

    private fun displayDiabetesResult(result: GetData?) {
        if(result != null) {
            binding.edtUsia.setText(result.age?.toString() ?: "")
            binding.edtBmi.setText(result.bmi?.toString() ?: "")
            binding.edtKehamilan.setText(result.pregnancies?.toString() ?: "")
            binding.edtTekananDarah.setText(result.bloodPressure?.toString() ?: "")
            binding.edtLipatanKulit.setText(result.skinThickness?.toString() ?: "")
            binding.edtGulaDarah.setText(result.glucose?.toString() ?: "")
            binding.edtInsulin.setText(result.insulin?.toString() ?: "")
            binding.edtDpf.setText(result.diabetesPedigreeFunction?.toString() ?: "")
        }
    }
    private fun clickButton(){
        binding.btnCek.setOnClickListener {
            val pregnancies = binding.edtKehamilan.text.toString()
            val glucose = binding.edtGulaDarah.text.toString()
            val bloodPressure = binding.edtTekananDarah.text.toString()
            val skinThickness = binding.edtLipatanKulit.text.toString()
            val insulin = binding.edtInsulin.text.toString()
            val bmi = binding.edtBmi.text.toString()
            val diabetesPedigreeFunction = binding.edtDpf.text.toString()
            val age = binding.edtUsia.text.toString()

            var isValid = true

            if (pregnancies.isEmpty()) {
                binding.edtKehamilanLayout.error = "Kolom tidak boleh kosong"
                isValid = false
            } else {
                isValid = true
            }

            if (glucose.isEmpty()) {
                binding.edtGulaDarahLayout.error = "Kolom tidak boleh kosong"
                isValid = false
            } else {
                isValid = true
            }

            if (bloodPressure.isEmpty()) {
                binding.edtTekananDarahlayout.error = "Kolom tidak boleh kosong"
                isValid = false
            } else {
                isValid = true
            }

            if (isValid) {
                lifecycleScope.launch {
                    cekDiabetesViewModel.checkDiabetes(
                        pregnancies.toInt(),
                        glucose.toInt(),
                        bloodPressure.toInt(),
                        skinThickness.toInt(),
                        insulin.toInt(),
                        bmi.toDouble(),
                        diabetesPedigreeFunction.toDouble(),
                        age.toInt())
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


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}