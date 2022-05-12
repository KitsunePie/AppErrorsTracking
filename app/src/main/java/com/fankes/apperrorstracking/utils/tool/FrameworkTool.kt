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
@file:Suppress("UNCHECKED_CAST")

package com.fankes.apperrorstracking.utils.tool

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.fankes.apperrorstracking.bean.AppErrorsInfoBean
import com.fankes.apperrorstracking.const.Const
import com.highcapable.yukihookapi.hook.log.loggerE

/**
 * 系统框架控制工具
 */
object FrameworkTool {

    /** 回调获取的 APP 异常信息 */
    private var onAppErrorsInfoDataCallback: ((ArrayList<AppErrorsInfoBean>) -> Unit)? = null

    /** 回调 APP 异常信息是否清空 */
    private var onClearAppErrorsInfoDataCallback: (() -> Unit)? = null

    /** 模块广播接收器 */
    private val moduleHandlerReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent == null) return
                runCatching {
                    intent.getStringExtra(Const.KEY_MODULE_HOST_FETCH)?.also {
                        if (it.isNotBlank()) when (it) {
                            Const.TYPE_APP_ERRORS_DATA_GET -> runCatching {
                                onAppErrorsInfoDataCallback?.invoke(
                                    intent.getSerializableExtra(Const.TAG_APP_ERRORS_DATA_CONTENT) as ArrayList<AppErrorsInfoBean>
                                )
                            }.onFailure { onAppErrorsInfoDataCallback?.invoke(arrayListOf()) }
                            Const.TYPE_APP_ERRORS_DATA_CLEAR -> onClearAppErrorsInfoDataCallback?.invoke()
                            else -> {}
                        }
                    }
                }.onFailure { loggerE(msg = "Cannot receiver message, please restart system", e = it) }
            }
        }
    }

    /**
     * 发送广播
     * @param context 实例
     * @param type 类型
     */
    private fun pushReceiver(context: Context, type: String) {
        context.sendBroadcast(Intent().apply {
            action = Const.ACTION_HOST_HANDLER_RECEIVER
            putExtra(Const.KEY_MODULE_HOST_FETCH, type)
        })
    }

    /**
     * 获取 APP 异常信息数组
     * @param context 实例
     * @param it 回调数据
     */
    fun fetchAppErrorsInfoData(context: Context, it: (ArrayList<AppErrorsInfoBean>) -> Unit) {
        onAppErrorsInfoDataCallback = it
        pushReceiver(context, Const.TYPE_APP_ERRORS_DATA_GET)
    }

    /**
     * 清空 APP 异常信息数组
     * @param context 实例
     * @param it 成功后回调
     */
    fun clearAppErrorsInfoData(context: Context, it: () -> Unit) {
        onClearAppErrorsInfoDataCallback = it
        pushReceiver(context, Const.TYPE_APP_ERRORS_DATA_CLEAR)
    }

    /**
     * 注册广播
     * @param context 实例
     */
    fun registerReceiver(context: Context) = runCatching {
        context.registerReceiver(moduleHandlerReceiver, IntentFilter().apply { addAction(Const.ACTION_MODULE_HANDLER_RECEIVER) })
    }

    /**
     * 取消注册
     * @param context 实例
     */
    fun unregisterReceiver(context: Context) = runCatching {
        context.unregisterReceiver(moduleHandlerReceiver)
    }
}