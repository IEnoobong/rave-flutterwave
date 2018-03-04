package co.enoobong.rave.flutterwave.network

import co.enoobong.rave.flutterwave.config.ApiEndpoints
import co.enoobong.rave.flutterwave.data.Bank
import co.enoobong.rave.flutterwave.data.ChargeRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


/**
 * @author Ibanga Enoobong I
 * @since 2/27/18.
 */
interface ApiService {

    @POST(ApiEndpoints.DIRECT_CHARGE)
    fun directCharge(@Body chargeRequest: ChargeRequest): Call<String>

    @GET(ApiEndpoints.GET_BANKS)
    fun getBanks(): Call<List<Bank>>
}