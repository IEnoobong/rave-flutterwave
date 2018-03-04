package co.enoobong.rave.flutterwave.service

import co.enoobong.rave.flutterwave.data.AccountPayload
import co.enoobong.rave.flutterwave.data.ApiResponse
import co.enoobong.rave.flutterwave.data.Bank
import co.enoobong.rave.flutterwave.data.Callbacks
import co.enoobong.rave.flutterwave.data.CardPayload
import co.enoobong.rave.flutterwave.data.ChargeRequest
import co.enoobong.rave.flutterwave.data.ChargeResponseData
import co.enoobong.rave.flutterwave.data.Payload
import co.enoobong.rave.flutterwave.network.ApiClient
import co.enoobong.rave.flutterwave.util.GsonInstance
import co.enoobong.rave.flutterwave.util.SingletonHolder
import co.enoobong.rave.flutterwave.util.encrypt
import co.enoobong.rave.flutterwave.util.errorParsingError
import co.enoobong.rave.flutterwave.util.toErrorDataResponse
import co.enoobong.rave.flutterwave.util.toJsonString
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
    companion object : SingletonHolder<RavePay, String>(::RavePay) {
        private val L = Logger.getLogger(RavePay::class.java.name)
    }

    private val apiService = ApiClient.apiService

    /**
     * @param payload Payload object
     */
    fun chargeCard(
        payload: CardPayload,
        callback: Callbacks.OnChargeRequestComplete
    ) {
        makeCharge(payload, callback)
    }

    fun chargeAccount(
        payload: AccountPayload,
        callback: Callbacks.OnChargeRequestComplete
    ) {
        makeCharge(payload, callback)
    }

    private fun makeCharge(
        payload: Payload,
        callback: Callbacks.OnChargeRequestComplete
    ) {
        val requestAsJsonString = payload.toJsonString()
        val encryptedRequest = encrypt(requestAsJsonString, secretKey)

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
                    try {
                        val errorString = response.errorBody()?.string()
                        val errorDataResponse = errorString.toErrorDataResponse()

                        callback.onError(errorDataResponse.message, errorString)
                    } catch (ex: Exception) {
                        L.log(Level.SEVERE, ex.message, ex)
                        callback.onError("error", errorParsingError)
                    }
                }
            }

            override fun onFailure(call: Call<String>?, t: Throwable) {
                callback.onError(t.message, "")
            }

        })
    }

    fun getBanks(callback: Callbacks.OnGetBanksRequestComplete) {
        apiService.getBanks().enqueue(object : Callback<List<Bank>> {
            override fun onFailure(call: Call<List<Bank>>?, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<List<Bank>>?, response: Response<List<Bank>>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    try {
                        val errorString = response.errorBody()?.string()
                        val errorDataResponse = errorString.toErrorDataResponse()
                        callback.onError(errorDataResponse.message)

                    } catch (ex: Exception) {
                        L.log(Level.SEVERE, ex.message, ex)
                        callback.onError("An error occurred while retrieving banks")
                    }
                }
            }
        })
    }


}