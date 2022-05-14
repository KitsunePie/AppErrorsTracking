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
@file:Suppress("MemberVisibilityCanBePrivate")

package com.fankes.apperrorstracking.const

import com.fankes.apperrorstracking.BuildConfig
import com.fankes.apperrorstracking.bean.AppErrorsInfoBean

/**
 * 全局常量
 */
object Const {

    /** 当前模块的包名 */
    const val MODULE_PACKAGE_NAME = BuildConfig.APPLICATION_ID

    /** 当前模块的版本名称 */
    const val MODULE_VERSION_NAME = BuildConfig.VERSION_NAME

    /** 当前模块的版本号 */
    const val MODULE_VERSION_CODE = BuildConfig.VERSION_CODE

    /** 当前模块的版本校验 */
    const val MODULE_VERSION_VERIFY = "${MODULE_VERSION_NAME}_${MODULE_VERSION_CODE}_202205141842"

    /** [AppErrorsInfoBean] 传值 */
    const val EXTRA_APP_ERRORS_INFO = "app_errors_info_extra"

    /** 模块接收广播 */
    const val ACTION_MODULE_HANDLER_RECEIVER = "module_handler_action"

    /** 宿主接收广播 */
    const val ACTION_HOST_HANDLER_RECEIVER = "host_handler_action"

    /** 模块与宿主交互数据 */
    const val KEY_MODULE_HOST_FETCH = "module_host_data_fetch_key"

    /** 校验模块宿主版本一致性 */
    const val TYPE_MODULE_VERSION_VERIFY = "module_version_verify_type"

    /** 获取 [AppErrorsInfoBean] 数据 */
    const val TYPE_APP_ERRORS_DATA_GET = "app_errors_data_get_type"

    /** 清空 [AppErrorsInfoBean] 数据 */
    const val TYPE_APP_ERRORS_DATA_CLEAR = "app_errors_data_clear_type"

    /** 删除指定 [AppErrorsInfoBean] 数据 */
    const val TYPE_APP_ERRORS_DATA_REMOVE = "app_errors_data_remove_type"

    /** 当前模块的版本校验标签 */
    const val TAG_MODULE_VERSION_VERIFY = "module_version_verify_tag"

    /** 获取到的 [AppErrorsInfoBean] 数据内容 */
    const val TAG_APP_ERRORS_DATA_GET_CONTENT = "app_errors_data_get_content_tag"

    /** 指定删除的 [AppErrorsInfoBean] 数据内容 */
    const val TAG_APP_ERRORS_DATA_REMOVE_CONTENT = "app_errors_data_remove_content_tag"
}