package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences

class AppPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("kid_task_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_YOUTUBE_TIME_LIMIT = "youtube_time_limit"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_PARENTAL_PIN = "parental_pin"
    }

    var youtubeTimeLimit: Int
        get() = prefs.getInt(KEY_YOUTUBE_TIME_LIMIT, 30) // default 30 mins
        set(value) = prefs.edit().putInt(KEY_YOUTUBE_TIME_LIMIT, value).apply()

    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    var parentalPin: String
        get() = prefs.getString(KEY_PARENTAL_PIN, "1234") ?: "1234"
        set(value) = prefs.edit().putString(KEY_PARENTAL_PIN, value).apply()
}
