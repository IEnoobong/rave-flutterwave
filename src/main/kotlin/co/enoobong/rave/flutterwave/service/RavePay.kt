package co.enoobong.rave.flutterwave.service

import co.enoobong.rave.flutterwave.data.AccountPayload
import co.enoobong.rave.flutterwave.data.ApiResponse
import co.enoobong.rave.flutterwave.data.Bank
import co.enoobong.rave.flutterwave.data.CardPayload
import co.enoobong.rave.flutterwave.data.ChargeRequest
import co.enoobong.rave.flutterwave.data.ChargeResponseData
import co.enoobong.rave.flutterwave.data.ExchangeRateData
import co.enoobong.rave.flutterwave.data.GetFeeResponseData
import co.enoobong.rave.flutterwave.data.GetFeesPayload
import co.enoobong.rave.flutterwave.data.Payload
import co.enoobong.rave.flutterwave.data.PreauthorizeCardData
import co.enoobong.rave.flutterwave.data.RefundResponseData
import co.enoobong.rave.flutterwave.data.RefundVoidResponseData
import co.enoobong.rave.flutterwave.data.RequeryRequestPayload
import co.enoobong.rave.flutterwave.data.RequeryResponseData
import co.enoobong.rave.flutterwave.data.ValidateChargePayload
import co.enoobong.rave.flutterwave.data.XRequeryRequestPayload
import co.enoobong.rave.flutterwave.data.XRequeryResponseData
import co.enoobong.rave.flutterwave.network.ApiClient
import co.enoobong.rave.flutterwave.util.GsonInstance
import co.enoobong.rave.flutterwave.util.SingletonHolder
import co.enoobong.rave.flutterwave.util.encrypt
import co.enoobong.rave.flutterwave.util.encryptSecretKey
import co.enoobong.rave.flutterwave.util.errorParsingError
import co.enoobong.rave.flutterwave.util.toErrorDataResponse
import co.enoobong.rave.flutterwave.util.toJsonString
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.logging.Level
import java.util.logging.Logger


/**
 * @author Ibanga Enoobong I
 * @since 3/2/18.
 */
class RavePay private constructor(private val secretKey: String) {
    internal companion object : SingletonHolder<RavePay, String>(::RavePay) {
        private val L = Logger.getLogger(RavePay::class.java.name)
    }

    private val apiService = ApiClient.apiService

    /**
     * @param payload Payload object
     */
    fun chargeCard(
        payload: CardPayload,
        callback: RaveCallback<ApiResponse<ChargeResponseData>>
    ) {
        makeCharge(payload, callback)
    }

    fun chargeAccount(
        payload: AccountPayload,
        callback: RaveCallback<ApiResponse<ChargeResponseData>>
    ) {
        makeCharge(payload, callback)
    }

    private fun makeCharge(
        payload: Payload,
        callback: RaveCallback<ApiResponse<ChargeResponseData>>
    ) {
        val requestAsJsonString = payload.toJsonString()
        val encryptedRequest = encrypt(requestAsJsonString, encryptSecretKey(secretKey))

        val chargeRequest = ChargeRequest(payload.PBFPubKey, encryptedRequest)

        apiService.directCharge(chargeRequest).enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful) {
                    val type = object : TypeToken<ApiResponse<ChargeResponseData>>() {}.type
                    response.body()?.let {
                        val apiResponse =
                            GsonInstance.GSON.fromJson<ApiResponse<ChargeResponseData>>(
                                it, type
                            )
                        callback.onSuccess(apiResponse, it)
                    }
                } else {
                    val errorString = response.errorBody()?.string()

                    handleUnsuccessfulRequests(errorString, callback)
                }
            }

            override fun onFailure(call: Call<String>?, t: Throwable) {
                callback.onError(t.message, "")
            }

        })
    }

    fun getBanks(callback: RaveCallback<ApiResponse<@JvmSuppressWildcards List<Bank>>>) {
        apiService.getBanks().enqueue(object : Callback<List<Bank>> {
            override fun onFailure(call: Call<List<Bank>>?, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<List<Bank>>?, response: Response<List<Bank>>) {
                if (response.isSuccessful) {
                    val apiResponse = ApiResponse("success", "Request Successful", response.body())
                    callback.onSuccess(apiResponse)
                } else {
                    val errorString = response.errorBody()?.string()
                    handleUnsuccessfulRequests(errorString, callback)
                }
            }
        })
    }

    fun validateCardCharge(
        validateCharge: ValidateChargePayload,
        callback: RaveCallback<ApiResponse<ChargeResponseData>>
    ) {
        validateCharge(validateCharge, callback, false)
    }

    fun validateAccountCharge(
        validateCharge: ValidateChargePayload,
        callback: RaveCallback<ApiResponse<ChargeResponseData>>
    ) {

        validateCharge(validateCharge, callback, true)
    }

    private fun validateCharge(
        validateCharge: ValidateChargePayload,
        callback: RaveCallback<ApiResponse<ChargeResponseData>>,
        isAccountCharge: Boolean
    ) {
        val call = if (isAccountCharge) {
            apiService.validateAccountCharge(validateCharge)
        } else {
            apiService.validateCardCharge(validateCharge)
        }

        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable) {
                callback.onError(t.message, "")
            }

            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val type = object : TypeToken<ApiResponse<ChargeResponseData>>() {}.type
                        val apiResponse =
                            GsonInstance.GSON.fromJson<ApiResponse<ChargeResponseData>>(
                                it, type
                            )
                        callback.onSuccess(apiResponse, it)
                    }

                } else {
                    val errorString = response.errorBody()?.string()

                    handleUnsuccessfulRequests(errorString, callback)
                }
            }
        })
    }

    fun verifyTransaction(
        requeryRequestPayload: RequeryRequestPayload,
        callback: RaveCallback<ApiResponse<RequeryResponseData>>
    ) {

        requeryRequestPayload.SECKEY = secretKey

        apiService.requeryTx(requeryRequestPayload).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        try {
                            val type =
                                object : TypeToken<ApiResponse<RequeryResponseData>>() {}.type
                            val apiResponse =
                                GsonInstance.GSON.fromJson<ApiResponse<RequeryResponseData>>(
                                    it, type
                                )
                            callback.onSuccess(apiResponse, it)

                        } catch (ex: Exception) {
                            L.severe(ex.message)
                            callback.onError(errorParsingError, it)
                        }
                    }
                } else {
                    val errorString = response.errorBody()?.string()

                    handleUnsuccessfulRequests(errorString, callback)

                }
            }

        })
    }

    fun verifyTransactionUsingXRequery(
        xRequeryRequestPayload: XRequeryRequestPayload, callback:
        RaveCallback<ApiResponse<@JvmSuppressWildcards List<XRequeryResponseData>>>
    ) {
        xRequeryRequestPayload.SECKEY = secretKey

        apiService.xRequeryTx(xRequeryRequestPayload).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        try {
                            //Workaround because the API can either return a list (could be empty) or an object
                            // but users get a list always
                            val isResponseArray =
                                JsonParser().parse(it).asJsonObject.get("data").isJsonArray
                            val apiResponse = if (isResponseArray) {
                                val type = object :
                                    TypeToken<ApiResponse<List<XRequeryResponseData>>>() {}
                                    .type
                                GsonInstance.GSON.fromJson<ApiResponse<List<XRequeryResponseData>>>(
                                    it,
                                    type
                                )
                            } else {
                                val type = object :
                                    TypeToken<ApiResponse<XRequeryResponseData>>() {}
                                    .type
                                val singleTx =
                                    GsonInstance.GSON.fromJson<ApiResponse<XRequeryResponseData>>(
                                        it,
                                        type
                                    )
                                val singleTxList =
                                    if (singleTx.data == null) emptyList() else listOf(singleTx.data)
                                ApiResponse(singleTx.status, singleTx.message, singleTxList)
                            }

                            callback.onSuccess(apiResponse, it)

                        } catch (ex: Exception) {
                            L.log(Level.SEVERE, ex.message, ex)
                            callback.onError(errorParsingError, it)
                        }
                    }
                } else {
                    val errorString = response.errorBody()?.string()

                    handleUnsuccessfulRequests(errorString, callback)

                }
            }

        })
    }

    /**
     * @param cardPayload to preauthorize a card you need to a
     */
    fun preauthorizeCard(
        cardPayload: CardPayload, callback:
        RaveCallback<ApiResponse<PreauthorizeCardData>>
    ) {
        cardPayload.chargeType = "preauth"

        val requestAsJsonString = cardPayload.toJsonString()
        val encryptedRequest = encrypt(requestAsJsonString, encryptSecretKey(secretKey))

        val chargeRequest = ChargeRequest(cardPayload.PBFPubKey, encryptedRequest)

        apiService.directCharge(chargeRequest).enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful) {
                    val type = object : TypeToken<ApiResponse<PreauthorizeCardData>>() {}.type
                    response.body()?.let {
                        val apiResponse =
                            GsonInstance.GSON.fromJson<ApiResponse<PreauthorizeCardData>>(
                                it, type
                            )
                        callback.onSuccess(apiResponse, it)
                    }
                } else {
                    val errorString = response.errorBody()?.string()

                    handleUnsuccessfulRequests(errorString, callback)
                }
            }

            override fun onFailure(call: Call<String>?, t: Throwable) {
                callback.onError(t.message, "")
            }

        })
    }

    fun capturePreauthorizedFunds(
        flwRef: String, callback:
        RaveCallback<ApiResponse<PreauthorizeCardData>>
    ) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("flwRef", flwRef)
        jsonObject.addProperty("SECKEY", secretKey)

        apiService.capturePreauthorizeCard(jsonObject).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful) {
                    val type = object : TypeToken<ApiResponse<PreauthorizeCardData>>() {}.type
                    response.body()?.let {
                        val apiResponse =
                            GsonInstance.GSON.fromJson<ApiResponse<PreauthorizeCardData>>(
                                it, type
                            )
                        callback.onSuccess(apiResponse, it)
                    }
                } else {
                    val errorString = response.errorBody()?.string()

                    handleUnsuccessfulRequests(errorString, callback)
                }
            }

        })

    }

    /**
     * @param action action to carry out either refund or void
     */

    fun refundOrVoidPreauthorization(
        flwRef: String, action: String, callback:
        RaveCallback<ApiResponse<RefundVoidResponseData>>
    ) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ref", flwRef)
        jsonObject.addProperty("action", action)
        jsonObject.addProperty("SECKEY", secretKey)

        apiService.refundOrVoidPreauthorization(jsonObject).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful) {
                    val type = object : TypeToken<ApiResponse<RefundVoidResponseData>>() {}.type
                    response.body()?.let {
                        val apiResponse =
                            GsonInstance.GSON.fromJson<ApiResponse<RefundVoidResponseData>>(
                                it, type
                            )
                        callback.onSuccess(apiResponse, it)
                    }
                } else {
                    val errorString = response.errorBody()?.string()

                    handleUnsuccessfulRequests(errorString, callback)
                }
            }

        })
    }

    private fun <T : ApiResponse<*>> handleUnsuccessfulRequests(
        errorString: String?, callback:
        RaveCallback<T>
    ) {
        try {
            val errorDataResponse = errorString.toErrorDataResponse()

            callback.onError(errorDataResponse.message, errorString)
        } catch (ex: Exception) {
            L.log(Level.SEVERE, ex.message, ex)
            callback.onError(errorParsingError, errorString)
        }
    }

    fun getFees(
        getFeesPayload: GetFeesPayload, callback:
        RaveCallback<ApiResponse<GetFeeResponseData>>
    ) {
        apiService.getFees(getFeesPayload).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful) {
                    val type = object : TypeToken<ApiResponse<GetFeeResponseData>>() {}.type
                    response.body()?.let {
                        val apiResponse =
                            GsonInstance.GSON.fromJson<ApiResponse<GetFeeResponseData>>(
                                it, type
                            )
                        callback.onSuccess(apiResponse, it)
                    }
                } else {
                    val errorString = response.errorBody()?.string()

                    handleUnsuccessfulRequests(errorString, callback)
                }
            }

        })
    }

    /**
     * Rave allows you initiate refunds for only <strong>Successful</strong> transactions
     *
     * @param flwRef the flwRef returned in @see ChargeResponseData
     */

    fun refundTransaction(flwRef: String, callback: RaveCallback<ApiResponse<RefundResponseData>>) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ref", flwRef)
        jsonObject.addProperty("seckey", secretKey)

        apiService.refundTransaction(jsonObject).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful) {
                    val type = object : TypeToken<ApiResponse<RefundResponseData>>() {}.type
                    response.body()?.let {
                        val apiResponse =
                            GsonInstance.GSON.fromJson<ApiResponse<RefundResponseData>>(
                                it, type
                            )
                        callback.onSuccess(apiResponse, it)
                    }
                } else {
                    val errorString = response.errorBody()?.string()
                    handleUnsuccessfulRequests(errorString, callback)
                }
            }
        })
    }

    /**
     * Allows for conversion of currencies in real time to charge your customers in alternate
     * currencies.
     *
     * @param originCurrency This is the currency to convert from
     *
     * @param destinationCurrency This is the currency to convert to
     *
     * @param amount This is the amount being converted, it is an optional field.
     *
     * @param callback response callbacks
     *
     * @see <a href="https://flutterwavedevelopers.readme.io/v2
     * .0/reference#exchange-rates">Flutterwave Rave Documentation</a> for possible exchange rate
     * combinations
     *
     */
    fun convertCurrency(
        originCurrency: String, destinationCurrency: String, amount: Double?,
        callback: RaveCallback<ApiResponse<ExchangeRateData>>
    ) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("origin_currency", originCurrency)
        jsonObject.addProperty("destination_currency", destinationCurrency)
        jsonObject.addProperty("amount", amount)
        jsonObject.addProperty("SECKEY", secretKey)

        apiService.convertCurrency(jsonObject).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful) {
                    val type = object : TypeToken<ApiResponse<ExchangeRateData>>() {}.type
                    response.body()?.let {
                        val apiResponse =
                            GsonInstance.GSON.fromJson<ApiResponse<ExchangeRateData>>(
                                it, type
                            )
                        callback.onSuccess(apiResponse, it)
                    }
                } else {
                    val errorString = response.errorBody()?.string()
                    handleUnsuccessfulRequests(errorString, callback)
                }
            }
        })

    }
}