# AppErrorsTracking

[![Blank](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/KitsunePie/AppErrorsTracking)
[![Blank](https://img.shields.io/badge/license-AGPL3.0-blue)](https://github.com/KitsunePie/AppErrorsTracking/blob/master/LICENSE)
[![Blank](https://img.shields.io/badge/version-v1.0.1-green)](https://github.com/KitsunePie/AppErrorsTracking/releases)
[![Blank](https://img.shields.io/github/downloads/KitsunePie/AppErrorsTracking/total?label=Release)](https://github.com/KitsunePie/AppErrorsTracking/releases)
[![Blank](https://img.shields.io/github/downloads/Xposed-Modules-Repo/com.fankes.apperrorstracking/total?label=LSPosed%20Repo&logo=Android&style=flat&labelColor=F48FB1&logoColor=ffffff)](https://github.com/Xposed-Modules-Repo/com.fankes.apperrorstracking/releases)
<br/><br/>
Added more features to app's errors dialog, fixed custom rom deleted dialog, the best experience to Android developer.

This project is an Xposed module that can be used in any Android system, currently only tested in **LSPosed**.

This module is specially designed for Android developers.

When it is possible that the computer cannot be connected and ADB cannot be performed, this module can be used to quickly capture any exception
of any installed apps, so as to quickly locate the problem.

The error log of apps crashing is an invaluable asset for developers. If you are not a developer, you can still install this module to provide
developers with more exception information to quickly solve problems.

> Minimum support Android 8.1

**应用异常跟踪**

为原生 FC 对话框增加更多功能并修复国内定制 ROM 删除 FC 对话框的问题，给 Android 开发者带来更好的体验。

此项目为 Xposed 模块，可用在任何 Android 系统中，目前仅在 **LSPosed** 中测试通过。

此模块专为 Android 开发者而打造。

在可能的无法连接电脑，不能进行 ADB 调试的时候，可通过此模块来快速捕获任意已安装应用的任意异常，以便快速定位问题。

应用发生崩溃的错误日志对开发者来说是无价的财富，若你不是开发者，你依然可以安装此模块，以便给开发者提供更多异常信息快速解决问题。

> 最低支持 Android 8.1

## Project Reason

I really can't understand, except for MIUI (except stable version), Android ROMs in mainland China have chosen to delete the dialog box (FC
dialog) of apps crashes. I thought this was always a feature until I decompiled the system. frame, only to confirm that it was indeed deleted.

Does the product manager think that it is the best solution to let the user not see the error, and the apps will crash and exit directly, or is
there another **hidden secret**?

**项目缘由**

我实在是不能理解，国内 ROM 除了 MIUI(稳定版除外) 都选择了删除应用程序崩溃的对话框(FC 对话框)，我曾以为这一直是一个特性，直到我去反编译了系统框架，才确认确实是被删掉了。

难道产品经理认为，让用户看不到错误，应用直接闪退，逃避就是最好的解决方案吗，还是说**另有隐情**呢？

## Woking Principle

Unlike `Thread.UncaughtExceptionHandler`, we use the native method to capture apps errors in all directions by injecting the system framework,
without generating additional registration monitoring, which is better than the original exception monitoring in performance.

At the same time, the system-level exception capture can also capture the `stack trace` of the native platform.

**工作原理**

不同于 `Thread.UncaughtExceptionHandler`，我们通过注入系统框架，使用原生方式全方位捕获应用异常，不会产生额外的注册监听，在性能上相比原始的异常监听会更好。

同时系统级别的异常捕获还可捕获原生层的 `stack trace`。

## Feature

- Completely replaces the system's apps errors dialog

- Logs exceptions for each apps and persists until restart

- Copy, share, export errors stack trace functions

- Errors history record function, which can be entered through the notification bar tile "errors history record" and the main interface of the
  module

- Apps errors statistics function

- Errors display function for multi-process apps

**功能**

- 完全取代系统的应用错误对话框

- 记录每个应用的异常，直到重新启动前持续保留

- 复制、分享、导出异常堆栈功能

- 异常历史记录功能，可通过通知栏磁贴“异常历史记录”进入和模块主界面进入

- 应用异常统计功能

- 多进程应用的异常显示功能

## Translation contribution

Contributions to this project are welcome to translate it into your country's language.

**翻译贡献**

欢迎为此项目做出贡献，将其翻译为您国家的语言。

## License

- [AGPL-3.0](https://www.gnu.org/licenses/agpl-3.0.html)

```
Copyright (C) 2019-2022 Fankes Studio(qzmmcn@163.com)

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

版权所有 © 2019-2022 Fankes Studio(qzmmcn@163.com)