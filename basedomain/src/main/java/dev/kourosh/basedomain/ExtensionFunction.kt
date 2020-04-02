package dev.kourosh.basedomain

import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


fun logV(any: Any) = L.v(any.toString())
fun logD(any: Any) = L.d(any.toString())
fun logI(any: Any) = L.i(any.toString())
fun logW(any: Any) = L.w(any.toString())
fun logE(any: Any) = L.e(any.toString())

fun launchIO(block: suspend CoroutineScope.() -> Unit) {
    GlobalScope.launch(Dispatchers.IO) {
        block(this)
    }
}

fun currentIso8601() =
    SimpleDateFormat(
        "yyyy/MM/dd HH:mm",
        Locale.getDefault()
    ).format(Date(System.currentTimeMillis()))!!

fun generateUUID() = UUID.randomUUID()!!.toString()

