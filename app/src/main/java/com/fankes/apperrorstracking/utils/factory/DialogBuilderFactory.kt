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
 * This file is Created by fankes on 2022/5/12.
 */
@file:Suppress("unused", "DEPRECATION")

package com.fankes.apperrorstracking.utils.factory

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fankes.apperrorstracking.locale.LocaleString
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.shape.MaterialShapeDrawable

/**
 * 构造对话框
 * @param it 对话框方法体
 */
fun Context.showDialog(it: DialogBuilder.() -> Unit) = DialogBuilder(this).apply(it).show()

/**
 * 对话框构造器
 * @param context 实例
 */
class DialogBuilder(val context: Context) {

    private var instanceAndroidX: androidx.appcompat.app.AlertDialog.Builder? = null // 实例对象
    private var instanceAndroid: android.app.AlertDialog.Builder? = null // 实例对象

    private var isSystemAlert = false // 标识为系统级别的对话框

    private var onCancel: (() -> Unit)? = null // 对话框取消监听

    private var dialogInstance: Dialog? = null // 对话框实例

    private var customLayoutView: View? = null // 自定义布局

    /**
     * 是否需要使用 AndroidX 风格对话框
     * @return [Boolean]
     */
    private val isUsingAndroidX get() = runCatching { context is AppCompatActivity }.getOrNull() ?: false

    init {
        if (isUsingAndroidX) runCatching {
            instanceAndroidX = MaterialAlertDialogBuilder(context).apply {
                background = (background as MaterialShapeDrawable).apply { setCornerSize(15.dpFloat(context)) }
            }
        } else runCatching {
            instanceAndroid = android.app.AlertDialog.Builder(
                context,
                if (context.isSystemInDarkMode) android.R.style.Theme_Material_Dialog else android.R.style.Theme_Material_Light_Dialog
            )
        }
    }

    /** 设置对话框不可关闭 */
    fun noCancelable() {
        if (isUsingAndroidX)
            runCatching { instanceAndroidX?.setCancelable(false) }
        else runCatching { instanceAndroid?.setCancelable(false) }
    }

    /**
     * 设置为系统级别对话框
     *
     * - ❗仅可在系统级别的 APP 中生效
     */
    fun makeSystemAlert() {
        isSystemAlert = true
    }

    /** 设置对话框标题 */
    var title
        get() = ""
        set(value) {
            if (isUsingAndroidX)
                runCatching { instanceAndroidX?.setTitle(value) }
            else runCatching { instanceAndroid?.setTitle(value) }
        }

    /** 设置对话框消息内容 */
    var msg
        get() = ""
        set(value) {
            if (isUsingAndroidX)
                runCatching { instanceAndroidX?.setMessage(value) }
            else runCatching { instanceAndroid?.setMessage(value) }
        }

    /** 设置进度条对话框消息内容 */
    var progressContent
        get() = ""
        set(value) {
            if (customLayoutView == null)
                customLayoutView = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER or Gravity.START
                    addView(ProgressBar(context))
                    addView(View(context).apply { layoutParams = ViewGroup.LayoutParams(20.dp(context), 5) })
                    addView(TextView(context).apply {
                        tag = "progressContent"
                        text = value
                    })
                    setPadding(20.dp(context), 20.dp(context), 20.dp(context), 20.dp(context))
                }
            else customLayoutView?.findViewWithTag<TextView>("progressContent")?.text = value
        }

    /**
     * 设置对话框自定义布局
     * @return [customLayoutView]
     */
    var view
        get() = customLayoutView
        set(value) {
            customLayoutView = value
        }

    /**
     * 设置对话框确定按钮
     * @param text 按钮文本内容
     * @param it 点击事件
     */
    fun confirmButton(text: String = LocaleString.confirm, it: () -> Unit = {}) {
        if (isUsingAndroidX)
            runCatching { instanceAndroidX?.setPositiveButton(text) { _, _ -> it() } }
        else runCatching { instanceAndroid?.setPositiveButton(text) { _, _ -> it() } }
    }

    /**
     * 设置对话框取消按钮
     * @param text 按钮文本内容
     * @param it 点击事件
     */
    fun cancelButton(text: String = LocaleString.cancel, it: () -> Unit = {}) {
        if (isUsingAndroidX)
            runCatching { instanceAndroidX?.setNegativeButton(text) { _, _ -> it() } }
        else runCatching { instanceAndroid?.setNegativeButton(text) { _, _ -> it() } }
    }

    /**
     * 设置对话框第三个按钮
     * @param text 按钮文本内容
     * @param it 点击事件
     */
    fun neutralButton(text: String = LocaleString.more, it: () -> Unit = {}) {
        if (isUsingAndroidX)
            runCatching { instanceAndroidX?.setNeutralButton(text) { _, _ -> it() } }
        else runCatching { instanceAndroid?.setNeutralButton(text) { _, _ -> it() } }
    }

    /**
     * 当对话框关闭时
     * @param it 回调
     */
    fun onCancel(it: () -> Unit) {
        onCancel = it
    }

    /** 取消对话框 */
    fun cancel() = dialogInstance?.cancel()

    /** 显示对话框 */
    internal fun show() {
        if (isUsingAndroidX) runCatching {
            instanceAndroidX?.create()?.apply {
                customLayoutView?.let { setView(it) }
                dialogInstance = this
                setOnCancelListener { onCancel?.invoke() }
                /** 只有 SystemUid 才能响应系统级别的对话框 */
                if (isSystemAlert) runCatching { window?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT) }
            }?.show()
        } else runCatching {
            instanceAndroid?.create()?.apply {
                customLayoutView?.let { setView(it) }
                window?.attributes
                window?.setBackgroundDrawable(
                    InsetDrawable(
                        GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            if (context.isSystemInDarkMode) intArrayOf(0xFF2D2D2D.toInt(), 0xFF2D2D2D.toInt())
                            else intArrayOf(Color.WHITE, Color.WHITE)
                        ).apply {
                            shape = GradientDrawable.RECTANGLE
                            gradientType = GradientDrawable.LINEAR_GRADIENT
                            cornerRadius = 15.dpFloat(this@DialogBuilder.context)
                        }, 30.dp(context), 0, 30.dp(context), 0
                    )
                )
                dialogInstance = this
                setOnCancelListener { onCancel?.invoke() }
                /** 只有 SystemUid 才能响应系统级别的对话框 */
                if (isSystemAlert) runCatching { window?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT) }
            }?.show()
        }
    }
}