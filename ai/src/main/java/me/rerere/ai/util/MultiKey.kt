package me.rerere.ai.util

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * 多 Key 管理中的一条 Key。
 *
 * 存储时整体序列化成 JSON 塞进 ProviderSetting.apiKey 字段，
 * 这样不用改上游所有 provider 的数据结构，[KeyRoulette] 也能继续按字符串解析。
 */
@Serializable
data class ManagedKey(
    val value: String,
    /** 是否启用；关闭后不参与轮换 */
    val enabled: Boolean = true,
    /** 权重，越大被分到越多；<=0 等同于不参与 */
    val weight: Int = 1,
    /** 最近一次健康检查是否通过；null 表示还没测过 */
    val healthy: Boolean? = null,
    /** 最近一次失败原因，健康时清空 */
    val lastError: String? = null,
)

/** 负载均衡策略 */
@Serializable
enum class KeyStrategy {
    /** 轮询：按顺序依次使用 */
    ROUND_ROBIN,
    /** 随机：每次随机挑一个可用的 */
    RANDOM,
    /** 最久未用优先 */
    LRU,
}

/**
 * 多 Key 配置。开启后 [keys] 生效，此时 ProviderSetting.apiKey 里存的是这份 JSON。
 *
 * 为了让上游不动，字段名与 JSON 结构都保持稳定：
 *  - 关闭多 Key 时，apiKey 就是普通单 Key 字符串
 *  - 开启时，apiKey 是本对象的 JSON 字符串（以 `{` 开头，可据此区分）
 */
@Serializable
data class MultiKeyConfig(
    val enabled: Boolean = true,
    val keys: List<ManagedKey> = emptyList(),
    val strategy: KeyStrategy = KeyStrategy.ROUND_ROBIN,
)

private val MULTI_KEY_JSON = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

/**
 * apiKey 字段是否存放的是多 Key 配置 JSON。
 * 真实 API Key 不会以 `{` 开头，因此可以安全判定。
 */
fun String.isMultiKeyConfig(): Boolean = trimStart().startsWith("{")

/** 尝试把 apiKey 解析成多 Key 配置；不是 JSON 时返回 null。 */
fun String.parseMultiKeyConfig(): MultiKeyConfig? {
    if (!isMultiKeyConfig()) return null
    return runCatching { MULTI_KEY_JSON.decodeFromString<MultiKeyConfig>(this) }.getOrNull()
}

/** 序列化成可存进 apiKey 的字符串。 */
fun MultiKeyConfig.toApiKeyString(): String = MULTI_KEY_JSON.encodeToString(this)

/**
 * 取出真正要参与轮换的 Key 列表（已过滤禁用与空值）。
 *
 * [KeyRoulette] 拿到的就是这个结果拼成的多行字符串。
 */
fun MultiKeyConfig.activeKeyValues(): List<String> =
    keys.filter { it.enabled && it.value.isNotBlank() && it.weight > 0 }.map { it.value }
