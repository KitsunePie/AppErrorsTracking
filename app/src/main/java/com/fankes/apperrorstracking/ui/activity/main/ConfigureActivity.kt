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
 * This file is Created by fankes on 2022/6/4.
 */
package com.fankes.apperrorstracking.ui.activity.main

import androidx.core.view.isVisible
import com.fankes.apperrorstracking.bean.AppFiltersBean
import com.fankes.apperrorstracking.bean.AppInfoBean
import com.fankes.apperrorstracking.bean.enum.AppFiltersType
import com.fankes.apperrorstracking.data.AppErrorsConfigData
import com.fankes.apperrorstracking.data.enum.AppErrorsConfigType
import com.fankes.apperrorstracking.databinding.ActivityConfigBinding
import com.fankes.apperrorstracking.databinding.AdapterAppInfoBinding
import com.fankes.apperrorstracking.databinding.DiaAppConfigBinding
import com.fankes.apperrorstracking.databinding.DiaAppsFilterBinding
import com.fankes.apperrorstracking.locale.LocaleString
import com.fankes.apperrorstracking.ui.activity.base.BaseActivity
import com.fankes.apperrorstracking.utils.factory.appIconOf
import com.fankes.apperrorstracking.utils.factory.bindAdapter
import com.fankes.apperrorstracking.utils.factory.newThread
import com.fankes.apperrorstracking.utils.factory.showDialog
import com.fankes.apperrorstracking.utils.tool.FrameworkTool

class ConfigureActivity : BaseActivity<ActivityConfigBinding>() {

    /** 过滤条件 */
    private var appFilters = AppFiltersBean()

    /** 回调适配器改变 */
    private var onChanged: (() -> Unit)? = null

    /** 全部的 APP 信息 */
    private val listData = ArrayList<AppInfoBean>()

    override fun onCreate() {
        binding.titleBackIcon.setOnClickListener { finish() }
        binding.globalIcon.setOnClickListener {
            showAppConfigDialog(LocaleString.globalConfig, isShowGlobalConfig = false) { type ->
                AppErrorsConfigData.putAppShowingType(type)
                onChanged?.invoke()
            }
        }
        binding.batchIcon.setOnClickListener {
            showAppConfigDialog(LocaleString.batchOperationsNumber(listData.size), isNotSetDefaultValue = true) { type ->
                showDialog {
                    title = LocaleString.notice
                    msg = LocaleString.areYouSureApplySiteApps(listData.size)
                    confirmButton {
                        listData.takeIf { it.isNotEmpty() }?.forEach { AppErrorsConfigData.putAppShowingType(type, it.packageName) }
                        onChanged?.invoke()
                    }
                    cancelButton()
                }
            }
        }
        binding.filterIcon.setOnClickListener {
            showDialog<DiaAppsFilterBinding> {
                title = LocaleString.filterByCondition
                binding.filtersRadioUser.isChecked = appFilters.type == AppFiltersType.USER
                binding.filtersRadioSystem.isChecked = appFilters.type == AppFiltersType.SYSTEM
                binding.filtersRadioAll.isChecked = appFilters.type == AppFiltersType.ALL
                binding.appFiltersEdit.apply {
                    requestFocus()
                    invalidate()
                    if (appFilters.name.isNotBlank()) {
                        setText(appFilters.name)
                        setSelection(appFilters.name.length)
                    }
                }
                /** 设置 [AppFiltersBean.type] */
                fun setAppFiltersType() {
                    appFilters.type = when {
                        binding.filtersRadioUser.isChecked -> AppFiltersType.USER
                        binding.filtersRadioSystem.isChecked -> AppFiltersType.SYSTEM
                        binding.filtersRadioAll.isChecked -> AppFiltersType.ALL
                        else -> error("Invalid app filters type")
                    }
                }
                confirmButton {
                    setAppFiltersType()
                    appFilters.name = binding.appFiltersEdit.text.toString().trim()
                    refreshData()
                }
                cancelButton()
                if (appFilters.name.isNotBlank())
                    neutralButton(LocaleString.clearFilters) {
                        setAppFiltersType()
                        appFilters.name = ""
                        refreshData()
                    }
            }
        }
        binding.listView.apply {
            bindAdapter {
                onBindDatas { listData }
                onBindViews<AdapterAppInfoBinding> { binding, position ->
                    listData[position].also { bean ->
                        binding.appIcon.setImageDrawable(bean.icon)
                        binding.appNameText.text = bean.name
                        binding.configTypeText.text = when {
                            AppErrorsConfigData.isAppShowingType(AppErrorsConfigType.GLOBAL, bean.packageName) -> LocaleString.followGlobalConfig
                            AppErrorsConfigData.isAppShowingType(AppErrorsConfigType.DIALOG, bean.packageName) -> LocaleString.showErrorsDialog
                            AppErrorsConfigData.isAppShowingType(AppErrorsConfigType.NOTIFY, bean.packageName) -> LocaleString.showErrorsNotify
                            AppErrorsConfigData.isAppShowingType(AppErrorsConfigType.TOAST, bean.packageName) -> LocaleString.showErrorsToast
                            AppErrorsConfigData.isAppShowingType(AppErrorsConfigType.NOTHING, bean.packageName) -> LocaleString.showNothing
                            else -> "Unknown type"
                        }
                    }
                }
            }.apply { onChanged = { notifyDataSetChanged() } }
            setOnItemClickListener { _, _, p, _ ->
                listData[p].also { bean ->
                    showAppConfigDialog(bean.name, bean.packageName) { type ->
                        AppErrorsConfigData.putAppShowingType(type, bean.packageName)
                        onChanged?.invoke()
                    }
                }
            }
        }
        /** 模块未完全激活将显示警告 */
        if (MainActivity.isModuleValied.not())
            showDialog {
                title = LocaleString.notice
                msg = LocaleString.moduleNotFullyActivatedTip
                confirmButton { FrameworkTool.restartSystem(context) }
                cancelButton()
                noCancelable()
            }
        /** 开始刷新数据 */
        refreshData()
    }

    /**
     * 显示应用配置对话框
     * @param title 对话框标题
     * @param packageName APP 包名 - 默认空 (空时使用全局配置的默认值)
     * @param isNotSetDefaultValue 是否不设置选项的默认值 - 默认否
     * @param isShowGlobalConfig 是否显示跟随全局配置选项 - 默认是
     * @param result 回调类型结果
     */
    private fun showAppConfigDialog(
        title: String,
        packageName: String = "",
        isNotSetDefaultValue: Boolean = false,
        isShowGlobalConfig: Boolean = true,
        result: (AppErrorsConfigType) -> Unit
    ) {
        showDialog<DiaAppConfigBinding> {
            this.title = title
            binding.configRadio0.isVisible = isShowGlobalConfig
            if (isNotSetDefaultValue.not()) {
                if (isShowGlobalConfig) binding.configRadio0.isChecked =
                    AppErrorsConfigData.isAppShowingType(AppErrorsConfigType.GLOBAL, packageName)
                binding.configRadio1.isChecked = AppErrorsConfigData.isAppShowingType(AppErrorsConfigType.DIALOG, packageName)
                binding.configRadio2.isChecked = AppErrorsConfigData.isAppShowingType(AppErrorsConfigType.NOTIFY, packageName)
                binding.configRadio3.isChecked = AppErrorsConfigData.isAppShowingType(AppErrorsConfigType.TOAST, packageName)
                binding.configRadio4.isChecked = AppErrorsConfigData.isAppShowingType(AppErrorsConfigType.NOTHING, packageName)
            }
            confirmButton {
                result(
                    when {
                        binding.configRadio0.isChecked -> AppErrorsConfigType.GLOBAL
                        binding.configRadio1.isChecked -> AppErrorsConfigType.DIALOG
                        binding.configRadio2.isChecked -> AppErrorsConfigType.NOTIFY
                        binding.configRadio3.isChecked -> AppErrorsConfigType.TOAST
                        binding.configRadio4.isChecked -> AppErrorsConfigType.NOTHING
                        else -> error("Invalid config type")
                    }
                )
                FrameworkTool.refreshFrameworkPrefsData(context)
            }
            cancelButton()
        }
    }

    /** 刷新列表数据 */
    private fun refreshData() {
        binding.listProgressView.isVisible = true
        binding.globalIcon.isVisible = false
        binding.batchIcon.isVisible = false
        binding.filterIcon.isVisible = false
        binding.listView.isVisible = false
        binding.listNoDataView.isVisible = false
        binding.titleCountText.text = LocaleString.loading
        FrameworkTool.fetchAppListData(context = this, appFilters) {
            /** 设置一个临时变量用于更新列表数据 */
            val tempsData = ArrayList<AppInfoBean>()
            newThread {
                runCatching {
                    it.takeIf { e -> e.isNotEmpty() }?.forEach { e ->
                        tempsData.add(e)
                        e.icon = appIconOf(e.packageName)
                    }
                }
                if (isDestroyed.not()) runOnUiThread {
                    listData.clear()
                    listData.addAll(tempsData)
                    onChanged?.invoke()
                    binding.listView.post { binding.listView.setSelection(0) }
                    binding.listProgressView.isVisible = false
                    binding.globalIcon.isVisible = true
                    binding.batchIcon.isVisible = listData.isNotEmpty()
                    binding.filterIcon.isVisible = true
                    binding.listView.isVisible = listData.isNotEmpty()
                    binding.listNoDataView.isVisible = listData.isEmpty()
                    binding.titleCountText.text = LocaleString.resultCount(listData.size)
                } else tempsData.clear()
            }
        }
    }
}