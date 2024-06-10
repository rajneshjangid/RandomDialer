package com.phone.randomdialer.Utils

import android.content.SharedPreferences

object AppPreference {
    private const val CURRENT_CALL_INDEX = "current_call_index"
    const val LAST_API_FETCH_TIME = "api_fetch_time"
    fun getCurrentCallIndex(sharedPreferences: SharedPreferences) =
        sharedPreferences.getInt(CURRENT_CALL_INDEX, -1)

    fun setCurrentCallIndex(sharedPreferences: SharedPreferences, index: Int) {
        sharedPreferences.edit().putInt(
            CURRENT_CALL_INDEX, index
        ).apply()
    }

    fun getLastApiCallTime(sharedPreferences: SharedPreferences): Long {
        return sharedPreferences.getLong(LAST_API_FETCH_TIME, 0)
    }

    fun updateLastApiCallTime(sharedPreferences: SharedPreferences, time: Long) {
        sharedPreferences.edit().putLong(LAST_API_FETCH_TIME, time).apply()
    }
}