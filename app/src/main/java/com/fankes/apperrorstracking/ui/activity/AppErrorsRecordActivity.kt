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
@file:Suppress("UNCHECKED_CAST")

package com.fankes.apperrorstracking.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.view.isVisible
import com.fankes.apperrorstracking.R
import com.fankes.apperrorstracking.bean.AppErrorsInfoBean
import com.fankes.apperrorstracking.const.Const
import com.fankes.apperrorstracking.databinding.ActivityAppErrorsRecordBinding
import com.fankes.apperrorstracking.databinding.AdapterAppErrorsRecordBinding
import com.fankes.apperrorstracking.ui.activity.base.BaseActivity
import com.fankes.apperrorstracking.utils.factory.appIcon
import com.fankes.apperrorstracking.utils.factory.appName
import com.fankes.apperrorstracking.utils.factory.toast
import java.text.SimpleDateFormat
import java.util.*

class AppErrorsRecordActivity : BaseActivity<ActivityAppErrorsRecordBinding>() {

    /** 回调适配器改变 */
    private var onChanged: (() -> Unit)? = null

    /** 全部的 APP 异常数据 */
    private val listData = ArrayList<AppErrorsInfoBean>()

    override fun onCreate() {
        binding.titleBackIcon.setOnClickListener { onBackPressed() }
        binding.clearAllIcon.setOnClickListener {
            // TODO 待实现
            toast(msg = "Coming soon")
        }
        binding.exportAllIcon.setOnClickListener {
            // TODO 待实现
            toast(msg = "Coming soon")
        }
        /** 设置列表元素和 Adapter */
        binding.listView.apply {
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
                        holder.errorsTimeText.text = SimpleDateFormat.getDateTimeInstance().format(Date(it.timestamp))
                        holder.errorTypeIcon.setImageResource(if (it.isNativeCrash) R.drawable.ic_cpp else R.drawable.ic_java)
                        holder.errorTypeText.text = if (it.isNativeCrash) "Native crash" else it.exceptionClassName.let { text ->
                            if (text.contains(other = ".")) text.split(".").let { e -> e[e.lastIndex] } else text
                        }
                        holder.errorMsgText.text = it.exceptionMessage
                    }
                    return cView!!
                }
            }.apply {
                setOnItemClickListener { _, _, p, _ -> AppErrorsDetailActivity.start(context, listData[p]) }
                onChanged = { notifyDataSetChanged() }
            }
        }
        /** 注册广播 */
        registerReceiver(moduleHandlerReceiver, IntentFilter().apply { addAction(Const.ACTION_MODULE_HANDLER_RECEIVER) })
    }

    /** 更新列表数据 */
    private fun refreshData() {
        sendBroadcast(Intent().apply {
            action = Const.ACTION_HOST_HANDLER_RECEIVER
            putExtra(Const.TYPE_APP_ERRORS_DATA_CONTROL, Const.TYPE_APP_ERRORS_DATA_CONTROL_GET_DATA)
        })
    }

    /** 模块广播接收器 */
    private val moduleHandlerReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent == null) return
                when (intent.getStringExtra(Const.TYPE_APP_ERRORS_DATA_CONTROL)) {
                    Const.TYPE_APP_ERRORS_DATA_CONTROL_GET_DATA ->
                        (intent.getSerializableExtra(Const.TAG_APP_ERRORS_DATA_CONTENT) as? ArrayList<AppErrorsInfoBean>)?.also {
                            listData.clear()
                            it.takeIf { e -> e.isNotEmpty() }?.forEach { e -> listData.add(e) }
                            onChanged?.invoke()
                            binding.listView.isVisible = listData.isNotEmpty()
                            binding.listNoDataView.isVisible = listData.isEmpty()
                        }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        /** 执行更新 */
        refreshData()
    }

    override fun onDestroy() {
        super.onDestroy()
        /** 取消注册 */
        unregisterReceiver(moduleHandlerReceiver)
    }
}