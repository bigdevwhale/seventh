package app.seventh.data

import android.content.Context
import app.seventh.domain.Game
import kotlinx.serialization.json.Json
import java.io.File

/** The one family in play, kept as a JSON file; written whole after every move, through a temp file so a crash never leaves half a save. */
class SaveStore(context: Context) {
    private val file = File(context.filesDir, "family.json")
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = false }

    fun load(): Game? = runCatching { if (file.exists()) json.decodeFromString(Game.serializer(), file.readText()) else null }.getOrNull()

    fun save(g: Game) = saveRaw(encode(g))

    @Synchronized
    fun saveRaw(text: String) {
        val tmp = File(file.parentFile, file.name + ".tmp")
        tmp.writeText(text)
        if (!tmp.renameTo(file)) { file.delete(); tmp.renameTo(file) }
    }

    fun encode(g: Game) = json.encodeToString(Game.serializer(), g)
}
