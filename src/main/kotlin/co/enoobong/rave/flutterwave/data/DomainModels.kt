package co.enoobong.rave.flutterwave.data

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

/**
 * @author Ibanga Enoobong I
 * @since 2/27/18.
 */
open class Payload(
    val PBFPubKey: String,
    val amount: BigDecimal,
    val country: Country,
    val currency: Currency,
    val email: String,
    val IP: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastname") val lastName: String,
    @SerializedName("txRef") val transactionRef: String
)

class CardPayload(
    PBFPubKey: String,
    @SerializedName("cardno") val cardNumber: String,
    val cvv: String,
    @SerializedName("expirymonth") val expiryMonth: String,
    @SerializedName("expiryyear") val expiryYear: String,
    currency: Currency, country: Country,
    amount: BigDecimal,
    email: String,
    @SerializedName("phonenumber") val phoneNumber: String,
    firstName: String,
    lastName: String,
    IP: String,
    transactionRef: String,
    @SerializedName("redirect_url") val redirectUrl: String
) : Payload(PBFPubKey, amount, country, currency, email, IP, firstName, lastName, transactionRef) {

    var pin: String? = null
    @SerializedName("suggested_auth")
    var suggestedAuth: String? = null
    @SerializedName("device_fingerprint")
    var deviceFingerprint: String? = null
    @SerializedName("charge_type")
    var chargeType: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CardPayload) return false

        if (cardNumber != other.cardNumber) return false
        if (cvv != other.cvv) return false
        if (expiryMonth != other.expiryMonth) return false
        if (expiryYear != other.expiryYear) return false
        if (phoneNumber != other.phoneNumber) return false
        if (redirectUrl != other.redirectUrl) return false
        if (pin != other.pin) return false
        if (suggestedAuth != other.suggestedAuth) return false
        if (deviceFingerprint != other.deviceFingerprint) return false
        if (chargeType != other.chargeType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cardNumber.hashCode()
        result = 31 * result + cvv.hashCode()
        result = 31 * result + expiryMonth.hashCode()
        result = 31 * result + expiryYear.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + redirectUrl.hashCode()
        result = 31 * result + (pin?.hashCode() ?: 0)
        result = 31 * result + (suggestedAuth?.hashCode() ?: 0)
        result = 31 * result + (deviceFingerprint?.hashCode() ?: 0)
        result = 31 * result + (chargeType?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "CardPayload(cardNumber='$cardNumber', cvv='$cvv', expiryMonth='$expiryMonth', expiryYear='$expiryYear', phoneNumber='$phoneNumber', redirectUrl='$redirectUrl', pin=$pin, suggestedAuth=$suggestedAuth, deviceFingerprint=$deviceFingerprint, chargeType=$chargeType)"
    }

}

class AccountPayload(
    PBFPubKey: String,
    @SerializedName("accountnumber") val accountNumber: String,
    @SerializedName("accountbank") val accountBank: String,
    currency: Currency,
    country: Country,
    amount: BigDecimal,
    email: String,
    @SerializedName("phonenumber") val phoneNumber: String,
    firstName: String,
    lastName: String,
    IP: String,
    transactionRef: String,
    @SerializedName("payment_type") val paymentType: String
) : Payload(PBFPubKey, amount, country, currency, email, IP, firstName, lastName, transactionRef) {
    //Only used for Zenith bank account payment) (Format: DDMMYYYY e.g. 21051990)
    @SerializedName("passcode")
    val passCode: String? = null

    @SerializedName("device_fingerprint")
    var deviceFingerprint: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AccountPayload) return false

        if (accountNumber != other.accountNumber) return false
        if (accountBank != other.accountBank) return false
        if (phoneNumber != other.phoneNumber) return false
        if (paymentType != other.paymentType) return false
        if (passCode != other.passCode) return false
        if (deviceFingerprint != other.deviceFingerprint) return false

        return true
    }

    override fun hashCode(): Int {
        var result = accountNumber.hashCode()
        result = 31 * result + accountBank.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + paymentType.hashCode()
        result = 31 * result + (passCode?.hashCode() ?: 0)
        result = 31 * result + (deviceFingerprint?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "AccountPayload(accountNumber='$accountNumber', accountBank='$accountBank', phoneNumber='$phoneNumber', paymentType='$paymentType', passCode=$passCode, deviceFingerprint=$deviceFingerprint)"
    }


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
