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
 * This file is Created by fankes on 2022/5/12.
 */
package com.fankes.apperrorstracking.const

import com.fankes.apperrorstracking.bean.AppErrorsInfoBean

/**
 * 全局常量
 */
object Const {

    /** [AppErrorsInfoBean] 传值 */
    const val EXTRA_APP_ERRORS_INFO = "app_errors_info_extra"

    /** 模块接收广播 */
    const val ACTION_MODULE_HANDLER_RECEIVER = "module_handler_action"

    /** 宿主接收广播 */
    const val ACTION_HOST_HANDLER_RECEIVER = "host_handler_action"

    /** [AppErrorsInfoBean] 控制数据 */
    const val TYPE_APP_ERRORS_DATA_CONTROL = "app_errors_data_control_type"

    /** [AppErrorsInfoBean] 获取数据 */
    const val TYPE_APP_ERRORS_DATA_CONTROL_GET_DATA = "app_errors_data_control_get_data_type"

    /** [AppErrorsInfoBean] 获取到的数据内容 */
    const val TAG_APP_ERRORS_DATA_CONTENT = "app_errors_data_content_tag"
}