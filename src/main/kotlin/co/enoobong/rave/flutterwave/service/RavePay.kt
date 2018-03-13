package co.enoobong.rave.flutterwave.service

import co.enoobong.rave.flutterwave.data.AccountPayload
import co.enoobong.rave.flutterwave.data.ApiResponse
import co.enoobong.rave.flutterwave.data.Bank
import co.enoobong.rave.flutterwave.data.CardPayload
import co.enoobong.rave.flutterwave.data.ChargeRequest
import co.enoobong.rave.flutterwave.data.ChargeResponseData
import co.enoobong.rave.flutterwave.data.Environment
import co.enoobong.rave.flutterwave.data.ExchangeRateData
import co.enoobong.rave.flutterwave.data.GetFeeResponseData
import co.enoobong.rave.flutterwave.data.GetFeesPayload
import co.enoobong.rave.flutterwave.data.PreauthorizeCardData
import co.enoobong.rave.flutterwave.data.RefundResponseData
import co.enoobong.rave.flutterwave.data.RefundVoidResponseData
import co.enoobong.rave.flutterwave.data.RequeryRequestPayload
import co.enoobong.rave.flutterwave.data.RequeryResponseData
import co.enoobong.rave.flutterwave.data.ValidateChargePayload
import co.enoobong.rave.flutterwave.data.XRequeryRequestPayload
import co.enoobong.rave.flutterwave.data.XRequeryResponseData
import co.enoobong.rave.flutterwave.network.ApiClient
import co.enoobong.rave.flutterwave.util.encryptRequest
import co.enoobong.rave.flutterwave.util.encryptSecretKey
import co.enoobong.rave.flutterwave.util.errorParsingError
import co.enoobong.rave.flutterwave.util.fromJson
import co.enoobong.rave.flutterwave.util.requireBody
import co.enoobong.rave.flutterwave.util.toErrorDataResponse
import co.enoobong.rave.flutterwave.util.toJsonString
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author Ibanga Enoobong I
 * @since 3/2/18.
 */
class RavePay private constructor(private val ravePayBuilder: Builder) {

    companion object {
        private val L = Logger.getLogger(RavePay::class.java.name)
        internal val GSON = Gson()

        @JvmStatic
        private operator fun invoke(ravePayBuilder: Builder): RavePay {
            return RavePay(ravePayBuilder)
        }
    }

    class Builder {
        internal var whichEnvironment = Environment.STAGING

        internal var userSecretKey = ""
            private set
        internal var userPublicKey = ""
            private set

        fun setEnvironment(environment: Environment) = this.also {
            whichEnvironment = environment
        }

        fun setSecretKey(secretKey: String) = this.also {
            userSecretKey = secretKey
        }

        fun setPublicKey(publicKey: String) = this.also {
            userPublicKey = publicKey
        }

        fun build(): RavePay {
            if (userSecretKey.isBlank() or userPublicKey.isBlank()) {
                L.severe("Secret/Public Key was empty")
                throw IllegalArgumentException("Secret/Public key cannot be empty or blank")
            }
            return RavePay(this)
        }
    }

    internal var apiService = ApiClient.createApiService(ravePayBuilder.whichEnvironment)

    /**
     * @param payload Payload object
     */
    fun chargeCard(
        payload: CardPayload,
        callback: RaveCallback<ApiResponse<ChargeResponseData>>
    ) {
        makeCharge(payload.toJsonString(), ravePayBuilder.userPublicKey, callback)
    }

    fun chargeAccount(
        payload: AccountPayload,
        callback: RaveCallback<ApiResponse<ChargeResponseData>>
    ) {
        makeCharge(payload.toJsonString(), ravePayBuilder.userPublicKey, callback)
    }

    private fun makeCharge(
        payload: String,
        publicKey: String,
        callback: RaveCallback<ApiResponse<ChargeResponseData>>
    ) {
        val encryptedRequest =
            encryptRequest(payload, encryptSecretKey(ravePayBuilder.userSecretKey))

        val chargeRequest = ChargeRequest(encryptedRequest)
        chargeRequest.publicKey = publicKey

        apiService.directCharge(chargeRequest).enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful) {
                    val responseAsJsonString = response.requireBody()
                    try {
                        val apiResponse =
                            GSON.fromJson<ApiResponse<ChargeResponseData>>(responseAsJsonString)
                        callback.onSuccess(apiResponse, responseAsJsonString)
                    } catch (ex: Exception) {
                        L.severe(ex.message)
                        callback.onError(errorParsingError, responseAsJsonString)
                    }
                } else {
                    val errorString = response.errorBody()?.string()

                    handleUnsuccessfulRequests(errorString, callback)
                }
            }

            override fun onFailure(call: Call<String>?, t: Throwable) {
                callback.onError(t.message)
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

        validateCharge.publicKey = ravePayBuilder.userPublicKey

        val call = if (isAccountCharge) {
            apiService.validateAccountCharge(validateCharge)
        } else {
            apiService.validateCardCharge(validateCharge)
        }

        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful) {
                    val responseAsJsonString = response.requireBody()
                    try {
                        val apiResponse =
                            GSON.fromJson<ApiResponse<ChargeResponseData>>(responseAsJsonString)
                        callback.onSuccess(apiResponse, responseAsJsonString)
                    } catch (ex: Exception) {
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

    fun verifyTransaction(
        requeryRequestPayload: RequeryRequestPayload,
        callback: RaveCallback<ApiResponse<RequeryResponseData>>
    ) {

        requeryRequestPayload.secretKey = ravePayBuilder.userSecretKey

        apiService.requeryTransaction(requeryRequestPayload).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful) {
                    val responseAsJsonString = response.requireBody()
                    try {
                        val apiResponse =
                            GSON.fromJson<ApiResponse<RequeryResponseData>>(responseAsJsonString)
                        callback.onSuccess(apiResponse, responseAsJsonString)
                    } catch (ex: Exception) {
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

    fun verifyTransactionUsingXRequery(
        xRequeryRequestPayload: XRequeryRequestPayload,
        callback:
        RaveCallback<ApiResponse<@JvmSuppressWildcards List<XRequeryResponseData>>>
    ) {
        xRequeryRequestPayload.secretKey = ravePayBuilder.userSecretKey

        apiService.xRequeryTransaction(xRequeryRequestPayload).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful) {
                    val responseAsJsonString = response.requireBody()
                    try {
                        //Workaround because the API can either return a list (could be empty) or an object
                        // but users get a list always
                        val isResponseArray =
                            JsonParser().parse(responseAsJsonString).asJsonObject.get("data")
                                .isJsonArray
                        val apiResponse = if (isResponseArray) {
                            GSON.fromJson(responseAsJsonString)
                        } else {
                            val singleTransaction =
                                GSON.fromJson<ApiResponse<XRequeryResponseData>>(
                                    responseAsJsonString)
                            val singleTransactionList =
                                if (singleTransaction.data == null) emptyList() else listOf(
                                    singleTransaction.data
                                )
                            ApiResponse(
                                singleTransaction.status,
                                singleTransaction.message,
                                singleTransactionList
                            )
                        }

                        callback.onSuccess(apiResponse, responseAsJsonString)
                    } catch (ex: Exception) {
                        L.log(Level.SEVERE, ex.message, ex)
                        callback.onError(errorParsingError, responseAsJsonString)
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
        cardPayload: CardPayload,
        callback:
        RaveCallback<ApiResponse<PreauthorizeCardData>>
    ) {
        cardPayload.apply {
            chargeType = "preauth"
        }

        val requestAsJsonString = cardPayload.toJsonString()
        val encryptedRequest =
            encryptRequest(requestAsJsonString, encryptSecretKey(ravePayBuilder.userSecretKey))

        val chargeRequest = ChargeRequest(encryptedRequest)
        chargeRequest.publicKey = ravePayBuilder.userPublicKey

        apiService.directCharge(chargeRequest).enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful) {
                    val responseAsJsonString = response.requireBody()
                    try {
                        val apiResponse =
                            GSON.fromJson<ApiResponse<PreauthorizeCardData>>(responseAsJsonString)
                        callback.onSuccess(apiResponse, responseAsJsonString)
                    } catch (ex: Exception) {
                        L.severe(ex.message)
                        callback.onError(errorParsingError, responseAsJsonString)
                    }
                } else {
                    val errorString = response.errorBody()?.string()

                    handleUnsuccessfulRequests(errorString, callback)
                }
            }

            override fun onFailure(call: Call<String>?, t: Throwable) {
                callback.onError(t.message)
            }
        })
    }

    fun capturePreauthorizedFunds(
        flwRef: String,
        callback:
        RaveCallback<ApiResponse<PreauthorizeCardData>>
    ) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("flwRef", flwRef)
        jsonObject.addProperty("SECKEY", ravePayBuilder.userSecretKey)

        apiService.capturePreauthorizeCard(jsonObject).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful) {
                    val responseAsJsonString = response.requireBody()
                    try {
                        val apiResponse =
                            GSON.fromJson<ApiResponse<PreauthorizeCardData>>(responseAsJsonString)
                        callback.onSuccess(apiResponse, responseAsJsonString)
                    } catch (ex: Exception) {
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

    /**
     * @param action action to carry out either refund or void
     */

    fun refundOrVoidPreauthorization(
        flwRef: String,
        action: String,
        callback:
        RaveCallback<ApiResponse<RefundVoidResponseData>>
    ) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ref", flwRef)
        jsonObject.addProperty("action", action)
        jsonObject.addProperty("SECKEY", ravePayBuilder.userSecretKey)

        apiService.refundOrVoidPreauthorization(jsonObject).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful) {
                    val responseAsJsonString = response.requireBody()
                    try {
                        val apiResponse =
                            GSON.fromJson<ApiResponse<RefundVoidResponseData>>(responseAsJsonString)
                        callback.onSuccess(apiResponse, responseAsJsonString)
                    } catch (ex: Exception) {
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

    private fun <T : ApiResponse<*>> handleUnsuccessfulRequests(
        errorString: String?,
        callback:
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
        getFeesPayload: GetFeesPayload,
        callback:
        RaveCallback<ApiResponse<GetFeeResponseData>>
    ) {
        getFeesPayload.publicKey = ravePayBuilder.userPublicKey

        apiService.getFees(getFeesPayload).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful) {
                    val responseAsJsonString = response.requireBody()
                    try {
                        val apiResponse =
                            GSON.fromJson<ApiResponse<GetFeeResponseData>>(responseAsJsonString)
                        callback.onSuccess(apiResponse, responseAsJsonString)
                    } catch (ex: Exception) {
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

    /**
     * Rave allows you initiate refunds for only <strong>Successful</strong> transactions
     *
     * @param flwRef the flwRef returned in @see ChargeResponseData
     */

    fun refundTransaction(flwRef: String, callback: RaveCallback<ApiResponse<RefundResponseData>>) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ref", flwRef)
        jsonObject.addProperty("seckey", ravePayBuilder.userSecretKey)

        apiService.refundTransaction(jsonObject).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful) {
                    val responseAsJsonString = response.requireBody()
                    val apiResponse =
                        GSON.fromJson<ApiResponse<RefundResponseData>>(responseAsJsonString)
                    callback.onSuccess(apiResponse, responseAsJsonString)
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
        originCurrency: String,
        destinationCurrency: String,
        amount: Double?,
        callback: RaveCallback<ApiResponse<ExchangeRateData>>
    ) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("origin_currency", originCurrency)
        jsonObject.addProperty("destination_currency", destinationCurrency)
        jsonObject.addProperty("amount", amount)
        jsonObject.addProperty("SECKEY", ravePayBuilder.userSecretKey)

        apiService.convertCurrency(jsonObject).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.isSuccessful) {
                    val responseAsJsonString = response.requireBody()
                    try {
                        val apiResponse =
                            GSON.fromJson<ApiResponse<ExchangeRateData>>(responseAsJsonString)
                        callback.onSuccess(apiResponse, responseAsJsonString)
                    } catch (ex: Exception) {
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

    /**
     * This is currently only available for GTB and Zenith Bank.
     *
     * FOR GTB you need to display {@literal *737*50*amount*159#} and the <code>flwRef</code>
     * returned for the customer to complete the transaction
     *
     * Use webhooks to get notified on transaction, and set it to pending, then complete/failed
     * once notified with same status on webhook. See guide on using webhooks here:
     * @see <a href="https://flutterwavedevelopers.readme.io/v2
     * .0/docs/events-webhooks">Flutterwave Rave Documentation</a> for how to use webhook
     *
     * After notification requery and confirm final status.
     */
    fun ussdCharge(
        payload: AccountPayload,
        callback: RaveCallback<ApiResponse<ChargeResponseData>>
    ) {
        makeCharge(payload.toJsonString(), ravePayBuilder.userPublicKey, callback)
    }
}