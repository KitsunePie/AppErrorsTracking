/*
 * AppErrorsTracking - Added more features to app's crash dialog, fixed custom rom deleted dialog, the best experience to Android developer.
 * Copyright (C) 2017-2023 Fankes Studio(qzmmcn@163.com)
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
 * This file is created by fankes on 2022/5/10.
 */
package com.fankes.apperrorsdemo.native

object Channel {

    init {
        System.loadLibrary("demo_app")
    }

    fun throwRuntimeException() {
        throw RuntimeException("Exception test")
    }

    fun throwIllegalStateException() {
        throw IllegalStateException("Exception test")
    }

    fun throwNullPointerException() {
        throw NullPointerException("Exception test")
    }

    fun throwException() {
        throw Exception()
    }

    external fun throwNativeException()
}