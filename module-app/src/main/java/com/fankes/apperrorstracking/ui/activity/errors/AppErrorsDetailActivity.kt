/*
 * AppErrorsTracking - Added more features to app's crash dialog, fixed custom rom deleted dialog, the best experience to Android developer.
 * Copyright (C) 2017-2024 Fankes Studio(qzmmcn@163.com)
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
 * This file is created by fankes on 2022/5/7.
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
import com.fankes.apperrorstracking.locale.locale
import com.fankes.apperrorstracking.ui.activity.base.BaseActivity
import com.fankes.apperrorstracking.utils.factory.appIconOf
import com.fankes.apperrorstracking.utils.factory.appNameOf
import com.fankes.apperrorstracking.utils.factory.copyToClipboard
import com.fankes.apperrorstracking.utils.factory.dp
import com.fankes.apperrorstracking.utils.factory.getSerializableExtraCompat
import com.fankes.apperrorstracking.utils.factory.navigate
import com.fankes.apperrorstracking.utils.factory.openSelfSetting
import com.fankes.apperrorstracking.utils.factory.showDialog
import com.fankes.apperrorstracking.utils.factory.toast
import com.fankes.apperrorstracking.utils.tool.StackTraceShareHelper
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
        if (initUi(intent).not()) return
        binding.titleBackIcon.setOnClickListener { onBackPressed() }
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
        binding.detailTitleText.setOnClickListener { binding.appPanelScrollView.smoothScrollTo(0, 0) }
        resetScrollView()
    }

    /**
     * 从 [Intent] 中解析 [AppErrorsInfoBean] 并加载至界面
     *
     * @param intent 用于获取并解析 [AppErrorsInfoBean] 的 [Intent] 实例
     * @return [Boolean] 是否解析成功：true 为成功；false 为失败，可能是 [Intent] 为空或者 [AppErrorsInfoBean] 为空
     */
    private fun initUi(intent: Intent?): Boolean {
        val appErrorsInfo = runCatching { intent?.getSerializableExtraCompat<AppErrorsInfoBean>(EXTRA_APP_ERRORS_INFO) }.getOrNull()
        if (appErrorsInfo == null) {
            toastAndFinish(name = "AppErrorsInfo")
            return false
        }
        if (appErrorsInfo.isEmpty) {
            binding.appPanelScrollView.isVisible = false
            showDialog {
                title = locale.notice
                msg = locale.unableGetAppErrorsRecordTip
                confirmButton(locale.gotIt) {
                    cancel()
                    finish()
                }
                cancelButton(locale.goItNow) {
                    cancel()
                    finish()
                    navigate<AppErrorsRecordActivity>()
                }
                noCancelable()
            }
            return false
        }
        binding.appInfoItem.setOnClickListener { openSelfSetting(appErrorsInfo.packageName) }
        binding.printIcon.setOnClickListener {
            loggerE(msg = appErrorsInfo.stackTrace)
            toast(locale.printToLogcatSuccess)
        }
        binding.copyIcon.setOnClickListener {
            StackTraceShareHelper.showChoose(context = this, locale.copyErrorStack) { sDeviceBrand, sDeviceModel, sDisplay, sPackageName ->
                copyToClipboard(appErrorsInfo.stackOutputShareContent(sDeviceBrand, sDeviceModel, sDisplay, sPackageName))
            }
        }
        binding.exportIcon.setOnClickListener {
            StackTraceShareHelper.showChoose(context = this, locale.exportToFile) { sDeviceBrand, sDeviceModel, sDisplay, sPackageName ->
                stackTrace = appErrorsInfo.stackOutputFileContent(sDeviceBrand, sDeviceModel, sDisplay, sPackageName)
                runCatching {
                    startActivityForResult(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "*/application"
                        val packageName = if (sPackageName) appErrorsInfo.packageName else "anonymous"
                        putExtra(Intent.EXTRA_TITLE, "${packageName}_${appErrorsInfo.utcTime}.log")
                    }, WRITE_REQUEST_CODE)
                }.onFailure { toast(msg = "Start Android SAF failed") }
            }
        }
        binding.shareIcon.setOnClickListener {
            StackTraceShareHelper.showChoose(context = this, locale.shareErrorStack) { sDeviceBrand, sDeviceModel, sDisplay, sPackageName ->
                startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, appErrorsInfo.stackOutputShareContent(sDeviceBrand, sDeviceModel, sDisplay, sPackageName))
                }, locale.shareErrorStack))
            }
        }
        binding.appIcon.setImageDrawable(appIconOf(appErrorsInfo.packageName))
        binding.appNameText.text = appNameOf(appErrorsInfo.packageName).ifBlank { appErrorsInfo.packageName }
        binding.appVersionText.text = appErrorsInfo.versionBrand
        binding.appUserIdText.isVisible = appErrorsInfo.userId > 0
        binding.appUserIdText.text = locale.userId(appErrorsInfo.userId)
        binding.appCpuAbiText.text = appErrorsInfo.cpuAbi.ifBlank { locale.noCpuAbi }
        binding.appTargetSdkText.text = locale.appTargetSdk(appErrorsInfo.targetSdk)
        binding.appMinSdkText.text = locale.appMinSdk(appErrorsInfo.minSdk)
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
        binding.appPanelScrollView.setOnScrollChangeListener { _, _, y, _, _ ->
            binding.detailTitleText.text = if (y >= 30.dp(context = this@AppErrorsDetailActivity))
                appNameOf(appErrorsInfo.packageName).ifBlank { appErrorsInfo.packageName }
            else locale.appName
        }
        return true
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
                toast(locale.outputStackSuccess)
            } ?: toast(locale.outputStackFail)
        }.onFailure { toast(locale.outputStackFail) }
    }

    override fun onBackPressed() {
        intent?.removeExtra(EXTRA_APP_ERRORS_INFO)
        finish()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (initUi(intent)) binding.appPanelScrollView.scrollTo(0, 0)
    }
}