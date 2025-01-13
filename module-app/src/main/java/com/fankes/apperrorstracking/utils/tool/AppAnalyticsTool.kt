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
 * This file is created by fankes on 2022/10/5.
 */
@file:Suppress("unused")

package com.fankes.apperrorstracking.utils.tool

import android.app.Application
import android.widget.CompoundButton
import com.fankes.apperrorstracking.generated.ModuleAppProperties
import com.highcapable.yukihookapi.hook.factory.prefs
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes

/**
 * Microsoft App Center 工具
 */
object AppAnalyticsTool {

    /** App Secret */
    private const val APP_CENTER_SECRET = ModuleAppProperties.APP_CENTER_SECRET

    /** 启用匿名统计收集使用情况功能 */
    private val ENABLE_APP_CENTER_ANALYTICS = PrefsData("_enable_app_center_analytics", true)

    /** 当前实例 */
    private var instance: Application? = null

    /**
     * 是否启用匿名统计收集使用情况功能
     * @return [Boolean]
     */
    private var isEnableAppCenterAnalytics
        get() = instance?.prefs()?.get(ENABLE_APP_CENTER_ANALYTICS) ?: true
        set(value) {
            instance?.prefs()?.edit { put(ENABLE_APP_CENTER_ANALYTICS, value) }
        }

    /** 是否可用 */
    val isAvailable = APP_CENTER_SECRET.isNotBlank()

    /** 绑定到 [CompoundButton] 自动设置选中状态 */
    fun CompoundButton.bindAppAnalytics() {
        isChecked = isEnableAppCenterAnalytics
        setOnCheckedChangeListener { button, isChecked ->
            if (button.isPressed.not()) return@setOnCheckedChangeListener
            isEnableAppCenterAnalytics = isChecked
        }
    }

    /**
     * 上传分析日志
     * @param name 事件名称
     * @param data 事件详细内容 - 默认空
     */
    fun trackEvent(name: String, data: HashMap<String, String>? = null) {
        if (data != null) Analytics.trackEvent(name, data)
        else Analytics.trackEvent(name)
    }

    /**
     * 初始化 App Center
     * @param instance 实例
     */
    fun init(instance: Application) {
        this.instance = instance
        if (isEnableAppCenterAnalytics && isAvailable)
            AppCenter.start(instance, APP_CENTER_SECRET, Analytics::class.java, Crashes::class.java)
    }
}