package dev.kourosh.accountmanager.datamodels


data class Token(
        val accessToken: String = "",
        val tokenType: String = "",
        val refreshToken: String = "",
        val expiresIn: Int = 0,
        val scope: String?,
        val jti: String?
)