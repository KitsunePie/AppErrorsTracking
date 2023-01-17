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
package com.fankes.apperrorstracking.hook.entity

import android.app.ApplicationErrorReport
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Message
import android.util.ArrayMap
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import com.fankes.apperrorstracking.BuildConfig
import com.fankes.apperrorstracking.R
import com.fankes.apperrorstracking.bean.AppErrorsDisplayBean
import com.fankes.apperrorstracking.bean.AppErrorsInfoBean
import com.fankes.apperrorstracking.bean.AppInfoBean
import com.fankes.apperrorstracking.bean.MutedErrorsAppBean
import com.fankes.apperrorstracking.data.AppErrorsRecordData
import com.fankes.apperrorstracking.data.ConfigData
import com.fankes.apperrorstracking.data.factory.isAppShowErrorsDialog
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
import com.highcapable.yukihookapi.hook.log.loggerD
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.log.loggerI
import com.highcapable.yukihookapi.hook.log.loggerW
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.MessageClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType

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

    /**
     * APP 进程异常数据定义类
     * @param errors [AppErrorsClass] 实例
     * @param proc [ProcessRecordClass] 实例
     * @param resultData [AppErrorDialog_DataClass] 实例 - 默认空
     */
    private class AppErrorsProcessData(errors: Any?, proc: Any?, resultData: Any? = null) {

        /**
         * 获取当前包列表实例
         * @return [Any] or null
         */
        private val pkgList = if (ProcessRecordClass.toClass().hasMethod { name = "getPkgList"; emptyParam() })
            ProcessRecordClass.toClass().method { name = "getPkgList"; emptyParam() }.get(proc).call()
        else ProcessRecordClass.toClass().field { name = "pkgList" }.get(proc).any()

        /**
         * 获取当前包列表数组大小
         * @return [Int]
         */
        private val pkgListSize = PackageListClass.toClassOrNull()?.method { name = "size"; emptyParam() }?.get(pkgList)?.int()
            ?: ProcessRecordClass.toClass().field { name = "pkgList" }.get(proc).cast<ArrayMap<*, *>>()?.size ?: -1

        /**
         * 获取当前 pid 信息
         * @return [Int]
         */
        val pid = ProcessRecordClass.toClass().field { name { it == "mPid" || it == "pid" } }.get(proc).int()

        /**
         * 获取当前用户 ID 信息
         * @return [Int]
         */
        val userId = ProcessRecordClass.toClass().field { name = "userId" }.get(proc).int()

        /**
         * 获取当前 APP 信息
         * @return [ApplicationInfo] or null
         */
        val appInfo = ProcessRecordClass.toClass().field { name = "info" }.get(proc).cast<ApplicationInfo>()

        /**
         * 获取当前进程名称
         * @return [String]
         */
        val processName = ProcessRecordClass.toClass().field { name = "processName" }.get(proc).string()

        /**
         * 获取当前 APP、进程 包名
         * @return [String]
         */
        val packageName = appInfo?.packageName ?: processName

        /**
         * 获取当前进程是否为可被启动的 APP - 非框架 APP
         * @return [Boolean]
         */
        val isActualApp = pkgListSize == 1 && appInfo != null

        /**
         * 获取当前进程是否为主进程
         * @return [Boolean]
         */
        val isMainProcess = packageName == processName

        /**
         * 获取当前进程是否为后台进程
         * @return [Boolean]
         */
        val isBackgroundProcess = UserControllerClass.toClass()
            .method { name { it == "getCurrentProfileIds" || it == "getCurrentProfileIdsLocked" } }
            .get(ActivityManagerServiceClass.toClass().field { name = "mUserController" }
                .get(AppErrorsClass.toClass().field { name = "mService" }.get(errors).any()).any())
            .invoke<IntArray>()?.takeIf { it.isNotEmpty() }?.any { it != userId } ?: false

        /**
         * 获取当前进程是否短时内重复崩溃
         * @return [Boolean]
         */
        val isRepeatingCrash = resultData?.let { AppErrorDialog_DataClass.toClass().field { name = "repeating" }.get(it).boolean() } ?: false
    }

    /** 注册生命周期 */
    private fun registerLifecycle() {
        onAppLifecycle {
            /** 解锁后清空已记录的忽略错误 APP */
            registerReceiver(Intent.ACTION_USER_PRESENT) { _, _ -> mutedErrorsIfUnlockApps.clear() }
            /** 刷新模块 Resources 缓存 */
            registerReceiver(Intent.ACTION_LOCALE_CHANGED) { _, _ -> refreshModuleAppResources() }
            /** 启动时从本地获取异常记录数据 */
            onCreate { AppErrorsRecordData.init(context = this) }
        }
        FrameworkTool.Host.with(instance = this) {
            onOpenAppUsedFramework {
                appContext?.openApp(it.first, it.second)
                loggerI(msg = "Opened \"${it.first}\"${it.second.takeIf { e -> e > 0 }?.let { e -> " --user $e" } ?: ""}")
            }
            onPushAppErrorInfoData {
                AppErrorsRecordData.allData.firstOrNull { e -> e.pid == it } ?: run {
                    loggerW(msg = "Cannot received crash application data --pid $it")
                    AppErrorsInfoBean.createEmpty()
                }
            }
            onPushAppErrorsInfoData { AppErrorsRecordData.allData.toArrayList() }
            onRemoveAppErrorsInfoData {
                loggerI(msg = "Removed app errors info data for package \"${it.packageName}\"")
                AppErrorsRecordData.remove(it)
            }
            onClearAppErrorsInfoData {
                loggerI(msg = "Cleared all app errors info data, size ${AppErrorsRecordData.allData.size}")
                AppErrorsRecordData.clearAll()
            }
            onMutedErrorsIfUnlock {
                mutedErrorsIfUnlockApps.add(it)
                loggerI(msg = "Muted \"$it\" until unlocks")
            }
            onMutedErrorsIfRestart {
                mutedErrorsIfRestartApps.add(it)
                loggerI(msg = "Muted \"$it\" until restarts")
            }
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
                    MutedErrorsAppBean.MuteType.UNTIL_UNLOCKS -> {
                        loggerI(msg = "Unmuted if unlocks errors app \"${it.packageName}\"")
                        mutedErrorsIfUnlockApps.remove(it.packageName)
                    }
                    MutedErrorsAppBean.MuteType.UNTIL_REBOOTS -> {
                        loggerI(msg = "Unmuted if restarts errors app \"${it.packageName}\"")
                        mutedErrorsIfRestartApps.remove(it.packageName)
                    }
                }
            }
            onUnmuteAllErrorsApps {
                loggerI(msg = "Unmute all errors apps --unlocks ${mutedErrorsIfUnlockApps.size} --restarts ${mutedErrorsIfRestartApps.size}")
                mutedErrorsIfUnlockApps.clear()
                mutedErrorsIfRestartApps.clear()
            }
            onPushAppListData { filters ->
                appContext?.let { context ->
                    context.listOfPackages()
                        .filter { it.packageName.let { e -> e != "android" && e != BuildConfig.APPLICATION_ID } }
                        .let { info ->
                            arrayListOf<AppInfoBean>().apply {
                                (if (filters.name.isNotBlank()) info.filter {
                                    it.packageName.contains(filters.name) || context.appNameOf(it.packageName).contains(filters.name)
                                } else info).let { result ->
                                    if (filters.isContainsSystem.not())
                                        result.filter { (it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
                                    else result
                                }.sortedByDescending { it.lastUpdateTime }
                                    .forEach { add(AppInfoBean(name = context.appNameOf(it.packageName), packageName = it.packageName)) }
                            }.apply { loggerD(msg = "Fetched installed packages list, size $size") }
                        }
                } ?: arrayListOf()
            }
        }
    }

    /**
     * 处理 APP 进程异常信息展示
     * @param context 当前实例
     */
    private fun AppErrorsProcessData.handleShowAppErrorUi(context: Context) {
        /** 当前 APP 名称 */
        val appName = appInfo?.let { context.appNameOf(it.packageName) } ?: packageName

        /** 当前 APP 名称 (包含用户 ID) */
        val appNameWithUserId = if (userId != 0) "$appName (${LocaleString.userId(userId)})" else appName

        /** 崩溃标题 */
        val errorTitle = if (isRepeatingCrash) LocaleString.aerrRepeatedTitle(appNameWithUserId) else LocaleString.aerrTitle(appNameWithUserId)

        /** 使用通知推送异常信息 */
        fun showAppErrorsWithNotify() =
            context.pushNotify(
                channelId = "APPS_ERRORS",
                channelName = LocaleString.appName,
                title = errorTitle,
                content = LocaleString.appErrorsTip,
                icon = IconCompat.createWithBitmap(moduleAppResources.drawableOf(R.drawable.ic_notify).toBitmap()),
                color = 0xFFFF6200.toInt(),
                intent = AppErrorsRecordActivity.intent()
            )

        /** 使用 Toast 展示异常信息 */
        fun showAppErrorsWithToast() = context.toast(errorTitle)

        /** 使用对话框展示异常信息 */
        fun showAppErrorsWithDialog() =
            AppErrorsDisplayActivity.start(
                context, AppErrorsDisplayBean(
                    pid = pid,
                    userId = userId,
                    packageName = packageName,
                    processName = processName,
                    appName = appName,
                    title = errorTitle,
                    isShowAppInfoButton = isActualApp,
                    isShowReopenButton = isActualApp &&
                            (isRepeatingCrash.not() || ConfigData.isEnableAlwaysShowsReopenAppOptions) &&
                            context.isAppCanOpened(packageName) &&
                            isMainProcess,
                    isShowCloseAppButton = isActualApp
                )
            )
        /** 判断是否为已忽略的 APP */
        if (mutedErrorsIfUnlockApps.contains(packageName) || mutedErrorsIfRestartApps.contains(packageName)) return
        /** 判断是否为后台进程 */
        if ((isBackgroundProcess || context.isAppCanOpened(packageName).not()) && ConfigData.isEnableOnlyShowErrorsInFront) return
        /** 判断是否为主进程 */
        if (isMainProcess.not() && ConfigData.isEnableOnlyShowErrorsInMain) return
        when {
            packageName == BuildConfig.APPLICATION_ID -> {
                context.toast(msg = "AppErrorsTracking has crashed, please see the log in console")
                loggerE(msg = "AppErrorsTracking has crashed itself, please see the Android Runtime Exception in console")
            }
            ConfigData.isEnableAppConfigTemplate -> when {
                isAppShowNothing(packageName) -> {}
                isAppShowErrorsNotify(packageName) -> showAppErrorsWithNotify()
                isAppShowErrorsToast(packageName) -> showAppErrorsWithToast()
                isAppShowErrorsDialog(packageName) -> showAppErrorsWithDialog()
            }
            else -> showAppErrorsWithDialog()
        }
        /** 打印错误日志 */
        if (isActualApp) loggerE(
            msg = "Application \"$packageName\" ${if (isRepeatingCrash) "keeps stopping" else "has stopped"}" +
                    (if (packageName != processName) " --process \"$processName\"" else "") +
                    "${if (userId != 0) " --user $userId" else ""} --pid $pid"
        ) else loggerE(msg = "Process \"$processName\" ${if (isRepeatingCrash) "keeps stopping" else "has stopped"} --pid $pid")
    }

    /**
     * 处理 APP 进程异常数据
     * @param info 系统错误报告数据实例
     */
    private fun AppErrorsProcessData.handleAppErrorsInfo(info: ApplicationErrorReport.CrashInfo?) {
        AppErrorsRecordData.add(AppErrorsInfoBean.clone(pid, userId, appInfo?.packageName, info))
        loggerI(msg = "Received crash application data${if (userId != 0) " --user $userId" else ""} --pid $pid")
    }

    override fun onHook() {
        /** 注册生命周期 */
        registerLifecycle()
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
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            ActivityTaskManagerService_LocalServiceClass.hook {
                injectMember {
                    method {
                        name = "canShowErrorDialogs"
                        emptyParam()
                    }
                    replaceToFalse()
                }.ignoredNoSuchMemberFailure()
            }.ignoredHookClassNotFoundFailure()
            ActivityManagerServiceClass.hook {
                injectMember {
                    method {
                        name = "canShowErrorDialogs"
                        emptyParam()
                    }
                    replaceToFalse()
                }.ignoredNoSuchMemberFailure()
            }.ignoredHookClassNotFoundFailure()
        }
        /** 干掉原生错误对话框 - 如果上述方法全部失效则直接结束对话框 */
        AppErrorDialogClass.hook {
            injectMember {
                method {
                    name = "onCreate"
                    param(BundleClass)
                }
                afterHook { instance<Dialog>().cancel() }
            }.ignoredNoSuchMemberFailure()
            injectMember {
                method {
                    name = "onStart"
                    emptyParam()
                }
                afterHook { instance<Dialog>().cancel() }
            }.ignoredNoSuchMemberFailure()
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
                    val context = appContext ?: field { name = "mContext" }.get(instance).cast<Context>() ?: return@afterHook

                    /** 当前错误数据 */
                    val resultData = args().first().cast<Message>()?.obj

                    /** 当前进程信息 */
                    val proc = AppErrorDialog_DataClass.toClass().field { name = "proc" }.get(resultData).any()
                    /** 创建 APP 进程异常数据类 */
                    AppErrorsProcessData(instance, proc, resultData).handleShowAppErrorUi(context)
                }
            }
            injectMember {
                method {
                    name = "handleAppCrashInActivityController"
                    returnType = BooleanType
                }
                afterHook {
                    /** 当前进程信息 */
                    val proc = args().first().any() ?: return@afterHook loggerW(msg = "Received but got null ProcessRecord")
                    /** 创建 APP 进程异常数据类 */
                    AppErrorsProcessData(instance, proc).handleAppErrorsInfo(args(index = 1).cast())
                }
            }
        }
    }
}