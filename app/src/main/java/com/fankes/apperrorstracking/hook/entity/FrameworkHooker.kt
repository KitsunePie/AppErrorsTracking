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
@file:Suppress("DEPRECATION", "UseCompatLoadingForDrawables")

package com.fankes.apperrorstracking.hook.entity

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.graphics.Color
import android.os.Message
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.fankes.apperrorstracking.R
import com.fankes.apperrorstracking.utils.drawable.drawabletoolbox.DrawableBuilder
import com.fankes.apperrorstracking.utils.factory.*
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.type.android.MessageClass

object FrameworkHooker : YukiBaseHooker() {

    private const val AppErrorsClass = "com.android.server.am.AppErrors"

    private const val AppErrorResultClass = "com.android.server.am.AppErrorResult"

    private const val AppErrorDialog_DataClass = "com.android.server.am.AppErrorDialog\$Data"

    private const val ProcessRecordClass = "com.android.server.am.ProcessRecord"

    private const val PackageListClass = "com.android.server.am.PackageList"

    private val ErrorDialogControllerClass = VariousClass(
        "com.android.server.am.ProcessRecord\$ErrorDialogController",
        "com.android.server.am.ErrorDialogController"
    )

    /** 已打开的错误对话框数组 */
    private var openedErrorsDialogs = HashMap<String, AlertDialog>()

    /** 已忽略错误的 APP 数组 - 直到重新解锁 */
    private var ignoredErrorsIfUnlockApps = HashSet<String>()

    /** 已忽略错误的 APP 数组 - 直到重新启动 */
    private var ignoredErrorsIfRestartApps = HashSet<String>()

    /** 是否已经注册广播 */
    private var isRegisterReceiver = false

    /** 用户解锁屏幕广播接收器 */
    private val userPresentReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                /** 解锁后清空已记录的忽略错误 APP */
                ignoredErrorsIfUnlockApps.clear()
            }
        }
    }

    /** 语言区域改变广播接收器 */
    private val localeChangedReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                /** 刷新模块 Resources 缓存 */
                refreshModuleAppResources()
            }
        }
    }

    /**
     * 注册广播接收器
     * @param context 实例
     */
    private fun registerReceiver(context: Context) {
        if (isRegisterReceiver) return
        context.registerReceiver(userPresentReceiver, IntentFilter().apply { addAction(Intent.ACTION_USER_PRESENT) })
        context.registerReceiver(localeChangedReceiver, IntentFilter().apply { addAction(Intent.ACTION_LOCALE_CHANGED) })
        isRegisterReceiver = true
    }

    /**
     * 获取 I18n 字符串
     * @param resId 模块资源 Id
     * @param objArrs 格式化数组
     * @return [String]
     */
    private fun string(resId: Int, vararg objArrs: Any) = moduleAppResources.getString(resId, *objArrs)

    /**
     * 创建对话框按钮
     * @param context 实例
     * @param drawableId 按钮图标
     * @param content 按钮文本
     * @param it 点击事件回调
     * @return [LinearLayout]
     */
    private fun createButtonItem(context: Context, drawableId: Int, content: String, it: () -> Unit) =
        LinearLayout(context).apply {
            background = DrawableBuilder().rounded().cornerRadius(15.dp(context)).ripple().rippleColor(0xFFAAAAAA.toInt()).build()
            gravity = Gravity.CENTER or Gravity.START
            layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            addView(ImageView(context).apply {
                setImageDrawable(moduleAppResources.getDrawable(drawableId))
                layoutParams = ViewGroup.LayoutParams(25.dp(context), 25.dp(context))
                setColorFilter(if (context.isSystemInDarkMode) Color.WHITE else Color.BLACK)
            })
            addView(View(context).apply { layoutParams = ViewGroup.LayoutParams(15.dp(context), 0) })
            addView(TextView(context).apply {
                text = content
                textSize = 16f
                ellipsize = TextUtils.TruncateAt.END
                setSingleLine()
                setTextColor(if (context.isSystemInDarkMode) 0xFFDDDDDD.toInt() else 0xFF777777.toInt())
            })
            setPadding(19.dp(context), 16.dp(context), 19.dp(context), 16.dp(context))
            setOnClickListener { it() }
        }

    override fun onHook() {
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
                    val appName = appInfo?.let { context.packageManager.getApplicationLabel(it) } ?: packageName

                    /** 是否为 APP */
                    val isApp = (PackageListClass.clazz.method {
                        name = "size"
                        emptyParam()
                    }.get(ProcessRecordClass.clazz.method {
                        name = "getPkgList"
                        emptyParam()
                    }.get(proc).call()).int() == 1) && appInfo != null

                    /** 是否短时内重复错误 */
                    val isRepeating = AppErrorDialog_DataClass.clazz.field { name = "repeating" }.get(errData).boolean()
                    /** 注册广播 */
                    registerReceiver(context)
                    /** 打印错误日志 */
                    loggerE(msg = "Process \"$packageName\" has crashed${if (isRepeating) " again" else ""}")
                    /** 判断是否被忽略 - 在后台就不显示对话框 */
                    if (ignoredErrorsIfUnlockApps.contains(packageName) || ignoredErrorsIfRestartApps.contains(packageName) || errResult == -2)
                        return@afterHook
                    /** 关闭重复的对话框 */
                    openedErrorsDialogs[packageName]?.cancel()
                    /** 创建自定义对话框 */
                    AlertDialog.Builder(
                        context, if (context.isSystemInDarkMode)
                            android.R.style.Theme_Material_Dialog
                        else android.R.style.Theme_Material_Light_Dialog
                    ).create().apply {
                        setTitle(string(if (isRepeating) R.string.aerr_repeated_title else R.string.aerr_title, appName))
                        setView(LinearLayout(context).apply {
                            orientation = LinearLayout.VERTICAL
                            /** 应用信息按钮 */
                            val appInfoButton =
                                createButtonItem(context, R.drawable.ic_baseline_info, string(R.string.app_info)) {
                                    cancel()
                                    context.openSelfSetting(packageName)
                                }

                            /** 关闭应用按钮 */
                            val closeAppButton =
                                createButtonItem(context, R.drawable.ic_baseline_close, string(R.string.close_app)) { cancel() }

                            /** 重新打开按钮 */
                            val reOpenButton =
                                createButtonItem(context, R.drawable.ic_baseline_refresh, string(R.string.reopen_app)) {
                                    cancel()
                                    context.openApp(packageName)
                                }

                            /** 错误详情按钮 */
                            val errorDetailButton =
                                createButtonItem(context, R.drawable.ic_baseline_bug_report, string(R.string.error_detail)) {
                                    // TODO 待开发
                                }

                            /** 忽略按钮 - 直到解锁 */
                            val ignoredUntilUnlockButton =
                                createButtonItem(context, R.drawable.ic_baseline_eject, string(R.string.ignore_if_unlock)) {
                                    cancel()
                                    ignoredErrorsIfUnlockApps.add(packageName)
                                    context.toast(string(R.string.ignore_if_unlock_tip, appName))
                                }

                            /** 忽略按钮 - 直到重启 */
                            val ignoredUntilRestartButton =
                                createButtonItem(context, R.drawable.ic_baseline_eject, string(R.string.ignore_if_restart)) {
                                    cancel()
                                    ignoredErrorsIfRestartApps.add(packageName)
                                    context.toast(string(R.string.ignore_if_restart_tip, appName))
                                }
                            /** 判断进程是否为 APP */
                            if (isApp) {
                                addView(appInfoButton)
                                addView(if (isRepeating.not() && context.isAppCanOpened(packageName)) reOpenButton else closeAppButton)
                            } else addView(closeAppButton)
                            /** 始终添加错误详情按钮 */
                            addView(errorDetailButton)
                            /** 始终添加忽略按钮 */
                            addView(ignoredUntilUnlockButton)
                            addView(ignoredUntilRestartButton)
                            /** 设置边距 */
                            setPadding(6.dp(context), 15.dp(context), 6.dp(context), 6.dp(context))
                        })
                        /** 只有 SystemUid 才能响应系统级别的对话框 */
                        window?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
                        /** 记录实例 */
                        openedErrorsDialogs[packageName] = this
                        /** 设置取消对话框监听 */
                        setOnCancelListener { openedErrorsDialogs.remove(packageName) }
                    }.show()
                }
            }
        }
    }
}
