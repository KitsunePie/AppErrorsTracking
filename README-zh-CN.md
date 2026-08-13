# AppErrorsTracking

[![GitHub license](https://img.shields.io/github/license/KitsunePie/AppErrorsTracking?color=blue&style=flat-square)](https://github.com/KitsunePie/AppErrorsTracking/blob/master/LICENSE)
[![GitHub CI](https://img.shields.io/github/actions/workflow/status/KitsunePie/AppErrorsTracking/commit_ci.yml?label=CI%20builds&style=flat-square)](https://github.com/KitsunePie/AppErrorsTracking/actions/workflows/commit_ci.yml)
[![GitHub release](https://img.shields.io/github/v/release/KitsunePie/AppErrorsTracking?display_name=release&logo=github&color=green&style=flat-square)](https://github.com/KitsunePie/AppErrorsTracking/releases)
![GitHub all releases](https://img.shields.io/github/downloads/KitsunePie/AppErrorsTracking/total?label=downloads&style=flat-square)
![GitHub all releases](https://img.shields.io/github/downloads/Xposed-Modules-Repo/com.fankes.apperrorstracking/total?label=LSPosed%20downloads&labelColor=F48FB1&style=flat-square)

[![Telegram CI](https://img.shields.io/badge/CI%20builds-Telegram-blue.svg?logo=telegram&style=flat-square)](https://t.me/AppErrorsTracking_CI)
[![Telegram](https://img.shields.io/badge/discussion-Telegram-blue.svg?logo=telegram&style=flat-square)](https://t.me/XiaofangInternet)
[![QQ](https://img.shields.io/badge/discussion-QQ-blue.svg?logo=tencent-qq&logoColor=red&style=flat-square)](https://qm.qq.com/cgi-bin/qm/qr?k=dp2h5YhWiga9WWb_Oh7kSHmx01X8I8ii&jump_from=webapi&authKey=Za5CaFP0lk7+Zgsk2KpoBD7sSaYbeXbsDgFjiWelOeH4VSionpxFJ7V0qQBSqvFM)
[![QQ 频道](https://img.shields.io/badge/discussion-QQ%20频道-blue.svg?logo=tencent-qq&logoColor=red&style=flat-square)](https://pd.qq.com/s/44gcy28h)

<img src="img-src/icon.png" width = "100" height = "100" alt="LOGO"/>

[English](README.md) | 简体中文 | [日本語](README-ja-JP.md)

为原生 FC 对话框增加更多功能并修复国内定制 ROM 删除 FC 对话框的问题，给 Android 开发者带来更好的体验。

此项目为 Xposed 模块，可用在任何 Android 系统中，目前仅在 **LSPosed** 中测试通过。

此模块专为 Android 开发者而打造。

在可能的无法连接电脑，不能进行 ADB 调试的时候，可通过此模块来快速捕获任意已安装应用的任意异常，以便快速定位问题。

应用发生崩溃的错误日志对开发者来说是无价的财富，若你不是开发者，你依然可以安装此模块，以便给开发者提供更多异常信息快速解决问题。

> 最低支持 Android 7.0

## 项目缘由

我实在是不能理解，国内 ROM 除了 MIUI(稳定版除外) 都选择了删除应用程序崩溃的对话框(FC 对话框)，我曾以为这一直是一个特性，直到我去反编译了系统框架，才确认确实是被删掉了。

难道产品经理认为，让用户看不到错误，应用直接闪退，逃避就是最好的解决方案吗，还是说**另有隐情**呢？

## 工作原理

不同于 `Thread.UncaughtExceptionHandler`，我们通过注入系统框架，使用原生方式全方位捕获应用异常，不会产生额外的注册监听，在性能上相比原始的异常监听会更好。

同时系统级别的异常捕获还可捕获原生层的 `stack trace`。

## 注意事项

系统原生方式捕获的异常只能为 APP 自身未进行处理的异常，若 APP 自身拥有自定义的 `Thread.UncaughtExceptionHandler`
类似 **Bugly** 这样的自动收集异常功能，系统就无法获取到 APP 是否真正发生异常而闪退(FC)，例如 **QQ**、**TIM**。

## 功能列表

- [x] 完全取代系统的应用错误对话框

- [x] 记录每个应用的异常，直到重新启动前持续保留

- [x] 复制、分享、导出异常堆栈功能

- [x] 异常历史记录功能，可通过通知栏磁贴“异常历史记录”进入和模块主界面进入

- [x] 应用异常统计功能

- [x] 多进程应用的异常显示功能

## 翻译贡献

欢迎为此项目做出贡献，将其翻译为您国家的语言。

## 发行渠道

| <img src="https://avatars.githubusercontent.com/in/15368?s=64&v=4" width = "30" height = "30" alt="LOGO"/> | [GitHub CI](https://github.com/KitsunePie/AppErrorsTracking/actions/workflows/commit_ci.yml) | CI 自动构建 (测试版) |
|------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------|---------------|

| <img src="https://github.com/peter-iakovlev/Telegram/blob/public/Icon.png?raw=true" width = "30" height = "30" alt="LOGO"/> | [Telegram CI 频道](https://t.me/AppErrorsTracking_CI) | CI 自动构建 (测试版) |
|-----------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------|---------------|

| <img src="https://avatars.githubusercontent.com/in/15368?s=64&v=4" width = "30" height = "30" alt="LOGO"/> | [GitHub Releases](https://github.com/KitsunePie/AppErrorsTracking/releases) | 正式版 (稳定版) |
|------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------|-----------|

| <img src="https://avatars.githubusercontent.com/u/78217009?s=200&v=4?raw=true" width = "30" height = "30" alt="LOGO"/> | [Xposed-Modules-Repo](https://github.com/Xposed-Modules-Repo/com.fankes.apperrorstracking/releases) | 正式版 (稳定版) |
|------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------|-----------|

本模块发布地址仅限于上述所列出的地址，从其他非正规渠道下载到的版本或对您造成任何影响均与我们无关。

## 注意事项

<h3>1.&nbsp;本软件免费、由兴趣驱动开发，仅供学习交流使用。如果你是从其他非官方渠道付费获得本软件，可能已遭遇欺诈，欢迎向我们举报可疑行为。</h3>

<h3>2.&nbsp;本软件采用 <strong>GNU Affero General Public License (AGPL 3.0)</strong> 许可证。根据该许可证的要求：</h3>

- 任何衍生作品必须采用相同的 AGPL 许可证
- 分发本软件或其修改版本时，必须提供完整的源代码
- 必须保留原始的版权声明及许可证信息
- 不得额外施加限制来限制他人对本软件的自由使用

<h3>3.&nbsp;我们鼓励在遵守 AGPL 3.0 条款的前提下进行自由传播和改进，但请尊重作者署名权，勿冒用原作者名义。</h3>

## 更多项目

<!--suppress HtmlDeprecatedAttribute -->
<div align="center">
    <h2>嘿，还请君留步！👋</h2>
    <h3>如果你觉得这个项目能给你提供帮助，不妨继续往下看看我的更多项目吧！</h3>
    <h3>如果这些项目能为你提供帮助，不妨为我点个关注或者 star ⭐️ 吧！</h3>
    <h1><a href="https://github.com/fankes/fankes/blob/main/project-promote/README-zh-CN.md">→ 查看更多关于我的项目，请点击这里 ←</a></h1>
</div>

## Star History

![Star History Chart](https://star-history.dera.page/svg?repos=KitsunePie/AppErrorsTracking&type=Date)

## 许可证

- [AGPL-3.0](https://www.gnu.org/licenses/agpl-3.0.html)

```
Copyright (C) 2017 Fankes Studio(qzmmcn@163.com)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```

Powered by [YukiHookAPI](https://github.com/HighCapable/YukiHookAPI)

版权所有 © 2017 Fankes Studio(qzmmcn@163.com)
