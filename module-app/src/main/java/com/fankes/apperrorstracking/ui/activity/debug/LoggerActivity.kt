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
 * This file is Created by fankes on 2022/10/4.
 */
@file:Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")

package com.fankes.apperrorstracking.ui.activity.debug

import android.app.Activity
import android.content.Intent
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.core.view.isVisible
import com.fankes.apperrorstracking.R
import com.fankes.apperrorstracking.const.PackageName
import com.fankes.apperrorstracking.databinding.ActivitiyLoggerBinding
import com.fankes.apperrorstracking.databinding.AdapterLoggerBinding
import com.fankes.apperrorstracking.databinding.DiaLoggerFilterBinding
import com.fankes.apperrorstracking.locale.LocaleString
import com.fankes.apperrorstracking.ui.activity.base.BaseActivity
import com.fankes.apperrorstracking.utils.factory.bindAdapter
import com.fankes.apperrorstracking.utils.factory.copyToClipboard
import com.fankes.apperrorstracking.utils.factory.showDialog
import com.fankes.apperrorstracking.utils.factory.toUtcTime
import com.fankes.apperrorstracking.utils.factory.toast
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.highcapable.yukihookapi.hook.log.YukiHookLogger
import com.highcapable.yukihookapi.hook.log.YukiLoggerData
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.text.SimpleDateFormat
import java.util.Date

class LoggerActivity : BaseActivity<ActivitiyLoggerBinding>() {

    companion object {

        /** 请求保存文件回调标识 */
        private const val WRITE_REQUEST_CODE = 0
    }

    /** 回调适配器改变 */
    private var onChanged: (() -> Unit)? = null

    /** 过滤条件 */
    private var filters = arrayListOf("D", "I", "W", "E")

    /** 全部的调试日志数据 */
    private val listData = ArrayList<YukiLoggerData>()

    override fun onCreate() {
        binding.titleBackIcon.setOnClickListener { finish() }
        binding.refreshIcon.setOnClickListener { refreshData() }
        binding.filterIcon.setOnClickListener {
            showDialog<DiaLoggerFilterBinding> {
                title = LocaleString.filterByCondition
                binding.configCheck0.isChecked = filters.any { it == "D" }
                binding.configCheck1.isChecked = filters.any { it == "I" }
                binding.configCheck2.isChecked = filters.any { it == "W" }
                binding.configCheck3.isChecked = filters.any { it == "E" }
                confirmButton {
                    filters.clear()
                    if (binding.configCheck0.isChecked) filters.add("D")
                    if (binding.configCheck1.isChecked) filters.add("I")
                    if (binding.configCheck2.isChecked) filters.add("W")
                    if (binding.configCheck3.isChecked) filters.add("E")
                    refreshData()
                }
                cancelButton()
            }
        }
        binding.exportAllIcon.setOnClickListener {
            runCatching {
                startActivityForResult(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/application"
                    putExtra(Intent.EXTRA_TITLE, "app_errors_tracking_${System.currentTimeMillis().toUtcTime()}.log")
                }, WRITE_REQUEST_CODE)
            }.onFailure { toast(msg = "Start Android SAF failed") }
        }
        /** 设置列表元素和 Adapter */
        binding.listView.apply {
            bindAdapter {
                onBindDatas { listData }
                onBindViews<AdapterLoggerBinding> { binding, position ->
                    listData[position].also { bean ->
                        binding.priorityText.apply {
                            text = bean.priority
                            setBackgroundResource(
                                when (bean.priority) {
                                    "D" -> R.drawable.bg_logger_d_round
                                    "I" -> R.drawable.bg_logger_i_round
                                    "W" -> R.drawable.bg_logger_w_round
                                    "E" -> R.drawable.bg_logger_e_round
                                    else -> R.drawable.bg_logger_d_round
                                }
                            )
                        }
                        binding.messageText.text = bean.msg.format()
                        binding.timeText.text = bean.timestamp.format()
                        binding.throwableText.isVisible = bean.throwable != null
                        binding.throwableText.text = bean.throwable?.toStackTrace() ?: ""
                    }
                }
            }.apply { onChanged = { notifyDataSetChanged() } }
            registerForContextMenu(this)
        }
    }

    /** 更新列表数据 */
    private fun refreshData() {
        dataChannel(PackageName.SYSTEM_FRAMEWORK).obtainLoggerInMemoryData {
            listData.clear()
            it.takeIf { e -> e.isNotEmpty() }?.reversed()?.filter { filters.any { e -> it.priority == e } }?.forEach { e -> listData.add(e) }
            onChanged?.invoke()
            binding.listView.post { binding.listView.setSelection(0) }
            binding.exportAllIcon.isVisible = listData.isNotEmpty()
            binding.listView.isVisible = listData.isNotEmpty()
            binding.listNoDataView.isVisible = listData.isEmpty()
            binding.listNoDataView.text = if (filters.size < 4) LocaleString.noListResult else LocaleString.noListData
        }
    }

    /**
     * 格式化为本地时间格式
     * @return [String]
     */
    private fun Long.format() = SimpleDateFormat.getDateTimeInstance().format(Date(this))

    /**
     * 格式化消息字符串样式
     * @return [String]
     */
    private fun String.format() = replace("--", "\n--")

    /**
     * 获取完整的异常堆栈内容
     * @return [String]
     */
    private fun Throwable.toStackTrace() = ByteArrayOutputStream().also { printStackTrace(PrintStream(it)) }.toString().trim()

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        menuInflater.inflate(R.menu.menu_logger_action, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.menuInfo is AdapterView.AdapterContextMenuInfo)
            (item.menuInfo as? AdapterView.AdapterContextMenuInfo?)?.also {
                if (item.itemId == R.id.logger_copy)
                    copyToClipboard(listData[it.position].let { e -> e.toString() + (e.throwable?.toStackTrace()?.let { t -> "\n$t" } ?: "") })
            }
        return super.onContextItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) runCatching {
            data?.data?.let {
                contentResolver?.openOutputStream(it)?.apply { write(YukiHookLogger.contents(listData).toByteArray()) }?.close()
                toast(LocaleString.exportAllLogsSuccess)
            } ?: toast(LocaleString.exportAllLogsFail)
        }.onFailure { toast(LocaleString.exportAllLogsFail) }
    }

    override fun onResume() {
        super.onResume()
        /** 执行更新 */
        refreshData()
    }
}