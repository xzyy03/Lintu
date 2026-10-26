package me.rerere.rikkahub.utils

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.core.net.toUri
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.rerere.common.http.await
import me.rerere.rikkahub.AppScope
import me.rerere.rikkahub.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request

private const val GITHUB_OWNER = "xzyy03"
private const val GITHUB_REPO = "Lintu"
private const val API_URL = "https://api.github.com/repos/$GITHUB_OWNER/$GITHUB_REPO/releases/latest"

class UpdateChecker(
    private val client: OkHttpClient,
    appScope: AppScope,
) {
    private val json = Json { ignoreUnknownKeys = true }

    private val state = MutableStateFlow<UiState<UpdateInfo>>(UiState.Idle)

    /** 最近一次检查结果；首次由 [refresh] 触发 */
    val updateState: StateFlow<UiState<UpdateInfo>> = state.asStateFlow()

    private val scope = appScope

    private var job: Job? = null

    /**
     * 主动检查更新。关于页点「版本更新」时调用，会重新请求一次并把结果写进 [updateState]。
     * 已有请求在跑时复用同一个 Job，避免并发重复请求。
     */
    fun refresh() {
        if (job?.isActive == true) return
        job = scope.launch {
            state.value = UiState.Loading
            state.value = try {
                UiState.Success(fetch())
            } catch (e: Exception) {
                UiState.Error(e)
            }
        }
    }

    private suspend fun fetch(): UpdateInfo {
        val response = client.newCall(
            Request.Builder()
                .url(API_URL)
                .get()
                .addHeader(
                    "User-Agent",
                    "Lintu ${BuildConfig.VERSION_NAME} #${BuildConfig.VERSION_CODE}"
                )
                .addHeader("Accept", "application/vnd.github+json")
                .build()
        ).await()
        if (!response.isSuccessful) {
            throw Exception("Failed to fetch update info")
        }
        return parseGithubRelease(response.body.string())
    }

    /**
     * 把 GitHub Releases API 的 latest 响应映射成应用内使用的 [UpdateInfo]。
     * tag 形如 v3.1.1-lintu，去掉前缀 v 后作为版本号；asset 直接取 apk。
     */
    private fun parseGithubRelease(body: String): UpdateInfo {
        val root = json.parseToJsonElement(body).jsonObject
        val tag = root["tag_name"]?.jsonPrimitive?.contentOrNull.orEmpty()
        val version = tag.removePrefix("v")
        val changelog = root["body"]?.jsonPrimitive?.contentOrNull.orEmpty()
        val publishedAt = root["published_at"]?.jsonPrimitive?.contentOrNull.orEmpty()
        val downloads = root["assets"]?.jsonArray.orEmpty()
            .mapNotNull { element ->
                val asset = element.jsonObject
                val name = asset["name"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
                if (!name.endsWith(".apk", ignoreCase = true)) return@mapNotNull null
                val url = asset["browser_download_url"]?.jsonPrimitive?.contentOrNull
                    ?: return@mapNotNull null
                val sizeBytes = asset["size"]?.jsonPrimitive?.contentOrNull?.toLongOrNull() ?: 0L
                UpdateDownload(
                    name = name,
                    url = url,
                    size = if (sizeBytes > 0) {
                        "%.1f MB".format(sizeBytes / 1024.0 / 1024.0)
                    } else {
                        ""
                    },
                )
            }
        if (version.isBlank()) throw Exception("Empty release tag")
        return UpdateInfo(
            version = version,
            publishedAt = publishedAt,
            changelog = changelog,
            downloads = downloads,
        )
    }

    fun downloadUpdate(context: Context, download: UpdateDownload) {
        runCatching {
            val request = DownloadManager.Request(download.url.toUri()).apply {
                // 设置下载时通知栏的标题和描述
                setTitle(download.name)
                setDescription("正在下载更新包...")
                // 下载完成后通知栏可见
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                // 允许在移动网络和WiFi下下载
                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                // 设置文件保存路径
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, download.name)
                // 允许下载的文件类型
                setMimeType("application/vnd.android.package-archive")
            }
            // 获取系统的DownloadManager
            val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
            // 你可以保存返回的downloadId到本地，以便后续查询下载进度或状态
        }.onFailure {
            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show()
            context.openUrl(download.url) // 跳转到下载页面
        }
    }
}

@Serializable
data class UpdateDownload(
    val name: String,
    val url: String,
    val size: String
)

@Serializable
data class UpdateInfo(
    val version: String,
    val publishedAt: String,
    val changelog: String,
    val downloads: List<UpdateDownload>
)

/**
 * 版本号值类，封装版本号字符串并提供比较功能
 *
 * 支持完整的 SemVer 规范：MAJOR.MINOR.PATCH[-prerelease][+build]
 * - 预发布版本优先级低于正式版：1.0.0-alpha < 1.0.0
 * - 预发布标识符按段逐个比较：数字按数值比较，字符串按字典序比较
 * - 预发布标识符优先级：alpha < beta < rc（通过字典序自然满足）
 * - build metadata（+号后面的部分）不影响优先级比较
 */
@JvmInline
value class Version(val value: String) : Comparable<Version> {

    private fun parse(): ParsedVersion {
        // 去掉 build metadata（+号后面的部分）
        val withoutBuild = value.split("+").first()
        // 分离主版本号和预发布标识符
        val hyphenIndex = withoutBuild.indexOf('-')
        val (coreStr, prereleaseStr) = if (hyphenIndex >= 0) {
            withoutBuild.substring(0, hyphenIndex) to withoutBuild.substring(hyphenIndex + 1)
        } else {
            withoutBuild to null
        }
        val core = coreStr.split(".").map { it.toIntOrNull() ?: 0 }
        val prerelease = prereleaseStr?.split(".")
        return ParsedVersion(core, prerelease)
    }

    override fun compareTo(other: Version): Int {
        val a = this.parse()
        val b = other.parse()

        // 先比较主版本号
        val maxLen = maxOf(a.core.size, b.core.size)
        for (i in 0 until maxLen) {
            val ap = if (i < a.core.size) a.core[i] else 0
            val bp = if (i < b.core.size) b.core[i] else 0
            if (ap != bp) return ap.compareTo(bp)
        }

        // 主版本号相同时比较预发布标识符
        // 有预发布标识符的版本优先级低于没有的：1.0.0-alpha < 1.0.0
        return when {
            a.prerelease == null && b.prerelease == null -> 0
            a.prerelease != null && b.prerelease == null -> -1
            a.prerelease == null && b.prerelease != null -> 1
            else -> comparePrerelease(a.prerelease!!, b.prerelease!!)
        }
    }

    companion object {
        fun compare(version1: String, version2: String): Int {
            return Version(version1).compareTo(Version(version2))
        }

        private fun comparePrerelease(a: List<String>, b: List<String>): Int {
            val maxLen = maxOf(a.size, b.size)
            for (i in 0 until maxLen) {
                // 字段少的优先级更低：1.0.0-alpha < 1.0.0-alpha.1
                if (i >= a.size) return -1
                if (i >= b.size) return 1

                val aNum = a[i].toIntOrNull()
                val bNum = b[i].toIntOrNull()

                val cmp = when {
                    // 都是字：按数值比较
                    aNum != null && bNum != null -> aNum.compareTo(bNum)
                    // 数字优先级低于字符串
                    aNum != null -> -1
                    bNum != null -> 1
                    // 都是字符串：按字典序比较
                    else -> a[i].compareTo(b[i])
                }
                if (cmp != 0) return cmp
            }
            return 0
        }
    }
}

private data class ParsedVersion(
    val core: List<Int>,
    val prerelease: List<String>?,
)

// 扩展操作符函数，使比较更直观
operator fun String.compareTo(other: Version): Int = Version(this).compareTo(other)
operator fun Version.compareTo(other: String): Int = this.compareTo(Version(other))
