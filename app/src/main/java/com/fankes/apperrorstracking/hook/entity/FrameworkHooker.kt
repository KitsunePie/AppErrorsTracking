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

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Message
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import com.fankes.apperrorstracking.BuildConfig
import com.fankes.apperrorstracking.R
import com.fankes.apperrorstracking.bean.AppErrorsDisplayBean
import com.fankes.apperrorstracking.bean.AppErrorsInfoBean
import com.fankes.apperrorstracking.bean.AppInfoBean
import com.fankes.apperrorstracking.bean.MutedErrorsAppBean
import com.fankes.apperrorstracking.data.ConfigData
import com.fankes.apperrorstracking.data.factory.isAppShowErrorsNotify
import com.fankes.apperrorstracking.data.factory.isAppShowErrorsToast
import com.fankes.apperrorstracking.data.factory.isAppShowNothing
import com.fankes.apperrorstracking.locale.LocaleString
import com.fankes.apperrorstracking.ui.activity.errors.AppErrorsDisplayActivity
import com.fankes.apperrorstracking.ui.activity.errors.AppErrorsRecordActivity
import com.fankes.apperrorstracking.utils.factory.*
import com.fankes.apperrorstracking.utils.tool.FrameworkTool
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.MessageClass

object FrameworkHooker : YukiBaseHooker() {

    private const val ActivityManagerServiceClass = "com.android.server.am.ActivityManagerService"
    private const val UserControllerClass = "com.android.server.am.UserController"
    private const val AppErrorsClass = "com.android.server.am.AppErrors"
    private const val AppErrorDialogClass = "com.android.server.am.AppErrorDialog"
    private const val AppErrorDialog_DataClass = "com.android.server.am.AppErrorDialog\$Data"
    private const val ProcessRecordClass = "com.android.server.am.ProcessRecord"
    private const val ActivityTaskManagerService_LocalServiceClass = "com.android.server.wm.ActivityTaskManagerService\$LocalService"

    private val PackageListClass = VariousClass(
        "com.android.server.am.ProcessRecord\$PackageList",
        "com.android.server.am.PackageList"
    )

    private val ErrorDialogControllerClass = VariousClass(
        "com.android.server.am.ProcessRecord\$ErrorDialogController",
        "com.android.server.am.ErrorDialogController"
    )

    /** 已忽略错误的 APP 数组 - 直到重新解锁 */
    private var mutedErrorsIfUnlockApps = HashSet<String>()

    /** 已忽略错误的 APP 数组 - 直到重新启动 */
    private var mutedErrorsIfRestartApps = HashSet<String>()

    /** 已记录的 APP 异常信息数组 - 直到重新启动 */
    private val appErrorsRecords = ArrayList<AppErrorsInfoBean>()

    /** 注册 */
    private fun register() {
        onAppLifecycle {
            /** 解锁后清空已记录的忽略错误 APP */
            registerReceiver(Intent.ACTION_USER_PRESENT) { _, _ -> mutedErrorsIfUnlockApps.clear() }
            /** 刷新模块 Resources 缓存 */
            registerReceiver(Intent.ACTION_LOCALE_CHANGED) { _, _ -> refreshModuleAppResources() }
        }
        FrameworkTool.Host.with(instance = this) {
            onOpenAppUsedFramework { appContext?.openApp(it) }
            onPushAppErrorsInfoData { appErrorsRecords }
            onRemoveAppErrorsInfoData { appErrorsRecords.remove(it) }
            onClearAppErrorsInfoData { appErrorsRecords.clear() }
            onMutedErrorsIfUnlock { mutedErrorsIfUnlockApps.add(it) }
            onMutedErrorsIfRestart { mutedErrorsIfRestartApps.add(it) }
            onPushMutedErrorsAppsData {
                arrayListOf<MutedErrorsAppBean>().apply {
                    mutedErrorsIfUnlockApps.takeIf { it.isNotEmpty() }
                        ?.forEach { add(MutedErrorsAppBean(MutedErrorsAppBean.MuteType.UNTIL_UNLOCKS, it)) }
                    mutedErrorsIfRestartApps.takeIf { it.isNotEmpty() }
                        ?.forEach { add(MutedErrorsAppBean(MutedErrorsAppBean.MuteType.UNTIL_REBOOTS, it)) }
                }
            }
            onUnmuteErrorsApp {
                when (it.type) {
                    MutedErrorsAppBean.MuteType.UNTIL_UNLOCKS -> mutedErrorsIfUnlockApps.remove(it.packageName)
                    MutedErrorsAppBean.MuteType.UNTIL_REBOOTS -> mutedErrorsIfRestartApps.remove(it.packageName)
                }
            }
            onUnmuteAllErrorsApps {
                mutedErrorsIfUnlockApps.clear()
                mutedErrorsIfRestartApps.clear()
            }
            onPushAppListData { filters ->
                appContext?.let { context ->
                    arrayListOf<AppInfoBean>().apply {
                        context.listOfPackages().also { info ->
                            (if (filters.name.isNotBlank())
                                info.filter { it.packageName.contains(filters.name) || context.appNameOf(it.packageName).contains(filters.name) }
                            else info).let { result ->
                                if (filters.isContainsSystem.not())
                                    result.filter { (it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
                                else result
                            }.sortedByDescending { it.lastUpdateTime }
                                .forEach { add(AppInfoBean(name = context.appNameOf(it.packageName), packageName = it.packageName)) }
                            /** 移除模块自身 */
                            removeIf { it.packageName == BuildConfig.APPLICATION_ID }
                        }
                    }
                } ?: arrayListOf()
            }
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
        }.ignoredHookClassNotFoundFailure()
        /** 干掉原生错误对话框 - API 30 以下 */
        ActivityTaskManagerService_LocalServiceClass.hook {
            injectMember {
                method {
                    name = "canShowErrorDialogs"
                    emptyParam()
                }
                replaceToFalse()
            }
        }.by { Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q }
        /** 干掉原生错误对话框 - 如果上述方法全部失效则直接结束对话框 */
        AppErrorDialogClass.hook {
            injectMember {
                method {
                    name = "onCreate"
                    param(BundleClass)
                }
                afterHook { instance<Dialog>().cancel() }
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

                    /** 当前进程信息 */
                    val proc = AppErrorDialog_DataClass.toClass().field { name = "proc" }.get(errData).any()

                    /** 当前 UserId 信息 */
                    val userId = ProcessRecordClass.toClass().field { name = "userId" }.get(proc).int()

                    /** 当前 APP 信息 */
                    val appInfo = ProcessRecordClass.toClass().field { name = "info" }.get(proc).cast<ApplicationInfo>()

                    /** 当前进程名称 */
                    val processName = ProcessRecordClass.toClass().field { name = "processName" }.get(proc).string()

                    /** 当前 APP、进程 包名 */
                    val packageName = appInfo?.packageName ?: processName

                    /** 当前 APP 名称 */
                    val appName = appInfo?.let { context.appNameOf(it.packageName) } ?: packageName

                    /** 是否为 APP */
                    val isApp = (PackageListClass.toClass().method {
                        name = "size"
                        emptyParam()
                    }.get(if (ProcessRecordClass.toClass().hasMethod {
                            name = "getPkgList"
                            emptyParam()
                        }) ProcessRecordClass.toClass().method {
                        name = "getPkgList"
                        emptyParam()
                    }.get(proc).call() else ProcessRecordClass.toClass().field {
                        name = "pkgList"
                    }.get(proc).any()).int() == 1 && appInfo != null)

                    /** 是否为主进程 */
                    val isMainProcess = packageName == processName

                    /** 是否为后台进程 */
                    val isBackgroundProcess = UserControllerClass.toClass().method { name = "getCurrentProfileIds" }
                        .get(ActivityManagerServiceClass.toClass().field { name = "mUserController" }
                            .get(field { name = "mService" }.get(instance).any()).any())
                        .invoke<IntArray>()?.takeIf { it.isNotEmpty() }?.any { it != userId } ?: false

                    /** 是否短时内重复错误 */
                    val isRepeating = AppErrorDialog_DataClass.toClass().field { name = "repeating" }.get(errData).boolean()

                    /** 崩溃标题 */
                    val errorTitle = if (isRepeating) LocaleString.aerrRepeatedTitle(appName) else LocaleString.aerrTitle(appName)
                    /** 打印错误日志 */
                    if (isApp) loggerE(
                        msg = "App \"$packageName\"${if (packageName != processName) " --process \"$processName\"" else ""}" +
                                " has crashed${if (isRepeating) " again" else ""}"
                    ) else loggerE(msg = "Process \"$processName\" has crashed${if (isRepeating) " again" else ""}")
                    /** 判断是否为模块自身崩溃 */
                    if (packageName == BuildConfig.APPLICATION_ID) {
                        context.toast(msg = "AppErrorsTracking has crashed, please see the log in console")
                        return@afterHook
                    }
                    /** 判断是否为已忽略的 APP */
                    if (mutedErrorsIfUnlockApps.contains(packageName) || mutedErrorsIfRestartApps.contains(packageName)) return@afterHook
                    /** 判断配置模块启用状态 */
                    if (ConfigData.isEnableAppConfigTemplate) {
                        if (isAppShowNothing(packageName)) return@afterHook
                        if (isAppShowErrorsNotify(packageName)) {
                            context.pushNotify(
                                channelId = "APPS_ERRORS",
                                channelName = LocaleString.appName,
                                title = errorTitle,
                                content = LocaleString.appErrorsTip,
                                icon = IconCompat.createWithBitmap(moduleAppResources.drawableOf(R.mipmap.ic_notify).toBitmap()),
                                color = 0xFFFF6200.toInt(),
                                intent = AppErrorsRecordActivity.intent()
                            )
                            return@afterHook
                        }
                        if (isAppShowErrorsToast(packageName)) {
                            context.toast(errorTitle)
                            return@afterHook
                        }
                    }
                    /** 判断是否为后台进程 */
                    if ((isBackgroundProcess || context.isAppCanOpened(packageName).not()) && ConfigData.isEnableOnlyShowErrorsInFront)
                        return@afterHook
                    /** 判断是否为主进程 */
                    if (isMainProcess.not() && ConfigData.isEnableOnlyShowErrorsInMain) return@afterHook
                    /** 启动错误对话框显示窗口 */
                    AppErrorsDisplayActivity.start(
                        context, AppErrorsDisplayBean(
                            packageName = packageName,
                            processName = processName,
                            appName = appName,
                            title = errorTitle,
                            isShowAppInfoButton = isApp,
                            isShowReopenButton = isApp && (isRepeating.not() || ConfigData.isEnableAlwaysShowsReopenAppOptions)
                                    && context.isAppCanOpened(packageName) && isMainProcess,
                            isShowCloseAppButton = isApp
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
                    val appInfo = ProcessRecordClass.toClass().field { name = "info" }.get(args().first().any()).cast<ApplicationInfo>()
                    /** 添加当前异常信息到第一位 */
                    appErrorsRecords.add(0, AppErrorsInfoBean.clone(appInfo?.packageName, args().last().cast()))
                }
            }
        }
    }
}