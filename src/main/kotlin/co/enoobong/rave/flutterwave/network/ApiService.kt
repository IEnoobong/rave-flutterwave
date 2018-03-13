package co.enoobong.rave.flutterwave.network

import co.enoobong.rave.flutterwave.data.Bank
import co.enoobong.rave.flutterwave.data.ChargeRequest
import co.enoobong.rave.flutterwave.data.GetFeesPayload
import co.enoobong.rave.flutterwave.data.RequeryRequestPayload
import co.enoobong.rave.flutterwave.data.ValidateChargePayload
import co.enoobong.rave.flutterwave.data.XRequeryRequestPayload
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * @author Ibanga Enoobong I
 * @since 2/27/18.
 */
internal interface ApiService {

    @POST("flwv3-pug/getpaidx/api/charge")
    fun directCharge(@Body chargeRequest: ChargeRequest): Call<String>

    @POST("/flwv3-pug/getpaidx/api/validatecharge")
    fun validateCardCharge(@Body validateCharge: ValidateChargePayload): Call<String>

    @POST("/flwv3-pug/getpaidx/api/validate")
    fun validateAccountCharge(@Body validateCharge: ValidateChargePayload): Call<String>

    @GET("/flwv3-pug/getpaidx/api/flwpbf-banks.js?json=1")
    fun getBanks(): Call<List<Bank>>

    @POST("/flwv3-pug/getpaidx/api/verify")
    fun requeryTransaction(@Body body: RequeryRequestPayload): Call<String>

    @POST("/flwv3-pug/getpaidx/api/xrequery")
    fun xRequeryTransaction(@Body body: XRequeryRequestPayload): Call<String>

    @POST("/flwv3-pug/getpaidx/api/capture")
    fun capturePreauthorizeCard(@Body body: JsonObject): Call<String>

    @POST("/flwv3-pug/getpaidx/api/refundorvoid")
    fun refundOrVoidPreauthorization(@Body body: JsonObject): Call<String>

    @POST("/flwv3-pug/getpaidx/api/fee")
    fun getFees(@Body body: GetFeesPayload): Call<String>

    @POST("/gpx/merchant/transactions/refund")
    fun refundTransaction(@Body body: JsonObject): Call<String>

    @POST("/flwv3-pug/getpaidx/api/forex")
    fun convertCurrency(@Body body: JsonObject): Call<String>

//    const val CHARGE_TOKEN = "/flwv3-pug/getpaidx/api/tokenized/charge"
//    const val CHECK_FEE = "/flwv3-pug/getpaidx/api/fee"
}