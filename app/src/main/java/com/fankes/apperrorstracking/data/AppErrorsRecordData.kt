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
 * This file is Created by fankes on 2023/1/17.
 */
@file:Suppress("StaticFieldLeak")

package com.fankes.apperrorstracking.data

import android.content.Context
import android.provider.Settings
import com.fankes.apperrorstracking.bean.AppErrorsInfoBean
import com.fankes.apperrorstracking.utils.factory.toEntityOrNull
import com.fankes.apperrorstracking.utils.factory.toJsonOrNull
import com.highcapable.yukihookapi.hook.log.loggerE
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

/**
 *  [AppErrorsInfoBean] 存储控制类
 */
object AppErrorsRecordData {

    /** 异常记录数据文件目录路径 */
    private const val FOLDER_PATH = "/data/misc/app_errors_records/"

    /** 当前实例 */
    private var context: Context? = null

    /**
     * 获取当前异常记录数据目录
     * @return [File]
     */
    private val errorsInfoDataFolder by lazy { File(FOLDER_PATH) }

    /**
     * 获取当前全部异常记录数据文件
     * @return [File]
     */
    private val errorsInfoDataFiles get() = errorsInfoDataFolder.listFiles() ?: emptyArray()

    /** 已记录的全部 APP 异常信息数组 */
    var allData = CopyOnWriteArrayList<AppErrorsInfoBean>()

    /**
     * 初始化存储控制类
     * @param context 实例
     */
    fun init(context: Context) {
        this.context = context
        initializeDataDirectory()
        allData = readAllDataFromFiles()
    }

    /** 初始化异常记录数据目录 */
    private fun initializeDataDirectory() {
        runCatching {
            errorsInfoDataFolder.also { if (it.exists().not() || it.isFile) it.apply { delete(); mkdirs() } }
        }.onFailure {
            loggerE(msg = "Can't create directory \"$FOLDER_PATH\", there will be problems with the app errors records function", e = it)
        }
    }

    /**
     * 获取旧版异常记录数据并自动转换到新版
     * @return [ArrayList]<[AppErrorsInfoBean]> or null
     */
    private fun copyOldDataFromResolverString() = context?.let {
        val keyName = "app_errors_data"
        runCatching {
            Settings.Secure.getString(it.contentResolver, keyName)
                ?.toEntityOrNull<CopyOnWriteArrayList<AppErrorsInfoBean>>()
                ?.onEach { e ->
                    e.toJsonOrNull()?.also { json -> File(errorsInfoDataFolder.absolutePath, e.jsonFileName).writeText(json) }
                }.let { result ->
                    if (result != null) {
                        Settings.Secure.putString(it.contentResolver, keyName, "")
                        result
                    } else null
                }
        }.getOrNull()
    }

    /**
     * 从文件获取全部异常记录数据
     * @return [ArrayList]<[AppErrorsInfoBean]>
     */
    private fun readAllDataFromFiles() = copyOldDataFromResolverString() ?: CopyOnWriteArrayList<AppErrorsInfoBean>().apply {
        errorsInfoDataFiles.takeIf { it.isNotEmpty() }?.forEach { it.readText().toEntityOrNull<AppErrorsInfoBean>()?.let { e -> add(e) } }
    }

    /**
     * 添加新的异常记录数据
     * @param bean [AppErrorsInfoBean] 实例
     */
    fun add(bean: AppErrorsInfoBean) {
        allData.add(0, bean)
        bean.toJsonOrNull()?.runCatching { File(errorsInfoDataFolder.absolutePath, bean.jsonFileName).writeText(this) }
    }

    /**
     * 移除指定的异常记录数据
     * @param bean [AppErrorsInfoBean] 实例
     */
    fun remove(bean: AppErrorsInfoBean) {
        allData.remove(bean)
        runCatching { File(errorsInfoDataFolder.absolutePath, bean.jsonFileName).delete() }
    }

    /** 清除全部异常记录数据 */
    fun clearAll() {
        allData.clear()
        runCatching { errorsInfoDataFolder.deleteRecursively() }
        initializeDataDirectory()
    }
}