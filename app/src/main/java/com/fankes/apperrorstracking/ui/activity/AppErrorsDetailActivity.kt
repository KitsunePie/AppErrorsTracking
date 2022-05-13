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
@file:Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")

package com.fankes.apperrorstracking.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.view.WindowManager
import androidx.core.view.isGone
import com.fankes.apperrorstracking.R
import com.fankes.apperrorstracking.bean.AppErrorsInfoBean
import com.fankes.apperrorstracking.const.Const
import com.fankes.apperrorstracking.databinding.ActivityAppErrorsDetailBinding
import com.fankes.apperrorstracking.locale.LocaleString
import com.fankes.apperrorstracking.ui.activity.base.BaseActivity
import com.fankes.apperrorstracking.utils.factory.*
import com.highcapable.yukihookapi.hook.log.loggerE

class AppErrorsDetailActivity : BaseActivity<ActivityAppErrorsDetailBinding>() {

    companion object {

        /** 请求保存文件回调标识 */
        private const val WRITE_REQUEST_CODE = 0

        /**
         * 启动 [AppErrorsDetailActivity]
         * @param context 实例
         * @param appErrorsInfo 应用异常信息
         * @param isOutSide 是否从外部启动
         */
        fun start(context: Context, appErrorsInfo: AppErrorsInfoBean, isOutSide: Boolean = false) =
            context.navigate<AppErrorsDetailActivity>(isOutSide) { putExtra(Const.EXTRA_APP_ERRORS_INFO, appErrorsInfo) }
    }

    /** 预导出的异常堆栈 */
    private var stackTrace = ""

    /** 已经显示的截图对话框 */
    private var observerDialog: DialogBuilder? = null

    /** 已截图的路径数组 */
    private val observerPaths = hashSetOf<String>()

    /** 截图监听注册 */
    private val observer = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            if (observerPaths.contains(uri?.path ?: "")) return
            observerDialog?.cancel()
            showDialog {
                observerDialog = this
                title = LocaleString.dontScreenshot
                msg = LocaleString.dontScreenshotTip
                confirmButton(LocaleString.gotIt)
                noCancelable()
            }
            uri?.path?.let { observerPaths.add(it) }
            /** 截图一次后就禁止再次截图 */
            window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    override fun onCreate() {
        val appErrorsInfo = runCatching { intent?.getSerializableExtra(Const.EXTRA_APP_ERRORS_INFO) as? AppErrorsInfoBean }.getOrNull()
            ?: return toastAndFinish()
        binding.appInfoItem.setOnClickListener { openSelfSetting(appErrorsInfo.packageName) }
        binding.titleBackIcon.setOnClickListener { onBackPressed() }
        binding.printIcon.setOnClickListener {
            loggerE(msg = appErrorsInfo.stackTrace)
            toast(LocaleString.printToLogcatSuccess)
        }
        binding.copyIcon.setOnClickListener { copyToClipboard(appErrorsInfo.stackTrace) }
        binding.exportIcon.setOnClickListener {
            stackTrace = appErrorsInfo.stackOutputContent
            runCatching {
                startActivityForResult(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/application"
                    putExtra(Intent.EXTRA_TITLE, "${appErrorsInfo.packageName}_${appErrorsInfo.timestamp}.log")
                }, WRITE_REQUEST_CODE)
            }.onFailure { toast(msg = "Start Android SAF failed") }
        }
        binding.appIcon.setImageDrawable(appIcon(appErrorsInfo.packageName))
        binding.appNameText.text = appName(appErrorsInfo.packageName)
        binding.appVersionText.text = appVersion(appErrorsInfo.packageName)
        binding.appAbiText.text = appCpuAbi(appErrorsInfo.packageName).ifBlank { LocaleString.noCpuAbi }
        binding.jvmErrorPanel.isGone = appErrorsInfo.isNativeCrash
        binding.errorTypeIcon.setImageResource(if (appErrorsInfo.isNativeCrash) R.drawable.ic_cpp else R.drawable.ic_java)
        binding.errorInfoText.text = appErrorsInfo.exceptionMessage
        binding.errorTypeText.text = appErrorsInfo.exceptionClassName
        binding.errorFileNameText.text = appErrorsInfo.throwFileName
        binding.errorThrowClassText.text = appErrorsInfo.throwClassName
        binding.errorThrowMethodText.text = appErrorsInfo.throwMethodName
        binding.errorLineNumberText.text = appErrorsInfo.throwLineNumber.toString()
        binding.errorRecordTimeText.text = appErrorsInfo.time
        binding.errorStackText.text = appErrorsInfo.stackTrace
        binding.appPanelScrollView.setOnScrollChangeListener { _, _, y, _, _ ->
            binding.detailTitleText.text = if (y >= 30.dp(context = this)) appName(appErrorsInfo.packageName) else LocaleString.appName
        }
        binding.detailTitleText.setOnClickListener { binding.appPanelScrollView.smoothScrollTo(0, 0) }
        /** 注册截图监听 */
        contentResolver?.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, observer)
    }

    /** 弹出提示并退出 */
    private fun toastAndFinish() {
        toast(msg = "Invalid AppErrorsInfo, exit")
        finish()
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
        intent?.removeExtra(Const.EXTRA_APP_ERRORS_INFO)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        /** 解除注册截图监听 */
        contentResolver?.unregisterContentObserver(observer)
    }
}