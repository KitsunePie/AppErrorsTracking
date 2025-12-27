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
 * This file is created by fankes on 2022/5/14.
 */
@file:Suppress("SetTextI18n")

package com.fankes.apperrorstracking.ui.activity.main

import android.os.Build
import androidx.core.view.isVisible
import com.fankes.apperrorstracking.R
import com.fankes.apperrorstracking.const.ModuleVersion
import com.fankes.apperrorstracking.data.ConfigData
import com.fankes.apperrorstracking.data.factory.bind
import com.fankes.apperrorstracking.databinding.ActivityMainBinding
import com.fankes.apperrorstracking.locale.locale
import com.fankes.apperrorstracking.ui.activity.base.BaseActivity
import com.fankes.apperrorstracking.ui.activity.debug.LoggerActivity
import com.fankes.apperrorstracking.ui.activity.errors.AppErrorsMutedActivity
import com.fankes.apperrorstracking.ui.activity.errors.AppErrorsRecordActivity
import com.fankes.apperrorstracking.utils.factory.hideOrShowLauncherIcon
import com.fankes.apperrorstracking.utils.factory.isLauncherIconShowing
import com.fankes.apperrorstracking.utils.factory.isSystemLanguageSimplifiedChinese
import com.fankes.apperrorstracking.utils.factory.navigate
import com.fankes.apperrorstracking.utils.factory.openBrowser
import com.fankes.apperrorstracking.utils.factory.showDialog
import com.fankes.apperrorstracking.utils.factory.toast
import com.fankes.apperrorstracking.utils.tool.AppAnalyticsTool
import com.fankes.apperrorstracking.utils.tool.AppAnalyticsTool.bindAppAnalytics
import com.fankes.apperrorstracking.utils.tool.FrameworkTool
import com.fankes.apperrorstracking.utils.tool.GithubReleaseTool
import com.fankes.projectpromote.ProjectPromote
import com.highcapable.betterandroid.ui.extension.view.isUnderline
import com.highcapable.yukihookapi.YukiHookAPI

class MainActivity : BaseActivity<ActivityMainBinding>() {

    companion object {

        /** 系统版本 */
        private val systemVersion = "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT}) ${Build.DISPLAY}"

        /** 模块是否有效 */
        var isModuleValied = false
    }

    override fun onCreate() {
        checkingTopComponentName()
        /** 检查更新 */
        GithubReleaseTool.checkingForUpdate(context = this, ModuleVersion.NAME) { version, function ->
            binding.mainTextReleaseVersion.apply {
                text = locale.clickToUpdate(version)
                isVisible = true
                setOnClickListener { function() }
            }
        }
        /** 推广、恰饭 */
        if (YukiHookAPI.Status.isXposedModuleActive) ProjectPromote.show(activity = this, ModuleVersion.toString())
        /** 显示开发者提示 */
        if (ConfigData.isShowDeveloperNotice)
            showDialog {
                title = locale.developerNotice
                msg = locale.developerNoticeTip
                confirmButton(locale.gotIt) { ConfigData.isShowDeveloperNotice = false }
                noCancelable()
            }
        /** 设置 CI 自动构建标识 */
        if (ModuleVersion.isCiMode)
            binding.mainTextReleaseVersion.apply {
                text = "CI ${ModuleVersion.GITHUB_COMMIT_ID}"
                isVisible = true
                setOnClickListener {
                    showDialog {
                        title = locale.ciNoticeDialogTitle
                        msg = locale.ciNoticeDialogContent(ModuleVersion.GITHUB_COMMIT_ID)
                        confirmButton(locale.gotIt)
                        noCancelable()
                    }
                }
            }
        binding.mainTextVersion.text = locale.moduleVersion(ModuleVersion.NAME)
        binding.mainTextSystemVersion.text = locale.systemVersion(systemVersion)
        binding.onlyShowErrorsInFrontSwitch.bind(ConfigData.ENABLE_ONLY_SHOW_ERRORS_IN_FRONT)
        binding.onlyShowErrorsInMainProcessSwitch.bind(ConfigData.ENABLE_ONLY_SHOW_ERRORS_IN_MAIN)
        binding.alwaysShowsReopenAppOptionsSwitch.bind(ConfigData.ENABLE_ALWAYS_SHOWS_REOPEN_APP_OPTIONS)
        binding.shareWithFile.bind(ConfigData.SHARE_WITH_FILE)
        binding.enableAppsConfigsTemplateSwitch.bind(ConfigData.ENABLE_APP_CONFIG_TEMPLATE) {
            onInitialize { binding.mgrAppsConfigsTemplateButton.isVisible = it }
            onChanged { reinitialize() }
        }
        binding.errorsDialogPreventMisoperationSwitch.bind(ConfigData.ENABLE_PREVENT_MISOPERATION_FOR_DIALOG)
        binding.enableMaterial3AppErrorsDialogSwitch.bind(ConfigData.ENABLE_MATERIAL3_STYLE_APP_ERRORS_DIALOG)
        /** 设置匿名统计 */
        binding.appAnalyticsConfigItem.isVisible = AppAnalyticsTool.isAvailable
        binding.enableAnonymousStatisticsSwitch.bindAppAnalytics()
        /** 系统版本点击事件 */
        binding.mainTextSystemVersion.setOnClickListener {
            showDialog {
                title = locale.notice
                msg = systemVersion
                confirmButton(locale.gotIt)
            }
        }
        /** 管理应用配置模板按钮点击事件 */
        binding.mgrAppsConfigsTemplateButton.setOnClickListener { whenActivated { navigate<ConfigureActivity>() } }
        /** 功能管理按钮点击事件 */
        binding.viewErrorsRecordButton.setOnClickListener { whenActivated { navigate<AppErrorsRecordActivity>() } }
        binding.viewMutedErrorsAppsButton.setOnClickListener { whenActivated { navigate<AppErrorsMutedActivity>() } }
        /** 调试日志按钮点击事件 */
        binding.titleLoggerIcon.setOnClickListener { navigate<LoggerActivity>() }
        /** 重启按钮点击事件 */
        binding.titleRestartIcon.setOnClickListener { FrameworkTool.restartSystem(context = this) }
        /** 项目地址按钮点击事件 */
        binding.titleGithubIcon.setOnClickListener { openBrowser(url = "https://github.com/KitsunePie/AppErrorsTracking") }
        /** 恰饭！ */
        binding.paymentFollowingZhCnItem.isVisible = isSystemLanguageSimplifiedChinese
        binding.linkWithFollowMe.isUnderline = true
        binding.linkWithFollowMe.setOnClickListener {
            openBrowser(url = "https://www.coolapk.com/u/876977", packageName = "com.coolapk.market")
        }
        /** 设置桌面图标显示隐藏 */
        binding.hideIconInLauncherSwitch.isChecked = isLauncherIconShowing.not()
        binding.hideIconInLauncherSwitch.setOnCheckedChangeListener { btn, b ->
            if (btn.isPressed.not()) return@setOnCheckedChangeListener
            hideOrShowLauncherIcon(b)
        }
    }

    /** 刷新模块状态 */
    private fun refreshModuleStatus() {
        binding.mainLinStatus.setBackgroundResource(
            when {
                YukiHookAPI.Status.isXposedModuleActive && isModuleValied.not() -> R.drawable.bg_yellow_round
                YukiHookAPI.Status.isXposedModuleActive -> R.drawable.bg_green_round
                else -> R.drawable.bg_dark_round
            }
        )
        binding.mainImgStatus.setImageResource(
            when {
                YukiHookAPI.Status.isXposedModuleActive -> R.drawable.ic_success
                else -> R.drawable.ic_warn
            }
        )
        binding.mainTextStatus.text = when {
            YukiHookAPI.Status.isXposedModuleActive && isModuleValied.not() -> locale.moduleNotFullyActivated
            YukiHookAPI.Status.isXposedModuleActive -> locale.moduleIsActivated
            else -> locale.moduleNotActivated
        }
        binding.mainTextApiWay.isVisible = YukiHookAPI.Status.isXposedModuleActive
        binding.mainTextApiWay.text = "Activated by ${YukiHookAPI.Status.Executor.name} API ${YukiHookAPI.Status.Executor.apiLevel}"
    }

    /**
     * 当模块激活后才能执行相应功能
     * @param callback 激活后回调
     */
    private inline fun whenActivated(callback: () -> Unit) {
        if (YukiHookAPI.Status.isXposedModuleActive) callback() else toast(locale.moduleNotActivated)
    }

    override fun onResume() {
        super.onResume()
        /** 刷新模块状态 */
        refreshModuleStatus()
        /** 检查模块激活状态 */
        FrameworkTool.checkingActivated(context = this) { isValied ->
            isModuleValied = isValied
            refreshModuleStatus()
        }
    }
}