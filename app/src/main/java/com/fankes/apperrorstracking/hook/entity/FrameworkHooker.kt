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
 * This file is Created by fankes on 2022/5/7.
 */
@file:Suppress("UseCompatLoadingForDrawables")

package com.fankes.apperrorstracking.hook.entity

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Message
import com.fankes.apperrorstracking.bean.AppErrorsDisplayBean
import com.fankes.apperrorstracking.bean.AppErrorsInfoBean
import com.fankes.apperrorstracking.locale.LocaleString
import com.fankes.apperrorstracking.ui.activity.errors.AppErrorsDisplayActivity
import com.fankes.apperrorstracking.utils.factory.appName
import com.fankes.apperrorstracking.utils.factory.isAppCanOpened
import com.fankes.apperrorstracking.utils.factory.openApp
import com.fankes.apperrorstracking.utils.tool.FrameworkTool
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.type.android.MessageClass

object FrameworkHooker : YukiBaseHooker() {

    private const val AppErrorsClass = "com.android.server.am.AppErrors"

    private const val AppErrorResultClass = "com.android.server.am.AppErrorResult"

    private const val AppErrorDialog_DataClass = "com.android.server.am.AppErrorDialog\$Data"

    private const val ProcessRecordClass = "com.android.server.am.ProcessRecord"

    private val PackageListClass = VariousClass(
        "com.android.server.am.ProcessRecord\$PackageList",
        "com.android.server.am.PackageList"
    )

    private val ErrorDialogControllerClass = VariousClass(
        "com.android.server.am.ProcessRecord\$ErrorDialogController",
        "com.android.server.am.ErrorDialogController"
    )

    /** 已忽略错误的 APP 数组 - 直到重新解锁 */
    private var ignoredErrorsIfUnlockApps = hashSetOf<String>()

    /** 已忽略错误的 APP 数组 - 直到重新启动 */
    private var ignoredErrorsIfRestartApps = hashSetOf<String>()

    /** 已记录的 APP 异常信息数组 - 直到重新启动 */
    private val appErrorsRecords = arrayListOf<AppErrorsInfoBean>()

    /** 注册 */
    private fun register() {
        onAppLifecycle {
            /** 解锁后清空已记录的忽略错误 APP */
            registerReceiver(Intent.ACTION_USER_PRESENT) { _, _ -> ignoredErrorsIfUnlockApps.clear() }
            /** 刷新模块 Resources 缓存 */
            registerReceiver(Intent.ACTION_LOCALE_CHANGED) { _, _ -> refreshModuleAppResources() }
        }
        FrameworkTool.Host.with(instance = this) {
            onOpenAppUsedFramework { appContext.openApp(it) }
            onPushAppErrorsInfoData { appErrorsRecords }
            onRemoveAppErrorsInfoData { appErrorsRecords.remove(it) }
            onClearAppErrorsInfoData { appErrorsRecords.clear() }
            onIgnoredErrorsIfUnlock { ignoredErrorsIfUnlockApps.add(it) }
            onIgnoredErrorsIfRestart { ignoredErrorsIfRestartApps.add(it) }
        }
    }

    override fun onHook() {
        /** 注册 */
        register()
        /** 干掉原生错误对话框 - 如果有 */
        ErrorDialogControllerClass.hook {
            injectMember {
                method {
                    name = "hasCrashDialogs"
                    emptyParam()
                }
                replaceToTrue()
            }
            injectMember {
                method {
                    name = "showCrashDialogs"
                    paramCount = 1
                }
                intercept()
            }
        }
        /** 注入自定义错误对话框 */
        AppErrorsClass.hook {
            injectMember {
                method {
                    name = "handleShowAppErrorUi"
                    param(MessageClass)
                }
                afterHook {
                    /** 当前实例 */
                    val context = field { name = "mContext" }.get(instance).cast<Context>() ?: return@afterHook

                    /** 错误数据 */
                    val errData = args().first().cast<Message>()?.obj

                    /** 错误结果 */
                    val errResult = AppErrorResultClass.clazz.method {
                        name = "get"
                        emptyParam()
                    }.get(AppErrorDialog_DataClass.clazz.field { name = "result" }.get(errData).any()).int()

                    /** 当前进程信息 */
                    val proc = AppErrorDialog_DataClass.clazz.field { name = "proc" }.get(errData).any()

                    /** 当前 APP 信息 */
                    val appInfo = ProcessRecordClass.clazz.field { name = "info" }.get(proc).cast<ApplicationInfo>()

                    /** 当前进程名称 */
                    val processName = ProcessRecordClass.clazz.field { name = "processName" }.get(proc).string()

                    /** 当前 APP、进程 包名 */
                    val packageName = appInfo?.packageName ?: processName

                    /** 当前 APP 名称 */
                    val appName = appInfo?.let { context.appName(it.packageName) } ?: packageName

                    /** 是否为 APP */
                    val isApp = (PackageListClass.clazz.method {
                        name = "size"
                        emptyParam()
                    }.get(if (ProcessRecordClass.clazz.hasMethod {
                            name = "getPkgList"
                            emptyParam()
                        }) ProcessRecordClass.clazz.method {
                        name = "getPkgList"
                        emptyParam()
                    }.get(proc).call() else ProcessRecordClass.clazz.field {
                        name = "pkgList"
                    }.get(proc).self).int() == 1 && appInfo != null)

                    /** 是否短时内重复错误 */
                    val isRepeating = AppErrorDialog_DataClass.clazz.field { name = "repeating" }.get(errData).boolean()
                    /** 打印错误日志 */
                    if (isApp) loggerE(
                        msg = "App \"$packageName\"${if (packageName != processName) " --process \"$processName\"" else ""}" +
                                " has crashed${if (isRepeating) " again" else ""}"
                    ) else loggerE(msg = "Process \"$processName\" has crashed${if (isRepeating) " again" else ""}")
                    /** 判断是否被忽略 - 在后台就不显示对话框 */
                    if (ignoredErrorsIfUnlockApps.contains(packageName) || ignoredErrorsIfRestartApps.contains(packageName) || errResult == -2)
                        return@afterHook
                    /** 启动错误对话框显示窗口 */
                    AppErrorsDisplayActivity.start(
                        context, AppErrorsDisplayBean(
                            packageName = packageName,
                            appName = appName,
                            title = if (isRepeating) LocaleString.aerrRepeatedTitle(appName) else LocaleString.aerrTitle(appName),
                            isApp = isApp,
                            isShowReopenButton = isRepeating.not() && context.isAppCanOpened(packageName)
                        )
                    )
                }
            }
            injectMember {
                method {
                    name = "crashApplication"
                    paramCount = 2
                }
                afterHook {
                    /** 当前 APP 信息 */
                    val appInfo = ProcessRecordClass.clazz.field { name = "info" }.get(args().first().any()).cast<ApplicationInfo>()
                    /** 添加当前异常信息到第一位 */
                    appErrorsRecords.add(0, AppErrorsInfoBean.clone(appInfo?.packageName, args().last().cast()))
                }
            }
        }
    }
}
