package dev.kourosh.basedomain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

fun currentDateTimeIso8601(withSecond: Boolean = false, spacer: String = "T") = SimpleDateFormat(
    if (withSecond)
        "yyyy-MM-dd HH:mm:ss"
    else
        "yyyy-MM-dd HH:mm", Locale.ENGLISH
).format(Date(System.currentTimeMillis()))!!.replace(" ", spacer)

fun String?.emptyToNull(): String? {
    return if (isNullOrBlank() || isNullOrEmpty())
        null
    else {
        this
    }
}

val uuid: String
    get() = UUID.randomUUID()!!.toString()
