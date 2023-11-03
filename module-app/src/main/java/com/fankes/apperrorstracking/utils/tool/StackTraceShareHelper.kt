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
 * This file is created by fankes on 2023/11/3.
 */
package com.fankes.apperrorstracking.utils.tool

import android.content.Context
import com.fankes.apperrorstracking.databinding.DiaStackTraceShareBinding
import com.fankes.apperrorstracking.utils.factory.showDialog

/**
 * 异常堆栈分享工具类
 */
object StackTraceShareHelper {

    /**
     * 显示分享选择器
     * @param context 当前实例
     * @param title 对话框标题
     * @param onChoose 回调选择的结果
     */
    fun showChoose(
        context: Context,
        title: String,
        onChoose: (sDeviceBrand: Boolean, sDeviceModel: Boolean, sDisplay: Boolean, sPackageName: Boolean) -> Unit
    ) {
        context.showDialog<DiaStackTraceShareBinding> {
            this.title = title
            confirmButton {
                val sDeviceBrand = binding.configCheck0.isChecked
                val sDeviceModel = binding.configCheck1.isChecked
                val sDisplay = binding.configCheck2.isChecked
                val sPackageName = binding.configCheck3.isChecked
                onChoose(sDeviceBrand, sDeviceModel, sDisplay, sPackageName)
                cancel()
            }
            cancelButton()
        }
    }
}