package dev.kourosh.accountmanager.accountmanager

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.os.Bundle
import dev.kourosh.accountmanager.UserDataKeys
import dev.kourosh.basedomain.ErrorCode
import dev.kourosh.basedomain.Result
import kotlinx.coroutines.CompletableDeferred

class AuthenticationCRUD(context: Context,private val accountType: String,private val authTokenType: String = "FullAccess") {
    private val accountManager = AccountManager.get(context)!!

    fun createOrUpdateAccount(
        username: String,
        password: String?,
        token: String,
        userData: HashMap<UserDataKeys, String>
    ) {
        if (username.isEmpty()) {
            throw IllegalArgumentException("$username not available ")
        }
        val account = Account(username, accountType)
        if (isAvailableAccount(username)) {
            updateUserData(account, userData)
        } else {
            accountManager.addAccountExplicitly(account, password, userData.toBundle())
        }
        accountManager.setAuthToken(account, accountType, token)
    }

    fun getAccount(username: String): Account? {
        if (username.isEmpty()) {
            throw IllegalArgumentException("$username not available ")
        }
        return Account(username, accountType)
    }

    private fun getAllAccounts(): Array<Account> = accountManager.getAccountsByType(accountType)

    fun isAvailableAccount(username: String): Boolean {
        if (username.isEmpty()) {
            throw IllegalArgumentException("$username not available ")
        }
        for (account in getAllAccounts()) {
            if (account.name == username) {
                return true
            }
        }
        return false
    }

    fun isAccountValid(username: String, password: String) =
        getAccount(username) != null && accountManager.getPassword(getAccount(username)!!) == password

    private fun HashMap<UserDataKeys, String>.toBundle(): Bundle {
        val bundle = Bundle()
        for ((key, value) in this) {
            bundle.putString(key.name, value)
        }
        return bundle
    }

    private fun updateToken(account: Account, token: String) {
        accountManager.setAuthToken(account, authTokenType, token)
    }

    private fun updateToken(userName: String, token: String) {
        val account = getAccount(userName)
        accountManager.setAuthToken(account, authTokenType, token)
    }

    private fun updateUserData(account: Account, userData: Map<UserDataKeys, String>) {
        for ((key, value) in userData) {
            if (key == UserDataKeys.ACCESS_TOKEN) updateToken(account, value)

            accountManager.setUserData(account, key.name, value)
        }
    }

    fun updateUserData(userName: String, userData: Map<UserDataKeys, String>) {
        updateUserData(getAccount(userName)!!, userData)
    }

    fun getUserData(account: Account, key: UserDataKeys) =
        accountManager.getUserData(account, key.name)

    fun getUserData(userName: String, key: UserDataKeys) = getUserData(getAccount(userName)!!, key)

    suspend fun getToken(username: String): Result<String> {
        val account = getAccount(username)

        if (account == null) {
            return Result.Error("اکانت موجود نیست", ErrorCode.UNAVAILABLE_ACCOUNT)
        } else {

            if (isTimeOut(getUserData(username, UserDataKeys.EXPIRE_IN))) {
                return Result.Error("توکن منقضی شده", ErrorCode.TOKEN_EXPIRED)
            } else {
                val response = CompletableDeferred<Result<String>>()
                accountManager.getAuthToken(getAccount(username)!!,authTokenType,null,null,{ future ->if (future.isDone && !future.isCancelled) {
                            try {
                                val bundle = future.result
                                val authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN, null)
                                if (!authToken.isNullOrEmpty()) {
                                    response.complete(Result.Success(authToken))
                                } else {
                                    response.complete(
                                        Result.Error(
                                            "خطا در دریافت توکن",
                                            ErrorCode.UNAUTHORIZED
                                        )
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                response.complete(
                                    Result.Error(
                                        "خطا در دریافت توکن",
                                        ErrorCode.UNAUTHORIZED
                                    )
                                )
                            }
                        } else {
                            response.complete(
                                Result.Error(
                                    "خطا در دریافت توکن",
                                    ErrorCode.UNAUTHORIZED
                                )
                            )
                        }
                    },
                    null
                )
                return response.await()
            }
        }
    }

    /*fun getToken(userName: String) = Single.create(SingleOnSubscribe<String> {
        val account = getAccount(userName)

        val callback = AccountManagerCallback<Bundle?> { future ->
            try {
                val bundle = future.result
                val authToken = bundle!!.getString(AccountManager.KEY_AUTHTOKEN, null)
                if (account == null || account.isUnavailable())
                    it.onError(NullPointerException("null account"))
                else if (isTimeOut(getUserData(account, UserDataKeys.EXPIRE_IN)))
                    it.onError(TokenExpireException(getUserData(userName, UserDataKeys.REFRESH_TOKEN)))
                else
                    it.onSuccess(authToken)
            } catch (e: Exception) {
                it.onError(e)
            }
        }
        accountManager.getAuthToken(account, authTokenType, null, null, callback, null)
    })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!*/

    private fun isTimeOut(timeOut: String) = timeOut.toLong() <= (System.currentTimeMillis())

    private fun Account.isUnavailable(): Boolean {
        for (account in accountManager.accounts) {
            if (account.name == this.name) return false
        }
        return true
    }

    fun invalidToken(userName: String) {
        val account = getAccount(userName)
        if (account != null)
            updateUserData(userName, hashMapOf(UserDataKeys.EXPIRE_IN to "0"))

    }
}