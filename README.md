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

English | [简体中文](https://github.com/KitsunePie/AppErrorsTracking/blob/master/README-zh-CN.md)

Added more features to app's errors dialog, fixed custom rom deleted dialog, the best experience to Android developer.

This project is a Xposed Module that can be used in any Android system, currently only tested in **LSPosed**.

This Xposed Module is specially designed for Android developers.

When it is possible that the computer cannot be connected and ADB cannot be performed, this module can be used to quickly capture any errors of
any installed apps, to quickly locate the problem.

The error log of apps crashing is an invaluable asset for developers. If you are not a developer, you can still install this module to provide
developers with more exception information to quickly solve problems.

> Minimum support Android 7.0

## Project Reason

I really can't understand, except for MIUI (except stable version), Android ROMs in mainland China have chosen to delete the dialog box (FC
dialog) of apps crashes. I thought this was always a feature until I decompiled the system. frame, only to confirm that it was indeed deleted.

Does the product manager think that it is the best solution to let the user not see the error, and the apps will crash and exit directly, or is
there another **hidden secret**?

## Woking Principle

Unlike `Thread.UncaughtExceptionHandler`, we use the native method to capture apps errors in all directions by injecting the system framework,
without generating additional registration monitoring, which is better than the original exception monitoring in performance.

At the same time, the system-level exception capture can also capture the `stack trace` of the native platform.

## Precautions

The errors captured by the system natively can only be errors that are not handled by the apps itself. If the apps itself has a
custom `Thread.UncaughtExceptionHandler`
Similar to **Bugly** to automatically collect errors, the system cannot obtain whether the apps actually crashes **(FC)**.

## Features List

- [x] Completely replaces the system's apps errors dialog

- [x] Logs exceptions for each app and persists until restart

- [x] Copy, share, export errors stack trace functions

- [x] Errors history record function,
  which can be entered through the notification bar tile "errors history record" and the main interface of the module

- [x] Apps errors statistics function

- [x] Errors display function for multi-process apps

## Translation Contribution

Contributions to this project are welcome to translate it into your country's language.

## Release Channel Description

- [Automatic Build on Commit](https://github.com/KitsunePie/AppErrorsTracking/actions/workflows/commit_ci.yml)

The above update is automatically triggered after the code `commit`.

The specific update content can be viewed by clicking the text above and going to **GitHub Actions**.

This update is automatically compiled and released by the open source process, **no guarantee of its stability**, so the released version is
**for testing only**, and there is no special explanation or even the version may change or remain the same as the current stable version.

If you need to download the CI installation packages,
please click the `CI builds | Telegram` tag at the top to join the Telegram CI automatic build channel.

- [Release](https://github.com/KitsunePie/AppErrorsTracking/releases)
- [Xposed-Modules-Repo](https://github.com/Xposed-Modules-Repo/com.fankes.apperrorstracking/releases)

The above update is a manually released stable version.

For the specific update content, you can click the text above to go to the designated release page to view.

The update of the stable version will be released to the above address at the same time and updated synchronously.

The releases of this Xposed Module is limited to the urls listed above.

We have nothing to do with versions downloaded from other informal channels or any impact on you.

## Promotion

If you are looking for a Gradle plugin that can automatically manage Gradle project dependencies,
you can check out the [SweetDependency](https://github.com/HighCapable/SweetDependency) project.

If you are looking for a Gradle plugin that can automatically generate properties key-values,
you can check out the [SweetProperty](https://github.com/HighCapable/SweetProperty) project.

This project also uses **SweetDependency** and **SweetProperty**.

## Star History

![Star History Chart](https://api.star-history.com/svg?repos=KitsunePie/AppErrorsTracking&type=Date)

## License

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

Copyright © 2017-2023 Fankes Studio(qzmmcn@163.com)