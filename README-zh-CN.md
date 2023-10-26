# AppErrorsTracking

[![GitHub license](https://img.shields.io/github/license/KitsunePie/AppErrorsTracking?color=blue)](https://github.com/KitsunePie/AppErrorsTracking/blob/master/LICENSE)
[![GitHub CI](https://img.shields.io/github/actions/workflow/status/KitsunePie/AppErrorsTracking/commit_ci.yml?label=CI%20builds)](https://github.com/KitsunePie/AppErrorsTracking/actions/workflows/commit_ci.yml)
[![GitHub release](https://img.shields.io/github/v/release/KitsunePie/AppErrorsTracking?display_name=release&logo=github&color=green)](https://github.com/KitsunePie/AppErrorsTracking/releases)
![GitHub all releases](https://img.shields.io/github/downloads/KitsunePie/AppErrorsTracking/total?label=downloads)
![GitHub all releases](https://img.shields.io/github/downloads/Xposed-Modules-Repo/com.fankes.apperrorstracking/total?label=LSPosed%20downloads&labelColor=F48FB1)

[![Telegram CI](https://img.shields.io/badge/CI%20builds-Telegram-blue.svg?logo=telegram)](https://t.me/AppErrorsTracking_CI)
[![Telegram](https://img.shields.io/badge/discussion-Telegram-blue.svg?logo=telegram)](https://t.me/XiaofangInternet)
[![QQ](https://img.shields.io/badge/discussion-QQ-blue.svg?logo=tencent-qq&logoColor=red)](https://qm.qq.com/cgi-bin/qm/qr?k=dp2h5YhWiga9WWb_Oh7kSHmx01X8I8ii&jump_from=webapi&authKey=Za5CaFP0lk7+Zgsk2KpoBD7sSaYbeXbsDgFjiWelOeH4VSionpxFJ7V0qQBSqvFM)
[![QQ 频道](https://img.shields.io/badge/discussion-QQ%20频道-blue.svg?logo=tencent-qq&logoColor=red)](https://pd.qq.com/s/44gcy28h)

<img src="https://github.com/KitsunePie/AppErrorsTracking/blob/master/img-src/icon.png?raw=true" width = "100" height = "100" alt="LOGO"/>

[English](https://github.com/KitsunePie/AppErrorsTracking/blob/master/README.md) | 简体中文

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

## 项目推广

如果你正在寻找一个可以自动管理 Gradle 项目依赖的 Gradle 插件，你可以了解一下 [SweetDependency](https://github.com/HighCapable/SweetDependency) 项目。

如果你正在寻找一个可以自动生成属性键值的 Gradle 插件，你可以了解一下 [SweetProperty](https://github.com/HighCapable/SweetProperty) 项目。

本项目同样使用了 **SweetDependency** 和 **SweetProperty**。

## 捐赠支持

工作不易，无意外情况此项目将继续维护下去，提供更多可能，欢迎打赏。

<img src="https://github.com/fankes/fankes/blob/main/img-src/payment_code.jpg?raw=true" width = "500" alt="Payment Code"/>

## Star History

![Star History Chart](https://api.star-history.com/svg?repos=KitsunePie/AppErrorsTracking&type=Date)

## 许可证

- [AGPL-3.0](https://www.gnu.org/licenses/agpl-3.0.html)

```
Copyright (C) 2017-2023 Fankes Studio(qzmmcn@163.com)

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

版权所有 © 2017-2023 Fankes Studio(qzmmcn@163.com)