package dev.kourosh.baseapp

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.snackbar.Snackbar
import dev.kourosh.baseapp.enums.MessageType
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.calligraphy3.CalligraphyUtils
import io.github.inflationx.viewpump.ViewPump
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

fun succeededTone() {
	ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100).startTone(ToneGenerator.TONE_PROP_ACK, 200)
}

fun failedTone() {
	ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100).startTone(ToneGenerator.TONE_PROP_NACK, 200)
}

fun Context.vibrate(millisecond: Long = 200) {
	val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
	if(VERSION.SDK_INT >= VERSION_CODES.O) {
		vibrator.vibrate(VibrationEffect.createOneShot(millisecond, VibrationEffect.DEFAULT_AMPLITUDE))
	} else {
		vibrator.vibrate(millisecond)
	}
}

fun showSnackBar(view: View, context: Context, message: String, type: MessageType, duration: Int = Snackbar.LENGTH_LONG) {
	val snackBar = Snackbar.make(view, message, duration)
	snackBar.view.setBackgroundColor(ContextCompat.getColor(context, type.backgroundColor))
	val tv = snackBar.view.findViewById(R.id.snackbar_text) as TextView
	tv.setTextColor(ContextCompat.getColor(context, type.textColor))
	CalligraphyUtils.applyFontToTextView(context, tv, "fonts/isM.ttf")
	ViewCompat.setLayoutDirection(snackBar.view, ViewCompat.LAYOUT_DIRECTION_RTL)
	snackBar.show()
}
object GZip {
	fun compress(str: String): ByteArray? {
		if(str.isEmpty()) {
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
			while(line != null) {
				outStr += line
				line = bf.readLine()
			}
			println("Output String length : "+outStr.length)
			outStr
		} catch(e: Exception) {
			null
		}
	}
}

fun Application.initApp(){
	ViewPump.init(
		ViewPump.builder().addInterceptor(
			CalligraphyInterceptor(
				CalligraphyConfig.Builder().setDefaultFontPath(
					getString(R.string.isRegular)
				).setFontAttrId(R.attr.fontPath).build()
			)
		).build()
	)
}