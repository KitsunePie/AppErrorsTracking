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
 * This file is Created by fankes on 2022/6/1.
 */
package com.fankes.apperrorstracking.ui.activity.errors

import android.content.Context
import android.os.Build
import androidx.core.view.isVisible
import com.fankes.apperrorstracking.R
import com.fankes.apperrorstracking.bean.AppErrorsDisplayBean
import com.fankes.apperrorstracking.data.ConfigData
import com.fankes.apperrorstracking.databinding.ActivityAppErrorsDisplayBinding
import com.fankes.apperrorstracking.databinding.DiaAppErrorsDisplayBinding
import com.fankes.apperrorstracking.locale.LocaleString
import com.fankes.apperrorstracking.ui.activity.base.BaseActivity
import com.fankes.apperrorstracking.utils.factory.*
import com.fankes.apperrorstracking.utils.tool.FrameworkTool

class AppErrorsDisplayActivity : BaseActivity<ActivityAppErrorsDisplayBinding>() {

    companion object {

        /** 当前实例 - 单例运行 */
        private var instance: AppErrorsDisplayActivity? = null

        /** [AppErrorsDisplayBean] 传值 */
        private const val EXTRA_APP_ERRORS_DISPLAY = "app_errors_display_extra"

        /**
         * 启动 [AppErrorsDisplayActivity]
         * @param context 实例
         * @param appErrorsDisplay 应用异常信息显示
         */
        fun start(context: Context, appErrorsDisplay: AppErrorsDisplayBean) =
            context.navigate<AppErrorsDisplayActivity>(isOutSide = true) { putExtra(EXTRA_APP_ERRORS_DISPLAY, appErrorsDisplay) }
    }

    override fun onCreate() {
        instance?.finish()
        instance = this
        val appErrorsDisplay = runCatching { intent?.getSerializableExtraCompat<AppErrorsDisplayBean>(EXTRA_APP_ERRORS_DISPLAY) }.getOrNull()
            ?: return toastAndFinish(name = "AppErrorsDisplay")
        /** 设置 Material 3 动态颜色主题 */
        if (ConfigData.isEnableMaterial3StyleAppErrorsDialog) setTheme(R.style.Theme_AppErrorsTracking_Translucent_Material3)
        /** 显示对话框 */
        showDialog<DiaAppErrorsDisplayBinding>(ConfigData.isEnableMaterial3StyleAppErrorsDialog.not()) {
            title = appErrorsDisplay.title
            if (ConfigData.isEnableMaterial3StyleAppErrorsDialog && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                arrayOf(
                    binding.appInfoIcon, binding.closeAppIcon,
                    binding.reopenAppIcon, binding.errorDetailIcon,
                    binding.mutedIfUnlockIcon, binding.mutedIfRestartIcon
                ).forEach { it.setColorFilter(resources.colorOf(android.R.color.system_accent1_600)) }
            binding.processNameText.isVisible = appErrorsDisplay.packageName != appErrorsDisplay.processName
            binding.appInfoItem.isVisible = appErrorsDisplay.isShowAppInfoButton
            binding.closeAppItem.isVisible = appErrorsDisplay.isShowReopenButton.not() && appErrorsDisplay.isShowCloseAppButton
            binding.reopenAppItem.isVisible = appErrorsDisplay.isShowReopenButton
            binding.processNameText.text = LocaleString.crashProcess(appErrorsDisplay.processName)
            binding.appInfoItem.setOnClickListener {
                cancel()
                openSelfSetting(appErrorsDisplay.packageName)
            }
            binding.closeAppItem.setOnClickListener { cancel() }
            binding.reopenAppItem.setOnClickListener {
                FrameworkTool.openAppUsedFramework(context, appErrorsDisplay.packageName, appErrorsDisplay.userId)
                cancel()
            }
            binding.errorDetailItem.setOnClickListener {
                FrameworkTool.fetchAppErrorInfoData(context, appErrorsDisplay.pid) { appErrorsInfo ->
                    appErrorsInfo.takeIf { it.isEmpty.not() }?.also {
                        AppErrorsDetailActivity.start(context, it)
                        cancel()
                    } ?: toast(LocaleString.unableGetAppErrorsRecordTip)
                }
            }
            binding.mutedIfUnlockItem.setOnClickListener {
                FrameworkTool.mutedErrorsIfUnlock(context, appErrorsDisplay.packageName) {
                    toast(LocaleString.muteIfUnlockTip(appErrorsDisplay.appName))
                    cancel()
                }
            }
            binding.mutedIfRestartItem.setOnClickListener {
                FrameworkTool.mutedErrorsIfRestart(context, appErrorsDisplay.packageName) {
                    toast(LocaleString.muteIfRestartTip(appErrorsDisplay.appName))
                    cancel()
                }
            }
            onCancel { finish() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}