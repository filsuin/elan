package fr.elan.app.data

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import fr.elan.app.domain.AppSnapshot
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

object SnapshotSerializer : Serializer<AppSnapshot> {
    override val defaultValue = AppSnapshot()
    override suspend fun readFrom(input: InputStream): AppSnapshot {
        // I/O errors propagate as such. Only invalid JSON/schema is reported as corruption.
        val output = ByteArrayOutputStream()
        val buffer = ByteArray(8192)
        while (true) {
            val count = input.read(buffer)
            if (count < 0) break
            if (output.size().toLong() + count > SnapshotJson.MAX_IMPORT_BYTES) {
                throw CorruptionException("Le journal local dépasse la taille maximale autorisée.")
            }
            output.write(buffer, 0, count)
        }
        val text = output.toString(Charsets.UTF_8.name())
        return try { SnapshotJson.decode(text) }
        catch (error: Exception) {
            throw CorruptionException("Le journal ne peut pas être lu. Aucune donnée n'a été effacée.", error)
        }
    }
    override suspend fun writeTo(t: AppSnapshot, output: OutputStream) {
        output.write(SnapshotJson.encode(t).toByteArray(Charsets.UTF_8))
    }
}

class ElanRepository(context: Context) {
    private val store = DataStoreFactory.create(
        serializer = SnapshotSerializer,
        produceFile = { context.applicationContext.dataStoreFile("elan-journal-v1.json") },
    )
    val data = store.data
    suspend fun update(transform: (AppSnapshot) -> AppSnapshot) { store.updateData { transform(it) } }
    suspend fun replace(snapshot: AppSnapshot) { store.updateData { snapshot } }
}
