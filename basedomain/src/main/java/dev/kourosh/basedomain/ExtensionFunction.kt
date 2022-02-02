package dev.kourosh.basedomain

import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

fun logV(any: Any) = L.v(any.toString())
fun logD(any: Any) = L.d(any.toString())
fun logI(any: Any) = L.i(any.toString())
fun logW(any: Any) = L.w(any.toString())
fun logE(any: Any) = L.e(any.toString())
val globalScope= CoroutineScope(Executors.newFixedThreadPool(5).asCoroutineDispatcher())
fun CoroutineScope.launchIO(block: suspend CoroutineScope.() -> Unit) {
    launch(Dispatchers.IO) {
        block(this)
    }
}

fun currentDateTimeIso8601(withSecond: Boolean = false, spacer: String = "T") = SimpleDateFormat(
    if (withSecond)
        "yyyy-MM-dd HH:mm:ss"
    else
        "yyyy-MM-dd HH:mm", Locale.ENGLISH
).format(Date(System.currentTimeMillis())).replace(" ", spacer)

fun String?.emptyToNull(): String? {
    return if (isNullOrBlank() || isNullOrEmpty())
        null
    else {
        this
    }
}

val uuid: String
    get() = UUID.randomUUID()!!.toString()
