package com.skye.diabetku.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.skye.diabetku.R
import com.skye.diabetku.data.Result
import com.skye.diabetku.data.remote.response.RegisterData
import com.skye.diabetku.databinding.FragmentProfileBinding
import com.skye.diabetku.viewmodel.ProfileViewModel
import com.skye.diabetku.viewmodel.ViewModelFactory

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.getUserData()
        observeDataResult()
        observeLogoutResult()


        val fabUbahProfile: ExtendedFloatingActionButton = binding.fabUbahProfile

        fabUbahProfile.setOnClickListener {
            val intent = Intent(requireContext(), UbahProfileActivity::class.java)
            startActivity(intent)
        }
        binding.buttonLogout.setOnClickListener {
            showLogoutConfirmDialog()
        }

    }
    private fun observeDataResult() {
        profileViewModel.dataResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
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
    }

    private fun displayData(profile: RegisterData?) {
        if(profile != null) {
            binding.tvUserName.text = profile.name ?: getString(R.string.nama_lengkap)
                binding.tvEmail.text = profile.email ?: getString(R.string.email)
                binding.tvUsia.text = profile.age?.let { "$it tahun" } ?: getString(R.string.tidak_ada_data)
                binding.tvTinggi.text = profile.height?.let { "$it cm" } ?: getString(R.string.tidak_ada_data)
                binding.tvBerat.text = profile.weight?.let { "$it kg" } ?: getString(R.string.tidak_ada_data)

            if (profile.profileImageUrl is String) {
                Glide.with(this)
                    .load(profile.profileImageUrl)
                    .placeholder(R.drawable.avatar_placeholder)
                    .into(binding.profileImage)
            } else {
                Glide.with(this)
                    .load(R.drawable.avatar_placeholder)
                    .into(binding.profileImage)
            }
        }


    }
    private fun showLogoutConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                profileViewModel.logout()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.dialog_background))
            .create()

        .show()
    }

    private fun observeLogoutResult() {
        lifecycleScope.launchWhenStarted {
            profileViewModel.logoutResult.collect { result ->
                when (result) {
                    is Result.Loading -> {
                    }
                    is Result.Success -> {
                        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        showToast("Logout Berhasil!")
                        activity?.finish()
                    }
                    is Result.Error -> {
                        showToast(result.error)
                    }
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onResume() {
        super.onResume()
        profileViewModel.getUserData()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}