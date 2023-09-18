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
 * This file is created by fankes on 2022/5/13.
 */
@file:Suppress("unused")

package com.fankes.apperrorstracking.utils.tool

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * 处理压缩文件
 */
object ZipFileTool {

    private const val BUFF_SIZE = 2048

    /**
     * 压缩整个文件夹中的所有文件 - 生成指定名称的 Zip 压缩包
     * @param filePath 文件所在目录
     * @param zipPath 压缩后 Zip 文件名称
     * @param isDirFlag Zip 文件中第一层是否包含一级目录 - true 包含 false没有
     */
    fun zipMultiFile(filePath: String, zipPath: String, isDirFlag: Boolean = false) {
        runCatching {
            val file = File(filePath)
            val zipFile = File(zipPath)
            val zipOut = ZipOutputStream(FileOutputStream(zipFile))
            if (file.isDirectory) {
                val files = file.listFiles() ?: emptyArray()
                for (fileSec in files)
                    if (isDirFlag) recursionZip(zipOut, fileSec, file.name + File.separator)
                    else recursionZip(zipOut, fileSec, "")
            }
            zipOut.close()
        }
    }

    /**
     * 解压文件
     * @param unZipPath 解压后的目录
     * @param zipPath 压缩文件目录
     */
    fun unZipFile(unZipPath: String, zipPath: String) {
        runCatching {
            unZipFileByInput(unZipPath, FileInputStream(zipPath))
        }
    }

    /**
     * 解压文件
     * @param unZipPath 解压后的目录
     * @param zips 压缩文件流
     */
    private fun unZipFileByInput(unZipPath: String, zips: FileInputStream) {
        val path = createSeparator(unZipPath)
        var bos: BufferedOutputStream? = null
        var zis: ZipInputStream? = null
        try {
            var filename: String
            zis = ZipInputStream(BufferedInputStream(zips))
            var ze: ZipEntry
            val buffer = ByteArray(BUFF_SIZE)
            var count: Int
            while (zis.nextEntry.also { ze = it } != null) {
                filename = ze.name
                createSubFolders(filename, path)
                if (ze.isDirectory) {
                    val fmd = File(path + filename)
                    fmd.mkdirs()
                    continue
                }
                bos = BufferedOutputStream(FileOutputStream(path + filename))
                while (zis.read(buffer).also { count = it } != -1) bos.write(buffer, 0, count)
                bos.flush()
                bos.close()
            }
        } catch (_: IOException) {
        } finally {
            runCatching {
                if (zis != null) {
                    zis.closeEntry()
                    zis.close()
                }
                bos?.close()
            }
        }
    }

    /** @base code */
    private fun recursionZip(zipOut: ZipOutputStream, file: File, baseDir: String) {
        if (file.isDirectory) {
            val files = file.listFiles() ?: emptyArray()
            for (fileSec in files) recursionZip(zipOut, fileSec, baseDir + file.name + File.separator)
        } else {
            val buf = ByteArray(1024)
            val input: InputStream = FileInputStream(file)
            zipOut.putNextEntry(ZipEntry(baseDir + file.name))
            var len: Int
            while (input.read(buf).also { len = it } != -1) {
                zipOut.write(buf, 0, len)
            }
            input.close()
        }
    }

    /** @base code */
    private fun createSubFolders(filename: String, path: String) {
        val subFolders = filename.split("/").toTypedArray()
        if (subFolders.size <= 1) return
        var pathNow = path
        for (i in 0 until subFolders.size - 1) {
            pathNow = pathNow + subFolders[i] + "/"
            val fmd = File(pathNow)
            if (fmd.exists()) continue
            fmd.mkdirs()
        }
    }

    /** @base code */
    private fun createSeparator(path: String): String {
        val dir = File(path)
        if (!dir.exists()) dir.mkdirs()
        return if (path.endsWith("/")) path else "$path/"
    }
}