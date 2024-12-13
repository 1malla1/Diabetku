package com.skye.diabetku.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.skye.diabetku.R
import com.skye.diabetku.data.Result
import com.skye.diabetku.data.remote.response.RegisterData
import com.skye.diabetku.databinding.ActivityUbahProfileBinding
import com.skye.diabetku.viewmodel.UbahProfileViewModel
import com.skye.diabetku.viewmodel.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UbahProfileActivity : AppCompatActivity() {
    private val ubahProfileViewModel: UbahProfileViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityUbahProfileBinding
    private var imageUri: Uri? = null
    private var currentProfileImageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUbahProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()

        ubahProfileViewModel.getUserData()
        setupAutoCompleteTextView(binding.edtJenisKelamin)
        setupDatePicker(binding.edtTanggalLahir)

        binding.profileImage.setOnClickListener { showImageOptionsDialog() }
        binding.btnSimpan.setOnClickListener { updateProfile() }

        observeData()
    }
    private fun setToolbar() {
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

    private fun observeData() {
        ubahProfileViewModel.dataResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    displayData(result.data.data)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showToast(result.error)
                }
            }
        }

        ubahProfileViewModel.updateDataResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.btnSimpan.isEnabled = false
                    binding.btnSimpan.text = getString(R.string.loading)
                }
                is Result.Success -> {
                    binding.btnSimpan.isEnabled = true
                    binding.btnSimpan.text = getString(R.string.simpan)
                    showToast("Profil berhasil diperbarui!")
                }
                is Result.Error -> {
                    binding.btnSimpan.isEnabled = true
                    binding.btnSimpan.text = getString(R.string.simpan)
                    showToast(result.error)
                }
            }
        }
    }

    private fun displayData(profile: RegisterData?) {
        profile?.let {
            binding.edtNama.setText(it.name)
            binding.edtEmail.setText(it.email)
            binding.edtTinggi.setText(it.height?.toString() ?: "")
            binding.edtBerat.setText(it.weight?.toString() ?: "")
            ubahProfileViewModel.setJenisKelamin(it.gender?.toString() ?: "")
            val formatDate = it.dateOfBirth?.let { date -> formatDate(date) }
            binding.edtTanggalLahir.setText(formatDate)
            binding.edtJenisKelamin.setText(it.gender?.toString(), false)

            currentProfileImageUrl = it.profileImageUrl.toString()
            Glide.with(this)
                .load(it.profileImageUrl ?: R.drawable.avatar_placeholder)
                .placeholder(R.drawable.avatar_placeholder)
                .into(binding.profileImage)
        }
    }

    private fun formatDate(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(inputDate)
            date?.let { outputFormat.format(it) } ?: inputDate
        } catch (e: Exception) {
            Log.e("DateFormatError", "Error formatting date: ${e.localizedMessage}")
            inputDate
        }
    }

    private fun updateProfile() {
        if (!validasiInput()) return

        val name = binding.edtNama.text.toString()
        val email = binding.edtEmail.text.toString()
        val dateOfBirth = binding.edtTanggalLahir.text.toString()
        val gender = binding.edtJenisKelamin.text.toString()
        val height = binding.edtTinggi.text.toString().toDoubleOrNull() ?: 0.0
        val weight = binding.edtBerat.text.toString().toDoubleOrNull() ?: 0.0

        ubahProfileViewModel.updateUserData(name, email, dateOfBirth, gender, height, weight)

        imageUri?.let { uploadImage(it) }
    }

    private fun showImageOptionsDialog() {
        val options = arrayOf("Lihat", "Ganti Foto")

        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Pilihan Foto Profil")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> viewProfileImage()
                1 -> openPhotoPicker()
            }
        }
        builder.show()
    }
    private fun viewProfileImage() {
        currentProfileImageUrl?.let { imageUrl ->
            val dialogView = layoutInflater.inflate(R.layout.dialog_profile_image, null)

            val profileImageView: ImageView = dialogView.findViewById(R.id.profileImageView)

            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.avatar_placeholder)
                .into(profileImageView)

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            dialog.show()
        } ?: showToast("Foto Profil Tidak Tersedia")
    }
    private fun openPhotoPicker() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            showImage(uri)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .placeholder(R.drawable.avatar_placeholder)
            .into(binding.profileImage)
    }

    private fun uploadImage(uri: Uri) {
        val file = uriToFile(uri, this)
        val fileName = getFileNameFromUri(uri) ?: "default_image.jpg"
        val contentType = contentResolver.getType(uri) ?: "image/jpeg"

        val photoPart = file.asRequestBody(contentType.toMediaTypeOrNull())
            .let { MultipartBody.Part.createFormData("profilePicture", fileName, it) }

        ubahProfileViewModel.uploadProfileImage(photoPart).observe(this) { result ->
            when (result) {
                is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val imageUrl = result.data
                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.avatar_placeholder)
                        .into(binding.profileImage)
                    showToast("Gambar berhasil diunggah!")
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Glide.with(this)
                        .load(currentProfileImageUrl ?: R.drawable.avatar_placeholder)
                        .placeholder(R.drawable.avatar_placeholder)
                        .into(binding.profileImage)
                    showToast("Gagal mengunggah gambar: ${result.error}")
                }
            }
        }
    }

    private fun uriToFile(selectedUri: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val fileName = getFileNameFromUri(selectedUri) ?: "temp.jpg"
        val tempFile = File(context.cacheDir, fileName)
        val inputStream = contentResolver.openInputStream(selectedUri) ?: return tempFile
        val outputStream = FileOutputStream(tempFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        return tempFile
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        val documentFile = androidx.documentfile.provider.DocumentFile.fromSingleUri(this, uri)
        documentFile?.let {
            Log.d("FileDebug", "DocumentFile Name: ${it.name}")
            return it.name
        }

        val projection = arrayOf(android.provider.MediaStore.MediaColumns.DISPLAY_NAME)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndexOrThrow(android.provider.MediaStore.MediaColumns.DISPLAY_NAME)
            if (it.moveToFirst()) {
                return it.getString(nameIndex)
            }
        }

        return "default_image_${System.currentTimeMillis()}.jpg"
    }

    private fun validasiInput(): Boolean {
        val genderItems = resources.getStringArray(R.array.simple_items)
        return when {
            binding.edtNama.text.isNullOrEmpty() -> {
                binding.edtNamaLayout.error = "Nama tidak boleh kosong"
                false
            }
            binding.edtEmail.text.isNullOrEmpty() -> {
               binding.edtEmailLayout.error = "Email tidak boleh kosong"
                false
            }
            binding.edtTanggalLahir.text.isNullOrEmpty() -> {
                binding.edtTanggalLahirLayout.error = "Tanggal lahir tidak boleh kosong"
                false
            }
            binding.edtJenisKelamin.text.isNullOrEmpty() || !genderItems.contains(binding.edtJenisKelamin.text.toString()) -> {
                binding.edtJenisKelaminLayout.error = "Jenis kelamin tidak valid atau kosong"
                binding.edtJenisKelamin.setTextColor(ContextCompat.getColor(this, R.color.red_500))
                false
            }
            else -> {
                binding.edtJenisKelaminLayout.error = null
                binding.edtJenisKelamin.setTextColor(ContextCompat.getColor(this, R.color.gray_700))
                true
            }
        }
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
                ubahProfileViewModel.setDate(selectedDate)
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

    private fun setupAutoCompleteTextView(autoCompleteTextView: AutoCompleteTextView) {
        val items = resources.getStringArray(R.array.simple_items)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, items)
        autoCompleteTextView.setAdapter(adapter)

        autoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val input = autoCompleteTextView.text.toString()
                if (!items.contains(input)) {
                    autoCompleteTextView.setText("")
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
