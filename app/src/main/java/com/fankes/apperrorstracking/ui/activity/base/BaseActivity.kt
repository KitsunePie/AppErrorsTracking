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
@file:Suppress("UNCHECKED_CAST")

package com.fankes.apperrorstracking.ui.activity.base

import android.app.ActivityManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.viewbinding.ViewBinding
import com.fankes.apperrorstracking.R
import com.fankes.apperrorstracking.utils.factory.isNotSystemInDarkMode
import com.fankes.apperrorstracking.utils.factory.toast
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.LayoutInflaterClass

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    /** 获取绑定布局对象 */
    lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = current().generic()?.argument()?.method {
            name = "inflate"
            param(LayoutInflaterClass)
        }?.get()?.invoke<VB>(layoutInflater) ?: error("binding failed")
        setContentView(binding.root)
        /** 隐藏系统的标题栏 */
        supportActionBar?.hide()
        /** 初始化沉浸状态栏 */
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = isNotSystemInDarkMode
            isAppearanceLightNavigationBars = isNotSystemInDarkMode
        }
        ResourcesCompat.getColor(resources, R.color.colorThemeBackground, null).also {
            window?.statusBarColor = it
            window?.navigationBarColor = it
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) window?.navigationBarDividerColor = it
        }
        /** 装载子类 */
        onCreate()
    }

    /** 回调 [onCreate] 方法 */
    abstract fun onCreate()

    /**
     * 在旧版的 Android 系统中使用了 activity-alias 标签从启动器启动 Activity 会造成其组件名称 (完整类名) 为代理名称
     *
     * 为了获取真实的顶层 Activity 组件名称 (完整类名) - 如果名称不正确将自动执行一次结束并重新打开当前 Activity
     */
    fun checkingTopComponentName() {
        /** 当前顶层的 Activity 组件名称 (完整类名) */
        val topComponentName = runCatching {
            @Suppress("DEPRECATION")
            (getSystemService(ACTIVITY_SERVICE) as? ActivityManager?)
                ?.getRunningTasks(9999)?.firstOrNull()?.topActivity?.className ?: ""
        }.getOrNull() ?: ""
        if (topComponentName.isNotBlank() && topComponentName != javaClass.name) {
            finish()
            startActivity(Intent(this, javaClass))
        }
    }

    /**
     * 弹出提示并退出
     * @param name 名称
     */
    fun toastAndFinish(name: String) {
        toast(msg = "Invalid $name, exit")
        finish()
    }
}