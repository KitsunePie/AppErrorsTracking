/*
 * AppErrorsTracking - Added more features to app's crash dialog, fixed custom rom deleted dialog, the best experience to Android developer.
 * Copyright (C) 2017 Fankes Studio(qzmmcn@163.com)
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
 * This file is created by fankes on 2022/5/10.
 */
package com.fankes.apperrorstracking.bean

import android.app.ApplicationErrorReport
import android.content.Context
import android.os.Build
import com.fankes.apperrorstracking.const.ModuleVersion
import com.fankes.apperrorstracking.locale.locale
import com.fankes.apperrorstracking.utils.factory.appCpuAbiOf
import com.fankes.apperrorstracking.utils.factory.appMinSdkOf
import com.fankes.apperrorstracking.utils.factory.appTargetSdkOf
import com.fankes.apperrorstracking.utils.factory.appVersionCodeOf
import com.fankes.apperrorstracking.utils.factory.appVersionNameOf
import com.fankes.apperrorstracking.utils.factory.difference
import com.fankes.apperrorstracking.utils.factory.toUtcTime
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 应用异常信息 bean
 * @param pid 进程 ID
 * @param userId 用户 ID
 * @param cpuAbi CPU 架构类型
 * @param packageName 包名
 * @param versionName 版本名称
 * @param versionCode 版本号
 * @param targetSdk 目标 SDK 版本
 * @param minSdk 最低 SDK 版本
 * @param isNativeCrash 是否为原生层异常
 * @param isAnr 是否为 ANR (Application Not Responding)
 * @param exceptionClassName 异常类名
 * @param exceptionMessage 异常信息
 * @param throwClassName 抛出异常的类名
 * @param throwFileName 抛出异常的文件名
 * @param throwMethodName 抛出异常的方法名
 * @param throwLineNumber 抛出异常的行号
 * @param stackTrace 异常堆栈
 * @param timestamp 记录时间戳
 */
data class AppErrorsInfoBean(
    @SerializedName("pid")
    var pid: Int = -1,
    @SerializedName("userId")
    var userId: Int = -1,
    @SerializedName("cpuAbi")
    var cpuAbi: String = "",
    @SerializedName("packageName")
    var packageName: String = "",
    @SerializedName("versionName")
    var versionName: String = "",
    @SerializedName("versionCode")
    var versionCode: Long = -1L,
    @SerializedName("targetSdk")
    var targetSdk: Int = -1,
    @SerializedName("minSdk")
    var minSdk: Int = -1,
    @SerializedName("isNativeCrash")
    var isNativeCrash: Boolean = false,
    @SerializedName("isAnr")
    var isAnr: Boolean = false,
    @SerializedName("exceptionClassName")
    var exceptionClassName: String = "",
    @SerializedName("exceptionMessage")
    var exceptionMessage: String = "",
    @SerializedName("throwFileName")
    var throwFileName: String = "",
    @SerializedName("throwClassName")
    var throwClassName: String = "",
    @SerializedName("throwMethodName")
    var throwMethodName: String = "",
    @SerializedName("throwLineNumber")
    var throwLineNumber: Int = -1,
    @SerializedName("stackTrace")
    var stackTrace: String = "",
    @SerializedName("timestamp")
    var timestamp: Long = -1L
) : Serializable {

    companion object {

        /**
         * 从 [ApplicationErrorReport.CrashInfo] 克隆
         * @param context 当前实例
         * @param pid APP 进程 ID
         * @param userId APP 用户 ID
         * @param packageName APP 包名
         * @param crashInfo [ApplicationErrorReport.CrashInfo]
         * @return [AppErrorsInfoBean]
         */
        fun clone(context: Context, pid: Int, userId: Int, packageName: String?, crashInfo: ApplicationErrorReport.CrashInfo?) =
            (crashInfo?.exceptionClassName?.lowercase() == "native crash").let { isNativeCrash ->
                AppErrorsInfoBean(
                    pid = pid,
                    userId = userId,
                    cpuAbi = packageName?.let { context.appCpuAbiOf(it) } ?: "",
                    packageName = packageName ?: "unknown",
                    versionName = packageName?.let { context.appVersionNameOf(it).ifBlank { "unknown" } } ?: "",
                    versionCode = packageName?.let { context.appVersionCodeOf(it) } ?: -1L,
                    targetSdk = packageName?.let { context.appTargetSdkOf(it) } ?: -1,
                    minSdk = packageName?.let { context.appMinSdkOf(it) } ?: -1,
                    isNativeCrash = isNativeCrash,
                    exceptionClassName = crashInfo?.exceptionClassName ?: "unknown",
                    exceptionMessage = if (isNativeCrash) crashInfo?.stackTrace.let {
                        if (it?.contains("Abort message: '") == true)
                            runCatching { it.split("Abort message: '")[1].split("'")[0] }.getOrNull()
                                ?: crashInfo?.exceptionMessage ?: "unknown"
                        else crashInfo?.exceptionMessage ?: "unknown"
                    } else crashInfo?.exceptionMessage ?: "unknown",
                    throwFileName = crashInfo?.throwFileName ?: "unknown",
                    throwClassName = crashInfo?.throwClassName ?: "unknown",
                    throwMethodName = crashInfo?.throwMethodName ?: "unknown",
                    throwLineNumber = crashInfo?.throwLineNumber ?: -1,
                    stackTrace = crashInfo?.stackTrace?.trim() ?: "unknown",
                    timestamp = System.currentTimeMillis()
                )
            }

        /**
         * 从 [ApplicationErrorReport.AnrInfo] 克隆
         * @param context 当前实例
         * @param pid APP 进程 ID
         * @param userId APP 用户 ID
         * @param packageName APP 包名
         * @param anrInfo [ApplicationErrorReport.AnrInfo]
         * @return [AppErrorsInfoBean]
         */
        fun cloneAnr(context: Context, pid: Int, userId: Int, packageName: String?, anrInfo: ApplicationErrorReport.AnrInfo?) =
            AppErrorsInfoBean(
                pid = pid,
                userId = userId,
                cpuAbi = packageName?.let { context.appCpuAbiOf(it) } ?: "",
                packageName = packageName ?: "unknown",
                versionName = packageName?.let { context.appVersionNameOf(it).ifBlank { "unknown" } } ?: "",
                versionCode = packageName?.let { context.appVersionCodeOf(it) } ?: -1L,
                targetSdk = packageName?.let { context.appTargetSdkOf(it) } ?: -1,
                minSdk = packageName?.let { context.appMinSdkOf(it) } ?: -1,
                isNativeCrash = false,
                isAnr = true,
                exceptionClassName = "ANR",
                exceptionMessage = anrInfo?.cause ?: "Application Not Responding",
                throwFileName = anrInfo?.activity?.toString() ?: "unknown",
                throwClassName = packageName ?: "unknown",
                throwMethodName = "unknown",
                throwLineNumber = -1,
                stackTrace = anrInfo?.info?.trim() ?: "unknown",
                timestamp = System.currentTimeMillis()
            )
    }

    /**
     * 获取当前内容是否为空
     * @return [Boolean]
     */
    val isEmpty get() = pid == -1 && userId == -1 && timestamp == -1L

    /**
     * 获取生成的 Json 文件名
     * @return [String]
     */
    val jsonFileName get() = "${packageName}_${pid}_$timestamp.json"

    /**
     * 获取 APP 版本信息与版本号
     * @return [String]
     */
    val versionBrand get() = if (versionName.isBlank()) "unknown" else "$versionName($versionCode)"

    /**
     * 获取异常本地化 UTC 时间
     * @return [String]
     */
    val utcTime get() = timestamp.toUtcTime()

    /**
     * 获取异常本地化经过时间
     * @return [String]
     */
    val crossTime
        get() = timestamp.difference(
            now = locale.momentAgo,
            second = locale.secondAgo,
            minute = locale.minuteAgo,
            hour = locale.hourAgo,
            day = locale.dayAgo,
            month = locale.monthAgo,
            year = locale.yearAgo
        )

    /**
     * 获取异常本地化时间
     * @return [String]
     */
    val dateTime get() = SimpleDateFormat.getDateTimeInstance().format(Date(timestamp)) ?: utcTime

    /**
     * 获取异常堆栈分享模板
     * @param sDeviceBrand
     * @param sDeviceModel
     * @param sDisplay
     * @param sPackageName
     * @return [String]
     */
    fun stackOutputShareContent(
        sDeviceBrand: Boolean = true,
        sDeviceModel: Boolean = true,
        sDisplay: Boolean = true,
        sPackageName: Boolean = true
    ) = """
          Generated by AppErrorsTracking $ModuleVersion
          Project URL: https://github.com/KitsunePie/AppErrorsTracking
          ===============
    """.trimIndent() + "\n${environmentInfo(sDeviceBrand, sDeviceModel, sDisplay, sPackageName)}"

    /**
     * 获取异常堆栈文件模板
     * @param sDeviceBrand
     * @param sDeviceModel
     * @param sDisplay
     * @param sPackageName
     * @return [String]
     */
    fun stackOutputFileContent(
        sDeviceBrand: Boolean = true,
        sDeviceModel: Boolean = true,
        sDisplay: Boolean = true,
        sPackageName: Boolean = true
    ) = """
          ================================================================
              Generated by AppErrorsTracking $ModuleVersion
              Project URL: https://github.com/KitsunePie/AppErrorsTracking
          ================================================================
    """.trimIndent() + "\n${environmentInfo(sDeviceBrand, sDeviceModel, sDisplay, sPackageName)}"

    /**
     * 获取运行环境信息
     * @param sDeviceBrand
     * @param sDeviceModel
     * @param sDisplay
     * @param sPackageName
     * @return [String]
     */
    private fun environmentInfo(
        sDeviceBrand: Boolean,
        sDeviceModel: Boolean,
        sDisplay: Boolean,
        sPackageName: Boolean
    ) = """
          [Device Brand]: ${Build.BRAND.by(sDeviceBrand)}
          [Device Model]: ${Build.MODEL.by(sDeviceModel)}
          [Display]: ${Build.DISPLAY.by(sDisplay)}
          [Android Version]: ${Build.VERSION.RELEASE}
          [Android API Level]: ${Build.VERSION.SDK_INT}
          [System Locale]: ${Locale.getDefault()}
          [Process ID]: $pid
          [User ID]: $userId
          [CPU ABI]: ${cpuAbi.ifBlank { "none" }}
          [Package Name]: ${packageName.by(sPackageName)}
          [Version Name]: ${versionName.ifBlank { "unknown" }}
          [Version Code]: ${versionCode.takeIf { it != -1L } ?: "unknown"}
          [Target SDK]: ${targetSdk.takeIf { it != -1 } ?: "unknown"}
          [Min SDK]: ${minSdk.takeIf { it != -1 } ?: "unknown"}
          [Error Type]: ${when {
            isAnr -> "ANR"
            isNativeCrash -> "Native"
            else -> "JVM"
        }}
          [Crash Time]: $utcTime
          [Stack Trace]:
    """.trimIndent() + "\n$stackTrace"

    /**
     * 判断字符串是否需要显示
     * @param isDisplay 是否需要显示
     * @return [String]
     */
    private fun String.by(isDisplay: Boolean) = if (isDisplay) this else "***"
}