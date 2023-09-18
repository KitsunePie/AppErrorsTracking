/*
 * AppErrorsTracking - Added more features to app's crash dialog, fixed custom rom deleted dialog, the best experience to Android developer.
 * Copyright (C) 2017-2023 Fankes Studio(qzmmcn@163.com)
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
import com.fankes.apperrorstracking.locale.LocaleString
import com.fankes.apperrorstracking.utils.factory.appCpuAbiOf
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
 * @param isNativeCrash 是否为原生层异常
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
    @SerializedName("isNativeCrash")
    var isNativeCrash: Boolean = false,
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
            now = LocaleString.momentAgo,
            second = LocaleString.secondAgo,
            minute = LocaleString.minuteAgo,
            hour = LocaleString.hourAgo,
            day = LocaleString.dayAgo,
            month = LocaleString.monthAgo,
            year = LocaleString.yearAgo
        )

    /**
     * 获取异常本地化时间
     * @return [String]
     */
    val dateTime get() = SimpleDateFormat.getDateTimeInstance().format(Date(timestamp)) ?: utcTime

    /**
     * 获取异常堆栈分享模板
     * @return [String]
     */
    val stackOutputShareContent
        get() = """
          Generated by AppErrorsTracking
          Project Url: https://github.com/KitsunePie/AppErrorsTracking
          ===============
          $environmentInfo
        """.trimIndent()

    /**
     * 获取异常堆栈文件模板
     * @return [String]
     */
    val stackOutputFileContent
        get() = """
          ================================================================
              Generated by AppErrorsTracking
              Project Url: https://github.com/KitsunePie/AppErrorsTracking
          ================================================================
        """.trimIndent()

    /**
     * 获取运行环境信息
     * @return [String]
     */
    private val environmentInfo
        get() = """
          [Device Brand]: ${Build.BRAND}
          [Device Model]: ${Build.MODEL}
          [Display]: ${Build.DISPLAY}
          [Android Version]: ${Build.VERSION.RELEASE}
          [Android API Level]: ${Build.VERSION.SDK_INT}
          [System Locale]: ${Locale.getDefault()}
          [Process ID]: $pid
          [User ID]: $userId
          [CPU ABI]: ${cpuAbi.ifBlank { "none" }}
          [Package Name]: $packageName
          [Version Name]: ${versionName.ifBlank { "unknown" }}
          [Version Code]: ${versionCode.takeIf { it != -1L } ?: "unknown"}
          [Error Type]: ${if (isNativeCrash) "Native" else "JVM"}
          [Crash Time]: $utcTime
          [Stack Trace]:
          $stackTrace
        """.trimIndent()
}