package dev.kourosh.basedomain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext

sealed class Result<out T : Any> {
    class Success<out T : Any>(val data: T) : Result<T>()
    class Error(val message: String, val errorCode: ErrorCode) : Result<Nothing>()
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
