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
import android.content.pm.PackageManager
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
import com.fankes.apperrorstracking.data.DataConst
import com.fankes.apperrorstracking.hook.factory.isAppShowErrorsNotify
import com.fankes.apperrorstracking.hook.factory.isAppShowErrorsToast
import com.fankes.apperrorstracking.hook.factory.isAppShowNothing
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

    /** ?????????????????? APP ?????? - ?????????????????? */
    private var mutedErrorsIfUnlockApps = HashSet<String>()

    /** ?????????????????? APP ?????? - ?????????????????? */
    private var mutedErrorsIfRestartApps = HashSet<String>()

    /** ???????????? APP ?????????????????? - ?????????????????? */
    private val appErrorsRecords = ArrayList<AppErrorsInfoBean>()

    /** ?????? */
    private fun register() {
        onAppLifecycle {
            /** ??????????????????????????????????????? APP */
            registerReceiver(Intent.ACTION_USER_PRESENT) { _, _ -> mutedErrorsIfUnlockApps.clear() }
            /** ???????????? Resources ?????? */
            registerReceiver(Intent.ACTION_LOCALE_CHANGED) { _, _ -> refreshModuleAppResources() }
        }
        FrameworkTool.Host.with(instance = this) {
            onOpenAppUsedFramework { appContext.openApp(it) }
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
                arrayListOf<AppInfoBean>().apply {
                    appContext.packageManager.getInstalledPackages(PackageManager.GET_CONFIGURATIONS).also { info ->
                        (if (filters.name.isNotBlank())
                            info.filter { it.packageName.contains(filters.name) || appContext.appName(it.packageName).contains(filters.name) }
                        else info).let { result ->
                            if (filters.isContainsSystem.not()) result.filter { (it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
                            else result
                        }.sortedByDescending { it.lastUpdateTime }
                            .forEach { add(AppInfoBean(name = appContext.appName(it.packageName), packageName = it.packageName)) }
                        /** ?????????????????? */
                        removeIf { it.packageName == BuildConfig.APPLICATION_ID }
                    }
                }
            }
        }
    }

    override fun onHook() {
        /** ?????? */
        register()
        /** ??????????????????????????? - ????????? */
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
        /** ??????????????????????????? - API 30 ?????? */
        ActivityTaskManagerService_LocalServiceClass.hook {
            injectMember {
                method {
                    name = "canShowErrorDialogs"
                    emptyParam()
                }
                replaceToFalse()
            }
        }.by { Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q }
        /** ??????????????????????????? - ?????????????????????????????????????????????????????? */
        AppErrorDialogClass.hook {
            injectMember {
                method {
                    name = "onCreate"
                    param(BundleClass)
                }
                afterHook { instance<Dialog>().cancel() }
            }
        }
        /** ?????????????????????????????? */
        AppErrorsClass.hook {
            injectMember {
                method {
                    name = "handleShowAppErrorUi"
                    param(MessageClass)
                }
                afterHook {
                    /** ???????????? */
                    val context = field { name = "mContext" }.get(instance).cast<Context>() ?: return@afterHook

                    /** ???????????? */
                    val errData = args().first().cast<Message>()?.obj

                    /** ?????????????????? */
                    val proc = AppErrorDialog_DataClass.clazz.field { name = "proc" }.get(errData).any()

                    /** ?????? UserId ?????? */
                    val userId = ProcessRecordClass.clazz.field { name = "userId" }.get(proc).int()

                    /** ?????? APP ?????? */
                    val appInfo = ProcessRecordClass.clazz.field { name = "info" }.get(proc).cast<ApplicationInfo>()

                    /** ?????????????????? */
                    val processName = ProcessRecordClass.clazz.field { name = "processName" }.get(proc).string()

                    /** ?????? APP????????? ?????? */
                    val packageName = appInfo?.packageName ?: processName

                    /** ?????? APP ?????? */
                    val appName = appInfo?.let { context.appName(it.packageName) } ?: packageName

                    /** ????????? APP */
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

                    /** ?????????????????? */
                    val isMainProcess = packageName == processName

                    /** ????????????????????? */
                    val isBackgroundProcess = UserControllerClass.clazz.method { name = "getCurrentProfileIds" }
                        .get(ActivityManagerServiceClass.clazz.field { name = "mUserController" }
                            .get(field { name = "mService" }.get(instance).any()).any())
                        .invoke<IntArray>()?.takeIf { it.isNotEmpty() }?.any { it != userId } ?: false

                    /** ??????????????????????????? */
                    val isRepeating = AppErrorDialog_DataClass.clazz.field { name = "repeating" }.get(errData).boolean()

                    /** ???????????? */
                    val errorTitle = if (isRepeating) LocaleString.aerrRepeatedTitle(appName) else LocaleString.aerrTitle(appName)

                    /** ???????????????????????????????????? */
                    val isAlwaysShowsReopenApp = prefs.get(DataConst.ENABLE_ALWAYS_SHOWS_REOPEN_APP_OPTIONS)
                    /** ?????????????????? */
                    if (isApp) loggerE(
                        msg = "App \"$packageName\"${if (packageName != processName) " --process \"$processName\"" else ""}" +
                                " has crashed${if (isRepeating) " again" else ""}"
                    ) else loggerE(msg = "Process \"$processName\" has crashed${if (isRepeating) " again" else ""}")
                    /** ????????????????????????????????? */
                    if (packageName == BuildConfig.APPLICATION_ID) {
                        context.toast(msg = "AppErrorsTracking has crashed, please see the log in console")
                        return@afterHook
                    }
                    /** ??????????????????????????? APP */
                    if (mutedErrorsIfUnlockApps.contains(packageName) || mutedErrorsIfRestartApps.contains(packageName)) return@afterHook
                    /** ?????????????????????????????? */
                    if (prefs.get(DataConst.ENABLE_APP_CONFIG_TEMPLATE)) {
                        if (isAppShowNothing(packageName)) return@afterHook
                        if (isAppShowErrorsNotify(packageName)) {
                            context.pushNotify(
                                channelId = "APPS_ERRORS",
                                channelName = LocaleString.appName,
                                title = errorTitle,
                                content = LocaleString.appErrorsTip,
                                icon = IconCompat.createWithBitmap(R.mipmap.ic_notify.drawableOf(moduleAppResources).toBitmap()),
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
                    /** ??????????????????????????? */
                    if ((isBackgroundProcess || context.isAppCanOpened(packageName).not())
                        && prefs.get(DataConst.ENABLE_ONLY_SHOW_ERRORS_IN_FRONT)
                    ) return@afterHook
                    /** ???????????????????????? */
                    if (isMainProcess.not() && prefs.get(DataConst.ENABLE_ONLY_SHOW_ERRORS_IN_MAIN)) return@afterHook
                    /** ????????????????????????????????? */
                    AppErrorsDisplayActivity.start(
                        context, AppErrorsDisplayBean(
                            packageName = packageName,
                            processName = processName,
                            appName = appName,
                            title = errorTitle,
                            isShowAppInfoButton = isApp,
                            isShowReopenButton = isApp && (isRepeating.not() || isAlwaysShowsReopenApp)
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
                    /** ?????? APP ?????? */
                    val appInfo = ProcessRecordClass.clazz.field { name = "info" }.get(args().first().any()).cast<ApplicationInfo>()
                    /** ???????????????????????????????????? */
                    appErrorsRecords.add(0, AppErrorsInfoBean.clone(appInfo?.packageName, args().last().cast()))
                }
            }
        }
    }
}
