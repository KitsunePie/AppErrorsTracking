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
 * This file is Created by fankes on 2022/5/10.
 */
package com.fankes.apperrorstracking.bean

import android.app.ApplicationErrorReport
import android.os.Build
import androidx.annotation.Keep
import com.fankes.apperrorstracking.locale.LocaleString
import com.fankes.apperrorstracking.utils.factory.difference
import com.fankes.apperrorstracking.utils.factory.toUtcTime
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

/**
 * 应用异常信息 bean
 * @param pid 进程 ID
 * @param userId 用户 ID
 * @param packageName 包名
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
    @Keep var pid: Int = -1,
    @Keep var userId: Int = -1,
    @Keep var packageName: String = "",
    @Keep var isNativeCrash: Boolean = false,
    @Keep var exceptionClassName: String = "",
    @Keep var exceptionMessage: String = "",
    @Keep var throwFileName: String = "",
    @Keep var throwClassName: String = "",
    @Keep var throwMethodName: String = "",
    @Keep var throwLineNumber: Int = -1,
    @Keep var stackTrace: String = "",
    @Keep var timestamp: Long = -1L
) : Serializable {

    companion object {

        /**
         * 从 [ApplicationErrorReport.CrashInfo] 克隆
         * @param pid APP 进程 ID
         * @param userId APP 用户 ID
         * @param packageName APP 包名
         * @param crashInfo [ApplicationErrorReport.CrashInfo]
         * @return [AppErrorsInfoBean]
         */
        fun clone(pid: Int, userId: Int, packageName: String?, crashInfo: ApplicationErrorReport.CrashInfo?) =
            (crashInfo?.exceptionClassName?.lowercase() == "native crash").let { isNativeCrash ->
                AppErrorsInfoBean(
                    pid = pid,
                    userId = userId,
                    packageName = packageName ?: "unknown",
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
    val jsonFileName get() = "${packageName}_${pid}_${timestamp}.json"

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
        get() = "Generated by AppErrorsTracking\n" +
                "Project Url: https://github.com/KitsunePie/AppErrorsTracking\n" +
                "===============\n" +
                "[Device Brand]: ${Build.BRAND}\n" +
                "[Device Model]: ${Build.MODEL}\n" +
                "[Display]: ${Build.DISPLAY}\n" +
                "[Android Version]: ${Build.VERSION.RELEASE}\n" +
                "[API Version]: ${Build.VERSION.SDK_INT}\n" +
                "[System Locale]: ${Locale.getDefault()}\n" +
                "[Package Name]: $packageName\n" +
                (if (userId > 0) "[User Id]: $userId\n" else "") +
                "[Error Type]: ${if (isNativeCrash) "Native" else "Jvm"}\n" +
                "[Crash Time]: $utcTime\n" +
                "[Stack Trace]:\n" + stackTrace

    /**
     * 获取异常堆栈文件模板
     * @return [String]
     */
    val stackOutputFileContent
        get() = "================================================================\n" +
                "    Generated by AppErrorsTracking\n" +
                "    Project Url: https://github.com/KitsunePie/AppErrorsTracking\n" +
                "================================================================\n" +
                "[Device Brand]: ${Build.BRAND}\n" +
                "[Device Model]: ${Build.MODEL}\n" +
                "[Display]: ${Build.DISPLAY}\n" +
                "[Android Version]: ${Build.VERSION.RELEASE}\n" +
                "[API Version]: ${Build.VERSION.SDK_INT}\n" +
                "[System Locale]: ${Locale.getDefault()}\n" +
                "[Package Name]: $packageName\n" +
                (if (userId > 0) "[User Id]: $userId\n" else "") +
                "[Error Type]: ${if (isNativeCrash) "Native" else "Jvm"}\n" +
                "[Crash Time]: $utcTime\n" +
                "[Stack Trace]:\n" + stackTrace
}