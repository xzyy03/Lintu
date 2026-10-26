package me.rerere.rikkahub.ui.pages.setting

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.AiMagic
import me.rerere.hugeicons.stroke.Alert01
import me.rerere.hugeicons.stroke.ArrowLeft01
import me.rerere.hugeicons.stroke.Book01
import me.rerere.hugeicons.stroke.Bookshelf01
import me.rerere.hugeicons.stroke.Brain02
import me.rerere.hugeicons.stroke.Clapping01
import me.rerere.hugeicons.stroke.Database02
import me.rerere.hugeicons.stroke.GlobalSearch
import me.rerere.hugeicons.stroke.Image02
import me.rerere.hugeicons.stroke.ImageUpload
import me.rerere.hugeicons.stroke.InLove
import me.rerere.hugeicons.stroke.LookTop
import me.rerere.hugeicons.stroke.McpServer
import me.rerere.hugeicons.stroke.Megaphone01
import me.rerere.hugeicons.stroke.Moon02
import me.rerere.hugeicons.stroke.Package
import me.rerere.hugeicons.stroke.ServerStack01
import me.rerere.hugeicons.stroke.Settings03
import me.rerere.hugeicons.stroke.Share04
import me.rerere.hugeicons.stroke.SmartPhone01
import me.rerere.hugeicons.stroke.Sun01
import me.rerere.hugeicons.stroke.Tiktok
import me.rerere.hugeicons.stroke.WavingHand01
import me.rerere.rikkahub.R
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.data.datastore.isNotConfigured
import me.rerere.rikkahub.data.files.FilesManager
import me.rerere.rikkahub.ui.components.ui.MiuixLargeTopBar
import me.rerere.rikkahub.ui.components.ui.SettingsDivider
import me.rerere.rikkahub.ui.components.ui.SettingsGroup
import me.rerere.rikkahub.ui.components.ui.SettingsGroupHeader
import me.rerere.rikkahub.ui.components.ui.SettingsRow
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.hooks.rememberColorMode
import me.rerere.rikkahub.ui.theme.ColorMode
import me.rerere.rikkahub.utils.openUrl
import me.rerere.rikkahub.utils.plus
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun SettingPage(vm: SettingVM = koinViewModel()) {
    val navController = LocalNavController.current
    val settings by vm.settings.collectAsStateWithLifecycle()
    val launchCount by vm.launchCount.collectAsStateWithLifecycle()
    val filesManager: FilesManager = koinInject()

    if (!settings.init && launchCount > 100 && (launchCount - settings.sponsorAlertDismissedAt) >= 50) {
        AlertDialog(
            onDismissRequest = {
                vm.updateSettings { it.copy(sponsorAlertDismissedAt = launchCount) }
            },
            icon = { Icon(HugeIcons.WavingHand01, null) },
            title = { Text(stringResource(R.string.setting_page_sponsor_alert_title)) },
            text = { Text(stringResource(R.string.setting_page_sponsor_alert_desc)) },
            confirmButton = {
                Button(onClick = {
                    vm.updateSettings { it.copy(sponsorAlertDismissedAt = launchCount) }
                    navController.navigate(Screen.SettingDonate)
                }) {
                    Text(stringResource(R.string.setting_page_sponsor_alert_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    vm.updateSettings { it.copy(sponsorAlertDismissedAt = launchCount) }
                }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    Scaffold(
        topBar = {
            MiuixLargeTopBar(
                title = stringResource(R.string.setting_page_title),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = HugeIcons.ArrowLeft01,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding + PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        ) {
            if (settings.isNotConfigured()) {
                item {
                    ProviderConfigWarningCard(
                        onClick = { navController.navigate(Screen.SettingProvider) },
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
            }

            item("generalSettings") {
                var colorMode by rememberColorMode()
                val themeName = settings.themeId
                SettingsGroupHeader(stringResource(R.string.setting_page_general_settings))
                SettingsGroup {
                    SettingsRow(
                        icon = HugeIcons.Settings03,
                        title = stringResource(R.string.setting_page_preferences),
                        onClick = { navController.navigate(Screen.SettingPreferences) },
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = colorMode.icon(),
                        title = stringResource(R.string.setting_page_color_mode),
                        value = stringResource(
                            when (colorMode) {
                                ColorMode.SYSTEM -> R.string.setting_page_color_mode_system
                                ColorMode.LIGHT -> R.string.setting_page_color_mode_light
                                ColorMode.DARK -> R.string.setting_page_color_mode_dark
                            }
                        ),
                        onClick = { navController.navigate(Screen.SettingTheme) },
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = HugeIcons.LookTop,
                        title = stringResource(R.string.setting_page_assistant),
                        value = themeName,
                        onClick = { navController.navigate(Screen.Assistant) },
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = HugeIcons.Package,
                        title = stringResource(R.string.setting_page_extensions),
                        onClick = { navController.navigate(Screen.Extensions) },
                    )
                }
            }

            item("modelServices") {
                SettingsGroupHeader(stringResource(R.string.setting_page_model_and_services))
                SettingsGroup {
                    SettingsRow(
                        icon = HugeIcons.Brain02,
                        title = stringResource(R.string.setting_page_providers),
                        onClick = { navController.navigate(Screen.SettingProvider) },
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = HugeIcons.AiMagic,
                        title = stringResource(R.string.setting_page_default_model),
                        onClick = { navController.navigate(Screen.SettingModels) },
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = HugeIcons.GlobalSearch,
                        title = stringResource(R.string.setting_page_search_service),
                        onClick = { navController.navigate(Screen.SettingSearch) },
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = HugeIcons.Megaphone01,
                        title = stringResource(R.string.setting_page_tts_service),
                        onClick = { navController.navigate(Screen.SettingSpeech) },
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = HugeIcons.Image02,
                        title = stringResource(R.string.setting_page_media),
                        onClick = { navController.navigate(Screen.SettingMedia) },
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = HugeIcons.McpServer,
                        title = stringResource(R.string.setting_page_mcp),
                        onClick = { navController.navigate(Screen.SettingMcp) },
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = HugeIcons.ServerStack01,
                        title = stringResource(R.string.setting_page_web_server),
                        onClick = { navController.navigate(Screen.SettingWeb) },
                    )
                }
            }

            item("dataSettings") {
                val storageState by produceState(-1 to 0L) {
                    value = filesManager.countChatFiles()
                }
                SettingsGroupHeader(stringResource(R.string.setting_page_data_settings))
                SettingsGroup {
                    SettingsRow(
                        icon = HugeIcons.Database02,
                        title = stringResource(R.string.setting_page_data_backup),
                        onClick = { navController.navigate(Screen.Backup) },
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = HugeIcons.ImageUpload,
                        title = stringResource(R.string.setting_page_chat_storage),
                        value = if (storageState.first == -1) {
                            null
                        } else {
                            stringResource(
                                R.string.setting_page_chat_storage_desc,
                                storageState.first,
                                storageState.second / 1024 / 1024.0
                            )
                        },
                        onClick = { navController.navigate(Screen.SettingFiles) },
                    )
                }
            }

            item("aboutSettings") {
                val context = LocalContext.current
                val shareText = stringResource(R.string.setting_page_share_text)
                val share = stringResource(R.string.setting_page_share)
                val noShareApp = stringResource(R.string.setting_page_no_share_app)
                SettingsGroupHeader(stringResource(R.string.setting_page_about))
                SettingsGroup {
                    SettingsRow(
                        icon = HugeIcons.Clapping01,
                        title = stringResource(R.string.setting_page_about),
                        onClick = { navController.navigate(Screen.SettingAbout) },
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = HugeIcons.Book01,
                        title = stringResource(R.string.setting_page_documentation),
                        onClick = {
                            val docUrl = if (java.util.Locale.getDefault().language == "zh") {
                                "https://docs.rikka-ai.com/zh/introduction"
                            } else {
                                "https://docs.rikka-ai.com/introduction"
                            }
                            context.openUrl(docUrl)
                        },
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = HugeIcons.Bookshelf01,
                        title = stringResource(R.string.setting_page_request_logs),
                        onClick = { navController.navigate(Screen.Log) },
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = HugeIcons.InLove,
                        title = stringResource(R.string.setting_page_donate),
                        onClick = { navController.navigate(Screen.SettingDonate) },
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = HugeIcons.Share04,
                        title = stringResource(R.string.setting_page_share),
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND)
                            intent.type = "text/plain"
                            intent.putExtra(Intent.EXTRA_TEXT, shareText)
                            try {
                                context.startActivity(Intent.createChooser(intent, share))
                            } catch (e: ActivityNotFoundException) {
                                Toast.makeText(context, noShareApp, Toast.LENGTH_SHORT).show()
                            }
                        },
                    )
                }
            }

            item("bottomSpacer") {
                Box(modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}

private fun ColorMode.icon(): ImageVector = when (this) {
    ColorMode.SYSTEM -> HugeIcons.SmartPhone01
    ColorMode.LIGHT -> HugeIcons.Sun01
    ColorMode.DARK -> HugeIcons.Moon02
}

@Composable
private fun ProviderConfigWarningCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .padding(0.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = HugeIcons.Alert01,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = stringResource(R.string.setting_page_config_api_title),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = stringResource(R.string.setting_page_config_api_desc),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            Button(
                onClick = onClick,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                ),
            ) {
                Text(stringResource(R.string.setting_page_config))
            }
        }
    }
}


