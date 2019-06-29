package dev.kourosh.basedomain

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Encryptor {

    fun encryptAES(
        text: String,
        secretKey: String,
        algorithm: String = "AES",
        transformation: String = "AES/CBC/PKCS5Padding",
        iv: ByteArray = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    ): String? {
        return try {
            val b64 = Base64.decode(secretKey, Base64.DEFAULT)
            val key = SecretKeySpec(b64, algorithm)
            val cipher = Cipher.getInstance(transformation)
            cipher!!.init(ENCRYPT_MODE, key, IvParameterSpec(iv))
            val encryptedData = cipher.doFinal(text.toByteArray())
            val data = String(Base64.encode(encryptedData, Base64.DEFAULT))
            data.subSequence(0, data.length - 1).toString()
        } catch (e: Exception) {
            null
        }
    }

    fun decryptAES(
        text: String,
        secretKey: String,
        algorithm: String = "AES",
        transformation: String = "AES/CBC/PKCS5Padding",
        iv: ByteArray = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    ): String? {
        return try {
            val b64 = Base64.decode(secretKey, Base64.DEFAULT)
            val key = SecretKeySpec(b64, algorithm)
            val cipher = Cipher.getInstance(transformation)
            cipher!!.init(DECRYPT_MODE, key, IvParameterSpec(iv))
            val encryptedBytes = Base64.decode(text, Base64.DEFAULT)
            val decryptedData = cipher.doFinal(encryptedBytes)
            String(decryptedData!!)
        } catch (e: Exception) {
            null
        }
    }

}