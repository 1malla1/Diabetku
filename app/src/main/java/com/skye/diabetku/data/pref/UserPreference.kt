package com.skye.diabetku.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.skye.diabetku.data.model.UserModel
import kotlinx.coroutines.flow.first

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveUserId(userId: Int) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
        }
    }

    suspend fun getUserId(): Int {
        val preferences = dataStore.data.first()
        return preferences[USER_ID_KEY] ?: -1
    }

    suspend fun saveUser(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = user.token
            preferences[IS_LOGIN_KEY] = user.isLogin
        }
    }

    suspend fun saveAccessToken(accessToken: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
        }
    }

    suspend fun getAccessToken(): String {
        val preferences = dataStore.data.first()
        return preferences[ACCESS_TOKEN_KEY] ?: ""
    }

    suspend fun saveRefreshToken(refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    suspend fun getRefreshToken(): String {
        val preferences = dataStore.data.first()
        return preferences[REFRESH_TOKEN_KEY] ?: ""
    }


    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[ACCESS_TOKEN_KEY] ?: "",
                preferences[IS_LOGIN_KEY] ?: false
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[IS_LOGIN_KEY] = false
            preferences[ACCESS_TOKEN_KEY] = ""
            preferences[USER_ID_KEY] = -1
            preferences[REFRESH_TOKEN_KEY] = ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refreshToken")
        private val USER_ID_KEY = intPreferencesKey("user_id")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}