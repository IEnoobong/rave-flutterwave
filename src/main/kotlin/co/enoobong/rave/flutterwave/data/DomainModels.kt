package co.enoobong.rave.flutterwave.data

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

/**
 * @author Ibanga Enoobong I
 * @since 2/27/18.
 */
data class Payload @JvmOverloads constructor(
    val PBFPubKey: String,
    val amount: BigDecimal,
    val country: Country,
    val currency: Currency,
    val email: String,
    val IP: String,
    val narration: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastname") val lastName: String,
    @SerializedName("txref") val transactionRef: String,
    val cvv: String? = null,
    val meta: List<Meta>? = null,
    @SerializedName("cardno") val cardNumber: String? = null,
    @SerializedName("accountname") val accountName: String? = null,
    @SerializedName("accountbank") val accountBank: String? = null,
    @SerializedName("expiryyear") val expiryYear: String? = null,
    @SerializedName("expirymonth") val expiryMonth: String? = null
) {
    var cardBIN: String = ""
    var SECKEY: String = ""
    var token: String = ""
    @SerializedName("billingzip")
    var billingZip: String = ""
    var pin: String = ""
    var passcode = ""
    @SerializedName("suggested_auth")
    var suggestedAuth = ""
    @SerializedName("payment_type")
    var paymentType = ""
    @SerializedName("phonenumber")
    var phoneNumber = ""
    @SerializedName("is_internet_banking")
    var isInternetBanking = ""
}


class Meta(
    @SerializedName("metaname") val metaName: String, @SerializedName("metavalue") val
    metaValue: String
)

data class RavePayload(val hash: String, val dynamicValue: String)

enum class Currency {
    NGN,
    USD,
    KES,
    EUR,
    GBP
}

enum class Country {
    NG,
    KE,
    GH,
    ZA
}

data class ChargeRequest(
    val PBFPubKey: String,
    val client: String,
    val alg: String = "3DES-24"
)

data class Bank(
    @SerializedName("bankname")
    val bankName: String,
    @SerializedName("bankcode")
    val bankCode: String,
    @SerializedName("isInternetbanking")
    val isInternetBanking: Boolean = false
)

data class SavedCard(
    val token: String, val first6: String, val last4: String, val cardType:
    String, val flwRef: String
)

data class ApiResponse<out T>(val status: String, val message: String, val data: T?)

class ChargeResponseData(
    @SerializedName("suggested_auth") val suggestedAuth: String, val
    chargeResponseCode: String, val authModelUsed: String, val flwRef: String, val
    chargeResponseMessage: String, @SerializedName("authurl") val authUrl: String
)

data class ErrorResponseData(@SerializedName("is_error") val isError: Boolean, val code: String)
