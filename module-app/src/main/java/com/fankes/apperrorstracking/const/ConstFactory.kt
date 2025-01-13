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
 * This file is created by fankes on 2023/9/19.
 */
@file:Suppress("MemberVisibilityCanBePrivate")

package com.fankes.apperrorstracking.const

import com.fankes.apperrorstracking.generated.ModuleAppProperties
import com.fankes.apperrorstracking.wrapper.BuildConfigWrapper

/**
 * 包名常量定义类
 */
object PackageName {

    /** 系统框架 */
    const val SYSTEM_FRAMEWORK = "android"
}

/**
 * 模块版本常量定义类
 */
object ModuleVersion {

    /** 当前 GitHub 提交的 ID (CI 自动构建) */
    const val GITHUB_COMMIT_ID = ModuleAppProperties.GITHUB_CI_COMMIT_ID

    /** 版本名称 */
    const val NAME = BuildConfigWrapper.VERSION_NAME

    /** 版本号 */
    const val CODE = BuildConfigWrapper.VERSION_CODE

    /** 是否为 CI 自动构建版本 */
    val isCiMode = GITHUB_COMMIT_ID.isNotBlank()

    /** 当前版本名称后缀 */
    val suffix = GITHUB_COMMIT_ID.let { if (it.isNotBlank()) "-$it" else "" }

    override fun toString() = "$NAME$suffix($CODE)"
}