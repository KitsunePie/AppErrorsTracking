/*
 * AppErrorsTracking - Added more features to app's crash dialog, fixed custom rom deleted dialog, the best experience to Android developer.
 * Copyright (C) 2017 Fankes Studio(qzmmcn@163.com)
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
@file:Suppress("DEPRECATION")

package com.fankes.apperrorstracking.utils.factory

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.viewbinding.ViewBinding
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.LayoutInflaterClass

/**
 * 绑定 [BaseAdapter] 到 [ListView]
 * @param initiate 方法体
 * @return [BaseAdapter]
 */
inline fun ListView.bindAdapter(initiate: BaseAdapterCreater.() -> Unit) =
    BaseAdapterCreater(context).apply(initiate).baseAdapter?.apply { adapter = this } ?: error("BaseAdapter not binded")

/**
 * [BaseAdapter] 创建类
 * @param context 实例
 */
class BaseAdapterCreater(val context: Context) {

    /** 当前 [List] 回调 */
    var listDataCallback: (() -> List<*>)? = null

    /** 当前 [BaseAdapter] */
    var baseAdapter: BaseAdapter? = null

    /**
     * 绑定 [List] 到 [ListView]
     * @param result 回调数据
     */
    fun onBindDatas(result: (() -> List<*>)) {
        listDataCallback = result
    }

    /**
     * 绑定 [BaseAdapter] 到 [ListView]
     * @param bindViews 回调 - ([VB] 每项,[Int] 下标)
     */
    inline fun <reified VB : ViewBinding> onBindViews(crossinline bindViews: (binding: VB, position: Int) -> Unit) {
        baseAdapter = object : BaseAdapter() {
            override fun getCount() = listDataCallback?.let { it() }?.size ?: 0
            override fun getItem(position: Int) = listDataCallback?.let { it() }?.get(position)
            override fun getItemId(position: Int) = position.toLong()
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                var holderView = convertView
                val holder: VB
                if (convertView == null) {
                    holder = VB::class.java.method {
                        name = "inflate"
                        param(LayoutInflaterClass)
                    }.get().invoke<VB>(LayoutInflater.from(context)) ?: error("ViewHolder binding failed")
                    holderView = holder.root.apply { tag = holder }
                } else holder = convertView.tag as VB
                bindViews(holder, position)
                return holderView ?: error("ViewHolder binding failed")
            }
        }
    }
}