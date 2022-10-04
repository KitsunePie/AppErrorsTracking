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
 * This file is Created by fankes on 2022/5/10.
 */
package com.fankes.apperrorstracking.application

import androidx.appcompat.app.AppCompatDelegate
import com.fankes.apperrorstracking.data.ConfigData
import com.fankes.apperrorstracking.locale.LocaleString
import com.fankes.apperrorstracking.utils.tool.AppAnalyticsTool
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication

class AppErrorsApplication : ModuleApplication() {

    override fun onCreate() {
        super.onCreate()
        /** 跟随系统夜间模式 */
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        /** 绑定 I18n */
        LocaleString.bind(instance = this)
        /** 装载存储控制类 */
        ConfigData.init(instance = this)
        /** 装载 App Center */
        AppAnalyticsTool.init(instance = this)
    }
}