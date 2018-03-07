package co.enoobong.rave.flutterwave.data

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

/**
 * @author Ibanga Enoobong I
 * @since 2/27/18.
 */
sealed class Payload {
    abstract val PBFPubKey: String
    abstract val amount: BigDecimal
    abstract val country: String
    abstract val currency: String
    abstract val email: String
    abstract val phoneNumber: String
    abstract val IP: String
    abstract val firstName: String
    abstract val lastName: String
    abstract val transactionRef: String
}

data class CardPayload(
    override val PBFPubKey: String,
    @SerializedName("cardno") val cardNumber: String,
    val cvv: String,
    @SerializedName("expirymonth") val expiryMonth: String,
    @SerializedName("expiryyear") val expiryYear: String,
    override val currency: String,
    override val country: String,
    override val amount: BigDecimal,
    override val email: String,
    @SerializedName("phonenumber") override val phoneNumber: String,
    @SerializedName("firstName") override val firstName: String,
    @SerializedName("lastname") override val lastName: String,
    override val IP: String,
    @SerializedName("txRef") override val transactionRef: String,
    @SerializedName("redirect_url") val redirectUrl: String
) : Payload() {

    var pin: String? = null
    @SerializedName("suggested_auth")
    var suggestedAuth: String? = null
    @SerializedName("device_fingerprint")
    var deviceFingerprint: String? = null
    @SerializedName("charge_type")
    var chargeType: String? = null
}

data class AccountPayload(
    override val PBFPubKey: String,
    @SerializedName("accountnumber") val accountNumber: String,
    @SerializedName("accountbank") val accountBank: String,
    override val currency: String,
    override val country: String,
    override val amount: BigDecimal,
    override val email: String,
    @SerializedName("phonenumber") override val phoneNumber: String,
    @SerializedName("lastname") override val firstName: String,
    @SerializedName("lastname") override val lastName: String,
    override val IP: String,
    @SerializedName("txRef") override val transactionRef: String,
    @SerializedName("payment_type") val paymentType: String
) : Payload() {
    //Only used for Zenith bank account payment) (Format: DDMMYYYY e.g. 21051990)
    @SerializedName("passcode")
    val passCode: String? = null

    @SerializedName("device_fingerprint")
    var deviceFingerprint: String? = null
}

class Meta(
    @SerializedName("metaname") val metaName: String, @SerializedName("metavalue") val
    metaValue: String
)

data class RavePayload(val hash: String, val dynamicValue: String)

enum class Environment {
    STAGING,
    LIVE
}

internal data class ChargeRequest(
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

data class ApiResponse<out T>(val status: String, val message: String, val data: T?)

data class ChargeResponseData(
    @SerializedName("suggested_auth") val suggestedAuth: String, val
    chargeResponseCode: String, val authModelUsed: String, val flwRef: String, val
    chargeResponseMessage: String, @SerializedName("authurl") val authUrl: String
)

data class ErrorResponseData(
    @SerializedName("is_error") val isError: Boolean, val code: String,
    val message: String
)

data class ValidateChargePayload(
    val PBFPubKey: String, @SerializedName("transaction_reference")
    val transactionRef: String, val otp: String
)

data class RequeryRequestPayload @JvmOverloads constructor(
    @SerializedName("flw_ref") val flwRef: String, val normalize: String = "1"
) {
    var SECKEY: String = ""
}

data class XRequeryRequestPayload @JvmOverloads constructor(
    @SerializedName("txref") var transactionRef: String = "",
    var flwRef: String = "",
    @SerializedName("last_attempt") var lastAttempt: String = "",
    @SerializedName("only_successful") var onlySuccessful: String = ""
) {
    var SECKEY: String = ""
}

data class RequeryResponseData(
    @SerializedName("tx_ref") val transactionRef: String,
    @SerializedName("flw_ref") val flwRef: String,
    @SerializedName("transaction_currency") val currency: String,
    val amount: BigDecimal,
    @SerializedName("charged_amount")
    val chargedAmount: BigDecimal,
    val card: CardDetails,
    val flwMeta: FlutterWaveMeta
)

data class FlutterWaveMeta(val chargeResponse: String, val chargeResponseMessage: String)

data class CardDetails(
    val cardBIN: String, @SerializedName("card_tokens") val cardTokens:
    List<CardToken>, val brand: String, @SerializedName("expirymonth") val expiryMonth: String,
    @SerializedName("expiryyear") val expiryYear: String, val last4digits: String
)

data class CardToken(
    @SerializedName("shortcode") val shortCode: String, @SerializedName
        ("embedtoken") val embedToken: String
)

data class XRequeryResponseData(
    @SerializedName("txid") val txId: Int,
    @SerializedName("txref") val transactionRef: String,
    @SerializedName("flwref") val flwRef: String,
    @SerializedName("devicefingerprint") val deviceFingerprint: String,
    @SerializedName("cycle") val cycle: String,
    @SerializedName("amount") val amount: Int,
    @SerializedName("currency") val currency: String,
    @SerializedName("chargedamount") val chargedAmount: Double,
    @SerializedName("appfee") val appFee: Double,
    @SerializedName("merchantfee") val merchantFee: Int,
    @SerializedName("merchantbearsfee") val merchantBearsFee: Int,
    @SerializedName("chargecode") val chargeCode: String,
    @SerializedName("chargemessage") val chargeMessage: String,
    @SerializedName("authmodel") val authModel: String,
    @SerializedName("ip") val IP: String,
    val narration: String,
    val status: String,
    @SerializedName("vbvcode") val vbvCode: String,
    @SerializedName("vbvmessage") val vbvMessage: String,
    @SerializedName("authurl") val authUrl: String,
    @SerializedName("paymenttype") val paymentType: String,
    @SerializedName("paymentid") val paymentId: String,
    @SerializedName("fraudstatus") val fraudStatus: String,
    @SerializedName("chargetype") val chargeType: String,
    @SerializedName("createdday") val createdDay: Int,
    @SerializedName("createddayname") val createdDayName: String,
    @SerializedName("createdweek") val createdWeek: Int,
    @SerializedName("createdmonth") val createdMonth: Int,
    @SerializedName("createdmonthname") val createdMonthName: String,
    @SerializedName("createdquarter") val createdQuarter: Int,
    @SerializedName("createdyear") val createdYear: Int,
    @SerializedName("createdyearisleap") val createdYearIsLeap: Boolean,
    @SerializedName("createddayispublicholiday") val createdDayIsPublicHoliday: Int,
    @SerializedName("createdhour") val createdHour: Int,
    @SerializedName("createdminute") val createdMinute: Int,
    @SerializedName("createdpmam") val createdPmam: String,
    @SerializedName("created") val created: String,
    @SerializedName("customerid") val customerId: Int,
    @SerializedName("custphone") val customerPhone: String,
    @SerializedName("custnetworkprovider") val customerNetworkProvider: String,
    @SerializedName("custname") val customerName: String,
    @SerializedName("custemail") val customerEmail: String,
    @SerializedName("custemailprovider") val customerEmailProvider: String,
    @SerializedName("custcreated") val customerCreateDate: String,
    @SerializedName("accountid") val accountId: Int
)

data class PreauthorizeCardData(
    @SerializedName("id") val id: Int,
    @SerializedName("txRef") val transactionRef: String,
    @SerializedName("flwRef") val flwRef: String,
    @SerializedName("redirectUrl") val redirectUrl: String,
    @SerializedName("device_fingerprint") val deviceFingerprint: String,
    @SerializedName("cycle") val cycle: String,
    @SerializedName("amount") val amount: Int,
    @SerializedName("charged_amount") val chargedAmount: Double,
    @SerializedName("appfee") val appFee: Double,
    @SerializedName("merchantfee") val merchantFee: Int,
    @SerializedName("merchantbearsfee") val merchantBearsFee: Int,
    @SerializedName("chargeResponseCode") val chargeResponseCode: String,
    @SerializedName("chargeResponseMessage") val chargeResponseMessage: String,
    @SerializedName("authModelUsed") val authModelUsed: String,
    @SerializedName("currency") val currency: String,
    @SerializedName("IP") val IP: String,
    @SerializedName("narration") val narration: String,
    @SerializedName("status") val status: String,
    @SerializedName("vbvrespmessage") val VBVRespMessage: String,
    @SerializedName("authurl") val authUrl: String,
    @SerializedName("vbvrespcode") val VBVRespCode: String,
    @SerializedName("paymentType") val paymentType: String,
    @SerializedName("paymentId") val paymentId: String,
    @SerializedName("fraud_status") val fraudStatus: String,
    @SerializedName("charge_type") val chargeType: String,
    @SerializedName("is_live") val isLive: Int,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("customerId") val customerId: Int,
    @SerializedName("AccountId") val accountId: Int,
    @SerializedName("customer") val customer: Customer
)

data class Customer(
    @SerializedName("id") val id: Int,
    @SerializedName("phone") val phone: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("AccountId") val accountId: Int
)

data class RefundVoidResponseData(
    @SerializedName("data") val data: RefundVoidResponseExtraData,
    @SerializedName("status") val status: String
)

data class RefundVoidResponseExtraData(
    @SerializedName("responsecode") val responseCode: String,
    @SerializedName("authorizeId") val authorizeId: String,
    @SerializedName("responsemessage") val responseMessage: String,
    @SerializedName("transactionreference") val transactionReference: String
)

/**
 *
 * @property paymentType This is an optional parameter to be used when the payment type is account payment.
 * A value of 2 is to be passed
 *
 * @property card6 This can be used only when the user has entered first 6digits of their card number,
 * it also helps determine international fees on the transaction if the card being used is
 * an international card
 */
data class GetFeesPayload(val amount: BigDecimal, val PBFPubKey: String, val currency: String) {

    @SerializedName("ptype")
    var paymentType: String? = null

    var card6: String? = null
}


data class GetFeeResponseData(
    @SerializedName("charge_amount") val chargeAmount: Double,
    val fee: Double, @SerializedName("merchantfee") val merchantFee: Double,
    @SerializedName("ravefee") val raveFee: Double
)


data class RefundResponseData(
    @SerializedName("AmountRefunded") val amountRefunded: Double,
    val walletId: Int,
    val createdAt: String, @SerializedName("AccountId") val accountId: Int,
    val id: Int,
    @SerializedName("FlwRef") val flwRef: String,
    @SerializedName("TransactionId") val transactionId: Int,
    val status: String,
    val updatedAt: String
)


data class ExchangeRateData(
    @SerializedName("responsecode") val responseCode: String?,
    @SerializedName("responsemessage") val responseMessage: String?,
    val rate: Int?, @SerializedName("origincurrency") val originCurrency: String?,
    @SerializedName("destinationcurrency") val destinationCurrency: String?,
    @SerializedName("lastupdated") val lastUpdated: String?,
    @SerializedName("converted_amount") val convertedAmount: Long?,
    @SerializedName("original_amount") val originalAmount: Long?
)
