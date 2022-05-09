# AppErrorsTracking

**应用异常跟踪**

Added more features to app's crash dialog, fixed custom rom deleted dialog, the best experience to Android developer.

为原生 FC 对话框增加更多功能并修复国内定制 ROM 删除 FC 对话框的问题，给 Android 开发者带来更好的体验。

## Project Reason

我实在是不能理解，国内 ROM 除了 MIUI(稳定版除外) 都选择了删除应用程序崩溃的对话框(FC 对话框)，我曾以为这一直是一个特性，直到我去反编译了系统框架，才确认确实是被删掉了。

难道产品经理认为，让用户看不到错误，应用直接闪退，逃避就是最好的解决方案吗，还是说**另有隐情**呢？

## Feature

此项目为 Xposed 模块，可用在任何 Android 系统中，目前仅在 **LSPosed** 中测试通过。

- 重新定制应用崩溃错误对话框

- “错误详情”按钮功能，可查看具体的异常堆栈

- “应用信息”按钮功能(原生功能)，点击可打开当前崩溃的应用详情页面

- “重新启动”按钮功能(原生功能)，在首次崩溃可点击按钮重新启动应用

- “屡次停止运行”显示(原生功能)

- “忽略（直到设备重新解锁/重新启动）”显示(原生功能)

- 对话框支持 Android 10 及以上系统的深色模式

## Future

此项目依然在开发中，现在未解决的问题和包含的问题如下

- “错误详情”按钮现在是无效的，还在开发

- 后台进程可能依然会弹出崩溃对话框且开发者选项里的设置无效，还在排查

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