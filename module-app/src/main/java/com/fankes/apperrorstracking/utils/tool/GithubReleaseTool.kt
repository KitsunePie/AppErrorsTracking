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
 * This file is created by fankes on 2023/1/23.
 */
package com.fankes.apperrorstracking.utils.tool

import android.app.Activity
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.TimeZone
import com.fankes.apperrorstracking.locale.locale
import com.fankes.apperrorstracking.utils.factory.openBrowser
import com.fankes.apperrorstracking.utils.factory.showDialog
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.io.Serializable
import java.util.Locale

/**
 * 获取 GitHub Release 最新版本工具类
 */
object GithubReleaseTool {

    /** 仓库作者 */
    private const val REPO_AUTHOR = "KitsunePie"

    /** 仓库名称 */
    private const val REPO_NAME = "AppErrorsTracking"

    /**
     * 获取最新版本信息
     * @param context 实例
     * @param version 当前版本
     * @param result 成功后回调 - ([String] 最新版本,[Function] 更新对话框方法体)
     */
    fun checkingForUpdate(context: Context, version: String, result: (String, () -> Unit) -> Unit) = runCatching {
        OkHttpClient().newBuilder().build().newCall(
            Request.Builder()
                .url("https://api.github.com/repos/$REPO_AUTHOR/$REPO_NAME/releases/latest")
                .get()
                .build()
        ).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) = runCatching {
                JSONObject(response.body.string()).apply {
                    GithubReleaseBean(
                        name = getString("name"),
                        htmlUrl = getString("html_url"),
                        content = getString("body"),
                        date = getString("published_at").localTime()
                    ).apply {
                        fun showUpdate() = context.showDialog {
                            title = locale.latestVersion(name)
                            msg = locale.latestVersionTip(date, content)
                            confirmButton(locale.updateNow) { context.openBrowser(htmlUrl) }
                            cancelButton()
                        }
                        if (name != version) (context as? Activity?)?.runOnUiThread {
                            showUpdate()
                            result(name) { showUpdate() }
                        }
                    }
                }
            }.getOrNull().let {}
        })
    }.getOrNull().let {}

    /**
     * 格式化时间为本地时区
     * @return [String] 本地时区时间
     */
    private fun String.localTime() = replace("T", " ").replace("Z", "").let {
        runCatching {
            val local = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ROOT).apply { timeZone = Calendar.getInstance().timeZone }
            val current = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ROOT).apply { timeZone = TimeZone.getTimeZone("GMT") }
            local.format(current.parse(it))
        }.getOrNull() ?: it
    }

    /**
     * GitHub Release bean
     * @param name 版本名称
     * @param htmlUrl 网页地址
     * @param content 更新日志
     * @param date 发布时间
     */
    private data class GithubReleaseBean(
        var name: String,
        var htmlUrl: String,
        var content: String,
        var date: String
    ) : Serializable
}