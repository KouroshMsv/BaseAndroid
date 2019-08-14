package dev.kourosh.basedomain


import android.util.Log

object L {

    private const val TAG = "KouroshMousavi--->"

    fun d(msg: String, tag: String = TAG) {
        Log.d(tag, msg)
    }

    fun e(msg: String, tag: String = TAG) {
        Log.e(tag, msg)
    }

    fun i(msg: String, tag: String = TAG) {
        Log.i(tag, msg)
    }

    fun v(msg: String, tag: String = TAG) {
        Log.v(tag, msg)
    }

    fun w(msg: String, tag: String = TAG) {
        Log.w(tag, msg)
    }

    fun ex(e: Throwable, tag: String = TAG) {
        Log.e(tag, "", e)
    }
}
