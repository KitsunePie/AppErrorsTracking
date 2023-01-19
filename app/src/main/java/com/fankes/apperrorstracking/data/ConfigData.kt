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
 * This file is Created by fankes on 2022/10/1.
 */
@file:Suppress("MemberVisibilityCanBePrivate")

package com.fankes.apperrorstracking.data

import android.content.Context
import android.os.Build
import android.widget.CompoundButton
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import com.highcapable.yukihookapi.hook.log.loggerW
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

/**
 * 全局配置存储控制类
 */
object ConfigData {

    /** 显示开发者提示 */
    val SHOW_DEVELOPER_NOTICE = PrefsData("_show_developer_notice", true)

    /** 启用 Material 3 风格的错误对话框 */
    val ENABLE_MATERIAL3_STYLE_APP_ERRORS_DIALOG = PrefsData("_enable_material3_style_dialog", Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)

    /** 仅对前台应用显示错误对话框 */
    val ENABLE_ONLY_SHOW_ERRORS_IN_FRONT = PrefsData("_enable_only_show_errors_in_front", false)

    /** 仅对应用主进程显示错误对话框 */
    val ENABLE_ONLY_SHOW_ERRORS_IN_MAIN = PrefsData("_enable_only_show_errors_in_main", false)

    /** 错误对话框始终显示“重新打开”选项 */
    val ENABLE_ALWAYS_SHOWS_REOPEN_APP_OPTIONS = PrefsData("_enable_always_shows_reopen_app_options", false)

    /** 启用应用配置模板 */
    val ENABLE_APP_CONFIG_TEMPLATE = PrefsData("_enable_app_config_template", false)

    /** 当前实例 - [Context] or [PackageParam] */
    private var instance: Any? = null

    /**
     * 初始化存储控制类
     * @param instance 实例 - 只能是 [Context] or [PackageParam]
     * @throws IllegalStateException 如果类型错误
     */
    fun init(instance: Any) {
        when (instance) {
            is Context, is PackageParam -> this.instance = instance
            else -> error("Unknown type for init ConfigData")
        }
        AppErrorsConfigData.refresh()
    }

    /**
     * 读取 [Set]<[String]> 数据
     * @param key 键值名称
     * @return [Set]<[String]>
     */
    internal fun getStringSet(key: String) = when (instance) {
        is Context -> (instance as Context).modulePrefs.getStringSet(key, setOf())
        is PackageParam -> (instance as PackageParam).prefs.getStringSet(key, setOf())
        else -> error("Unknown type for get prefs data")
    }

    /**
     * 存入 [Set]<[String]> 数据
     * @param key 键值名称
     * @param value 键值内容
     */
    internal fun putStringSet(key: String, value: Set<String>) {
        when (instance) {
            is Context -> (instance as Context).modulePrefs.putStringSet(key, value)
            is PackageParam -> loggerW(msg = "Not support for this method")
            else -> error("Unknown type for put prefs data")
        }
    }

    /**
     * 读取 [Int] 数据
     * @param data 键值数据模板
     * @return [Int]
     */
    internal fun getInt(data: PrefsData<Int>) = when (instance) {
        is Context -> (instance as Context).modulePrefs.get(data)
        is PackageParam -> (instance as PackageParam).prefs.get(data)
        else -> error("Unknown type for get prefs data")
    }

    /**
     * 存入 [Int] 数据
     * @param data 键值数据模板
     * @param value 键值内容
     */
    internal fun putInt(data: PrefsData<Int>, value: Int) {
        when (instance) {
            is Context -> (instance as Context).modulePrefs.put(data, value)
            is PackageParam -> loggerW(msg = "Not support for this method")
            else -> error("Unknown type for put prefs data")
        }
    }

    /**
     * 读取 [Boolean] 数据
     * @param data 键值数据模板
     * @return [Boolean]
     */
    private fun getBoolean(data: PrefsData<Boolean>) = when (instance) {
        is Context -> (instance as Context).modulePrefs.get(data)
        is PackageParam -> (instance as PackageParam).prefs.get(data)
        else -> error("Unknown type for get prefs data")
    }

    /**
     * 存入 [Boolean] 数据
     * @param data 键值数据模板
     * @param value 键值内容
     */
    private fun putBoolean(data: PrefsData<Boolean>, value: Boolean) {
        when (instance) {
            is Context -> (instance as Context).modulePrefs.put(data, value)
            is PackageParam -> loggerW(msg = "Not support for this method")
            else -> error("Unknown type for put prefs data")
        }
    }

    /**
     * 绑定到 [CompoundButton] 自动设置选中状态
     * @param data 键值数据模板
     * @param onChange 当改变时回调
     */
    fun CompoundButton.bind(data: PrefsData<Boolean>, onChange: (Boolean) -> Unit = {}) {
        isChecked = getBoolean(data).also(onChange)
        setOnCheckedChangeListener { button, isChecked ->
            if (button.isPressed) {
                putBoolean(data, isChecked)
                onChange(isChecked)
            }
        }
    }

    /**
     * 是否显示开发者提示
     * @return [Boolean]
     */
    var isShowDeveloperNotice
        get() = getBoolean(SHOW_DEVELOPER_NOTICE)
        set(value) {
            putBoolean(SHOW_DEVELOPER_NOTICE, value)
        }

    /**
     * 是否仅对前台应用显示错误对话框
     * @return [Boolean]
     */
    var isEnableOnlyShowErrorsInFront
        get() = getBoolean(ENABLE_ONLY_SHOW_ERRORS_IN_FRONT)
        set(value) {
            putBoolean(ENABLE_ONLY_SHOW_ERRORS_IN_FRONT, value)
        }

    /**
     * 是否仅对应用主进程显示错误对话框
     * @return [Boolean]
     */
    var isEnableOnlyShowErrorsInMain
        get() = getBoolean(ENABLE_ONLY_SHOW_ERRORS_IN_MAIN)
        set(value) {
            putBoolean(ENABLE_ONLY_SHOW_ERRORS_IN_MAIN, value)
        }

    /**
     * 错误对话框是否始终显示“重新打开”选项
     * @return [Boolean]
     */
    var isEnableAlwaysShowsReopenAppOptions
        get() = getBoolean(ENABLE_ALWAYS_SHOWS_REOPEN_APP_OPTIONS)
        set(value) {
            putBoolean(ENABLE_ALWAYS_SHOWS_REOPEN_APP_OPTIONS, value)
        }

    /**
     * 是否启用应用配置模板
     * @return [Boolean]
     */
    var isEnableAppConfigTemplate
        get() = getBoolean(ENABLE_APP_CONFIG_TEMPLATE)
        set(value) {
            putBoolean(ENABLE_APP_CONFIG_TEMPLATE, value)
        }

    /**
     * 是否启用 Material 3 风格的错误对话框
     * @return [Boolean]
     */
    var isEnableMaterial3StyleAppErrorsDialog
        get() = getBoolean(ENABLE_MATERIAL3_STYLE_APP_ERRORS_DIALOG)
        set(value) {
            putBoolean(ENABLE_MATERIAL3_STYLE_APP_ERRORS_DIALOG, value)
        }
}