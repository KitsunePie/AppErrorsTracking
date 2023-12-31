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
 * This file is created by fankes on 2023/2/3.
 */
@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.fankes.apperrorstracking.data.factory

import android.widget.CompoundButton
import com.fankes.apperrorstracking.data.ConfigData
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

/**
 * 绑定到 [CompoundButton] 自动设置选中状态
 * @param data 键值数据模板
 * @param initiate 方法体
 */
fun CompoundButton.bind(data: PrefsData<Boolean>, initiate: CompoundButtonDataBinder.(CompoundButton) -> Unit = {}) {
    val binder = CompoundButtonDataBinder(button = this).also { initiate(it, this) }
    isChecked = ConfigData.getBoolean(data).also { binder.initializeCallback?.invoke(it) }
    binder.applyChangesCallback = { ConfigData.putBoolean(data, it) }
    setOnCheckedChangeListener { button, isChecked ->
        if (button.isPressed) {
            if (binder.isAutoApplyChanges) binder.applyChangesCallback?.invoke(isChecked)
            binder.changedCallback?.invoke(isChecked)
        }
    }
}

/**
 * [CompoundButton] 数据绑定管理器实例
 * @param button 当前实例
 */
class CompoundButtonDataBinder(private val button: CompoundButton) {

    /** 状态初始化回调事件 */
    internal var initializeCallback: ((Boolean) -> Unit)? = null

    /** 状态改变回调事件 */
    internal var changedCallback: ((Boolean) -> Unit)? = null

    /** 应用更改回调事件 */
    internal var applyChangesCallback: ((Boolean) -> Unit)? = null

    /** 是否启用自动应用更改 */
    var isAutoApplyChanges = true

    /**
     * 监听状态初始化
     * @param result 回调结果
     */
    fun onInitialize(result: (Boolean) -> Unit) {
        initializeCallback = result
    }

    /**
     * 监听状态改变
     * @param result 回调结果
     */
    fun onChanged(result: (Boolean) -> Unit) {
        changedCallback = result
    }

    /** 重新初始化 */
    fun reinitialize() {
        initializeCallback?.invoke(button.isChecked)
    }

    /** 应用更改并重新初始化 */
    fun applyChangesAndReinitialize() {
        applyChanges()
        reinitialize()
    }

    /** 应用更改 */
    fun applyChanges() {
        applyChangesCallback?.invoke(button.isChecked)
    }

    /** 取消更改 */
    fun cancelChanges() {
        button.isChecked = button.isChecked.not()
    }
}