package com.skye.diabetku.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skye.diabetku.data.pref.UserPreference
import com.skye.diabetku.data.pref.dataStore
import com.skye.diabetku.data.repository.WithAuthRepository
import com.skye.diabetku.data.repository.UserRepository
import com.skye.diabetku.di.Injection

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val withAuthRepository: WithAuthRepository,
    private val userPreference: UserPreference
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(userRepository) as T
        }
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userRepository) as T
        }
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(userRepository,withAuthRepository) as T
        }
        if (modelClass.isAssignableFrom(AddGlucoseDataViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddGlucoseDataViewModel(withAuthRepository, userPreference) as T
        }
        if (modelClass.isAssignableFrom(PemantauanGulaDarahViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PemantauanGulaDarahViewModel(withAuthRepository) as T
        }
        if (modelClass.isAssignableFrom(CekDiabetesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CekDiabetesViewModel(withAuthRepository, userPreference) as T
        }
        if (modelClass.isAssignableFrom(UbahProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UbahProfileViewModel(withAuthRepository) as T
        }
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(withAuthRepository, userRepository) as T
        }
        if (modelClass.isAssignableFrom(EditGlucoseDataViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditGlucoseDataViewModel(withAuthRepository, userPreference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val userRepository = Injection.provideRepository(context)
                val bloodGlucoseRepository = Injection.provideBloodGlucoseRepository(context)
                val userPreference = UserPreference.getInstance(context.dataStore)

                ViewModelFactory(
                    userRepository,
                    bloodGlucoseRepository,
                    userPreference
                ).also { INSTANCE = it }
            }
        }
    }
}