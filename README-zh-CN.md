# AppErrorsTracking

[![Blank](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/KitsunePie/AppErrorsTracking)
[![Blank](https://img.shields.io/badge/license-AGPL3.0-blue)](https://github.com/KitsunePie/AppErrorsTracking/blob/master/LICENSE)
[![Blank](https://img.shields.io/badge/version-v1.1-green)](https://github.com/KitsunePie/AppErrorsTracking/releases)
[![Blank](https://img.shields.io/github/downloads/KitsunePie/AppErrorsTracking/total?label=Release)](https://github.com/KitsunePie/AppErrorsTracking/releases)
[![Blank](https://img.shields.io/github/downloads/Xposed-Modules-Repo/com.fankes.apperrorstracking/total?label=LSPosed%20Repo&logo=Android&style=flat&labelColor=F48FB1&logoColor=ffffff)](https://github.com/Xposed-Modules-Repo/com.fankes.apperrorstracking/releases)
<br/><br/>
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

- 完全取代系统的应用错误对话框

- 记录每个应用的异常，直到重新启动前持续保留

- 复制、分享、导出异常堆栈功能

- 异常历史记录功能，可通过通知栏磁贴“异常历史记录”进入和模块主界面进入

- 应用异常统计功能

- 多进程应用的异常显示功能

## 翻译贡献

欢迎为此项目做出贡献，将其翻译为您国家的语言。

## 发行渠道说明

- [Automatic Build on Commit](https://github.com/KitsunePie/AppErrorsTracking/actions/workflows/commit_ci.yml)

上述更新为代码 `commit` 后自动触发，具体更新内容可点击上方的文字前往 **Github Actions** 进行查看，本更新由开源的流程自动编译发布，**不保证其稳定性**，所发布的版本**仅供测试**，且不会特殊说明甚至可能会变更版本号或保持与当前稳定版相同的版本号。

- [Release](https://github.com/KitsunePie/AppErrorsTracking/releases)
- [Xposed-Modules-Repo](https://github.com/Xposed-Modules-Repo/com.fankes.apperrorstracking/releases)

上述更新为手动发布的稳定版，具体更新内容可点击上方的文字前往指定的发布页面查看，稳定版的更新将会同时发布到上述地址中，同步更新。

## 发行状态说明

![Blank](https://img.shields.io/badge/build-passing-brightgreen)

上述状态为当前稳定版与自动构建版本一致或当前代码改动与稳定版无功能差异。

![Blank](https://img.shields.io/badge/build-pending-dbab09)

上述状态为存在自动构建版本和新功能的更新但当前并未发布稳定版，处于预发行状态。

![Blank](https://img.shields.io/badge/build-problem-red)

上述状态为当前发行的稳定版可能存在严重问题但并未及时进行修复且并未发布稳定版。

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
along with this program.  If not, see <http://www.gnu.org/licenses/>.
```

Powered by [YukiHookAPI](https://github.com/fankes/YukiHookAPI)

版权所有 © 2017-2023 Fankes Studio(qzmmcn@163.com)