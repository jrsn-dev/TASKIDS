package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest

class AppPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("kid_task_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_SELECTED_CHILD_ID = "selected_child_id"
        private const val KEY_PARENTAL_PIN_HASH = "parental_pin_hash"
        private const val KEY_PIN_FAILED_ATTEMPTS = "pin_failed_attempts"
        private const val KEY_PIN_LOCK_UNTIL = "pin_lock_until"

        private const val KEY_ACTIVE_TASK_ID = "active_task_id"
        private const val KEY_TIMER_STARTED_AT = "timer_started_at"
        private const val KEY_TIMER_END_AT = "timer_end_at"
        private const val KEY_TIMER_REMAINING_SECONDS = "timer_remaining_seconds"
        private const val KEY_TIMER_PAUSED = "timer_paused"

        private const val DEFAULT_PIN = "0000"
    }

    init {
        val currentHash = prefs.getString(KEY_PARENTAL_PIN_HASH, null)
        val legacyDefaultHash = hashPin("1234")
        when {
            currentHash == null -> {
                prefs.edit().putString(KEY_PARENTAL_PIN_HASH, hashPin(DEFAULT_PIN)).apply()
            }
            currentHash == legacyDefaultHash -> {
                // One-time migration from the previous factory default.
                prefs.edit().putString(KEY_PARENTAL_PIN_HASH, hashPin(DEFAULT_PIN)).apply()
            }
        }
    }

    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    var selectedChildId: Long
        get() = prefs.getLong(KEY_SELECTED_CHILD_ID, 1L)
        set(value) = prefs.edit().putLong(KEY_SELECTED_CHILD_ID, value).apply()

    fun verifyPin(pin: String): Boolean {
        return hashPin(pin) == prefs.getString(KEY_PARENTAL_PIN_HASH, hashPin(DEFAULT_PIN))
    }

    fun updatePin(pin: String) {
        require(pin.length == 4 && pin.all { it.isDigit() })
        prefs.edit().putString(KEY_PARENTAL_PIN_HASH, hashPin(pin)).apply()
    }

    var pinFailedAttempts: Int
        get() = prefs.getInt(KEY_PIN_FAILED_ATTEMPTS, 0)
        set(value) = prefs.edit().putInt(KEY_PIN_FAILED_ATTEMPTS, value).apply()

    var pinLockUntil: Long
        get() = prefs.getLong(KEY_PIN_LOCK_UNTIL, 0L)
        set(value) = prefs.edit().putLong(KEY_PIN_LOCK_UNTIL, value).apply()

    fun persistTimer(
        taskId: Int,
        startedAt: Long,
        endAt: Long,
        remainingSeconds: Int,
        paused: Boolean
    ) {
        prefs.edit()
            .putInt(KEY_ACTIVE_TASK_ID, taskId)
            .putLong(KEY_TIMER_STARTED_AT, startedAt)
            .putLong(KEY_TIMER_END_AT, endAt)
            .putInt(KEY_TIMER_REMAINING_SECONDS, remainingSeconds)
            .putBoolean(KEY_TIMER_PAUSED, paused)
            .apply()
    }

    val activeTaskId: Int
        get() = prefs.getInt(KEY_ACTIVE_TASK_ID, 0)

    val timerStartedAt: Long
        get() = prefs.getLong(KEY_TIMER_STARTED_AT, 0L)

    val timerEndAt: Long
        get() = prefs.getLong(KEY_TIMER_END_AT, 0L)

    val timerRemainingSeconds: Int
        get() = prefs.getInt(KEY_TIMER_REMAINING_SECONDS, 0)

    val timerPaused: Boolean
        get() = prefs.getBoolean(KEY_TIMER_PAUSED, false)

    fun clearTimer() {
        prefs.edit()
            .remove(KEY_ACTIVE_TASK_ID)
            .remove(KEY_TIMER_STARTED_AT)
            .remove(KEY_TIMER_END_AT)
            .remove(KEY_TIMER_REMAINING_SECONDS)
            .remove(KEY_TIMER_PAUSED)
            .apply()
    }

    private fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
