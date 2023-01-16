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
 * This file is Created by fankes on 2022/10/3.
 */
package com.fankes.apperrorstracking.utils.factory

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

/**
 * 创建 [Gson] 实例
 * @return [Gson]
 */
val GSON by lazy { GsonBuilder().setLenient().create() ?: error("Gson create failed") }

/**
 * 实体类转 Json 字符串
 * @return [String]
 * @throws [JsonIOException] 如果不是有效的 Json 数据
 */
fun Any?.toJson() = GSON.toJson(this) ?: ""

/**
 * 实体类转 Json 字符串
 * @return [String] or null
 */
fun Any?.toJsonOrNull() = runCatching { toJson() }.getOrNull()

/**
 * Json 字符串转实体类
 * @return [T] or null
 * @throws [JsonSyntaxException] 如果 Json 格式不正确
 */
inline fun <reified T> String.toEntity(): T? = takeIf { it.isNotBlank() }.let { GSON.fromJson(this, object : TypeToken<T>() {}.type) }

/**
 * Json 字符串转实体类
 * @return [T] or null
 */
inline fun <reified T> String.toEntityOrNull(): T? = runCatching { toEntity<T?>() }.getOrNull()