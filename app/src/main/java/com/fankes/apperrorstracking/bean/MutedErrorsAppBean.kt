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
 * This file is Created by fankes on 2022/6/3.
 */
package com.fankes.apperrorstracking.bean

import java.io.Serializable

/**
 * 已忽略异常的应用 bean
 * @param type 类型
 * @param packageName 包名
 */
data class MutedErrorsAppBean(var type: MuteType, var packageName: String) : Serializable {

    /**
     * 已忽略的异常类型
     */
    enum class MuteType { UNTIL_UNLOCKS, UNTIL_REBOOTS }
}