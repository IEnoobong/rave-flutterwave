package co.enoobong.rave.flutterwave.util

import co.enoobong.rave.flutterwave.data.ApiResponse
import co.enoobong.rave.flutterwave.data.ErrorResponseData
import co.enoobong.rave.flutterwave.data.Payload
import co.enoobong.rave.flutterwave.service.RavePay
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import java.lang.reflect.Type

/**
 * @author Ibanga Enoobong I
 * @since 3/2/18.
 */

internal fun <T> Response<T>.requireBody() = body()!!

internal inline fun <reified T : Payload> T.toJsonString(): String {
    return RavePay.GSON.toJson(this, toType<T>())
}

internal inline fun <reified T : Any> Gson.fromJson(json: String): T = fromJson(json, toType<T>())

internal inline fun <reified T : Any> toType(): Type {
    return object : TypeToken<T>() {}.type
}

internal const val errorParsingError = "An error occurred while parsing the error response"

internal fun String?.toErrorDataResponse(): ApiResponse<ErrorResponseData> {
    this?.let {
        return RavePay.GSON.fromJson(it)
    }
    return ApiResponse("error", errorParsingError, null)
}
