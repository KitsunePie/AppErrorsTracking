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
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.fankes.apperrorstracking.BuildConfig
import com.fankes.apperrorstracking.R
import com.fankes.apperrorstracking.locale.LocaleString
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.type.android.ApplicationInfoClass

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
 * 弹出 [Toast]
 * @param msg 提示内容
 */
fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

/**
 * 跳转到指定页面
 *
 * [T] 为指定的 [Activity]
 * @param isOutSide 是否从外部启动
 * @param callback 回调 [Intent] 方法体
 */
inline fun <reified T : Activity> Context.navigate(isOutSide: Boolean = false, callback: Intent.() -> Unit = {}) = runCatching {
    startActivity((if (isOutSide) Intent() else Intent(if (this is Service) applicationContext else this, T::class.java)).apply {
        flags = if (this@navigate !is Activity) Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        else Intent.FLAG_ACTIVITY_NEW_TASK
        if (isOutSide) component = ComponentName(BuildConfig.APPLICATION_ID, T::class.java.name)
        callback(this)
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