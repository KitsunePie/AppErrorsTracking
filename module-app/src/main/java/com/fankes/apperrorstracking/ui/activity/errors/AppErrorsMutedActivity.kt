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
 * This file is created by fankes on 2022/6/3.
 */
package com.fankes.apperrorstracking.ui.activity.errors

import androidx.core.view.isVisible
import com.fankes.apperrorstracking.bean.MutedErrorsAppBean
import com.fankes.apperrorstracking.databinding.ActivityAppErrorsMutedBinding
import com.fankes.apperrorstracking.databinding.AdapterAppErrorsMutedBinding
import com.fankes.apperrorstracking.locale.LocaleString
import com.fankes.apperrorstracking.ui.activity.base.BaseActivity
import com.fankes.apperrorstracking.utils.factory.appIconOf
import com.fankes.apperrorstracking.utils.factory.appNameOf
import com.fankes.apperrorstracking.utils.factory.bindAdapter
import com.fankes.apperrorstracking.utils.factory.showDialog
import com.fankes.apperrorstracking.utils.tool.FrameworkTool

class AppErrorsMutedActivity : BaseActivity<ActivityAppErrorsMutedBinding>() {

    /** 回调适配器改变 */
    private var onChanged: (() -> Unit)? = null

    /** 全部的已忽略异常的 APP 信息 */
    private val listData = ArrayList<MutedErrorsAppBean>()

    override fun onCreate() {
        binding.titleBackIcon.setOnClickListener { onBackPressed() }
        binding.unmuteAllIcon.setOnClickListener {
            showDialog {
                title = LocaleString.notice
                msg = LocaleString.areYouSureUnmuteAll
                confirmButton { FrameworkTool.unmuteAllErrorsApps(context) { refreshData() } }
                cancelButton()
            }
        }
        /** 设置列表元素和 Adapter */
        binding.listView.bindAdapter {
            onBindDatas { listData }
            onBindViews<AdapterAppErrorsMutedBinding> { binding, position ->
                listData[position].also { bean ->
                    binding.appIcon.setImageDrawable(appIconOf(bean.packageName))
                    binding.appNameText.text = appNameOf(bean.packageName).ifBlank { bean.packageName }
                    binding.muteTypeText.text = when (bean.type) {
                        MutedErrorsAppBean.MuteType.UNTIL_UNLOCKS -> LocaleString.muteIfUnlock
                        MutedErrorsAppBean.MuteType.UNTIL_REBOOTS -> LocaleString.muteIfRestart
                    }
                    binding.unmuteButton.setOnClickListener { FrameworkTool.unmuteErrorsApp(context, bean) { refreshData() } }
                }
            }
        }.apply { onChanged = { notifyDataSetChanged() } }
    }

    /** 更新列表数据 */
    private fun refreshData() {
        FrameworkTool.fetchMutedErrorsAppsData(context = this) {
            listData.clear()
            it.takeIf { e -> e.isNotEmpty() }?.forEach { e -> listData.add(e) }
            onChanged?.invoke()
            binding.unmuteAllIcon.isVisible = listData.isNotEmpty()
            binding.listView.isVisible = listData.isNotEmpty()
            binding.listNoDataView.isVisible = listData.isEmpty()
        }
    }

    override fun onResume() {
        super.onResume()
        /** 执行更新 */
        refreshData()
    }
}