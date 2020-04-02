package dev.kourosh.basedomain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


sealed class Result<out T : Any> {

    class Success<T : Any>(val data: T) : Result<T>()
    class Error(val message: String, val errorCode: ErrorCode) : Result<Nothing>()

    suspend fun parseOnMain(
        success: (data: T) -> Unit,
        error: (message: String, errorCode: ErrorCode) -> Unit
    ) {
        when (this) {
            is Success -> {
                withContext(Dispatchers.Main) {
                    success(data)
                }
            }
            is Error -> {
                log()
                withContext(Dispatchers.Main) {
                    error(message, errorCode)
                }
            }
        }
    }

    suspend fun parseWithoutErrorOnMain(success: (data: T) -> Unit) {
        when (this) {
            is Success -> {
                withContext(Dispatchers.Main) {
                    success(data)
                }
            }
            is Error -> {
                log()
            }
        }
    }

    inline fun parse(
        success: (data: T) -> Unit,
        error: (message: String, errorCode: ErrorCode) -> Unit
    ) {
        when (this) {
            is Success -> {
                success(data)
            }
            is Error -> {
                log()
                error(message, errorCode)
            }
        }
    }

    inline fun parseWithoutError(success: (data: T) -> Unit) {
        when (this) {
            is Success -> {
                success(data)
            }
            is Error -> {
                log()
            }
        }
    }

    inline fun <R : Any> substitute(
        success: (data: T) -> Result<R>,
        error: (message: String, errorCode: ErrorCode) -> Result<R>
    ): Result<R> {
        return when (this) {
            is Success -> {
                success(data)
            }
            is Error -> {
                log()
                error(message, errorCode)
            }
        }
    }


    inline fun <B : Any> map(mapping: (T) -> B): Result<B> = when (this) {
        is Success -> Success(mapping(data))
        is Error -> this
    }

    inline fun <B : Any> bind(mapping: (T) -> Result<B>): Result<B> =
        when (this) {
            is Success -> mapping(data)
            is Error -> {
                log()
                this
            }
        }


    fun orNull(): T? =
        when (this) {
            is Success -> data
            is Error -> null
        }

    fun Error.log() {
        logE(
            """message: [${message}]
                        |statusCode: [${errorCode}
                        """.trimMargin()
        )
    }




}

enum class ErrorCode {
    HTTP_NOT_FOUND,
    HTTP_INTERNAL_ERROR,
    DB_ERROR,
    SERVICE_CALL_ERROR,
    UNKNOWN,
    TOKEN_EXPIRED,
    NETWORK_ERROR,
    SERVER_ERROR,
    UNAUTHORIZED,
    REFRESH_TOKEN_ERROR,
    UNAVAILABLE_ACCOUNT;
}

fun <T : Any> Result<T>.bindFailure(mapping: (message: String, errorCode: ErrorCode) -> Result<T>): Result<T> =
    when (this) {
        is Result.Success -> this
        is Result.Error -> mapping(message, errorCode)
    }
suspend fun <T : Any> Result<T>.whenUnAuthorized(unAuthorized: suspend (Result.Error) -> Result<T>): Result<T> {
    return when (val result = this) {
        is Result.Success -> {
            this
        }
        is Result.Error -> {
            result.log()
            when (result.errorCode) {
                ErrorCode.REFRESH_TOKEN_ERROR, ErrorCode.UNAUTHORIZED, ErrorCode.TOKEN_EXPIRED, ErrorCode.UNAVAILABLE_ACCOUNT -> {
                    unAuthorized(result)
                }
                else -> {
                    result
                }
            }

        }
    }
}