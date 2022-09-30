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
 * This file is Created by fankes on 2022/5/14.
 */
package com.fankes.apperrorstracking.data

import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

object DataConst {

    val SHOW_DEVELOPER_NOTICE = PrefsData("_show_developer_notice", true)

    val ENABLE_ONLY_SHOW_ERRORS_IN_FRONT = PrefsData("_enable_only_show_errors_in_front", false)
    val ENABLE_ONLY_SHOW_ERRORS_IN_MAIN = PrefsData("_enable_only_show_errors_in_main", false)
    val ENABLE_ALWAYS_SHOWS_REOPEN_APP_OPTIONS = PrefsData("_enable_always_shows_reopen_app_options", false)
    val ENABLE_APP_CONFIG_TEMPLATE = PrefsData("_enable_app_config_template", false)
}