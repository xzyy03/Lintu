package me.rerere.rikkahub.data.datastore

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import me.rerere.ai.provider.ProviderSetting
import me.rerere.ai.provider.QuotaQuery
import me.rerere.rikkahub.R
import me.rerere.rikkahub.ui.components.richtext.MarkdownBlock
import kotlin.uuid.Uuid

val DEFAULT_AUTO_MODEL_ID = Uuid.parse("b7055fb4-39f9-4042-a88a-0d80ed76cf08")

/** 内置 DeepSeek 提供商，快速入门页的迁移流程会直接跳到它 */
val DEEPSEEK_PROVIDER_ID = Uuid.parse("f099ad5b-ef03-446d-8e78-7e36787f780b")

// 二改版：不内置任何供应商预设，由用户自行添加
val DEFAULT_PROVIDERS: List<ProviderSetting> = emptyList()
