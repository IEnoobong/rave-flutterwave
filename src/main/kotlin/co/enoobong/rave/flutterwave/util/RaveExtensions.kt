package co.enoobong.rave.flutterwave.util

import co.enoobong.rave.flutterwave.data.ApiResponse
import co.enoobong.rave.flutterwave.data.ErrorResponseData
import co.enoobong.rave.flutterwave.data.Payload
import co.enoobong.rave.flutterwave.service.RavePay
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import java.lang.reflect.Type
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


/**
 * @author Ibanga Enoobong I
 * @since 3/2/18.
 */

@Throws(Exception::class)
internal fun String.toMd5(): String {
    val md = MessageDigest.getInstance("MD5")
    val array = md.digest(this.toByteArray())
    val sb = StringBuffer()
    for (i in array.indices) {
        sb.append(Integer.toHexString((array[i].toInt() and 0xFF) or 0x100).substring(1, 3))
    }
    return sb.toString()
}

private const val TARGET = "FLWSECK-"

internal fun encryptSecretKey(secretKey: String): String {
    val md5Hash = secretKey.toMd5()
    val cleanSecret = secretKey.replace(TARGET, "")
    val hashLength = md5Hash.length
    return cleanSecret.substring(0, 12) + md5Hash.substring(hashLength - 12, hashLength)
}

@Throws(Exception::class)
internal fun encryptRequest(unEncryptedData: String, key: String): String {
    val keyBytes = key.toByteArray()
    val secretKeySpec = SecretKeySpec(keyBytes, "DESede")
    val cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding")

    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
    val plainTextBytes = unEncryptedData.toByteArray()
    val buf = cipher.doFinal(plainTextBytes)
    return Base64.getEncoder().encodeToString(buf).trim().replace("\\n", "")
}

internal fun <T> Response<T>.requireBody() = body()!!

internal inline fun <reified T : Payload> T.toJsonString(): String {
    return RavePay.GSON.toJson(this, toType<T>())
}

internal inline fun <reified T : Any> Gson.fromJson(json: String): T = fromJson(json, toType<T>())

internal inline fun <reified T : Any> toType(): Type {
    return object : TypeToken<T>() {}.type
}

internal const val errorParsingError = "An error occurred parsing the error response"

internal fun String?.toErrorDataResponse(): ApiResponse<ErrorResponseData> {
    this?.let {
        val type = object : TypeToken<ApiResponse<ErrorResponseData>>() {}.type
        return RavePay.GSON.fromJson(this, type)
    }
    return ApiResponse("error", errorParsingError, null)
}
