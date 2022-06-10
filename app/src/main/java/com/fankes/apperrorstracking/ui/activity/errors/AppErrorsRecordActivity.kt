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
 * This file is Created by fankes on 2022/5/11.
 */
@file:Suppress("DEPRECATION", "OVERRIDE_DEPRECATION", "SetTextI18n")

package com.fankes.apperrorstracking.ui.activity.errors

import android.app.Activity
import android.content.Intent
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.AdapterContextMenuInfo
import androidx.core.view.isVisible
import com.fankes.apperrorstracking.R
import com.fankes.apperrorstracking.bean.AppErrorsInfoBean
import com.fankes.apperrorstracking.bean.AppFiltersBean
import com.fankes.apperrorstracking.databinding.ActivityAppErrorsRecordBinding
import com.fankes.apperrorstracking.databinding.AdapterAppErrorsRecordBinding
import com.fankes.apperrorstracking.databinding.DiaAppErrorsStatisticsBinding
import com.fankes.apperrorstracking.locale.LocaleString
import com.fankes.apperrorstracking.ui.activity.base.BaseActivity
import com.fankes.apperrorstracking.utils.factory.*
import com.fankes.apperrorstracking.utils.tool.FrameworkTool
import com.fankes.apperrorstracking.utils.tool.ZipFileTool
import java.io.File
import java.io.FileInputStream

class AppErrorsRecordActivity : BaseActivity<ActivityAppErrorsRecordBinding>() {

    companion object {

        /** 请求保存文件回调标识 */
        private const val WRITE_REQUEST_CODE = 0
    }

    /** 当前导出文件的路径 */
    private var outPutFilePath = ""

    /** 回调适配器改变 */
    private var onChanged: (() -> Unit)? = null

    /** 全部的 APP 异常信息 */
    private val listData = ArrayList<AppErrorsInfoBean>()

    override fun onCreate() {
        binding.titleBackIcon.setOnClickListener { onBackPressed() }
        binding.appErrorSisIcon.setOnClickListener {
            showDialog {
                title = LocaleString.notice
                progressContent = LocaleString.generatingStatistics
                noCancelable()
                FrameworkTool.fetchAppListData(context, AppFiltersBean(isContainsSystem = true)) {
                    Thread {
                        val errorsApps = listData.groupBy { it.packageName }
                            .map { it.key to it.value.size }
                            .sortedByDescending { it.second }
                            .takeIf { it.isNotEmpty() }
                        val mostAppPackageName = errorsApps?.get(0)?.first ?: ""
                        val mostErrorsType = listData.groupBy { it.exceptionClassName }
                            .map { it.key to it.value.size }
                            .sortedByDescending { it.second }
                            .takeIf { it.isNotEmpty() }?.get(0)?.first?.simpleThwName() ?: ""
                        val pptCount = (((errorsApps?.size?.toFloat() ?: 0f) * 100f) / it.size.toFloat()).decimal()
                        runOnUiThread {
                            cancel()
                            showDialog<DiaAppErrorsStatisticsBinding> {
                                title = LocaleString.appErrorsStatistics
                                binding.totalErrorsUnitText.text = LocaleString.totalErrorsUnit(listData.size)
                                binding.totalAppsUnitText.text = LocaleString.totalAppsUnit(it.size)
                                binding.mostErrorsAppIcon.setImageDrawable(appIcon(mostAppPackageName))
                                binding.mostErrorsAppText.text = appName(mostAppPackageName)
                                binding.mostErrorsTypeText.text = mostErrorsType
                                binding.totalPptOfErrorsText.text = "$pptCount%"
                                confirmButton(LocaleString.gotIt)
                            }
                        }
                    }.start()
                }
            }
        }
        binding.clearAllIcon.setOnClickListener {
            showDialog {
                title = LocaleString.notice
                msg = LocaleString.areYouSureClearErrors
                confirmButton {
                    FrameworkTool.clearAppErrorsInfoData(context) {
                        refreshData()
                        toast(LocaleString.allErrorsClearSuccess)
                    }
                }
                cancelButton()
            }
        }
        binding.exportAllIcon.setOnClickListener {
            showDialog {
                title = LocaleString.notice
                msg = LocaleString.areYouSureExportAllErrors
                confirmButton { exportAll() }
                cancelButton()
            }
        }
        /** 设置列表元素和 Adapter */
        binding.listView.apply {
            bindAdapter {
                onBindDatas { listData }
                onBindViews<AdapterAppErrorsRecordBinding> { binding, position ->
                    listData[position].also { bean ->
                        binding.appIcon.setImageDrawable(appIcon(bean.packageName))
                        binding.appNameText.text = appName(bean.packageName)
                        binding.errorsTimeText.text = bean.crossTime
                        binding.errorTypeIcon.setImageResource(if (bean.isNativeCrash) R.drawable.ic_cpp else R.drawable.ic_java)
                        binding.errorTypeText.text = if (bean.isNativeCrash) "Native crash" else bean.exceptionClassName.simpleThwName()
                        binding.errorMsgText.text = bean.exceptionMessage
                    }
                }
            }.apply { onChanged = { notifyDataSetChanged() } }
            registerForContextMenu(this)
            setOnItemClickListener { _, _, p, _ -> AppErrorsDetailActivity.start(context, listData[p]) }
        }
    }

    /** 更新列表数据 */
    private fun refreshData() {
        FrameworkTool.fetchAppErrorsInfoData(context = this) {
            listData.clear()
            it.takeIf { e -> e.isNotEmpty() }?.forEach { e -> listData.add(e) }
            onChanged?.invoke()
            binding.appErrorSisIcon.isVisible = listData.isNotEmpty()
            binding.clearAllIcon.isVisible = listData.isNotEmpty()
            binding.exportAllIcon.isVisible = listData.isNotEmpty()
            binding.listView.isVisible = listData.isNotEmpty()
            binding.listNoDataView.isVisible = listData.isEmpty()
        }
    }

    /** 打包导出全部 */
    private fun exportAll() {
        clearAllExportTemp()
        ("${cacheDir.absolutePath}/temp").also { path ->
            File(path).mkdirs()
            listData.takeIf { it.isNotEmpty() }?.forEach {
                File("$path/${it.packageName}_${it.timestamp}.log").writeText(it.stackOutputFileContent)
            }
            outPutFilePath = "${cacheDir.absolutePath}/temp_${System.currentTimeMillis()}.zip"
            ZipFileTool.zipMultiFile(path, outPutFilePath)
            runCatching {
                startActivityForResult(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/application"
                    putExtra(Intent.EXTRA_TITLE, "app_errors_info_${System.currentTimeMillis()}.zip")
                }, WRITE_REQUEST_CODE)
            }.onFailure { toast(msg = "Start Android SAF failed") }
        }
    }

    /** 清空导出的临时文件 */
    private fun clearAllExportTemp() {
        cacheDir.deleteRecursively()
        cacheDir.mkdirs()
    }

    /**
     * 获取异常的精简名称
     * @return [String]
     */
    private fun String.simpleThwName() =
        let { text -> if (text.contains(other = ".")) text.split(".").let { e -> e[e.lastIndex] } else text }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        menuInflater.inflate(R.menu.menu_list_detail_action, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.menuInfo is AdapterContextMenuInfo)
            (item.menuInfo as? AdapterContextMenuInfo?)?.also {
                when (item.itemId) {
                    R.id.aerrors_view_detail -> AppErrorsDetailActivity.start(context = this, listData[it.position])
                    R.id.aerrors_app_info -> openSelfSetting(listData[it.position].packageName)
                    R.id.aerrors_remove_record ->
                        showDialog {
                            title = LocaleString.notice
                            msg = LocaleString.areYouSureRemoveRecord
                            confirmButton { FrameworkTool.removeAppErrorsInfoData(context, listData[it.position]) { refreshData() } }
                            cancelButton()
                        }
                }
            }
        return super.onContextItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) runCatching {
            data?.data?.let {
                contentResolver?.openOutputStream(it)?.apply { write(FileInputStream(outPutFilePath).readBytes()) }?.close()
                clearAllExportTemp()
                toast(LocaleString.exportAllErrorsSuccess)
            } ?: toast(LocaleString.exportAllErrorsFail)
        }.onFailure { toast(LocaleString.exportAllErrorsFail) }
    }

    override fun onResume() {
        super.onResume()
        /** 执行更新 */
        refreshData()
    }
}