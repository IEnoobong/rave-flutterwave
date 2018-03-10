package co.enoobong.rave.flutterwave.service

import co.enoobong.rave.flutterwave.data.Environment
import co.enoobong.rave.flutterwave.network.ApiService
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
    given("a valid rave pay instance") {
        val secretKey = "FLWSECK-bb971402072265fb156e90a3578fe5e6-X"
        val publicKey = "FLWPUBK-1c0065cff0c9141555198872abc3ba08-X"


        val ravePay = RavePay.Builder()
            .setEnvironment(Environment.LIVE)
            .setPublicKey(publicKey)
            .setSecretKey(secretKey)
            .build()

        on("Charge card called") {
            val apiService = mock<ApiService>()
            ravePay.apiService = apiService
            whenever(apiService.directCharge(mock())).thenReturn(mock())
            ravePay.chargeCard(mock(), mock())
            it("should invoke direct charge api call") {
                verify(apiService).directCharge(mock())
            }
        }
    }

})