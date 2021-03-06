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
import com.fankes.apperrorstracking.ui.activity.errors.AppErrorsDisplayActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.shape.MaterialShapeDrawable
import com.highcapable.yukihookapi.annotation.CauseProblemsApi
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.LayoutInflaterClass

/**
 * ?????? [VB] ????????? View ?????????
 * @param initiate ??????????????????
 */
@JvmName(name = "showDialog-VB")
inline fun <reified VB : ViewBinding> Context.showDialog(initiate: DialogBuilder<VB>.() -> Unit) =
    DialogBuilder<VB>(context = this, VB::class.java).apply(initiate).show()

/**
 * ???????????????
 * @param initiate ??????????????????
 */
inline fun Context.showDialog(initiate: DialogBuilder<*>.() -> Unit) = DialogBuilder<ViewBinding>(context = this).apply(initiate).show()

/**
 * ??????????????????
 * @param context ??????
 * @param bindingClass [ViewBinding] ??? [Class] ?????? or null
 */
class DialogBuilder<VB : ViewBinding>(val context: Context, private val bindingClass: Class<*>? = null) {

    private var instanceAndroidX: androidx.appcompat.app.AlertDialog.Builder? = null // ????????????
    private var instanceAndroid: android.app.AlertDialog.Builder? = null // ????????????

    private var onCancel: (() -> Unit)? = null // ?????????????????????
    private var dialogInstance: Dialog? = null // ???????????????
    private var customLayoutView: View? = null // ???????????????

    /**
     * ?????? [DialogBuilder] ??????????????????
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
     * ?????????????????? AndroidX ???????????????
     * @return [Boolean]
     */
    private val isUsingAndroidX get() = runCatching { context is AppCompatActivity }.getOrNull() ?: false

    init {
        if (isUsingAndroidX) runCatching {
            instanceAndroidX = MaterialAlertDialogBuilder(context).also { builder ->
                if (context is AppErrorsDisplayActivity)
                    builder.background = (builder.background as MaterialShapeDrawable).apply { setCornerSize(15.dpFloat(context)) }
            }
        } else runCatching {
            instanceAndroid = android.app.AlertDialog.Builder(
                context,
                if (context.isSystemInDarkMode) android.R.style.Theme_Material_Dialog else android.R.style.Theme_Material_Light_Dialog
            )
        }
    }

    /** ??????????????????????????? */
    fun noCancelable() {
        if (isUsingAndroidX)
            runCatching { instanceAndroidX?.setCancelable(false) }
        else runCatching { instanceAndroid?.setCancelable(false) }
    }

    /** ????????????????????? */
    var title
        get() = ""
        set(value) {
            if (isUsingAndroidX)
                runCatching { instanceAndroidX?.setTitle(value) }
            else runCatching { instanceAndroid?.setTitle(value) }
        }

    /** ??????????????????????????? */
    var msg
        get() = ""
        set(value) {
            if (isUsingAndroidX)
                runCatching { instanceAndroidX?.setMessage(value) }
            else runCatching { instanceAndroid?.setMessage(value) }
        }

    /** ???????????????????????????????????? */
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
     * ???????????????????????????
     * @param text ??????????????????
     * @param callback ????????????
     */
    fun confirmButton(text: String = LocaleString.confirm, callback: () -> Unit = {}) {
        if (isUsingAndroidX)
            runCatching { instanceAndroidX?.setPositiveButton(text) { _, _ -> callback() } }
        else runCatching { instanceAndroid?.setPositiveButton(text) { _, _ -> callback() } }
    }

    /**
     * ???????????????????????????
     * @param text ??????????????????
     * @param callback ????????????
     */
    fun cancelButton(text: String = LocaleString.cancel, callback: () -> Unit = {}) {
        if (isUsingAndroidX)
            runCatching { instanceAndroidX?.setNegativeButton(text) { _, _ -> callback() } }
        else runCatching { instanceAndroid?.setNegativeButton(text) { _, _ -> callback() } }
    }

    /**
     * ??????????????????????????????
     * @param text ??????????????????
     * @param callback ????????????
     */
    fun neutralButton(text: String = LocaleString.more, callback: () -> Unit = {}) {
        if (isUsingAndroidX)
            runCatching { instanceAndroidX?.setNeutralButton(text) { _, _ -> callback() } }
        else runCatching { instanceAndroid?.setNeutralButton(text) { _, _ -> callback() } }
    }

    /**
     * ?????????????????????
     * @param callback ??????
     */
    fun onCancel(callback: () -> Unit) {
        onCancel = callback
    }

    /** ??????????????? */
    fun cancel() = dialogInstance?.cancel()

    /** ??????????????? */
    @CauseProblemsApi
    fun show() {
        /** ?????????????????? View ???????????????????????? [binding] ??????????????????????????????????????????????????? */
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