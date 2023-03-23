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
 * This file is Created by fankes on 2022/5/7.
 */
@file:Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")

package com.fankes.apperrorstracking.ui.activity.errors

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.fankes.apperrorstracking.R
import com.fankes.apperrorstracking.bean.AppErrorsInfoBean
import com.fankes.apperrorstracking.data.ConfigData
import com.fankes.apperrorstracking.data.factory.bind
import com.fankes.apperrorstracking.databinding.ActivityAppErrorsDetailBinding
import com.fankes.apperrorstracking.locale.LocaleString
import com.fankes.apperrorstracking.ui.activity.base.BaseActivity
import com.fankes.apperrorstracking.utils.factory.*
import com.highcapable.yukihookapi.hook.log.loggerE

class AppErrorsDetailActivity : BaseActivity<ActivityAppErrorsDetailBinding>() {

    companion object {

        /** 请求保存文件回调标识 */
        private const val WRITE_REQUEST_CODE = 0

        /** [AppErrorsInfoBean] 传值 */
        private const val EXTRA_APP_ERRORS_INFO = "app_errors_info_extra"

        /**
         * 启动 [AppErrorsDetailActivity]
         * @param context 实例
         * @param appErrorsInfo 应用异常信息
         */
        fun start(context: Context, appErrorsInfo: AppErrorsInfoBean) =
            context.navigate<AppErrorsDetailActivity> { putExtra(EXTRA_APP_ERRORS_INFO, appErrorsInfo) }
    }

    /** 预导出的异常堆栈 */
    private var stackTrace = ""

    override fun onCreate() {
        val appErrorsInfo = runCatching { intent?.getSerializableExtraCompat<AppErrorsInfoBean>(EXTRA_APP_ERRORS_INFO) }.getOrNull()
            ?: return toastAndFinish(name = "AppErrorsInfo")
        if (appErrorsInfo.isEmpty) {
            binding.appPanelScrollView.isVisible = false
            showDialog {
                title = LocaleString.notice
                msg = LocaleString.unableGetAppErrorsRecordTip
                confirmButton(LocaleString.gotIt) {
                    cancel()
                    finish()
                }
                cancelButton(LocaleString.goItNow) {
                    cancel()
                    finish()
                    navigate<AppErrorsRecordActivity>()
                }
                noCancelable()
            }
            return
        }
        binding.appInfoItem.setOnClickListener { openSelfSetting(appErrorsInfo.packageName) }
        binding.titleBackIcon.setOnClickListener { onBackPressed() }
        binding.printIcon.setOnClickListener {
            loggerE(msg = appErrorsInfo.stackTrace)
            toast(LocaleString.printToLogcatSuccess)
        }
        binding.copyIcon.setOnClickListener { copyToClipboard(appErrorsInfo.stackOutputShareContent) }
        binding.exportIcon.setOnClickListener {
            stackTrace = appErrorsInfo.stackOutputFileContent
            runCatching {
                startActivityForResult(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/application"
                    putExtra(Intent.EXTRA_TITLE, "${appErrorsInfo.packageName}_${appErrorsInfo.utcTime}.log")
                }, WRITE_REQUEST_CODE)
            }.onFailure { toast(msg = "Start Android SAF failed") }
        }
        binding.shareIcon.setOnClickListener {
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, appErrorsInfo.stackOutputShareContent)
            }, LocaleString.shareErrorStack))
        }
        binding.appIcon.setImageDrawable(appIconOf(appErrorsInfo.packageName))
        binding.appNameText.text = appNameOf(appErrorsInfo.packageName)
        binding.appVersionText.text = appErrorsInfo.versionBrand
        binding.appUserIdText.isVisible = appErrorsInfo.userId > 0
        binding.appUserIdText.text = LocaleString.userId(appErrorsInfo.userId)
        binding.appCpuAbiText.text = appErrorsInfo.cpuAbi.ifBlank { LocaleString.noCpuAbi }
        binding.jvmErrorPanel.isGone = appErrorsInfo.isNativeCrash
        binding.errorTypeIcon.setImageResource(if (appErrorsInfo.isNativeCrash) R.drawable.ic_cpp else R.drawable.ic_java)
        binding.errorInfoText.text = appErrorsInfo.exceptionMessage
        binding.errorTypeText.text = appErrorsInfo.exceptionClassName
        binding.errorFileNameText.text = appErrorsInfo.throwFileName
        binding.errorThrowClassText.text = appErrorsInfo.throwClassName
        binding.errorThrowMethodText.text = appErrorsInfo.throwMethodName
        binding.errorLineNumberText.text = appErrorsInfo.throwLineNumber.toString()
        binding.errorRecordTimeText.text = appErrorsInfo.dateTime
        binding.errorStackTraceMovableText.text = appErrorsInfo.stackTrace
        binding.errorStackTraceFixedText.text = appErrorsInfo.stackTrace
        binding.disableAutoWrapErrorStackTraceSwitch.bind(ConfigData.DISABLE_AUTO_WRAP_ERROR_STACK_TRACE) {
            onInitialize {
                binding.errorStackTraceScrollView.isVisible = it
                binding.errorStackTraceFixedText.isGone = it
            }
            onChanged {
                reinitialize()
                resetScrollView()
            }
        }
        binding.appPanelScrollView.setOnScrollChangeListener { _, _, y, _, _ ->
            binding.detailTitleText.text = if (y >= 30.dp(context = this)) appNameOf(appErrorsInfo.packageName) else LocaleString.appName
        }
        binding.detailTitleText.setOnClickListener { binding.appPanelScrollView.smoothScrollTo(0, 0) }
        val list = listOf(
            binding.errorInfoText,
            binding.errorTypeText,
            binding.errorFileNameText,
            binding.errorThrowClassText,
            binding.errorThrowMethodText,
            binding.errorLineNumberText,
            binding.errorRecordTimeText
        )
        list.forEach { i ->
            i.setOnLongClickListener {
                copyToClipboard(i.text as String)
                true
            }
        }
        resetScrollView()
    }

    /** 修复在一些小屏设备上设置了 [TextView.setTextIsSelectable] 后布局自动上滑问题 */
    private fun resetScrollView() {
        binding.rootView.post {
            binding.appPanelScrollView.scrollTo(0, 0)
            binding.errorStackTraceScrollView.scrollTo(0, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) runCatching {
            data?.data?.let {
                contentResolver?.openOutputStream(it)?.apply { write(stackTrace.toByteArray()) }?.close()
                toast(LocaleString.outputStackSuccess)
            } ?: toast(LocaleString.outputStackFail)
        }.onFailure { toast(LocaleString.outputStackFail) }
    }

    override fun onBackPressed() {
        intent?.removeExtra(EXTRA_APP_ERRORS_INFO)
        finish()
    }
}