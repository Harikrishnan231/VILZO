package com.drugfreeindia.app.data.repository

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.drugfreeindia.app.data.local.AppDatabase
import com.drugfreeindia.app.data.secure.SecurePreferencesManager
import com.drugfreeindia.app.domain.model.TrustedContact
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Trusted contacts live ONLY on this device, encrypted with an Android Keystore AES-256-GCM key
 * (EncryptedSharedPreferences). This class has no network dependency by design.
 */
@Singleton
class TrustedContactsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val secure: SecurePreferencesManager
) {
    private val serializer = ListSerializer(TrustedContact.serializer())
    private val _contacts = MutableStateFlow(read())
    val contacts: StateFlow<List<TrustedContact>> = _contacts.asStateFlow()
    val isEncryptionAvailable get() = secure.isAvailable

    private fun read(): List<TrustedContact> =
        secure.getString(KEY)?.let { runCatching { AppJson.decodeFromString(serializer, it) }.getOrNull() } ?: emptyList()

    private fun write(list: List<TrustedContact>) {
        secure.saveString(KEY, AppJson.encodeToString(serializer, list))
        _contacts.value = list
    }

    fun save(contact: TrustedContact): Boolean {
        val list = _contacts.value
        val exists = list.any { it.id == contact.id }
        if (!exists && list.size >= MAX) return false
        write(if (exists) list.map { if (it.id == contact.id) contact else it } else list + contact.copy(id = contact.id.ifBlank { UUID.randomUUID().toString() }))
        return true
    }

    fun delete(id: String) = write(_contacts.value.filterNot { it.id == id })

    fun clear() { secure.remove(KEY); _contacts.value = emptyList() }

    /**
     * One-time migration from the legacy v1 Room DB, where contacts were stored in plain text.
     * Copies them into encrypted storage, then deletes the old database file.
     */
    suspend fun migrateLegacy() = withContext(Dispatchers.IO) {
        val file = context.getDatabasePath(AppDatabase.LEGACY_NAME)
        if (!file.exists()) return@withContext
        runCatching {
            SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READONLY).use { db ->
                db.rawQuery("SELECT id, name, phone, relationship FROM trusted_contacts", null).use { c ->
                    val migrated = buildList {
                        while (c.moveToNext()) add(TrustedContact(c.getString(0), c.getString(1), c.getString(2), c.getString(3) ?: ""))
                    }
                    if (migrated.isNotEmpty() && _contacts.value.isEmpty()) write(migrated.take(MAX))
                }
            }
        }
        context.deleteDatabase(AppDatabase.LEGACY_NAME)
    }

    companion object {
        const val MAX = 5
        private const val KEY = "trusted_contacts_v1"
    }
}
