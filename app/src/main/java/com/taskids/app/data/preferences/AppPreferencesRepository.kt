package com.taskids.app.data.preferences

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest
import java.util.UUID

data class PersistedTimer(
    val taskId: Int,
    val executionId: Long,
    val totalSeconds: Int,
    val endAtMillis: Long,
    val pausedRemainingSeconds: Int,
    val running: Boolean
)

data class PinVerificationResult(
    val success: Boolean,
    val lockedForMillis: Long = 0L
)

class AppPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("taskids_secure_prefs", Context.MODE_PRIVATE)

    init {
        migrateLegacyPinIfNeeded(context)
    }

    var currentChildId: Int
        get() = prefs.getInt(KEY_CURRENT_CHILD_ID, 1)
        set(value) = prefs.edit().putInt(KEY_CURRENT_CHILD_ID, value).apply()

    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    var lastDailyResetDate: String
        get() = prefs.getString(KEY_LAST_DAILY_RESET, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LAST_DAILY_RESET, value).apply()

    val isParentalPinConfigured: Boolean
        get() = prefs.getBoolean(KEY_PIN_CONFIGURED, false) && prefs.contains(KEY_PIN_HASH)

    fun verifyPin(pin: String, now: Long = System.currentTimeMillis()): PinVerificationResult {
        if (!isParentalPinConfigured) return PinVerificationResult(success = false)
        val lockedUntil = prefs.getLong(KEY_PIN_LOCKED_UNTIL, 0L)
        if (lockedUntil > now) {
            return PinVerificationResult(success = false, lockedForMillis = lockedUntil - now)
        }

        val salt = pinSalt()
        val expected = prefs.getString(KEY_PIN_HASH, "") ?: ""
        val success = secureEquals(expected, hashPin(pin, salt))

        if (success) {
            prefs.edit()
                .putInt(KEY_PIN_ATTEMPTS, 0)
                .putLong(KEY_PIN_LOCKED_UNTIL, 0L)
                .apply()
            return PinVerificationResult(success = true)
        }

        val attempts = prefs.getInt(KEY_PIN_ATTEMPTS, 0) + 1
        if (attempts >= MAX_PIN_ATTEMPTS) {
            prefs.edit()
                .putInt(KEY_PIN_ATTEMPTS, 0)
                .putLong(KEY_PIN_LOCKED_UNTIL, now + PIN_LOCK_MILLIS)
                .apply()
            return PinVerificationResult(success = false, lockedForMillis = PIN_LOCK_MILLIS)
        }

        prefs.edit().putInt(KEY_PIN_ATTEMPTS, attempts).apply()
        return PinVerificationResult(success = false)
    }

    fun updateParentalPin(pin: String): Boolean {
        if (pin.length != 4 || !pin.all(Char::isDigit)) return false
        prefs.edit()
            .putString(KEY_PIN_HASH, hashPin(pin, pinSalt()))
            .putBoolean(KEY_PIN_CONFIGURED, true)
            .apply()
        return true
    }

    fun saveTimer(timer: PersistedTimer) {
        prefs.edit()
            .putInt(KEY_TIMER_TASK_ID, timer.taskId)
            .putLong(KEY_TIMER_EXECUTION_ID, timer.executionId)
            .putInt(KEY_TIMER_TOTAL_SECONDS, timer.totalSeconds)
            .putLong(KEY_TIMER_END_AT, timer.endAtMillis)
            .putInt(KEY_TIMER_PAUSED_REMAINING, timer.pausedRemainingSeconds)
            .putBoolean(KEY_TIMER_RUNNING, timer.running)
            .apply()
    }

    fun restoreTimer(): PersistedTimer? {
        val taskId = prefs.getInt(KEY_TIMER_TASK_ID, 0)
        val executionId = prefs.getLong(KEY_TIMER_EXECUTION_ID, 0L)
        val total = prefs.getInt(KEY_TIMER_TOTAL_SECONDS, 0)
        if (taskId <= 0 || executionId <= 0 || total <= 0) return null
        return PersistedTimer(
            taskId = taskId,
            executionId = executionId,
            totalSeconds = total,
            endAtMillis = prefs.getLong(KEY_TIMER_END_AT, 0L),
            pausedRemainingSeconds = prefs.getInt(KEY_TIMER_PAUSED_REMAINING, total),
            running = prefs.getBoolean(KEY_TIMER_RUNNING, false)
        )
    }

    fun clearTimer() {
        prefs.edit()
            .remove(KEY_TIMER_TASK_ID)
            .remove(KEY_TIMER_EXECUTION_ID)
            .remove(KEY_TIMER_TOTAL_SECONDS)
            .remove(KEY_TIMER_END_AT)
            .remove(KEY_TIMER_PAUSED_REMAINING)
            .remove(KEY_TIMER_RUNNING)
            .apply()
    }

    private fun migrateLegacyPinIfNeeded(context: Context) {
        if (prefs.contains(KEY_PIN_HASH)) return

        val legacyPrefs = context.getSharedPreferences("kid_task_prefs", Context.MODE_PRIVATE)
        val legacyPin = legacyPrefs.getString("parental_pin", null)
            ?.takeIf { it.length == 4 && it.all(Char::isDigit) }
        if (legacyPin != null) {
            val salt = pinSalt()
            prefs.edit()
                .putString(KEY_PIN_HASH, hashPin(legacyPin, salt))
                .putBoolean(KEY_PIN_CONFIGURED, true)
                .apply()
            legacyPrefs.edit().remove("parental_pin").apply()
        } else {
            prefs.edit().putBoolean(KEY_PIN_CONFIGURED, false).apply()
        }
    }

    private fun pinSalt(): String {
        val existing = prefs.getString(KEY_PIN_SALT, null)
        if (!existing.isNullOrBlank()) return existing
        val created = UUID.randomUUID().toString()
        prefs.edit().putString(KEY_PIN_SALT, created).commit()
        return created
    }

    private fun hashPin(pin: String, salt: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest("$salt:$pin".toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun secureEquals(a: String, b: String): Boolean {
        return MessageDigest.isEqual(a.toByteArray(), b.toByteArray())
    }

    companion object {
        private const val MAX_PIN_ATTEMPTS = 3
        private const val PIN_LOCK_MILLIS = 30_000L

        private const val KEY_CURRENT_CHILD_ID = "current_child_id"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_LAST_DAILY_RESET = "last_daily_reset"
        private const val KEY_PIN_HASH = "parental_pin_hash"
        private const val KEY_PIN_CONFIGURED = "parental_pin_configured"
        private const val KEY_PIN_SALT = "parental_pin_salt"
        private const val KEY_PIN_ATTEMPTS = "parental_pin_attempts"
        private const val KEY_PIN_LOCKED_UNTIL = "parental_pin_locked_until"
        private const val KEY_TIMER_TASK_ID = "timer_task_id"
        private const val KEY_TIMER_EXECUTION_ID = "timer_execution_id"
        private const val KEY_TIMER_TOTAL_SECONDS = "timer_total_seconds"
        private const val KEY_TIMER_END_AT = "timer_end_at"
        private const val KEY_TIMER_PAUSED_REMAINING = "timer_paused_remaining"
        private const val KEY_TIMER_RUNNING = "timer_running"
    }
}
