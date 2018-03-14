package co.enoobong.rave.flutterwave.util

import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * @author Ibanga Enoobong I
 * @since 3/9/18.
 */

@Throws(Exception::class)
private fun String.toMd5(): String {
    val md = MessageDigest.getInstance("MD5")
    val array = md.digest(this.toByteArray())
    val sb = StringBuilder()
    for (i in array.indices) {
        sb.append(Integer.toHexString((array[i].toInt() and 0xFF) or 0x100).substring(1, 3))
    }
    return sb.toString()
}

private const val TARGET = "FLWSECK-"

/**
 * Encrypt a secret key using MD5 Algorithm.
 *
 * Key should begin with FLWSECK-
 */
fun encryptSecretKey(secretKey: String): String {
    val md5Hash = secretKey.toMd5()
    val cleanSecret = secretKey.replace(TARGET, "")
    val hashLength = md5Hash.length
    return cleanSecret.substring(0, 12) + md5Hash.substring(hashLength - 12, hashLength)
}

/**
 * function to encrypt request using 3DES
 *
 * @param unEncryptedData this is the data containing sensitive information to be encrypted
 *
 * @param encryptedSecretKey this is your encrypted secret key {@link #encryptSecretKey}
 */
@Throws(Exception::class)
fun encryptRequest(unEncryptedData: String, encryptedSecretKey: String): String {
    val keyBytes = encryptedSecretKey.toByteArray()
    val secretKeySpec = SecretKeySpec(keyBytes, "DESede")
    val cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding")

    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
    val plainTextBytes = unEncryptedData.toByteArray()
    val buf = cipher.doFinal(plainTextBytes)
    return Base64.getEncoder().encodeToString(buf).trim().replace("\\n", "")
}
