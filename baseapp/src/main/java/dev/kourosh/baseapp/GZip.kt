package dev.kourosh.baseapp

import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object GZip {
    fun compress(str: String): ByteArray? {
        if (str.isEmpty()) {
            return null
        }
        val obj = ByteArrayOutputStream()
        val gzip = GZIPOutputStream(obj)
        gzip.write(str.toByteArray(Charsets.UTF_8))
        gzip.close()

        return obj.toByteArray()
    }

    fun decompress(str: ByteArray): String? {
        return try {
            val gis = GZIPInputStream(ByteArrayInputStream(str))
            val bf = BufferedReader(InputStreamReader(gis, Charsets.UTF_8))
            var outStr = ""
            var line: String? = bf.readLine()
            while (line != null) {
                outStr += line
                line = bf.readLine()
            }
            println("Output String length : " + outStr.length)
            outStr
        } catch (e: Exception) {
            null
        }
    }
}