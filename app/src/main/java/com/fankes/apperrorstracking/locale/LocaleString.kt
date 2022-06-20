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
    val appName get() = appName()

    /** @string Automatic generated */
    fun appName(vararg objArrs: Any) = R.string.app_name.bind(*objArrs)

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
    val muteIfUnlock get() = muteIfUnlock()

    /** @string Automatic generated */
    fun muteIfUnlock(vararg objArrs: Any) = R.string.mute_if_unlock.bind(*objArrs)

    /** @string Automatic generated */
    val muteIfRestart get() = muteIfRestart()

    /** @string Automatic generated */
    fun muteIfRestart(vararg objArrs: Any) = R.string.mute_if_restart.bind(*objArrs)

    /** @string Automatic generated */
    val muteIfUnlockTip get() = muteIfUnlockTip()

    /** @string Automatic generated */
    fun muteIfUnlockTip(vararg objArrs: Any) = R.string.mute_if_unlock_tip.bind(*objArrs)

    /** @string Automatic generated */
    val muteIfRestartTip get() = muteIfRestartTip()

    /** @string Automatic generated */
    fun muteIfRestartTip(vararg objArrs: Any) = R.string.mute_if_restart_tip.bind(*objArrs)

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

    /** @string Automatic generated */
    val areYouSureRemoveRecord get() = areYouSureRemoveRecord()

    /** @string Automatic generated */
    fun areYouSureRemoveRecord(vararg objArrs: Any) = R.string.are_you_sure_remove_record.bind(*objArrs)

    /** @string Automatic generated */
    val gotIt get() = gotIt()

    /** @string Automatic generated */
    fun gotIt(vararg objArrs: Any) = R.string.got_it.bind(*objArrs)

    /** @string Automatic generated */
    val moduleVersion get() = moduleVersion()

    /** @string Automatic generated */
    fun moduleVersion(vararg objArrs: Any) = R.string.module_version.bind(*objArrs)

    /** @string Automatic generated */
    val systemVersion get() = systemVersion()

    /** @string Automatic generated */
    fun systemVersion(vararg objArrs: Any) = R.string.system_version.bind(*objArrs)

    /** @string Automatic generated */
    val areYouSureRestartSystem get() = areYouSureRestartSystem()

    /** @string Automatic generated */
    fun areYouSureRestartSystem(vararg objArrs: Any) = R.string.are_your_sure_restart_system.bind(*objArrs)

    /** @string Automatic generated */
    val fastRestart get() = fastRestart()

    /** @string Automatic generated */
    fun fastRestart(vararg objArrs: Any) = R.string.fast_restart.bind(*objArrs)

    /** @string Automatic generated */
    val accessRootFail get() = accessRootFail()

    /** @string Automatic generated */
    fun accessRootFail(vararg objArrs: Any) = R.string.access_root_fail.bind(*objArrs)

    /** @string Automatic generated */
    val moduleNotActivated get() = moduleNotActivated()

    /** @string Automatic generated */
    fun moduleNotActivated(vararg objArrs: Any) = R.string.module_not_activated.bind(*objArrs)

    /** @string Automatic generated */
    val moduleIsActivated get() = moduleIsActivated()

    /** @string Automatic generated */
    fun moduleIsActivated(vararg objArrs: Any) = R.string.module_is_activated.bind(*objArrs)

    /** @string Automatic generated */
    val moduleNotFullyActivated get() = moduleNotFullyActivated()

    /** @string Automatic generated */
    fun moduleNotFullyActivated(vararg objArrs: Any) = R.string.module_not_fully_activated.bind(*objArrs)

    /** @string Automatic generated */
    val momentAgo get() = momentAgo()

    /** @string Automatic generated */
    fun momentAgo(vararg objArrs: Any) = R.string.moment_ago.bind(*objArrs)

    /** @string Automatic generated */
    val secondAgo get() = secondAgo()

    /** @string Automatic generated */
    fun secondAgo(vararg objArrs: Any) = R.string.second_ago.bind(*objArrs)

    /** @string Automatic generated */
    val minuteAgo get() = minuteAgo()

    /** @string Automatic generated */
    fun minuteAgo(vararg objArrs: Any) = R.string.minute_ago.bind(*objArrs)

    /** @string Automatic generated */
    val hourAgo get() = hourAgo()

    /** @string Automatic generated */
    fun hourAgo(vararg objArrs: Any) = R.string.hour_ago.bind(*objArrs)

    /** @string Automatic generated */
    val dayAgo get() = dayAgo()

    /** @string Automatic generated */
    fun dayAgo(vararg objArrs: Any) = R.string.day_ago.bind(*objArrs)

    /** @string Automatic generated */
    val monthAgo get() = monthAgo()

    /** @string Automatic generated */
    fun monthAgo(vararg objArrs: Any) = R.string.month_ago.bind(*objArrs)

    /** @string Automatic generated */
    val yearAgo get() = yearAgo()

    /** @string Automatic generated */
    fun yearAgo(vararg objArrs: Any) = R.string.year_ago.bind(*objArrs)

    /** @string Automatic generated */
    val crashProcess get() = crashProcess()

    /** @string Automatic generated */
    fun crashProcess(vararg objArrs: Any) = R.string.crash_process.bind(*objArrs)

    /** @string Automatic generated */
    val shareErrorStack get() = shareErrorStack()

    /** @string Automatic generated */
    fun shareErrorStack(vararg objArrs: Any) = R.string.share_error_stack.bind(*objArrs)

    /** @string Automatic generated */
    val areYouSureUnmuteAll get() = areYouSureUnmuteAll()

    /** @string Automatic generated */
    fun areYouSureUnmuteAll(vararg objArrs: Any) = R.string.are_you_sure_unmute_all.bind(*objArrs)

    /** @string Automatic generated */
    val filterByCondition get() = filterByCondition()

    /** @string Automatic generated */
    fun filterByCondition(vararg objArrs: Any) = R.string.filter_by_condition.bind(*objArrs)

    /** @string Automatic generated */
    val clearFilters get() = clearFilters()

    /** @string Automatic generated */
    fun clearFilters(vararg objArrs: Any) = R.string.clear_filters.bind(*objArrs)

    /** @string Automatic generated */
    val resultCount get() = resultCount()

    /** @string Automatic generated */
    fun resultCount(vararg objArrs: Any) = R.string.result_count.bind(*objArrs)

    /** @string Automatic generated */
    val loading get() = loading()

    /** @string Automatic generated */
    fun loading(vararg objArrs: Any) = R.string.loading.bind(*objArrs)

    /** @string Automatic generated */
    val showErrorsDialog get() = showErrorsDialog()

    /** @string Automatic generated */
    fun showErrorsDialog(vararg objArrs: Any) = R.string.show_errors_dialog.bind(*objArrs)

    /** @string Automatic generated */
    val showErrorsToast get() = showErrorsToast()

    /** @string Automatic generated */
    fun showErrorsToast(vararg objArrs: Any) = R.string.show_errors_toast.bind(*objArrs)

    /** @string Automatic generated */
    val showNothing get() = showNothing()

    /** @string Automatic generated */
    fun showNothing(vararg objArrs: Any) = R.string.show_nothing.bind(*objArrs)

    /** @string Automatic generated */
    val appErrorsStatistics get() = appErrorsStatistics()

    /** @string Automatic generated */
    fun appErrorsStatistics(vararg objArrs: Any) = R.string.app_errors_statistics.bind(*objArrs)

    /** @string Automatic generated */
    val totalErrorsUnit get() = totalErrorsUnit()

    /** @string Automatic generated */
    fun totalErrorsUnit(vararg objArrs: Any) = R.string.total_errors_unit.bind(*objArrs)

    /** @string Automatic generated */
    val totalAppsUnit get() = totalAppsUnit()

    /** @string Automatic generated */
    fun totalAppsUnit(vararg objArrs: Any) = R.string.total_apps_unit.bind(*objArrs)

    /** @string Automatic generated */
    val generatingStatistics get() = generatingStatistics()

    /** @string Automatic generated */
    fun generatingStatistics(vararg objArrs: Any) = R.string.generating_statistics.bind(*objArrs)

    /** @string Automatic generated */
    val moduleNotFullyActivatedTip get() = moduleNotFullyActivatedTip()

    /** @string Automatic generated */
    fun moduleNotFullyActivatedTip(vararg objArrs: Any) = R.string.module_not_fully_activated_tip.bind(*objArrs)
}