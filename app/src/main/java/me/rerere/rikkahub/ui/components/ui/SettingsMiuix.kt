package me.rerere.rikkahub.ui.components.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.ArrowRight01

/**
 * miuix 设置页组件：浅灰页面底 + 22dp 白色分组卡 + 组内 0.5dp 缩进分隔线 + 彩色线条图标 + 右侧取值。
 *
 * 结构与 ai.muse 的设置页保持一致：分组小标题在卡片外，卡片内只放行，
 * 行尾统一是「取值文本 + 右箭头」或开关。
 */

/** 设置页图标取色表：同一图标在所有页面用同一个颜色，不随主题预设漂移。 */
private val SettingsIconTints: Map<String, Color> = mapOf(
    "Settings03" to Color(0xFF3482FF),
    "SlidersHorizontal" to Color(0xFF3482FF),
    "LookTop" to Color(0xFF3482FF),
    "Package" to Color(0xFF8B5CF6),
    "AiMagic" to Color(0xFF3482FF),
    "Brain02" to Color(0xFF3482FF),
    "McpServer" to Color(0xFF8B5CF6),
    "GlobalSearch" to Color(0xFF3482FF),
    "Megaphone01" to Color(0xFF3482FF),
    "Image02" to Color(0xFFFF8A00),
    "ServerStack01" to Color(0xFF00A3A3),
    "Database02" to Color(0xFF00A3A3),
    "ImageUpload" to Color(0xFF3482FF),
    "Moon02" to Color(0xFF6B5BD6),
    "Sun01" to Color(0xFFFFB020),
    "SmartPhone01" to Color(0xFF3482FF),
    "Book01" to Color(0xFFFF8A00),
    "Bookshelf01" to Color(0xFFFF8A00),
    "Book03" to Color(0xFFFF8A00),
    "InLove" to Color(0xFFFF5A79),
    "Share04" to Color(0xFF3482FF),
    "Clapping01" to Color(0xFF3482FF),
    "Alert01" to Color(0xFFE54545),
    "Terminal" to Color(0xFF00B34A),
    "Clock01" to Color(0xFFFF8A00),
    "Notification01" to Color(0xFF3482FF),
    "Translate" to Color(0xFF00A3A3),
    "Palette" to Color(0xFF6B5BD6),
)

/** 未登记的图标统一用主色蓝。 */
internal fun settingsIconTint(icon: ImageVector): Color =
    SettingsIconTints[icon.name.substringAfterLast('.')] ?: Color(0xFF3482FF)

/** 分组小标题：卡片上方灰色小字，左对齐。 */
@Composable
fun SettingsGroupHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Normal,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp, top = 20.dp, bottom = 8.dp),
    )
}

/** 白色分组圆角卡片：内部按行排布，行间用 [SettingsDivider] 分隔。 */
@Composable
fun SettingsGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.surface),
        content = content,
    )
}

/**
 * 分组内单行：左侧彩色线条图标 + 标题，右侧可选尾随内容（取值/开关）+ 右箭头。
 *
 * @param value 右侧灰色取值文本（如「自动 · 默认蓝」），有 onClick 时箭头自动显示
 */
@Composable
fun SettingsRow(
    icon: ImageVector? = null,
    title: String,
    value: String? = null,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable RowScope.() -> Unit)? = null,
    enabled: Boolean = true,
    iconTint: Color? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .let { if (onClick != null && enabled) it.clickable { onClick() } else it }
            .alpha(if (enabled) 1f else 0.5f)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint ?: settingsIconTint(icon),
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        if (value != null) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
        trailing?.invoke(this)
        if (onClick != null && enabled) {
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = HugeIcons.ArrowRight01,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

/** 组内行间分隔线：从文字起始处（含图标宽度）开始，末行不画。 */
@Composable
fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 50.dp, end = 20.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant,
    )
}

/** 列表里按 index 自行拼卡片外观时用：首项圆上角、末项圆下角。 */
@Composable
fun LazySettingsGroupItem(
    index: Int,
    lastIndex: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(
        topStart = if (index == 0) 22.dp else 0.dp,
        topEnd = if (index == 0) 22.dp else 0.dp,
        bottomStart = if (index == lastIndex) 22.dp else 0.dp,
        bottomEnd = if (index == lastIndex) 22.dp else 0.dp,
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface),
    ) {
        content()
        if (index != lastIndex) SettingsDivider()
    }
}

/**
 * miuix 大标题顶栏：首行是返回/操作，下方左对齐大标题。
 *
 * 与 M3 TopAppBar 的区别只在排版——标题单独占一行用 headline 字号，形成设置页的层级感。
 */
@Composable
fun MiuixLargeTopBar(
    title: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.background,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    titleStyle: TextStyle = MaterialTheme.typography.headlineMedium,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            navigationIcon?.invoke()
            Spacer(Modifier.weight(1f))
            actions?.invoke(this)
        }
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = titleStyle,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
            )
        }
    }
}
