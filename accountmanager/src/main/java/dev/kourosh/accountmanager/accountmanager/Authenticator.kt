package dev.kourosh.accountmanager.accountmanager

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.app.Service
import android.content.Intent
import android.os.Bundle
import dev.kourosh.basedomain.logD
import dev.kourosh.basedomain.logE

open class Authenticator(private val service: Service, private val loginClass: Class<*>,val accountType: String , private val customIntent: Intent? = null,val authTokenType :String= "FullAccess") : AbstractAccountAuthenticator(service) {
  override fun addAccount(
    response: AccountAuthenticatorResponse?, accountType: String?, authTokenType: String?, requiredFeatures: Array<out String>?, options: Bundle?): Bundle {
    val intent = Intent(service, loginClass)
    intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
    logD(intent.extras ?: "")
    val bundle = Bundle()
    bundle.putParcelable(AccountManager.KEY_INTENT, intent)
    return Bundle.EMPTY
  }

  override fun getAuthToken(response: AccountAuthenticatorResponse?, account: Account?, authTokenType: String?, options: Bundle?): Bundle {
    val bundle = Bundle()
    if (account != null) {

      if (this.authTokenType == authTokenType) {
        val accountManager = AccountManager.get(service)
        val authToken: String? = accountManager.peekAuthToken(account, accountType)
        if (!authToken.isNullOrEmpty()) {

          bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
          bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
          bundle.putString(AccountManager.KEY_AUTHTOKEN, authToken)

          logD("intent : $bundle")
        } else {
          logD("auth token is empty")
          val intent = Intent(service, loginClass)
          accountManager.setAuthToken(account, this.authTokenType, null)
          intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
          intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name)
          if (customIntent != null) {
            intent.putExtras(customIntent)
          }
          logD("intent :  ${intent.extras}")
          bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        }
      } else {
        logE(
            """
                              invalid authTokenType
                              account auth token name: $authTokenType
                              your auth token name: ${this.authTokenType}
                          """
        )
        bundle.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType")
      }
    }
    return bundle
  }

  override fun getAuthTokenLabel(authTokenType: String?) = "Full access e-courier account"

  override fun confirmCredentials(response: AccountAuthenticatorResponse?, account: Account?, options: Bundle?): Bundle =
      Bundle.EMPTY

  override fun updateCredentials(response: AccountAuthenticatorResponse?, account: Account?, authTokenType: String?, options: Bundle?): Bundle =
      Bundle.EMPTY

  override fun hasFeatures(response: AccountAuthenticatorResponse?, account: Account?, features: Array<out String>?): Bundle =
      Bundle.EMPTY

  override fun editProperties(response: AccountAuthenticatorResponse?, accountType: String?): Bundle =
      Bundle.EMPTY
}