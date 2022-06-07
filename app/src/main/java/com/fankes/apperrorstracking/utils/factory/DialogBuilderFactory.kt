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
@file:Suppress("unused", "DEPRECATION", "OPT_IN_USAGE", "EXPERIMENTAL_API_USAGE")

package com.fankes.apperrorstracking.utils.factory

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.fankes.apperrorstracking.locale.LocaleString
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.shape.MaterialShapeDrawable
import com.highcapable.yukihookapi.annotation.CauseProblemsApi
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.LayoutInflaterClass

/**
 * 构造 [VB] 自定义 View 对话框
 * @param initiate 对话框方法体
 */
@JvmName(name = "showDialog-VB")
inline fun <reified VB : ViewBinding> Context.showDialog(initiate: DialogBuilder<VB>.() -> Unit) =
    DialogBuilder<VB>(context = this, VB::class.java).apply(initiate).show()

/**
 * 构造对话框
 * @param initiate 对话框方法体
 */
inline fun Context.showDialog(initiate: DialogBuilder<*>.() -> Unit) = DialogBuilder<ViewBinding>(context = this).apply(initiate).show()

/**
 * 对话框构造器
 * @param context 实例
 * @param bindingClass [ViewBinding] 的 [Class] 实例 or null
 */
class DialogBuilder<VB : ViewBinding>(val context: Context, private val bindingClass: Class<*>? = null) {

    private var instanceAndroidX: androidx.appcompat.app.AlertDialog.Builder? = null // 实例对象
    private var instanceAndroid: android.app.AlertDialog.Builder? = null // 实例对象

    private var onCancel: (() -> Unit)? = null // 对话框取消监听
    private var dialogInstance: Dialog? = null // 对话框实例
    private var customLayoutView: View? = null // 自定义布局

    /**
     * 获取 [DialogBuilder] 绑定布局对象
     * @return [VB]
     */
    val binding by lazy {
        bindingClass?.method {
            name = "inflate"
            param(LayoutInflaterClass)
        }?.get()?.invoke<VB>(LayoutInflater.from(context))?.apply {
            customLayoutView = root
        } ?: error("This dialog maybe not a custom view dialog")
    }

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
     * 设置对话框确定按钮
     * @param text 按钮文本内容
     * @param callback 点击事件
     */
    fun confirmButton(text: String = LocaleString.confirm, callback: () -> Unit = {}) {
        if (isUsingAndroidX)
            runCatching { instanceAndroidX?.setPositiveButton(text) { _, _ -> callback() } }
        else runCatching { instanceAndroid?.setPositiveButton(text) { _, _ -> callback() } }
    }

    /**
     * 设置对话框取消按钮
     * @param text 按钮文本内容
     * @param callback 点击事件
     */
    fun cancelButton(text: String = LocaleString.cancel, callback: () -> Unit = {}) {
        if (isUsingAndroidX)
            runCatching { instanceAndroidX?.setNegativeButton(text) { _, _ -> callback() } }
        else runCatching { instanceAndroid?.setNegativeButton(text) { _, _ -> callback() } }
    }

    /**
     * 设置对话框第三个按钮
     * @param text 按钮文本内容
     * @param callback 点击事件
     */
    fun neutralButton(text: String = LocaleString.more, callback: () -> Unit = {}) {
        if (isUsingAndroidX)
            runCatching { instanceAndroidX?.setNeutralButton(text) { _, _ -> callback() } }
        else runCatching { instanceAndroid?.setNeutralButton(text) { _, _ -> callback() } }
    }

    /**
     * 当对话框关闭时
     * @param callback 回调
     */
    fun onCancel(callback: () -> Unit) {
        onCancel = callback
    }

    /** 取消对话框 */
    fun cancel() = dialogInstance?.cancel()

    /** 显示对话框 */
    @CauseProblemsApi
    fun show() {
        /** 若当前自定义 View 的对话框没有调用 [binding] 将会对其手动调用一次以确保显示布局 */
        if (bindingClass != null) binding
        if (isUsingAndroidX) runCatching {
            instanceAndroidX?.create()?.apply {
                customLayoutView?.let { setView(it) }
                dialogInstance = this
                setOnCancelListener { onCancel?.invoke() }
            }?.show()
        } else runCatching {
            instanceAndroid?.create()?.apply {
                customLayoutView?.let { setView(it) }
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
            }?.show()
        }
    }
}