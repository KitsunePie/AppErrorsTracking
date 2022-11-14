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
import com.fankes.apperrorstracking.bean.AppFiltersBean
import com.fankes.apperrorstracking.bean.AppInfoBean
import com.fankes.apperrorstracking.bean.MutedErrorsAppBean
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
    const val SYSTEM_FRAMEWORK_NAME = "android"

    private const val CALL_APP_ERRORS_DATA_GET = "call_app_errors_data_get"
    private const val CALL_MUTED_ERRORS_APP_DATA_GET = "call_muted_app_errors_data_get"
    private const val CALL_APP_ERRORS_DATA_CLEAR = "call_app_errors_data_clear"
    private const val CALL_UNMUTE_ALL_ERRORS_APPS_DATA = "call_unmute_all_errors_apps_data"
    private const val CALL_APP_ERRORS_DATA_REMOVE_RESULT = "call_app_errors_data_remove_result"
    private const val CALL_APP_ERRORS_DATA_CLEAR_RESULT = "call_app_errors_data_clear_result"
    private const val CALL_MUTED_ERRORS_IF_UNLOCK_RESULT = "call_muted_errors_if_unlock_result"
    private const val CALL_MUTED_ERRORS_IF_RESTART_RESULT = "call_muted_errors_if_restart_result"
    private const val CALL_UNMUTE_ERRORS_APP_DATA_RESULT = "call_unmute_errors_app_data_result"
    private const val CALL_UNMUTE_ALL_ERRORS_APPS_DATA_RESULT = "call_unmute_all_errors_apps_data_result"

    private val CALL_APP_ERROR_DATA_GET = ChannelData<Int>("call_app_error_data_get")
    private val CALL_OPEN_SPECIFY_APP = ChannelData<Pair<String, Int>>("call_open_specify_app")
    private val CALL_APP_LIST_DATA_GET = ChannelData<AppFiltersBean>("call_app_info_list_data_get")
    private val CALL_APP_ERRORS_DATA_REMOVE = ChannelData<AppErrorsInfoBean>("call_app_errors_data_remove")
    private val CALL_APP_LIST_DATA_GET_RESULT = ChannelData<ArrayList<AppInfoBean>>("call_app_info_list_data_get_result")
    private val CALL_APP_ERROR_DATA_GET_RESULT = ChannelData<AppErrorsInfoBean>("call_app_error_data_get_result")
    private val CALL_APP_ERRORS_DATA_GET_RESULT = ChannelData<MutableList<AppErrorsInfoBean>>("call_app_errors_data_get_result")
    private val CALL_MUTED_ERRORS_APP_DATA_GET_RESULT = ChannelData<ArrayList<MutedErrorsAppBean>>("call_muted_app_errors_data_get_result")
    private val CALL_UNMUTE_ERRORS_APP_DATA = ChannelData<MutedErrorsAppBean>("call_unmute_errors_app_data")
    private val CALL_MUTED_ERRORS_IF_UNLOCK = ChannelData<String>("call_muted_errors_if_unlock")
    private val CALL_MUTED_ERRORS_IF_RESTART = ChannelData<String>("call_muted_errors_if_restart")

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
         * @param result 回调包名和用户 ID
         */
        fun onOpenAppUsedFramework(result: (Pair<String, Int>) -> Unit) = instance?.dataChannel?.wait(CALL_OPEN_SPECIFY_APP) { result(it) }

        /**
         * 监听发送指定 APP 异常信息
         * @param result 回调数据
         */
        fun onPushAppErrorInfoData(result: (Int) -> AppErrorsInfoBean) {
            instance?.dataChannel?.with { wait(CALL_APP_ERROR_DATA_GET) { put(CALL_APP_ERROR_DATA_GET_RESULT, result(it)) } }
        }

        /**
         * 监听发送 APP 异常信息数组
         * @param result 回调数据
         */
        fun onPushAppErrorsInfoData(result: () -> MutableList<AppErrorsInfoBean>) {
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
        fun onMutedErrorsIfUnlock(result: (String) -> Unit) {
            instance?.dataChannel?.with {
                wait(CALL_MUTED_ERRORS_IF_UNLOCK) {
                    result(it)
                    put(CALL_MUTED_ERRORS_IF_UNLOCK_RESULT)
                }
            }
        }

        /**
         * 监听忽略 APP 的错误直到设备重新启动
         * @param result 回调包名
         */
        fun onMutedErrorsIfRestart(result: (String) -> Unit) {
            instance?.dataChannel?.with {
                wait(CALL_MUTED_ERRORS_IF_RESTART) {
                    result(it)
                    put(CALL_MUTED_ERRORS_IF_RESTART_RESULT)
                }
            }
        }

        /**
         * 监听发送已忽略异常的 APP 信息数组
         * @param result 回调数据
         */
        fun onPushMutedErrorsAppsData(result: () -> ArrayList<MutedErrorsAppBean>) {
            instance?.dataChannel?.with { wait(CALL_MUTED_ERRORS_APP_DATA_GET) { put(CALL_MUTED_ERRORS_APP_DATA_GET_RESULT, result()) } }
        }

        /**
         * 监听取消指定已忽略异常的 APP
         * @param result 回调数据
         */
        fun onUnmuteErrorsApp(result: (MutedErrorsAppBean) -> Unit) {
            instance?.dataChannel?.with {
                wait(CALL_UNMUTE_ERRORS_APP_DATA) {
                    result(it)
                    put(CALL_UNMUTE_ERRORS_APP_DATA_RESULT)
                }
            }
        }

        /**
         * 监听取消全部已忽略异常的 APP
         * @param callback 回调
         */
        fun onUnmuteAllErrorsApps(callback: () -> Unit) {
            instance?.dataChannel?.with {
                wait(CALL_UNMUTE_ALL_ERRORS_APPS_DATA) {
                    callback()
                    put(CALL_UNMUTE_ALL_ERRORS_APPS_DATA_RESULT)
                }
            }
        }

        /**
         * 监听发送已安装 APP 列表数组
         * @param result 回调数据
         */
        fun onPushAppListData(result: (AppFiltersBean) -> ArrayList<AppInfoBean>) {
            instance?.dataChannel?.with { wait(CALL_APP_LIST_DATA_GET) { put(CALL_APP_LIST_DATA_GET_RESULT, result(it)) } }
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
                context.showDialog {
                    title = LocaleString.warning
                    msg = LocaleString.fastRestartProblem
                    confirmButton {
                        if (isRootAccess)
                            execShell(cmd = "killall zygote")
                        else context.snake(LocaleString.accessRootFail)
                    }
                    cancelButton()
                }
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
     * @param userId APP 用户 ID
     */
    fun openAppUsedFramework(context: Context, packageName: String, userId: Int) =
        context.dataChannel(SYSTEM_FRAMEWORK_NAME).put(CALL_OPEN_SPECIFY_APP, Pair(packageName, userId))

    /**
     * 获取指定 APP 异常信息
     * @param context 实例
     * @param pid 当前进程 ID
     * @param result 回调数据
     */
    fun fetchAppErrorInfoData(context: Context, pid: Int, result: (AppErrorsInfoBean) -> Unit) {
        context.dataChannel(SYSTEM_FRAMEWORK_NAME).with {
            wait(CALL_APP_ERROR_DATA_GET_RESULT) { result(it) }
            put(CALL_APP_ERROR_DATA_GET, pid)
        }
    }

    /**
     * 获取 APP 异常信息数组
     * @param context 实例
     * @param result 回调数据
     */
    fun fetchAppErrorsInfoData(context: Context, result: (ArrayList<AppErrorsInfoBean>) -> Unit) {
        context.dataChannel(SYSTEM_FRAMEWORK_NAME).with {
            wait(CALL_APP_ERRORS_DATA_GET_RESULT) { result(it as ArrayList<AppErrorsInfoBean>) }
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
    fun mutedErrorsIfUnlock(context: Context, packageName: String, callback: () -> Unit) {
        context.dataChannel(SYSTEM_FRAMEWORK_NAME).with {
            wait(CALL_MUTED_ERRORS_IF_UNLOCK_RESULT) { callback() }
            put(CALL_MUTED_ERRORS_IF_UNLOCK, packageName)
        }
    }

    /**
     * 忽略 [packageName] 的错误直到设备重新启动
     * @param context 实例
     * @param packageName APP 包名
     * @param callback 成功后回调
     */
    fun mutedErrorsIfRestart(context: Context, packageName: String, callback: () -> Unit) {
        context.dataChannel(SYSTEM_FRAMEWORK_NAME).with {
            wait(CALL_MUTED_ERRORS_IF_RESTART_RESULT) { callback() }
            put(CALL_MUTED_ERRORS_IF_RESTART, packageName)
        }
    }

    /**
     * 获取已忽略异常的 APP 信息数组
     * @param context 实例
     * @param result 回调数据
     */
    fun fetchMutedErrorsAppsData(context: Context, result: (ArrayList<MutedErrorsAppBean>) -> Unit) {
        context.dataChannel(SYSTEM_FRAMEWORK_NAME).with {
            wait(CALL_MUTED_ERRORS_APP_DATA_GET_RESULT) { result(it) }
            put(CALL_MUTED_ERRORS_APP_DATA_GET)
        }
    }

    /**
     * 取消指定已忽略异常的 APP
     * @param context 实例
     * @param mutedErrorsApp 指定已忽略异常的 APP 信息
     * @param callback 成功后回调
     */
    fun unmuteErrorsApp(context: Context, mutedErrorsApp: MutedErrorsAppBean, callback: () -> Unit) {
        context.dataChannel(SYSTEM_FRAMEWORK_NAME).with {
            wait(CALL_UNMUTE_ERRORS_APP_DATA_RESULT) { callback() }
            put(CALL_UNMUTE_ERRORS_APP_DATA, mutedErrorsApp)
        }
    }

    /**
     * 取消全部已忽略异常的 APP
     * @param context 实例
     * @param callback 成功后回调
     */
    fun unmuteAllErrorsApps(context: Context, callback: () -> Unit) {
        context.dataChannel(SYSTEM_FRAMEWORK_NAME).with {
            wait(CALL_UNMUTE_ALL_ERRORS_APPS_DATA_RESULT) { callback() }
            put(CALL_UNMUTE_ALL_ERRORS_APPS_DATA)
        }
    }

    /**
     * 获取已安装 APP 列表数组
     * @param context 实例
     * @param appFilters 过滤条件
     * @param result 回调数据
     */
    fun fetchAppListData(context: Context, appFilters: AppFiltersBean, result: (ArrayList<AppInfoBean>) -> Unit) {
        context.dataChannel(SYSTEM_FRAMEWORK_NAME).with {
            wait(CALL_APP_LIST_DATA_GET_RESULT) { result(it) }
            put(CALL_APP_LIST_DATA_GET, appFilters)
        }
    }
}
