package co.enoobong.rave.flutterwave.service

import co.enoobong.rave.flutterwave.data.AccountPayload
import co.enoobong.rave.flutterwave.data.CardPayload
import co.enoobong.rave.flutterwave.data.ChargeRequest
import co.enoobong.rave.flutterwave.data.Environment
import co.enoobong.rave.flutterwave.data.GetFeesPayload
import co.enoobong.rave.flutterwave.data.RequeryRequestPayload
import co.enoobong.rave.flutterwave.data.ValidateChargePayload
import co.enoobong.rave.flutterwave.data.XRequeryRequestPayload
import co.enoobong.rave.flutterwave.util.encryptRequest
import co.enoobong.rave.flutterwave.util.encryptSecretKey
import co.enoobong.rave.flutterwave.util.toJsonString
import com.google.gson.JsonObject
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/**
 * @author Ibanga Enoobong I
 * @since 3/10/18.
 */
class RavePaySpec : Spek({
    given("a valid RavePay instance") {

        val secretKey = "FLWSECK-bb971402072265fb156e90a3578fe5e6-X"
        val publicKey = "FLWPUBK-1c0065cff0c9141555198872abc3ba08-X"

        val ravePayBuilder = RavePay.Builder()
            .setEnvironment(Environment.STAGING)
            .setPublicKey(publicKey)
            .setSecretKey(secretKey)

        val ravePay = ravePayBuilder.build()

        beforeEachTest {
            ravePay.apiService = mock()
        }

        on("charge card called") {
            val cardPayload = mock<CardPayload>()

            val chargeRequest = ChargeRequest(
                encryptRequest(
                    cardPayload.toJsonString(),
                    encryptSecretKey(ravePayBuilder.userSecretKey)
                )
            )
            chargeRequest.publicKey = ravePayBuilder.userPublicKey

            val apiService = ravePay.apiService

            whenever(apiService.directCharge(chargeRequest)).thenReturn(mock())

            ravePay.chargeCard(cardPayload, mock())

            it("should invoke direct charge endpoint") {
                verify(apiService).directCharge(chargeRequest)
            }
        }

        on("charge account called") {
            val accountPayload = mock<AccountPayload>()

            val chargeRequest = ChargeRequest(
                encryptRequest(
                    accountPayload.toJsonString(),
                    encryptSecretKey(ravePayBuilder.userSecretKey)
                )
            )
            chargeRequest.publicKey = ravePayBuilder.userPublicKey

            val apiService = ravePay.apiService

            whenever(apiService.directCharge(chargeRequest)).thenReturn(mock())

            ravePay.chargeAccount(accountPayload, mock())

            it("should invoke direct charge endpoint") {
                verify(apiService).directCharge(chargeRequest)
            }
        }

        on("get banks called") {
            val apiService = ravePay.apiService

            whenever(apiService.getBanks()).thenReturn(mock())

            ravePay.getBanks(mock())

            it("should invoke get banks endpoint") {
                verify(apiService).getBanks()
            }
        }

        on("validate card charge called") {
            val apiService = ravePay.apiService

            val validateChargePayload = mock<ValidateChargePayload>()
            validateChargePayload.publicKey = ravePayBuilder.userPublicKey

            whenever(apiService.validateCardCharge(validateChargePayload)).thenReturn(mock())

            ravePay.validateCardCharge(validateChargePayload, mock())

            it("should invoke validate charge endpoint") {
                verify(apiService).validateCardCharge(validateChargePayload)
            }
        }

        on("validate account charge called") {
            val apiService = ravePay.apiService

            val validateChargePayload = mock<ValidateChargePayload>()
            validateChargePayload.publicKey = ravePayBuilder.userPublicKey

            whenever(apiService.validateAccountCharge(validateChargePayload)).thenReturn(mock())

            ravePay.validateAccountCharge(validateChargePayload, mock())

            it("should invoke validate charge endpoint") {
                verify(apiService).validateAccountCharge(validateChargePayload)
            }
        }

        on("verify transaction called") {
            val apiService = ravePay.apiService

            val requeryRequestPayload = mock<RequeryRequestPayload>()
            requeryRequestPayload.secretKey = ravePayBuilder.userSecretKey

            whenever(apiService.requeryTransaction(requeryRequestPayload)).thenReturn(mock())

            ravePay.verifyTransaction(requeryRequestPayload, mock())

            it("should invoke requery endpoint") {
                verify(apiService).requeryTransaction(requeryRequestPayload)
            }
        }

        on("x verify transaction called") {
            val apiService = ravePay.apiService

            val xRequeryRequestPayload = mock<XRequeryRequestPayload>()
            xRequeryRequestPayload.secretKey = ravePayBuilder.userSecretKey

            whenever(apiService.xRequeryTransaction(xRequeryRequestPayload)).thenReturn(mock())

            ravePay.verifyTransactionUsingXRequery(xRequeryRequestPayload, mock())

            it("should invoke requery endpoint") {
                verify(apiService).xRequeryTransaction(xRequeryRequestPayload)
            }
        }

        on("preauthorize card called") {
            val apiService = ravePay.apiService

            val cardPayload = mock<CardPayload>()
            cardPayload.chargeType = "preauth"

            val chargeRequest = ChargeRequest(
                encryptRequest(
                    cardPayload.toJsonString(),
                    encryptSecretKey(ravePayBuilder.userSecretKey)
                )
            )
            chargeRequest.publicKey = ravePayBuilder.userPublicKey

            whenever(apiService.directCharge(chargeRequest)).thenReturn(mock())

            ravePay.preauthorizeCard(cardPayload, mock())

            it("should invoke direct charge endpoint") {
                verify(apiService).directCharge(chargeRequest)
            }
        }

        on("capture preauthorized funds called") {
            val apiService = ravePay.apiService

            val flwRef = "FLW-MOCK-f93f6279e95ec91e8b92b1ba925f4822"
            val jsonObject = JsonObject()
            jsonObject.addProperty("flwRef", flwRef)
            jsonObject.addProperty("SECKEY", ravePayBuilder.userSecretKey)

            whenever(apiService.capturePreauthorizeCard(jsonObject)).thenReturn(mock())

            ravePay.capturePreauthorizedFunds(flwRef, mock())

            it("should invoke capture preauthorized card endpoint") {
                verify(apiService).capturePreauthorizeCard(jsonObject)
            }
        }

        on("refund or void preauthorization called") {
            val apiService = ravePay.apiService

            val flwRef = "FLW-MOCK-f93f6279e95ec91e8b92b1ba925f4822"
            val action = "refund"
            val jsonObject = JsonObject()
            jsonObject.addProperty("ref", flwRef)
            jsonObject.addProperty("action", action)
            jsonObject.addProperty("SECKEY", ravePayBuilder.userSecretKey)

            whenever(apiService.refundOrVoidPreauthorization(jsonObject)).thenReturn(mock())

            ravePay.refundOrVoidPreauthorization(flwRef, action, mock())

            it("should invoke refund or void preauthorization endpoint") {
                verify(apiService).refundOrVoidPreauthorization(jsonObject)
            }
        }

        on("get fees called") {
            val apiService = ravePay.apiService

            val feesPayload = mock<GetFeesPayload>()
            feesPayload.publicKey = ravePayBuilder.userPublicKey

            whenever(apiService.getFees(feesPayload)).thenReturn(mock())

            ravePay.getFees(feesPayload, mock())

            it("should invoke get fees endpoint") {
                verify(apiService).getFees(feesPayload)
            }
        }

        on("refund transaction called") {
            val apiService = ravePay.apiService

            val flwRef = "FLW-MOCK-f93f6279e95ec91e8b92b1ba925f4822"
            val jsonObject = JsonObject()
            jsonObject.addProperty("ref", flwRef)
            jsonObject.addProperty("seckey", ravePayBuilder.userSecretKey)

            whenever(apiService.refundTransaction(jsonObject)).thenReturn(mock())

            ravePay.refundTransaction(flwRef, mock())

            it("should invoke refund transaction endpoint") {
                verify(apiService).refundTransaction(jsonObject)
            }
        }

        on("convert currency called") {
            val apiService = ravePay.apiService

            val originCurrency = "NGN"
            val destinationCurrency = "USD"

            val jsonObject = JsonObject()
            jsonObject.addProperty("origin_currency", originCurrency)
            jsonObject.addProperty("destination_currency", destinationCurrency)
            jsonObject.addProperty("amount", null as Double?)
            jsonObject.addProperty("SECKEY", ravePayBuilder.userSecretKey)

            whenever(apiService.convertCurrency(jsonObject)).thenReturn(mock())

            ravePay.convertCurrency(originCurrency, destinationCurrency, null, mock())

            it("should invoke convert currency endpoint") {
                verify(apiService).convertCurrency(jsonObject)
            }
        }

        on("ussd charge called") {
            val apiService = ravePay.apiService

            val accountPayload = mock<AccountPayload>()

            val chargeRequest = ChargeRequest(
                encryptRequest(
                    accountPayload.toJsonString(),
                    encryptSecretKey(ravePayBuilder.userSecretKey)
                )
            )
            chargeRequest.publicKey = ravePayBuilder.userPublicKey

            whenever(apiService.directCharge(chargeRequest)).thenReturn(mock())

            ravePay.ussdCharge(accountPayload, mock())

            it("should invoke direct charge endpoint") {
                verify(apiService).directCharge(chargeRequest)
            }
        }
    }
})