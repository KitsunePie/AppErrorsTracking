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
import androidx.core.view.isGone
import com.fankes.apperrorstracking.R
import com.fankes.apperrorstracking.bean.AppErrorsInfoBean
import com.fankes.apperrorstracking.databinding.ActivityAppErrorsDetailBinding
import com.fankes.apperrorstracking.hook.entity.FrameworkHooker
import com.fankes.apperrorstracking.locale.LocaleString
import com.fankes.apperrorstracking.ui.activity.base.BaseActivity
import com.fankes.apperrorstracking.utils.factory.*
import com.highcapable.yukihookapi.hook.log.loggerE
import java.text.SimpleDateFormat
import java.util.*

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
            context.navigate<AppErrorsDetailActivity>(isOutSide) { putExtra(FrameworkHooker.APP_ERRORS_INFO, appErrorsInfo) }
    }

    /** 预导出的异常堆栈 */
    private var stackTrace = ""

    override fun onCreate() {
        val appErrorsInfo =
            intent?.getSerializableExtra(FrameworkHooker.APP_ERRORS_INFO) as? AppErrorsInfoBean ?: return toastAndFinish()

        /** 创建异常堆栈模板 */
        fun createStack() =
            "package name: ${appErrorsInfo.packageName} timestamp: ${appErrorsInfo.timestamp}\n${appErrorsInfo.stackTrace}"
        binding.titleBackIcon.setOnClickListener { onBackPressed() }
        binding.printIcon.setOnClickListener {
            loggerE(msg = createStack())
            toast(LocaleString.printToLogcatSuccess)
        }
        binding.copyIcon.setOnClickListener { copyToClipboard(appErrorsInfo.stackTrace) }
        binding.exportIcon.setOnClickListener {
            stackTrace = createStack()
            runCatching {
                startActivityForResult(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TITLE, "${appErrorsInfo.packageName}_${appErrorsInfo.timestamp}.log")
                }, WRITE_REQUEST_CODE)
            }.onFailure { toast(msg = "Start Android SAF failed") }
        }
        binding.appIcon.setImageDrawable(appIcon(appErrorsInfo.packageName))
        binding.appName.text = appName(appErrorsInfo.packageName)
        binding.appVersion.text = appVersion(appErrorsInfo.packageName)
        binding.jvmErrorPanel.isGone = appErrorsInfo.isNativeCrash
        binding.errorTypeIcon.setImageResource(if (appErrorsInfo.isNativeCrash) R.drawable.ic_cpp else R.drawable.ic_java)
        binding.errorInfoText.text = appErrorsInfo.exceptionMessage
        binding.errorTypeText.text = appErrorsInfo.exceptionClassName
        binding.errorFileNameText.text = appErrorsInfo.throwFileName
        binding.errorThrowClassText.text = appErrorsInfo.throwClassName
        binding.errorThrowMethodText.text = appErrorsInfo.throwMethodName
        binding.errorLineNumberText.text = appErrorsInfo.throwLineNumber.toString()
        binding.errorRecordTimeText.text = SimpleDateFormat.getDateTimeInstance().format(Date(appErrorsInfo.timestamp))
        binding.errorStackText.text = appErrorsInfo.stackTrace
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
        intent?.removeExtra(FrameworkHooker.APP_ERRORS_INFO)
        finish()
    }
}