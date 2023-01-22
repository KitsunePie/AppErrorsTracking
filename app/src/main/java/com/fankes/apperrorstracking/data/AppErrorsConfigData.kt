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
 * This file is Created by fankes on 2023/1/20.
 */
package com.fankes.apperrorstracking.data

import com.fankes.apperrorstracking.data.enum.AppErrorsConfigType
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

/**
 * 应用配置模版存储控制类
 */
object AppErrorsConfigData {

    /** 显示错误对话框键值名称 */
    private const val SHOW_ERRORS_DIALOG_APPS = "_show_errors_dialog_apps"

    /** 推送错误通知键值名称 */
    private const val SHOW_ERRORS_NOTIFY_APPS = "_show_errors_notify_apps"

    /** 显示错误 Toast 键值名称 */
    private const val SHOW_ERRORS_TOAST_APPS = "_show_errors_toast_apps"

    /** 什么也不显示键值名称 */
    private const val SHOW_ERRORS_NOTHING_APPS = "_show_errors_nothing_apps"

    /** 全局错误显示类型 */
    private val GLOBAL_SHOW_ERRORS_TYPE = PrefsData("_global_show_errors_type", AppErrorsConfigType.DIALOG.ordinal)

    /** 显示错误对话框的 APP 包名数组 */
    private var showDialogApps = HashSet<String>()

    /** 推送错误通知的 APP 包名数组 */
    private var showNotifyApps = HashSet<String>()

    /** 显示错误 Toast 的 APP 包名数组 */
    private var showToastApps = HashSet<String>()

    /** 什么也不显示的 APP 包名数组 */
    private var showNothingApps = HashSet<String>()

    /** 刷新存储控制类 */
    fun refresh() {
        showDialogApps = ConfigData.getStringSet(SHOW_ERRORS_DIALOG_APPS).toHashSet()
        showNotifyApps = ConfigData.getStringSet(SHOW_ERRORS_NOTIFY_APPS).toHashSet()
        showToastApps = ConfigData.getStringSet(SHOW_ERRORS_TOAST_APPS).toHashSet()
        showNothingApps = ConfigData.getStringSet(SHOW_ERRORS_NOTHING_APPS).toHashSet()
    }

    /**
     * 获取当前 APP 显示错误的类型是否为 [type]
     * @param type 当前类型
     * @param packageName 当前 APP 包名 - 不填为全局配置
     * @return [Boolean]
     */
    fun isAppShowingType(type: AppErrorsConfigType, packageName: String = "") =
        if (packageName.isNotBlank()) when (type) {
            AppErrorsConfigType.GLOBAL ->
                showDialogApps.contains(packageName).not() &&
                        showNotifyApps.contains(packageName).not() &&
                        showToastApps.contains(packageName).not() &&
                        showNothingApps.contains(packageName).not()
            AppErrorsConfigType.DIALOG -> showDialogApps.contains(packageName)
            AppErrorsConfigType.NOTIFY -> showNotifyApps.contains(packageName)
            AppErrorsConfigType.TOAST -> showToastApps.contains(packageName)
            AppErrorsConfigType.NOTHING -> showNothingApps.contains(packageName)
        } else ConfigData.getInt(GLOBAL_SHOW_ERRORS_TYPE) == type.ordinal

    /**
     * 写入当前 APP 显示错误的类型
     * @param type 当前类型
     * @param packageName 当前 APP 包名 - 不填为全局配置
     * @throws IllegalStateException 如果 [packageName] 为空 [type] 为 [AppErrorsConfigType.GLOBAL]
     */
    fun putAppShowingType(type: AppErrorsConfigType, packageName: String = "") {
        if (packageName.isBlank() && type == AppErrorsConfigType.GLOBAL)
            error("You can't still specify the \"follow global config\" type when saving the global config")
        fun saveAllData() {
            ConfigData.putStringSet(SHOW_ERRORS_DIALOG_APPS, showDialogApps)
            ConfigData.putStringSet(SHOW_ERRORS_NOTIFY_APPS, showNotifyApps)
            ConfigData.putStringSet(SHOW_ERRORS_TOAST_APPS, showToastApps)
            ConfigData.putStringSet(SHOW_ERRORS_NOTHING_APPS, showNothingApps)
        }
        if (packageName.isNotBlank()) when (type) {
            AppErrorsConfigType.GLOBAL -> {
                showDialogApps.remove(packageName)
                showNotifyApps.remove(packageName)
                showToastApps.remove(packageName)
                showNothingApps.remove(packageName)
                saveAllData()
            }
            AppErrorsConfigType.DIALOG -> {
                showDialogApps.add(packageName)
                showNotifyApps.remove(packageName)
                showToastApps.remove(packageName)
                showNothingApps.remove(packageName)
                saveAllData()
            }
            AppErrorsConfigType.NOTIFY -> {
                showDialogApps.remove(packageName)
                showNotifyApps.add(packageName)
                showToastApps.remove(packageName)
                showNothingApps.remove(packageName)
                saveAllData()
            }
            AppErrorsConfigType.TOAST -> {
                showDialogApps.remove(packageName)
                showNotifyApps.remove(packageName)
                showToastApps.add(packageName)
                showNothingApps.remove(packageName)
                saveAllData()
            }
            AppErrorsConfigType.NOTHING -> {
                showDialogApps.remove(packageName)
                showNotifyApps.remove(packageName)
                showToastApps.remove(packageName)
                showNothingApps.add(packageName)
                saveAllData()
            }
        } else ConfigData.putInt(GLOBAL_SHOW_ERRORS_TYPE, type.ordinal)
    }
}