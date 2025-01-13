/*
 * AppErrorsTracking - Added more features to app's crash dialog, fixed custom rom deleted dialog, the best experience to Android developer.
 * Copyright (C) 2017 Fankes Studio(qzmmcn@163.com)
 * https://github.com/KitsunePie/AppErrorsTracking
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 *
 * This file is created by fankes on 2022/6/1.
 */
package com.fankes.apperrorstracking.bean

import java.io.Serializable

/**
 * 应用异常信息显示 bean
 * @param pid APP 进程 ID
 * @param userId APP 用户 ID
 * @param packageName APP 包名
 * @param processName APP 进程名
 * @param appName APP 名称
 * @param title 标题
 * @param isShowAppInfoButton 是否显示应用信息按钮
 * @param isShowCloseAppButton 是否显示关闭应用按钮
 * @param isShowReopenButton 是否显示重新打开按钮
 */
data class AppErrorsDisplayBean(
    var pid: Int,
    var userId: Int,
    var packageName: String,
    var processName: String,
    var appName: String,
    var title: String,
    var isShowAppInfoButton: Boolean,
    var isShowCloseAppButton: Boolean,
    var isShowReopenButton: Boolean
) : Serializable