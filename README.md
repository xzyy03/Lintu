# Lintu

> 基于 [RikkaHub](https://github.com/rikkahub/rikkahub) 的 miuix（HyperOS）风格二改版本。
>
> **上游项目：<https://github.com/rikkahub/rikkahub>**
> 上游作者：[rikkahub](https://github.com/rikkahub)
> 本项目遵循上游的 **GNU AGPL-3.0** 协议发布，详见 [LICENSE](LICENSE) 与 [NOTICE](NOTICE)。

<div align="center">
  <img src="docs/icon.png" alt="Lintu" width="100" />
  <h1>Lintu</h1>
</div>

## 与上游的差异

本项目在 RikkaHub 基础上只做 UI 层面改造，功能逻辑保持与上游一致：

- **miuix / HyperOS 视觉风格**：浅灰页底 `#F5F5F6`、纯白圆角卡片、蓝色主色 `#3482FF`，圆角尺度整体放大一档
- **设置页重构**：大标题顶栏 + 分组卡片 + 彩色线条图标 + 右侧当前取值，参照 miuix 设置页排版
- **聊天气泡**：白底 + 深色描边的小圆角气泡
- **输入框**：迷你高度的白卡输入区，获得焦点与默认状态等高
- **应用图标与名称**：兔子图标，应用名 Lintu，包名 `me.rerere.rikkahub.miuix`，可与官方版共存
- **更新检查**：指向本仓库的 GitHub Releases

## 下载

前往 [Releases](https://github.com/xzyy03/Lintu/releases) 下载最新 APK。

- 包名：`me.rerere.rikkahub.miuix`（与官方 RikkaHub 互不影响，可同时安装）
- 支持架构：arm64-v8a
- 最低系统：Android 8.0（API 26）

> 本版本使用自签名证书分发，与官方版签名不同，无法覆盖安装官方版。

## 交流

- 交流群：**1125470056**

## 构建

与上游一致，需要 JDK 21、Android SDK 37、Node 22 + pnpm：

```bash
# 前端资源
cd web-ui && pnpm install --frozen-lockfile && cd ..

# 构建 GitHub 渠道 release
./gradlew assembleGithubRelease
```

## 协议

本项目以上游 [RikkaHub](https://github.com/rikkahub/rikkahub) 为基础修改而来，遵循 **GNU Affero General Public License v3.0**：

- 本项目的完整源代码在 <https://github.com/xzyy03/Lintu> 公开
- 任何基于本项目的分发与修改，同样需要遵守 AGPL-3.0 并保留上游署名
- 上游版权归 rikkahub 及各贡献者所有，本项目改造部分版权归本项目贡献者所有
