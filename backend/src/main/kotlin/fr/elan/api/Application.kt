package fr.elan.api

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.HttpStatusCode
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.sql.Connection
import java.io.File
import java.time.LocalDate
import javax.sql.DataSource

@Serializable
data class IdeaResponse(
    val id: Long,
    val date: String,
    val title: String,
    val text: String,
    val source: String,
)

@Serializable
data class HealthResponse(val status: String, val database: String)

@Serializable
data class ErrorResponse(val error: String)

@Serializable
data class AppUpdateResponse(val versionCode: Int, val versionName: String, val downloadPageUrl: String)

private data class Idea(val id: Long, val title: String, val text: String, val source: String)

fun main() {
    embeddedServer(Netty, host = "0.0.0.0", port = System.getenv("PORT")?.toIntOrNull() ?: 8080) {
        module()
    }.start(wait = true)
}

fun Application.module(dataSource: DataSource = createDataSource()) {
    install(CallLogging)
    install(ContentNegotiation) {
        json(Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        })
    }

    val repository = IdeaRepository(dataSource)
    routing {
        get("/health") {
            val databaseOk = runCatching {
                dataSource.connection.use { it.prepareStatement("SELECT 1").executeQuery().use { result -> result.next() } }
            }.getOrDefault(false)
            call.respond(
                if (databaseOk) HttpStatusCode.OK else HttpStatusCode.ServiceUnavailable,
                HealthResponse("ok", if (databaseOk) "ok" else "unavailable"),
            )
        }

        get("/api/v1/app-update") {
            val update = configuredAppUpdate()
            if (update == null) {
                call.respond(HttpStatusCode.ServiceUnavailable, ErrorResponse("Aucune APK de mise à jour n'est configurée."))
            } else {
                call.respond(AppUpdateResponse(update.versionCode, update.versionName, "${update.publicBaseUrl}/app/download"))
            }
        }

        get("/app/download") {
            val update = configuredAppUpdate()
            if (update == null) {
                call.respond(HttpStatusCode.ServiceUnavailable, ErrorResponse("Aucune APK de mise à jour n'est disponible."))
            } else {
                call.respondText(
                    """<!doctype html><html lang="fr"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Élan ${update.versionName}</title><body style="font:16px system-ui;max-width:640px;margin:12vh auto;padding:24px;color:#20332b"><h1>Élan ${update.versionName}</h1><p>Une nouvelle version de l’application est disponible.</p><p><a style="display:inline-block;padding:14px 20px;background:#316b52;color:white;border-radius:10px;text-decoration:none" href="/app/download/apk">Télécharger l’APK</a></p><p>Après le téléchargement, ouvre le fichier et autorise l’installation si Android le demande.</p></body></html>""",
                    ContentType.Text.Html,
                )
            }
        }

        get("/app/download/apk") {
            val update = configuredAppUpdate()
            if (update == null) {
                call.respond(HttpStatusCode.ServiceUnavailable, ErrorResponse("Aucune APK de mise à jour n'est disponible."))
            } else {
                call.response.headers.append(HttpHeaders.ContentType, "application/vnd.android.package-archive")
                call.response.headers.append(HttpHeaders.ContentDisposition, "attachment; filename=\"elan-${update.versionName}.apk\"")
                call.respondFile(update.apkFile)
            }
        }

        get("/api/v1/daily-ideas/today") {
            call.respondIdea(repository, LocalDate.now())
        }

        get("/api/v1/daily-ideas") {
            val rawDate = call.request.queryParameters["date"]
            val date = rawDate?.let {
                runCatching { LocalDate.parse(it) }.getOrNull()
            }
            if (rawDate != null && date == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Le paramètre date doit être au format AAAA-MM-JJ."))
            } else {
                call.respondIdea(repository, date ?: LocalDate.now())
            }
        }
    }
}

private data class ConfiguredAppUpdate(
    val versionCode: Int,
    val versionName: String,
    val publicBaseUrl: String,
    val apkFile: File,
)

private fun configuredAppUpdate(): ConfiguredAppUpdate? {
    val versionCode = System.getenv("APP_UPDATE_VERSION_CODE")?.toIntOrNull()?.takeIf { it > 0 } ?: return null
    val versionName = System.getenv("APP_UPDATE_VERSION_NAME")?.takeIf { Regex("^\\d+\\.\\d+\\.\\d+$").matches(it) } ?: return null
    val publicBaseUrl = System.getenv("APP_UPDATE_PUBLIC_BASE_URL")?.trimEnd('/')?.takeIf { it.startsWith("https://") } ?: return null
    val apkFile = File(System.getenv("APP_UPDATE_APK_PATH") ?: "/app/updates/elan.apk")
    if (!apkFile.isFile || !apkFile.canRead()) return null
    return ConfiguredAppUpdate(versionCode, versionName, publicBaseUrl, apkFile)
}

private suspend fun ApplicationCall.respondIdea(
    repository: IdeaRepository,
    date: LocalDate,
) {
    val idea = repository.ideaFor(date)
    if (idea == null) {
        respond(HttpStatusCode.NotFound, ErrorResponse("Aucune idée active disponible."))
    } else {
        respond(IdeaResponse(idea.id, date.toString(), idea.title, idea.text, idea.source))
    }
}

private fun createDataSource(): HikariDataSource {
    val config = HikariConfig().apply {
        jdbcUrl = System.getenv("DATABASE_URL")
            ?: "jdbc:postgresql://${System.getenv("POSTGRES_HOST") ?: "localhost"}:${System.getenv("POSTGRES_PORT") ?: "5432"}/${System.getenv("POSTGRES_DB") ?: "elan"}"
        username = System.getenv("POSTGRES_USER") ?: "elan"
        password = System.getenv("POSTGRES_PASSWORD")
            ?: error("POSTGRES_PASSWORD doit être défini")
        maximumPoolSize = 5
        minimumIdle = 1
        connectionTimeout = 10_000
    }
    return HikariDataSource(config)
}

private class IdeaRepository(private val dataSource: DataSource) {
    fun ideaFor(date: LocalDate): Idea? = dataSource.connection.use { connection ->
        val count = connection.activeIdeasCount()
        if (count == 0L) {
            null
        } else {
            val offset = Math.floorMod(date.toEpochDay(), count).toInt()
            connection.prepareStatement(
                """
                SELECT id, title, text, source
                FROM daily_ideas
                WHERE active = TRUE AND language = 'fr'
                ORDER BY id
                OFFSET ? LIMIT 1
                """.trimIndent(),
            ).use { statement ->
                statement.setInt(1, offset)
                statement.executeQuery().use { result ->
                    if (!result.next()) null
                    else Idea(result.getLong("id"), result.getString("title"), result.getString("text"), result.getString("source"))
                }
            }
        }
    }

    private fun Connection.activeIdeasCount(): Long {
        var count = 0L
        prepareStatement("SELECT COUNT(*) FROM daily_ideas WHERE active = TRUE AND language = 'fr'").use { statement ->
            statement.executeQuery().use { result ->
                result.next()
                count = result.getLong(1)
            }
        }
        return count
    }
}
