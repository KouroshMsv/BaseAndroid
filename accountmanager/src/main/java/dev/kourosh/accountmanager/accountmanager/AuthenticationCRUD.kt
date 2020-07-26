package dev.kourosh.accountmanager.accountmanager

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import dev.kourosh.accountmanager.UserDataKeys
import dev.kourosh.basedomain.ErrorCode
import dev.kourosh.basedomain.Result
import dev.kourosh.basedomain.logE

class AuthenticationCRUD(private val context: Context, private val accountType: String, private val authTokenType: String = "FullAccess") {
    private val accountManager = AccountManager.get(context)!!
    fun createOrUpdateAccount(username: String, password: String?, token: String, userData: HashMap<UserDataKeys, String>) {
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
            getAccount(username) != null && accountManager.getPassword(getAccount(username)) == password

    private fun HashMap<UserDataKeys, String>.toBundle(): Bundle {
        return Bundle().apply {
            for ((key, value) in this@toBundle) {
                putString(key.name, value)
            }
        }
    }

    private fun updateToken(account: Account, token: String) {
        accountManager.setAuthToken(account, authTokenType, token)
    }

    private fun updateToken(username: String, token: String) {
        val account = getAccount(username) ?: throw IllegalArgumentException("$username not available ")
        accountManager.setAuthToken(account, authTokenType, token)
    }

    private fun updateUserData(account: Account, userData: Map<UserDataKeys, String>) {
        for ((key, value) in userData) {
            if (key == UserDataKeys.ACCESS_TOKEN) updateToken(account, value)
            accountManager.setUserData(account, key.name, value)
        }
    }

    fun updateUserData(username: String, userData: Map<UserDataKeys, String>) {
        getAccount(username)?.apply {
            updateUserData(this, userData)
        }?: throw IllegalArgumentException("$username not available ")

    }

    fun getUserData(account: Account, key: UserDataKeys) =
            accountManager.getUserData(account, key.name)

    fun getUserData(username: String, key: UserDataKeys) =
            getAccount(username)?.run { getUserData(this, key) }

    fun getToken(username: String): Result<String> {
        val account = getAccount(username)
        return if (account == null) {
            Result.Error("اکانت موجود نیست", ErrorCode.UNAVAILABLE_ACCOUNT)
        } else {
            if (isTimeOut(getUserData(username, UserDataKeys.EXPIRE_IN))) {
                Result.Error("توکن منقضی شده", ErrorCode.TOKEN_EXPIRED)
            } else {
                val token = getUserData(account, UserDataKeys.ACCESS_TOKEN)
                if (token != null)
                    Result.Success(token)
                else
                    Result.Error("توکن منقضی شده", ErrorCode.TOKEN_EXPIRED)
            }
        }
    }

    fun deleteAccount(mobile: String) {
        val account = getAccount(mobile)
        try {
            if (account != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    accountManager.removeAccount(account, context as Activity, null, null)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    accountManager.removeAccount(account, null, null)
                }
            }
        } catch (e: Exception) {
            logE(e.message.toString())
        }
    }

    private fun isTimeOut(timeOut: String?) = timeOut?.toLong() ?: 0 <= (System.currentTimeMillis())

    fun invalidToken(username: String) {
        val account = getAccount(username)
        if (account != null)
            updateUserData(username, hashMapOf(UserDataKeys.EXPIRE_IN to "0"))

    }
}