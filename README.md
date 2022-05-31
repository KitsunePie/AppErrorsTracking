# AppErrorsTracking

**应用异常跟踪**

Added more features to app's crash dialog, fixed custom rom deleted dialog, the best experience to Android developer.

为原生 FC 对话框增加更多功能并修复国内定制 ROM 删除 FC 对话框的问题，给 Android 开发者带来更好的体验。

此项目为 Xposed 模块，可用在任何 Android 系统中，目前仅在 **LSPosed** 中测试通过。

> 最低支持 Android 8.1

## Project Reason

我实在是不能理解，国内 ROM 除了 MIUI(稳定版除外) 都选择了删除应用程序崩溃的对话框(FC 对话框)，我曾以为这一直是一个特性，直到我去反编译了系统框架，才确认确实是被删掉了。

难道产品经理认为，让用户看不到错误，应用直接闪退，逃避就是最好的解决方案吗，还是说**另有隐情**呢？

## Principle

不同于 `Thread.UncaughtExceptionHandler`，此项目通过注入系统框架，使用原生方式全方位捕获应用异常，不会产生额外的注册监听，在性能上相比原始的异常监听会更好。

同时系统级别的异常捕获还可捕获原生层的 stack。

## Feature

- 重新定制应用异常错误对话框

- 记录每个应用的异常，直到重新启动前持续保留

- “应用信息”按钮功能(原生功能)，点击可打开当前出错的应用详情页面

- “重新打开”按钮功能(原生功能)，在首次错误可点击按钮重新打开应用

- “屡次停止运行”显示(原生功能)

- “忽略（直到设备重新解锁/重新启动）”显示(原生功能)

- “错误详情”按钮功能，可查看具体的异常堆栈

- 导出异常堆栈到文件功能

- 复制异常堆栈功能

- 打印异常堆栈到控制台功能

- 异常历史记录功能，可通过通知栏磁贴“异常历史记录”进入和模块主界面进入(正在开发)

- 多进程 APP 的异常将会显示异常的进程名

- 支持 Android 10 及以上系统的深色模式

## Future

此项目依然在开发中，现在未解决的问题和包含的问题如下

- 排除列表功能

- 模块主界面进入“异常历史记录”功能

- 隐藏 APP 多进程和后台进程崩溃对话框功能

- 已忽略异常的 APP 查看功能

- 更多功能

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