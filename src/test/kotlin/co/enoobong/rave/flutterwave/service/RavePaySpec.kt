package co.enoobong.rave.flutterwave.service

import co.enoobong.rave.flutterwave.data.AccountPayload
import co.enoobong.rave.flutterwave.data.Callbacks
import co.enoobong.rave.flutterwave.data.CardPayload
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/**
 * @author Ibanga Enoobong I
 * @since 3/5/18.
 */
class RavePaySpec : Spek({
    given("a rave pay instance") {
        val ravePayMock = mock<RavePay>()

        on("charge charge card called") {
            val cardPayload = mock<CardPayload>()
            val callback = mock<Callbacks.OnChargeRequestComplete>()
            ravePayMock.chargeCard(cardPayload, callback)

            it("charge card method should be called") {
                verify(ravePayMock).chargeCard(cardPayload, callback)
            }
        }

        on("charge account called") {
            val cardPayload = mock<AccountPayload>()
            val callback = mock<Callbacks.OnChargeRequestComplete>()
            ravePayMock.chargeAccount(cardPayload, callback)

            it("charge method method should be called") {
                verify(ravePayMock).chargeAccount(cardPayload, callback)
            }
        }

        on("get banks called") {
            val callback = mock<Callbacks.OnGetBanksRequestComplete>()
            ravePayMock.getBanks(callback)

            it("charge method method should be called") {
                verify(ravePayMock).getBanks(callback)
            }
        }
    }

})

