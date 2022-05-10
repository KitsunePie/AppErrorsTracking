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
package com.fankes.apperrorstracking.bean

import java.io.Serializable

/**
 * 应用异常信息 bean
 * @param packageName 包名
 * @param isNativeCrash 是否为原生层异常
 * @param exceptionClassName 异常类名
 * @param exceptionMessage 异常信息
 * @param throwClassName 抛出异常的类名
 * @param throwFileName 抛出异常的文件名
 * @param throwMethodName 抛出异常的方法名
 * @param throwLineNumber 抛出异常的行号
 * @param stackTrace 异常堆栈
 * @param timestamp 记录时间戳
 */
data class AppErrorsInfoBean(
    var packageName: String,
    var isNativeCrash: Boolean,
    var exceptionClassName: String,
    var exceptionMessage: String,
    var throwFileName: String,
    var throwClassName: String,
    var throwMethodName: String,
    var throwLineNumber: Int,
    var stackTrace: String,
    var timestamp: Long,
) : Serializable