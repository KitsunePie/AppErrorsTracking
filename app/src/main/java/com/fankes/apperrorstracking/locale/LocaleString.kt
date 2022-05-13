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
 * This file is Created by fankes on 2022/5/11.
 */
@file:Suppress("MemberVisibilityCanBePrivate", "StaticFieldLeak", "unused")

package com.fankes.apperrorstracking.locale

import android.content.Context
import android.content.res.Resources
import com.fankes.apperrorstracking.R
import com.highcapable.yukihookapi.hook.param.PackageParam

/**
 * I18n 字符串实例
 */
object LocaleString {

    /** 当前的 [Context] */
    private var baseContext: Context? = null

    /** 当前的 [PackageParam] */
    private var basePackageParam: PackageParam? = null

    /** 当前的 [Resources] */
    private var baseResources: Resources? = null

    /**
     * 当前的 [Resources]
     * @return [Resources]
     * @throws IllegalStateException 如果 [LocaleString] 没有被绑定
     */
    private val resources
        get() = baseContext?.resources ?: basePackageParam?.moduleAppResources ?: baseResources
        ?: error("LocaleString must bind an instance first")

    /**
     * 绑定并初始化
     * @param instance 可以是 [Context]、[PackageParam]、[Resources]
     */
    fun bind(instance: Any) {
        when (instance) {
            is Context -> baseContext = instance
            is PackageParam -> basePackageParam = instance
            is Resources -> baseResources = instance
            else -> error("LocaleString bind an unknown instance")
        }
    }

    /**
     * 根据资源 Id 获取字符串
     * @param objArrs 格式化实例
     * @return [String]
     */
    private fun Int.bind(vararg objArrs: Any) = resources.getString(this, *objArrs)

    /** @string Automatic generated */
    val copied get() = copied()

    /** @string Automatic generated */
    fun copied(vararg objArrs: Any) = R.string.copied.bind(*objArrs)

    /** @string Automatic generated */
    val copyFail get() = copyFail()

    /** @string Automatic generated */
    fun copyFail(vararg objArrs: Any) = R.string.copy_fail.bind(*objArrs)

    /** @string Automatic generated */
    val printToLogcatSuccess get() = printToLogcatSuccess()

    /** @string Automatic generated */
    fun printToLogcatSuccess(vararg objArrs: Any) = R.string.print_to_logcat_success.bind(*objArrs)

    /** @string Automatic generated */
    val outputStackSuccess get() = outputStackSuccess()

    /** @string Automatic generated */
    fun outputStackSuccess(vararg objArrs: Any) = R.string.output_stack_success.bind(*objArrs)

    /** @string Automatic generated */
    val outputStackFail get() = outputStackFail()

    /** @string Automatic generated */
    fun outputStackFail(vararg objArrs: Any) = R.string.output_stack_fail.bind(*objArrs)

    /** @string Automatic generated */
    val aerrTitle get() = aerrTitle()

    /** @string Automatic generated */
    fun aerrTitle(vararg objArrs: Any) = R.string.aerr_title.bind(*objArrs)

    /** @string Automatic generated */
    val aerrRepeatedTitle get() = aerrRepeatedTitle()

    /** @string Automatic generated */
    fun aerrRepeatedTitle(vararg objArrs: Any) = R.string.aerr_repeated_title.bind(*objArrs)

    /** @string Automatic generated */
    val appInfo get() = appInfo()

    /** @string Automatic generated */
    fun appInfo(vararg objArrs: Any) = R.string.app_info.bind(*objArrs)

    /** @string Automatic generated */
    val closeApp get() = closeApp()

    /** @string Automatic generated */
    fun closeApp(vararg objArrs: Any) = R.string.close_app.bind(*objArrs)

    /** @string Automatic generated */
    val reopenApp get() = reopenApp()

    /** @string Automatic generated */
    fun reopenApp(vararg objArrs: Any) = R.string.reopen_app.bind(*objArrs)

    /** @string Automatic generated */
    val errorDetail get() = errorDetail()

    /** @string Automatic generated */
    fun errorDetail(vararg objArrs: Any) = R.string.error_detail.bind(*objArrs)

    /** @string Automatic generated */
    val ignoreIfUnlock get() = ignoreIfUnlock()

    /** @string Automatic generated */
    fun ignoreIfUnlock(vararg objArrs: Any) = R.string.ignore_if_unlock.bind(*objArrs)

    /** @string Automatic generated */
    val ignoreIfRestart get() = ignoreIfRestart()

    /** @string Automatic generated */
    fun ignoreIfRestart(vararg objArrs: Any) = R.string.ignore_if_restart.bind(*objArrs)

    /** @string Automatic generated */
    val ignoreIfUnlockTip get() = ignoreIfUnlockTip()

    /** @string Automatic generated */
    fun ignoreIfUnlockTip(vararg objArrs: Any) = R.string.ignore_if_unlock_tip.bind(*objArrs)

    /** @string Automatic generated */
    val ignoreIfRestartTip get() = ignoreIfRestartTip()

    /** @string Automatic generated */
    fun ignoreIfRestartTip(vararg objArrs: Any) = R.string.ignore_if_restart_tip.bind(*objArrs)

    /** @string Automatic generated */
    val confirm get() = confirm()

    /** @string Automatic generated */
    fun confirm(vararg objArrs: Any) = R.string.confirm.bind(*objArrs)

    /** @string Automatic generated */
    val cancel get() = cancel()

    /** @string Automatic generated */
    fun cancel(vararg objArrs: Any) = R.string.cancel.bind(*objArrs)

    /** @string Automatic generated */
    val more get() = more()

    /** @string Automatic generated */
    fun more(vararg objArrs: Any) = R.string.more.bind(*objArrs)

    /** @string Automatic generated */
    val notice get() = notice()

    /** @string Automatic generated */
    fun notice(vararg objArrs: Any) = R.string.notice.bind(*objArrs)

    /** @string Automatic generated */
    val areYouSureClearErrors get() = areYouSureClearErrors()

    /** @string Automatic generated */
    fun areYouSureClearErrors(vararg objArrs: Any) = R.string.are_you_sure_clear_errors.bind(*objArrs)

    /** @string Automatic generated */
    val allErrorsClearSuccess get() = allErrorsClearSuccess()

    /** @string Automatic generated */
    fun allErrorsClearSuccess(vararg objArrs: Any) = R.string.all_errors_clear_success.bind(*objArrs)

    /** @string Automatic generated */
    val areYouSureExportAllErrors get() = areYouSureExportAllErrors()

    /** @string Automatic generated */
    fun areYouSureExportAllErrors(vararg objArrs: Any) = R.string.are_you_sure_export_all_errors.bind(*objArrs)

    /** @string Automatic generated */
    val exportAllErrorsSuccess get() = exportAllErrorsSuccess()

    /** @string Automatic generated */
    fun exportAllErrorsSuccess(vararg objArrs: Any) = R.string.export_all_errors_success.bind(*objArrs)

    /** @string Automatic generated */
    val exportAllErrorsFail get() = exportAllErrorsFail()

    /** @string Automatic generated */
    fun exportAllErrorsFail(vararg objArrs: Any) = R.string.export_all_errors_fail.bind(*objArrs)

    /** @string Automatic generated */
    val noCpuAbi get() = noCpuAbi()

    /** @string Automatic generated */
    fun noCpuAbi(vararg objArrs: Any) = R.string.no_cpu_abi.bind(*objArrs)
}