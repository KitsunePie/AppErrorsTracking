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
@file:Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")

package com.fankes.apperrorstracking.ui.activity.errors

import android.app.Activity
import android.content.Intent
import android.view.*
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.BaseAdapter
import androidx.core.view.isVisible
import com.fankes.apperrorstracking.R
import com.fankes.apperrorstracking.bean.AppErrorsInfoBean
import com.fankes.apperrorstracking.databinding.ActivityAppErrorsRecordBinding
import com.fankes.apperrorstracking.databinding.AdapterAppErrorsRecordBinding
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
    private val listData = arrayListOf<AppErrorsInfoBean>()

    override fun onCreate() {
        binding.titleBackIcon.setOnClickListener { onBackPressed() }
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
        binding.listView.apply { // 改成 recycleview?
            adapter = object : BaseAdapter() {

                override fun getCount() = listData.size

                override fun getItem(position: Int) = listData[position]

                override fun getItemId(position: Int) = position.toLong()

                override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                    var cView = convertView
                    val holder: AdapterAppErrorsRecordBinding
                    if (convertView == null) {
                        holder = AdapterAppErrorsRecordBinding.inflate(LayoutInflater.from(context))
                        cView = holder.root
                        cView.tag = holder
                    } else holder = convertView.tag as AdapterAppErrorsRecordBinding
                    getItem(position).also {
                        holder.appIcon.setImageDrawable(appIcon(it.packageName))
                        holder.appNameText.text = appName(it.packageName)
                        holder.errorsTimeText.text = it.time
                        holder.errorTypeIcon.setImageResource(if (it.isNativeCrash) R.drawable.ic_cpp else R.drawable.ic_java)
                        holder.errorTypeText.text = if (it.isNativeCrash) "Native crash" else it.exceptionClassName.let { text ->
                            if (text.contains(other = ".")) text.split(".").let { e -> e[e.lastIndex] } else text
                        }
                        holder.errorMsgText.text = it.exceptionMessage
                    }
                    return cView!!
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
                File("$path/${it.packageName}_${it.timestamp}.log").writeText(it.stackOutputContent)
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