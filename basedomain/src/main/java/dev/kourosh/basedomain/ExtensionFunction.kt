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

suspend fun <T : Any> Result<T>.parseOnMain(success: (data: T) -> Unit,error: (message: String, errorCode: ErrorCode) -> Unit) {
    val result = this
    when (result) {
        is Result.Success -> {
            withContext(Dispatchers.Main) {
                success(result.data)
            }
        }
        is Result.Error -> {
            logE("""message: [${result.message}]|statusCode: [${result.errorCode}""".trimMargin()
            )
            withContext(Dispatchers.Main) {
                error(result.message, result.errorCode)
            }
        }
    }
}

inline fun <T : Any> Result<T>.parseResult(success: (data: T) -> Unit,error: (message: String, errorCode: ErrorCode) -> Unit) {
    when (val result = this) {
        is Result.Success -> {
            success(result.data)
        }
        is Result.Error -> {
            logE(
                """message: [${result.message}]
                |statusCode: [${result.errorCode}
            """.trimMargin()
            )
            error(result.message, result.errorCode)
        }
    }
}

inline fun <T : Any, R : Any> Result<T>.substitute(success: (data: T) -> Result<R>,error: (message: String,  errorCode: ErrorCode) -> Result<R>): Result<R> {
    return when (val result = this) {
        is Result.Success -> {
            success(result.data)
        }
        is Result.Error -> {
            logE(
                """message: [${result.message}]
                |statusCode: [${result.errorCode}
            """.trimMargin()
            )
            error(result.message, result.errorCode)
        }
    }
}

inline fun <T : Any, R : Any> Result<T>.whenSucceed(success: (data: T) -> Result<R>): Result<R> {
    val result = this
    return when (result) {
        is Result.Success -> {
            success(result.data)
        }
        is Result.Error -> {
            logE(
                """message: [${result.message}]
                |statusCode: [${result.errorCode}
            """.trimMargin()
            )
            result
        }
    }
}

inline fun <T : Any> Result<T>.whenFailed(error: (message: String, errorCode: ErrorCode) -> Result<T>): Result<T> {
    return when (val result = this) {
        is Result.Success -> {
            result
        }
        is Result.Error -> {
            error(result.message, result.errorCode)
        }
    }
}

inline fun <T : Any, R : Any> Result<T>.map(success: (data: T) -> R): Result<R> {
    val result = this
    return when (result) {
        is Result.Success -> {
            Result.Success(success(result.data))
        }
        is Result.Error -> {
            result
        }
    }
}

fun currentIso8601() =
    SimpleDateFormat(
        "yyyy/MM/dd HH:mm",
        Locale.getDefault()
    ).format(Date(System.currentTimeMillis()))!!

fun generateUUID() = UUID.randomUUID()!!.toString()

