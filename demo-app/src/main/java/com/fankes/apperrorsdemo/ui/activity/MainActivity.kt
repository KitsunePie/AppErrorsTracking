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
 * This file is Created by fankes on 2022/5/10.
 */
package com.fankes.apperrorsdemo.ui.activity

import android.content.Intent
import android.os.SystemClock
import com.fankes.apperrorsdemo.databinding.ActivityMainBinding
import com.fankes.apperrorsdemo.databinding.ActivityMultiProcessBinding
import com.fankes.apperrorsdemo.native.Channel
import com.fankes.apperrorsdemo.ui.activity.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun onCreate() {
        binding.titleBackIcon.setOnClickListener { onBackPressed() }
        binding.throwRuntimeButton.setOnClickListener { Channel.throwRuntimeException() }
        binding.throwIllegalStateButton.setOnClickListener { Channel.throwIllegalStateException() }
        binding.throwNullPointerButton.setOnClickListener { Channel.throwNullPointerException() }
        binding.throwExceptionButton.setOnClickListener { Channel.throwException() }
        binding.throwNativeErrorButton.setOnClickListener { Channel.throwNativeException() }
        binding.throwMultiProcessErrorButton.setOnClickListener { startActivity(Intent(this, MultiProcessActivity::class.java)) }
    }

    class MultiProcessActivity : BaseActivity<ActivityMultiProcessBinding>() {

        override fun onCreate() {
            Thread {
                SystemClock.sleep(600)
                error("Throw in multi-process")
            }.start()
        }
    }
}