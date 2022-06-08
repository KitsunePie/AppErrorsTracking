/*
 * AppErrorsTracking - Added more features to app's crash dialog, fixed custom rom deleted dialog, the best experience to Android developer.
 * Copyright (C) 2019-2022 Fankes Studio(qzmmcn@163.com)
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
 * This file is Created by fankes on 2022/6/8.
 */
@file:Suppress("unused")

package com.fankes.apperrorstracking.hook.factory

import android.content.Context
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import com.highcapable.yukihookapi.hook.param.PackageParam

/**
 * 获取此 APP 是否配置显示错误对话框
 * @param packageName APP 包名
 */
fun PackageParam.isAppShowErrorsDialog(packageName: String) = prefs.getBoolean("${packageName}_show_errors_dialog", true)

/**
 * 获取此 APP 是否配置显示错误 Toast 提示
 * @param packageName APP 包名
 */
fun PackageParam.isAppShowErrorsToast(packageName: String) = prefs.getBoolean("${packageName}_show_errors_toast", false)

/**
 * 获取此 APP 是否配置不显示任何提示
 * @param packageName APP 包名
 */
fun PackageParam.isAppShowNothing(packageName: String) = prefs.getBoolean("${packageName}_show_nothing", false)

/**
 * 获取此 APP 是否配置显示错误对话框
 * @param packageName APP 包名
 */
fun Context.isAppShowErrorsDialog(packageName: String) = modulePrefs.getBoolean("${packageName}_show_errors_dialog", true)

/**
 * 获取此 APP 是否配置显示错误 Toast 提示
 * @param packageName APP 包名
 */
fun Context.isAppShowErrorsToast(packageName: String) = modulePrefs.getBoolean("${packageName}_show_errors_toast", false)

/**
 * 获取此 APP 是否配置不显示任何提示
 * @param packageName APP 包名
 */
fun Context.isAppShowNothing(packageName: String) = modulePrefs.getBoolean("${packageName}_show_nothing", false)

/**
 * 设置此 APP 是否配置显示错误对话框
 * @param packageName APP 包名
 * @param isApply 是否设置
 */
fun Context.putAppShowErrorsDialog(packageName: String, isApply: Boolean) = modulePrefs.putBoolean("${packageName}_show_errors_dialog", isApply)

/**
 * 设置此 APP 是否配置显示错误 Toast 提示
 * @param packageName APP 包名
 * @param isApply 是否设置
 */
fun Context.putAppShowErrorsToast(packageName: String, isApply: Boolean) = modulePrefs.putBoolean("${packageName}_show_errors_toast", isApply)

/**
 * 设置此 APP 是否配置不显示任何提示
 * @param packageName APP 包名
 * @param isApply 是否设置
 */
fun Context.putAppShowNothing(packageName: String, isApply: Boolean) = modulePrefs.putBoolean("${packageName}_show_nothing", isApply)