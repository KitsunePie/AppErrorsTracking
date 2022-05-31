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

import android.content.Context
import com.fankes.apperrorstracking.bean.AppErrorsInfoBean
import com.fankes.apperrorstracking.locale.LocaleString
import com.fankes.apperrorstracking.utils.factory.execShell
import com.fankes.apperrorstracking.utils.factory.isRootAccess
import com.fankes.apperrorstracking.utils.factory.showDialog
import com.fankes.apperrorstracking.utils.factory.snake
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.channel.data.ChannelData

/**
 * 系统框架控制工具
 */
object FrameworkTool {

    /** 系统框架包名 */
    private const val SYSTEM_FRAMEWORK_NAME = "android"

    private const val CALL_APP_ERRORS_DATA_GET = "call_app_errors_data_get"
    private const val CALL_APP_ERRORS_DATA_REMOVE_RESULT = "call_app_errors_data_remove_result"
    private const val CALL_APP_ERRORS_DATA_CLEAR = "call_app_errors_data_clear"
    private const val CALL_APP_ERRORS_DATA_CLEAR_RESULT = "call_app_errors_data_clear_result"
    private const val CALL_IGNORED_ERRORS_IF_UNLOCK_RESULT = "call_ignored_errors_if_unlock_result"
    private const val CALL_IGNORED_ERRORS_IF_RESTART_RESULT = "call_ignored_errors_if_restart_result"

    private val CALL_OPEN_SPECIFY_APP = ChannelData<String>("call_open_specify_app")
    private val CALL_APP_ERRORS_DATA_REMOVE = ChannelData<AppErrorsInfoBean>("call_app_errors_data_remove")
    private val CALL_APP_ERRORS_DATA_GET_RESULT = ChannelData<ArrayList<AppErrorsInfoBean>>("call_app_errors_data_get_result")
    private val CALL_IGNORED_ERRORS_IF_UNLOCK = ChannelData<String>("call_ignored_errors_if_unlock")
    private val CALL_IGNORED_ERRORS_IF_RESTART = ChannelData<String>("call_ignored_errors_if_restart")

    /**
     * 宿主注册监听
     */
    object Host {

        /** [PackageParam] 实例 */
        private var instance: PackageParam? = null

        /**
         * 注册监听
         * @param instance 实例
         * @param initiate 实例方法体
         * @return [Host]
         */
        fun with(instance: PackageParam, initiate: Host.() -> Unit) = apply { this.instance = instance }.apply(initiate)

        /**
         * 监听使用系统框架打开 APP
         * @param result 回调包名
         */
        fun onOpenAppUsedFramework(result: (String) -> Unit) = instance?.dataChannel?.wait(CALL_OPEN_SPECIFY_APP) { result(it) }

        /**
         * 监听发送 APP 异常信息数组
         * @param result 回调数据
         */
        fun onPushAppErrorsInfoData(result: () -> ArrayList<AppErrorsInfoBean>) {
            instance?.dataChannel?.with { wait(CALL_APP_ERRORS_DATA_GET) { put(CALL_APP_ERRORS_DATA_GET_RESULT, result()) } }
        }

        /**
         * 监听移除指定 APP 异常信息
         * @param result 回调数据
         */
        fun onRemoveAppErrorsInfoData(result: (AppErrorsInfoBean) -> Unit) {
            instance?.dataChannel?.with {
                wait(CALL_APP_ERRORS_DATA_REMOVE) {
                    result(it)
                    put(CALL_APP_ERRORS_DATA_REMOVE_RESULT)
                }
            }
        }

        /**
         * 监听清空 APP 异常信息数组
         * @param callback 回调
         */
        fun onClearAppErrorsInfoData(callback: () -> Unit) {
            instance?.dataChannel?.with {
                wait(CALL_APP_ERRORS_DATA_CLEAR) {
                    callback()
                    put(CALL_APP_ERRORS_DATA_CLEAR_RESULT)
                }
            }
        }

        /**
         * 监听忽略 APP 的错误直到设备重新解锁
         * @param result 回调包名
         */
        fun onIgnoredErrorsIfUnlock(result: (String) -> Unit) {
            instance?.dataChannel?.with {
                wait(CALL_IGNORED_ERRORS_IF_UNLOCK) {
                    result(it)
                    put(CALL_IGNORED_ERRORS_IF_UNLOCK_RESULT)
                }
            }
        }

        /**
         * 监听忽略 APP 的错误直到设备重新启动
         * @param result 回调包名
         */
        fun onIgnoredErrorsIfRestart(result: (String) -> Unit) {
            instance?.dataChannel?.with {
                wait(CALL_IGNORED_ERRORS_IF_RESTART) {
                    result(it)
                    put(CALL_IGNORED_ERRORS_IF_RESTART_RESULT)
                }
            }
        }
    }

    /**
     * 重启系统
     * @param context 实例
     */
    fun restartSystem(context: Context) =
        context.showDialog {
            title = LocaleString.notice
            msg = LocaleString.areYouSureRestartSystem
            confirmButton {
                if (isRootAccess)
                    execShell(cmd = "reboot")
                else context.snake(LocaleString.accessRootFail)
            }
            neutralButton(LocaleString.fastRestart) {
                if (isRootAccess)
                    execShell(cmd = "killall zygote")
                else context.snake(LocaleString.accessRootFail)
            }
            cancelButton()
        }

    /**
     * 检查模块是否激活
     * @param context 实例
     * @param result 成功后回调
     */
    fun checkingActivated(context: Context, result: (Boolean) -> Unit) = context.dataChannel(SYSTEM_FRAMEWORK_NAME).checkingVersionEquals(result)

    /**
     * 使用系统框架打开 [packageName]
     * @param context 实例
     * @param packageName APP 包名
     */
    fun openAppUsedFramework(context: Context, packageName: String) =
        context.dataChannel(SYSTEM_FRAMEWORK_NAME).put(CALL_OPEN_SPECIFY_APP, packageName)

    /**
     * 获取 APP 异常信息数组
     * @param context 实例
     * @param result 回调数据
     */
    fun fetchAppErrorsInfoData(context: Context, result: (ArrayList<AppErrorsInfoBean>) -> Unit) {
        context.dataChannel(SYSTEM_FRAMEWORK_NAME).with {
            wait(CALL_APP_ERRORS_DATA_GET_RESULT) { result(it) }
            put(CALL_APP_ERRORS_DATA_GET)
        }
    }

    /**
     * 移除指定 APP 异常信息
     * @param context 实例
     * @param appErrorsInfo 指定 APP 异常信息
     * @param callback 成功后回调
     */
    fun removeAppErrorsInfoData(context: Context, appErrorsInfo: AppErrorsInfoBean, callback: () -> Unit) {
        context.dataChannel(SYSTEM_FRAMEWORK_NAME).with {
            wait(CALL_APP_ERRORS_DATA_REMOVE_RESULT) { callback() }
            put(CALL_APP_ERRORS_DATA_REMOVE, appErrorsInfo)
        }
    }

    /**
     * 清空 APP 异常信息数组
     * @param context 实例
     * @param callback 成功后回调
     */
    fun clearAppErrorsInfoData(context: Context, callback: () -> Unit) {
        context.dataChannel(SYSTEM_FRAMEWORK_NAME).with {
            wait(CALL_APP_ERRORS_DATA_CLEAR_RESULT) { callback() }
            put(CALL_APP_ERRORS_DATA_CLEAR)
        }
    }

    /**
     * 忽略 [packageName] 的错误直到设备重新解锁
     * @param context 实例
     * @param packageName APP 包名
     * @param callback 成功后回调
     */
    fun ignoredErrorsIfUnlock(context: Context, packageName: String, callback: () -> Unit) {
        context.dataChannel(SYSTEM_FRAMEWORK_NAME).with {
            wait(CALL_IGNORED_ERRORS_IF_UNLOCK_RESULT) { callback() }
            put(CALL_IGNORED_ERRORS_IF_UNLOCK, packageName)
        }
    }

    /**
     * 忽略 [packageName] 的错误直到设备重新启动
     * @param context 实例
     * @param packageName APP 包名
     * @param callback 成功后回调
     */
    fun ignoredErrorsIfRestart(context: Context, packageName: String, callback: () -> Unit) {
        context.dataChannel(SYSTEM_FRAMEWORK_NAME).with {
            wait(CALL_IGNORED_ERRORS_IF_RESTART_RESULT) { callback() }
            put(CALL_IGNORED_ERRORS_IF_RESTART, packageName)
        }
    }
}