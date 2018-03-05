package co.enoobong.rave.flutterwave.util

import co.enoobong.rave.flutterwave.data.ApiResponse
import co.enoobong.rave.flutterwave.data.ErrorResponseData
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
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
fun String.toMd5(): String {
    val md = MessageDigest.getInstance("MD5")
    val array = md.digest(this.toByteArray())
    val sb = StringBuffer()
    for (i in array.indices) {
        sb.append(Integer.toHexString(((array[i].toInt() and 0xFF) or 0x100)).substring(1, 3))
    }
    return sb.toString()
}

@Throws(Exception::class)
fun encrypt(unEncryptedData: String, key: String): String {
    val keyBytes = key.toByteArray()
    val secretKeySpec = SecretKeySpec(keyBytes, "DESede")
    val cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding")

    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
    val plainTextBytes = unEncryptedData.toByteArray()
    val buf = cipher.doFinal(plainTextBytes)
    return Base64.getEncoder().encodeToString(buf).trim().replace("\\n", "")
}

fun <T> T.toJsonString(): String {
    return GsonInstance.GSON.toJson(this, toType<T>())
}

const val errorParsingError = "An error occurred parsing the error response"

fun ResponseBody?.toErrorDataResponse(): ApiResponse<ErrorResponseData> {
    this?.let {
        val type = object : TypeToken<ApiResponse<ErrorResponseData>>() {}.type
        val errorString = it.string()
        return GsonInstance.GSON.fromJson(errorString, type)
    }
    return ApiResponse("error", errorParsingError, null)
}

fun String?.toErrorDataResponse(): ApiResponse<ErrorResponseData> {
    this?.let {
        val type = object : TypeToken<ApiResponse<ErrorResponseData>>() {}.type
        return GsonInstance.GSON.fromJson(this, type)
    }
    return ApiResponse("error", errorParsingError, null)
}

fun <T> toType(): Type {
    return object : TypeToken<T>() {}.type
}
