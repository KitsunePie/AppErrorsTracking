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
import com.fankes.apperrorstracking.databinding.ActivityConfigBinding
import com.fankes.apperrorstracking.databinding.AdapterAppInfoBinding
import com.fankes.apperrorstracking.databinding.DiaAppConfigBinding
import com.fankes.apperrorstracking.databinding.DiaAppsFilterBinding
import com.fankes.apperrorstracking.hook.factory.*
import com.fankes.apperrorstracking.locale.LocaleString
import com.fankes.apperrorstracking.ui.activity.base.BaseActivity
import com.fankes.apperrorstracking.utils.factory.appIcon
import com.fankes.apperrorstracking.utils.factory.bindAdapter
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
        binding.titleBackIcon.setOnClickListener { onBackPressed() }
        binding.filterIcon.setOnClickListener {
            showDialog<DiaAppsFilterBinding> {
                title = LocaleString.filterByCondition
                binding.containsSystemSwitch.isChecked = appFilters.isContainsSystem
                binding.appFiltersEdit.apply {
                    requestFocus()
                    invalidate()
                    if (appFilters.name.isNotBlank()) {
                        setText(appFilters.name)
                        setSelection(appFilters.name.length)
                    }
                }
                confirmButton {
                    appFilters.isContainsSystem = binding.containsSystemSwitch.isChecked
                    appFilters.name = binding.appFiltersEdit.text.toString().trim()
                    refreshData()
                }
                cancelButton()
                if (appFilters.name.isNotBlank())
                    neutralButton(LocaleString.clearFilters) {
                        appFilters.isContainsSystem = binding.containsSystemSwitch.isChecked
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
                            isAppShowErrorsDialog(bean.packageName) -> LocaleString.showErrorsDialog
                            isAppShowErrorsToast(bean.packageName) -> LocaleString.showErrorsToast
                            isAppShowNothing(bean.packageName) -> LocaleString.showNothing
                            else -> "Unknown type"
                        }
                    }
                }
            }.apply { onChanged = { notifyDataSetChanged() } }
            setOnItemClickListener { _, _, p, _ ->
                listData[p].also { bean ->
                    showDialog<DiaAppConfigBinding> {
                        title = bean.name
                        binding.configRadio1.isChecked = isAppShowErrorsDialog(bean.packageName)
                        binding.configRadio2.isChecked = isAppShowErrorsToast(bean.packageName)
                        binding.configRadio3.isChecked = isAppShowNothing(bean.packageName)
                        confirmButton {
                            putAppShowErrorsDialog(bean.packageName, binding.configRadio1.isChecked)
                            putAppShowErrorsToast(bean.packageName, binding.configRadio2.isChecked)
                            putAppShowNothing(bean.packageName, binding.configRadio3.isChecked)
                            onChanged?.invoke()
                        }
                        cancelButton()
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

    /** 刷新列表数据 */
    private fun refreshData() {
        binding.listProgressView.isVisible = true
        binding.filterIcon.isVisible = false
        binding.listView.isVisible = false
        binding.listNoDataView.isVisible = false
        binding.titleCountText.text = LocaleString.loading
        FrameworkTool.fetchAppListData(context = this, appFilters) {
            listData.clear()
            Thread {
                it.takeIf { e -> e.isNotEmpty() }?.forEach { e ->
                    listData.add(e)
                    e.icon = appIcon(e.packageName)
                }
                runOnUiThread {
                    onChanged?.invoke()
                    binding.listView.post { binding.listView.setSelection(0) }
                    binding.listProgressView.isVisible = false
                    binding.filterIcon.isVisible = true
                    binding.listView.isVisible = listData.isNotEmpty()
                    binding.listNoDataView.isVisible = listData.isEmpty()
                    binding.titleCountText.text = LocaleString.resultCount(listData.size)
                }
            }.start()
        }
    }
}