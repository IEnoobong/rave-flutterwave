package co.enoobong.rave.flutterwave.util

import co.enoobong.rave.flutterwave.data.ApiResponse
import co.enoobong.rave.flutterwave.data.ErrorResponseData
import co.enoobong.rave.flutterwave.data.Payload
import co.enoobong.rave.flutterwave.service.RaveCallback
import co.enoobong.rave.flutterwave.service.RavePay
import co.enoobong.rave.flutterwave.service.RavePay.Companion.GSON
import co.enoobong.rave.flutterwave.service.RavePay.Companion.L
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
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

internal inline fun <reified T : ApiResponse<*>> Call<String>.enqueue(callback: RaveCallback<T>) {
    enqueue(object : Callback<String> {
        override fun onFailure(call: Call<String>?, t: Throwable) {
            callback.onError(t.message)
        }

        override fun onResponse(call: Call<String>?, response: Response<String>) {
            if (response.isSuccessful) {
                val responseAsJsonString = response.requireBody()
                try {
                    val apiResponse = GSON.fromJson<T>(responseAsJsonString)

                    callback.onSuccess(apiResponse, response.requireBody())
                } catch (ex: JsonParseException) {
                    L.severe(ex.message)
                    callback.onError(errorParsingError, responseAsJsonString)
                } catch (ex: JsonSyntaxException) {
                    L.severe(ex.message)
                    callback.onError(errorParsingError, responseAsJsonString)
                }
            } else {
                val errorString = response.errorBody()?.string()
                handleUnsuccessfulRequests(errorString, callback)
            }
        }
    })
}

internal fun <T : ApiResponse<*>> handleUnsuccessfulRequests(
    errorString: String?,
    callback:
    RaveCallback<T>
) {
    try {
        val errorDataResponse = errorString.toErrorDataResponse()

        callback.onError(errorDataResponse.message, errorString)
    } catch (ex: JsonParseException) {
        L.severe(ex.message)
        callback.onError(errorParsingError, errorString)
    } catch (ex: JsonSyntaxException) {
        L.severe(ex.message)
        callback.onError(errorParsingError, errorString)
    }
}