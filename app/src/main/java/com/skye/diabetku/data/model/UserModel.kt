package com.skye.diabetku.data.model

data class UserModel(
    val token: String = "",
    val isLogin: Boolean = false,
)


data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val date_of_birth: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RefreshTokenRequest(
    val token: String
)

data class BloodGlucoseRequest(
    val userId: Int,
    val glucoseValue: Int,
    val testType: String,
    val testDate: String,
    val testTime: String
)

data class DiabetesCheckRequest(
    val pregnancies: Int,
    val glucose: Int,
    val blood_pressure: Int,
    val skin_thickness: Int,
    val insulin: Int,
    val bmi: Double,
    val diabetes_pedigree_function: Double,
    val age: Int
)


data class UpdateDataRequest(
    val gender: String,
    val date_of_birth: String,
    val name: String,
    val weight: Double,
    val email: String,
    val height: Double,
    val profile_image_url: String? = null
)

data class UpdateGlucoseRequest(
    val testTime: String,
    val userId: Int,
    val glucoseValue: Double,
    val testDate: String,
    val testType: String
)

data class FoodItem(
    val name: String,
    val carbohydrate: Double,
    val calories: Double,
    val estimatedGlucose: Double
)

data class FoodRequest(
    val glucose_level: Int
)
