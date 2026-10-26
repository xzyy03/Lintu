package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.ai.util.KeyStrategy
import me.rerere.ai.util.ManagedKey
import me.rerere.ai.util.MultiKeyConfig
import me.rerere.ai.util.parseMultiKeyConfig
import me.rerere.ai.util.toApiKeyString
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.ArrowRight01
import me.rerere.hugeicons.stroke.Delete02
import me.rerere.hugeicons.stroke.Edit02
import me.rerere.hugeicons.stroke.HeartCheck
import me.rerere.hugeicons.stroke.PlusSign
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.datastore.Settings
import me.rerere.rikkahub.ui.components.nav.BackButton
import me.rerere.rikkahub.ui.components.ui.CardGroup
import me.rerere.rikkahub.ui.components.ui.CardGroupValue
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.theme.CustomColors
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.Uuid

/**
 * 多 Key 管理页：统计概览 + 负载均衡策略 + 逐条 Key 的启停/健康/编辑/删除。
 *
 * 数据存在 provider 的 apiKey 字段里（MultiKeyConfig JSON），因此不需要改上游数据结构。
 */
@Composable
fun MultiKeyManagePage(providerId: String, vm: SettingVM = koinViewModel()) {
    val settings by vm.settings.collectAsStateWithLifecycle()
    val navController = LocalNavController.current
    val provider = settings.providers.find { it.id.toString() == providerId } ?: return

    val apiKeyString = when (provider) {
        is me.rerere.ai.provider.ProviderSetting.ApiKey -> provider.apiKey
        else -> ""
    }
    val config = remember(apiKeyString) {
        apiKeyString.parseMultiKeyConfig() ?: MultiKeyConfig(keys = emptyList())
    }

    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var editingValue by remember { mutableStateOf("") }
    var deletingIndex by remember { mutableStateOf<Int?>(null) }

    fun save(newConfig: MultiKeyConfig) {
        val id = provider.id
        vm.updateSettings { latest ->
            latest.copy(
                providers = latest.providers.map { p ->
                    if (p.id != id) p
                    else when (p) {
                        is me.rerere.ai.provider.ProviderSetting.OpenAI ->
                            p.copy(apiKey = newConfig.toApiKeyString())
                        is me.rerere.ai.provider.ProviderSetting.Google ->
                            p.copy(apiKey = newConfig.toApiKeyString())
                        is me.rerere.ai.provider.ProviderSetting.Claude ->
                            p.copy(apiKey = newConfig.toApiKeyString())
                        else -> p
                    }
                }
            )
        }
    }

    Scaffold(
        containerColor = CustomColors.topBarColors.containerColor,
        topBar = {
            TopAppBar(
                navigationIcon = { BackButton() },
                colors = CustomColors.topBarColors,
                title = { Text(stringResource(R.string.multi_key_title)) },
                actions = {
                    // 全部健康检查（当前实现只做本地校验：非空即视为正常）
                    IconButton(onClick = {
                        save(
                            config.copy(
                                keys = config.keys.map { key ->
                                    if (key.value.isBlank()) {
                                        key.copy(healthy = false, lastError = "Key 为空")
                                    } else {
                                        key.copy(healthy = true, lastError = null)
                                    }
                                }
                            )
                        )
                    }) {
                        Icon(HugeIcons.HeartCheck, null)
                    }
                    IconButton(onClick = {
                        editingIndex = -1
                        editingValue = ""
                    }) {
                        Icon(HugeIcons.PlusSign, null)
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item("stats") {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column {
                        StatRow(
                            label = stringResource(R.string.multi_key_total),
                            value = config.keys.size.toString(),
                        )
                        StatRow(
                            label = stringResource(R.string.multi_key_healthy),
                            value = config.keys.count { it.healthy == true }.toString(),
                        )
                        StatRow(
                            label = stringResource(R.string.multi_key_error),
                            value = config.keys.count { it.healthy == false }.toString(),
                        )
                        StatRow(
                            label = stringResource(R.string.multi_key_strategy),
                            value = when (config.strategy) {
                                KeyStrategy.ROUND_ROBIN -> stringResource(R.string.multi_key_strategy_round_robin)
                                KeyStrategy.RANDOM -> stringResource(R.string.multi_key_strategy_random)
                                KeyStrategy.LRU -> stringResource(R.string.multi_key_strategy_lru)
                            },
                            onClick = {
                                save(
                                    config.copy(
                                        strategy = when (config.strategy) {
                                            KeyStrategy.ROUND_ROBIN -> KeyStrategy.RANDOM
                                            KeyStrategy.RANDOM -> KeyStrategy.LRU
                                            KeyStrategy.LRU -> KeyStrategy.ROUND_ROBIN
                                        }
                                    )
                                )
                            },
                        )
                    }
                }
            }

            if (config.keys.isEmpty()) {
                item("empty") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.multi_key_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            itemsIndexed(config.keys) { index, key ->
                KeyRow(
                    index = index,
                    key = key,
                    onToggle = { enabled ->
                        save(
                            config.copy(
                                keys = config.keys.toMutableList().also {
                                    it[index] = key.copy(enabled = enabled)
                                }
                            )
                        )
                    },
                    onCheck = {
                        save(
                            config.copy(
                                keys = config.keys.toMutableList().also {
                                    it[index] = if (key.value.isBlank()) {
                                        key.copy(healthy = false, lastError = "Key 为空")
                                    } else {
                                        key.copy(healthy = true, lastError = null)
                                    }
                                }
                            )
                        )
                    },
                    onEdit = {
                        editingIndex = index
                        editingValue = key.value
                    },
                    onDelete = { deletingIndex = index },
                )
            }
        }
    }

    // 新增 / 编辑 Key
    if (editingIndex != null) {
        val isNew = editingIndex == -1
        val index = editingIndex ?: -1
        KeyEditDialog(
            title = stringResource(
                if (isNew) R.string.multi_key_add else R.string.multi_key_edit
            ),
            value = editingValue,
            onValueChange = { editingValue = it },
            onConfirm = {
                val trimmed = editingValue.trim()
                if (trimmed.isNotEmpty()) {
                    val newKeys = config.keys.toMutableList().apply {
                        if (isNew) add(ManagedKey(trimmed))
                        else this[index] = this[index].copy(value = trimmed)
                    }
                    save(config.copy(keys = newKeys))
                }
                editingIndex = null
            },
            onDismiss = { editingIndex = null },
        )
    }

    // 删除确认
    deletingIndex?.let { index ->
        AlertDialog(
            onDismissRequest = { deletingIndex = null },
            title = { Text(stringResource(R.string.multi_key_delete_title)) },
            text = { Text(stringResource(R.string.multi_key_delete_text)) },
            confirmButton = {
                TextButton(onClick = {
                    save(
                        config.copy(
                            keys = config.keys.toMutableList().also { it.removeAt(index) }
                        )
                    )
                    deletingIndex = null
                }) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingIndex = null }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun StatRow(label: String, value: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.weight(1f))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (onClick != null) {
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = HugeIcons.ArrowRight01,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
            )
        }
    }
}

@Composable
private fun KeyRow(
    index: Int,
    key: ManagedKey,
    onToggle: (Boolean) -> Unit,
    onCheck: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatusChip(healthy = key.healthy)
            Text(
                text = (index + 1).toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.weight(1f))
            Switch(checked = key.enabled, onCheckedChange = onToggle)
            IconButton(onClick = onCheck, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = HugeIcons.HeartCheck,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = HugeIcons.Edit02,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = HugeIcons.Delete02,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun StatusChip(healthy: Boolean?) {
    val (label, color) = when (healthy) {
        true -> stringResource(R.string.multi_key_healthy) to MaterialTheme.colorScheme.primary
        false -> stringResource(R.string.multi_key_error) to MaterialTheme.colorScheme.error
        null -> stringResource(R.string.multi_key_untested) to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 3.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun KeyEditDialog(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            androidx.compose.material3.OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.setting_provider_page_api_key)) },
                singleLine = false,
                maxLines = 4,
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(stringResource(R.string.confirm)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        },
    )
}
