package com.therealprince.untildone.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

data class ReleaseInfo(
    val version: String,
    val tagName: String,
    val name: String,
    val downloadUrl: String,
    val sizeBytes: Long,
    val body: String,
    val htmlUrl: String,
)

private data class GhAsset(
    val name: String,
    @SerializedName("browser_download_url") val downloadUrl: String,
    val size: Long,
    @SerializedName("content_type") val contentType: String,
)

private data class GhRelease(
    @SerializedName("tag_name") val tagName: String,
    val name: String?,
    val body: String?,
    @SerializedName("html_url") val htmlUrl: String,
    val assets: List<GhAsset>,
    val draft: Boolean = false,
    val prerelease: Boolean = false,
)

object UpdateManager {
    private const val RELEASES_API =
        "https://api.github.com/repos/therealprince/UntilDone/releases/latest"

    fun getCurrentVersionName(context: Context): String {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        return info.versionName.orEmpty()
    }

    suspend fun fetchLatestRelease(): ReleaseInfo? = withContext(Dispatchers.IO) {
        var conn: HttpURLConnection? = null
        try {
            conn = (URL(RELEASES_API).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("Accept", "application/vnd.github+json")
                setRequestProperty("User-Agent", "UntilDone-Updater")
                connectTimeout = 15_000
                readTimeout = 15_000
            }
            if (conn.responseCode !in 200..299) return@withContext null
            val json = conn.inputStream.bufferedReader().use { it.readText() }
            val release = Gson().fromJson(json, GhRelease::class.java) ?: return@withContext null
            if (release.draft || release.prerelease) return@withContext null
            val apk = release.assets.firstOrNull {
                it.name.endsWith(".apk", ignoreCase = true)
            } ?: return@withContext null
            ReleaseInfo(
                version = normalizeVersion(release.tagName),
                tagName = release.tagName,
                name = release.name?.takeIf { it.isNotBlank() } ?: release.tagName,
                downloadUrl = apk.downloadUrl,
                sizeBytes = apk.size,
                body = release.body.orEmpty(),
                htmlUrl = release.htmlUrl,
            )
        } catch (e: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    fun isNewerVersion(latest: String, current: String): Boolean {
        val l = parseVersion(latest)
        val c = parseVersion(current)
        if (l.isEmpty()) return false
        val len = maxOf(l.size, c.size)
        for (i in 0 until len) {
            val a = l.getOrElse(i) { 0 }
            val b = c.getOrElse(i) { 0 }
            if (a != b) return a > b
        }
        return false
    }

    private fun normalizeVersion(s: String): String =
        s.trim().removePrefix("v").removePrefix("V")

    private fun parseVersion(s: String): List<Int> =
        normalizeVersion(s).split('.', '-', '_')
            .mapNotNull { it.toIntOrNull() }

    suspend fun downloadApk(
        context: Context,
        url: String,
        expectedSize: Long,
        onProgress: (downloaded: Long, total: Long) -> Unit,
    ): File = withContext(Dispatchers.IO) {
        val dir = File(context.getExternalFilesDir(null), "updates").apply { mkdirs() }
        dir.listFiles()?.forEach { if (it.name.endsWith(".apk")) it.delete() }
        val outFile = File(dir, "UntilDone-update.apk")
        var conn: HttpURLConnection? = null
        try {
            conn = (URL(url).openConnection() as HttpURLConnection).apply {
                instanceFollowRedirects = true
                connectTimeout = 30_000
                readTimeout = 60_000
                setRequestProperty("User-Agent", "UntilDone-Updater")
            }
            val reported = conn.contentLengthLong
            val total = if (reported > 0) reported else expectedSize
            conn.inputStream.use { input ->
                FileOutputStream(outFile).use { output ->
                    val buf = ByteArray(64 * 1024)
                    var read: Int
                    var written = 0L
                    var lastEmit = 0L
                    while (input.read(buf).also { read = it } != -1) {
                        output.write(buf, 0, read)
                        written += read
                        val now = System.currentTimeMillis()
                        if (now - lastEmit > 100 || written == total) {
                            onProgress(written, total)
                            lastEmit = now
                        }
                    }
                    onProgress(written, total)
                }
            }
            outFile
        } finally {
            conn?.disconnect()
        }
    }

    fun canInstallPackages(context: Context): Boolean =
        context.packageManager.canRequestPackageInstalls()

    fun installPermissionIntent(context: Context): Intent =
        Intent(
            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
            Uri.parse("package:${context.packageName}")
        )

    fun installApk(context: Context, apk: File) {
        val uri = FileProvider.getUriForFile(
            context, "${context.packageName}.fileprovider", apk
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
    }
}
