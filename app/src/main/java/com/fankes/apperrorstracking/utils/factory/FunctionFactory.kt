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
@file:Suppress("DEPRECATION", "PrivateApi", "unused", "ObsoleteSdkInt")

package com.fankes.apperrorstracking.utils.factory

import android.app.Activity
import android.app.Service
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.fankes.apperrorstracking.BuildConfig
import com.fankes.apperrorstracking.R
import com.fankes.apperrorstracking.locale.LocaleString
import com.google.android.material.snackbar.Snackbar
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.type.android.ApplicationInfoClass
import com.topjohnwu.superuser.Shell
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * 系统深色模式是否开启
 * @return [Boolean] 是否开启
 */
val Context.isSystemInDarkMode get() = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

/**
 * 系统深色模式是否没开启
 * @return [Boolean] 是否开启
 */
inline val Context.isNotSystemInDarkMode get() = !isSystemInDarkMode

/**
 * dp 转换为 pxInt
 * @param context 使用的实例
 * @return [Int]
 */
fun Number.dp(context: Context) = dpFloat(context).toInt()

/**
 * dp 转换为 pxFloat
 * @param context 使用的实例
 * @return [Float]
 */
fun Number.dpFloat(context: Context) = toFloat() * context.resources.displayMetrics.density

/**
 * 获取 APP 名称
 * @param packageName 包名
 * @return [String]
 */
fun Context.appName(packageName: String) =
    runCatching {
        packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)
            .applicationInfo.loadLabel(packageManager).toString()
    }.getOrNull() ?: packageName

/**
 * 获取 APP 完整版本
 * @param packageName 包名
 * @return [String]
 */
fun Context.appVersion(packageName: String) =
    runCatching {
        packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)?.let { "${it.versionName} (${it.versionCode})" }
    }.getOrNull() ?: "<unknown>"

/**
 * 获取 APP CPU ABI 名称
 * @param packageName 包名
 * @return [String]
 */
fun Context.appCpuAbi(packageName: String) =
    runCatching {
        ApplicationInfoClass.field { name = "primaryCpuAbi" }
            .get(packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)?.applicationInfo).string()
    }.getOrNull() ?: ""

/**
 * 获取 APP 图标
 * @param packageName 包名
 * @return [Drawable]
 */
fun Context.appIcon(packageName: String) =
    runCatching {
        packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)
            .applicationInfo.loadIcon(packageManager)
    }.getOrNull() ?: ResourcesCompat.getDrawable(resources, R.drawable.ic_android, null)

/**
 * 计算与当前时间戳相差的友好时间
 * @param now 刚刚
 * @param second 秒前
 * @param minute 分钟前
 * @param hour 小时前
 * @param day 天前
 * @param month 月前
 * @param year 年前
 * @return [String] 友好时间
 */
fun Long.difference(now: String, second: String, minute: String, hour: String, day: String, month: String, year: String) =
    ((System.currentTimeMillis() - this) / 1000).toInt().let { diff ->
        when (diff) {
            in 0..10 -> now
            in 11..20 -> "10 $second"
            in 21..30 -> "20 $second"
            in 31..40 -> "30 $second"
            in 41..50 -> "40 $second"
            in 51..59 -> "50 $second"
            in 60..3599 -> "${(diff / 60).coerceAtLeast(1)} $minute"
            in 3600..86399 -> "${diff / 3600} $hour"
            in 86400..2591999 -> "${diff / 86400} $day"
            in 2592000..31103999 -> "${diff / 2592000} $month"
            else -> "${diff / 31104000} $year"
        }
    }

/**
 * 保留小数
 * @param count 要保留的位数 - 默认 2 位 - 最多 7 位
 * @return [String] 得到的字符串数字 - 格式化失败返回原始数字的字符串
 */
fun Number.decimal(count: Int = 2) = runCatching {
    DecimalFormat(
        when (count) {
            0 -> "0"
            1 -> "0.0"
            2 -> "0.00"
            3 -> "0.000"
            4 -> "0.0000"
            5 -> "0.00000"
            6 -> "0.000000"
            7 -> "0.0000000"
            else -> "0.0"
        }
    ).apply { roundingMode = RoundingMode.HALF_UP }.format(this) ?: toString()
}.getOrNull() ?: this

/**
 * 弹出 [Toast]
 * @param msg 提示内容
 */
fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

/**
 * 弹出 [Snackbar]
 * @param msg 提示内容
 * @param actionText 按钮文本 - 不写默认取消按钮
 * @param callback 按钮事件回调
 */
fun Context.snake(msg: String, actionText: String = "", callback: () -> Unit = {}) =
    Snackbar.make((this as Activity).findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).apply {
        if (actionText.isBlank()) return@apply
        setActionTextColor(if (isSystemInDarkMode) Color.BLACK else Color.WHITE)
        setAction(actionText) { callback() }
    }.show()

/**
 * 跳转到指定页面
 *
 * [T] 为指定的 [Activity]
 * @param isOutSide 是否从外部启动
 * @param initiate [Intent] 方法体
 */
inline fun <reified T : Activity> Context.navigate(isOutSide: Boolean = false, initiate: Intent.() -> Unit = {}) = runCatching {
    startActivity((if (isOutSide) Intent() else Intent(if (this is Service) applicationContext else this, T::class.java)).apply {
        flags = if (this@navigate !is Activity) Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        else Intent.FLAG_ACTIVITY_NEW_TASK
        if (isOutSide) component = ComponentName(BuildConfig.APPLICATION_ID, T::class.java.name)
        initiate(this)
    })
}.onFailure { toast(msg = "Start ${T::class.java.name} failed") }

/**
 * 复制到剪贴板
 * @param content 要复制的文本
 */
fun Context.copyToClipboard(content: String) = runCatching {
    (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).apply {
        setPrimaryClip(ClipData.newPlainText(null, content))
        (primaryClip?.getItemAt(0)?.text ?: "").also {
            if (it != content) toast(LocaleString.copyFail) else toast(LocaleString.copied)
        }
    }
}

/**
 * 跳转 APP 自身设置界面
 * @param packageName 包名
 */
fun Context.openSelfSetting(packageName: String = this.packageName) = runCatching {
    startActivity(Intent().apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", packageName, null)
    })
}.onFailure { toast(msg = "Cannot open '$packageName'") }

/**
 * 启动系统浏览器
 * @param url 网址
 * @param packageName 指定包名 - 可不填
 */
fun Context.openBrowser(url: String, packageName: String = "") = runCatching {
    startActivity(Intent().apply {
        if (packageName.isNotBlank()) setPackage(packageName)
        action = Intent.ACTION_VIEW
        data = Uri.parse(url)
        /** 防止顶栈一样重叠在自己的 APP 中 */
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    })
}.onFailure {
    if (packageName.isNotBlank()) snake(msg = "Cannot start '$packageName'")
    else snake(msg = "Start system browser failed")
}

/**
 * 当前 APP 是否可被启动
 * @param packageName 包名
 */
fun Context.isAppCanOpened(packageName: String = this.packageName) =
    runCatching { packageManager?.getLaunchIntentForPackage(packageName) != null }.getOrNull() ?: false

/**
 * 启动指定 APP
 * @param packageName 包名
 */
fun Context.openApp(packageName: String = this.packageName) = runCatching {
    startActivity(packageManager.getLaunchIntentForPackage(packageName)?.apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    })
}.onFailure { toast(msg = "Cannot start '$packageName'") }

/**
 * 是否有 Root 权限
 * @return [Boolean]
 */
val isRootAccess get() = runCatching { Shell.rootAccess() }.getOrNull() ?: false

/**
 * 执行命令
 * @param cmd 命令
 * @param isSu 是否使用 Root 权限执行 - 默认：是
 * @return [String] 执行结果
 */
fun execShell(cmd: String, isSu: Boolean = true) = runCatching {
    (if (isSu) Shell.su(cmd) else Shell.sh(cmd)).exec().out.let {
        if (it.isNotEmpty()) it[0].trim() else ""
    }
}.getOrNull() ?: ""