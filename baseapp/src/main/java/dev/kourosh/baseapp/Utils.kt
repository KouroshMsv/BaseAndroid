package dev.kourosh.baseapp

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import dev.kourosh.baseapp.enums.MessageType
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.calligraphy3.CalligraphyUtils
import io.github.inflationx.viewpump.ViewPump

fun generateTone(
    toneType: Int = ToneGenerator.TONE_PROP_ACK,
    duration: Int = 200,
    volume: Int = 100
) {
    ToneGenerator(AudioManager.STREAM_NOTIFICATION, volume).startTone(toneType, duration)
}


fun Context.vibrate(millisecond: Long = 200) {
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                millisecond,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    } else {
        vibrator.vibrate(millisecond)
    }
}

fun Fragment.openLink(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

fun Activity.openLink(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

fun showSnackBar(
    view: View,
    context: Context,
    message: String,
    type: MessageType,
    duration: Int = Snackbar.LENGTH_LONG
) {
    val snackBar = Snackbar.make(view, message, duration)
    snackBar.view.setBackgroundColor(ContextCompat.getColor(context, type.backgroundColor))
    val tv = snackBar.view.findViewById(R.id.snackbar_text) as TextView
    tv.setTextColor(ContextCompat.getColor(context, type.textColor))
    CalligraphyUtils.applyFontToTextView(context, tv, defaultFontPath)
    ViewCompat.setLayoutDirection(snackBar.view, ViewCompat.LAYOUT_DIRECTION_RTL)
    snackBar.show()
}
private lateinit var defaultFontPath:String

fun Application.initApp(defaultFontPath: String) {
    dev.kourosh.baseapp.defaultFontPath =defaultFontPath
    ViewPump.init(
        ViewPump.builder().addInterceptor(
            CalligraphyInterceptor(
                CalligraphyConfig.Builder().setDefaultFontPath(defaultFontPath).setFontAttrId(R.attr.fontPath).build()
            )
        ).build()
    )
}