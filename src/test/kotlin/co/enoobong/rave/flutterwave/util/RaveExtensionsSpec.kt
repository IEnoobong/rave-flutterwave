package co.enoobong.rave.flutterwave.util

import co.enoobong.rave.flutterwave.data.AccountPayload
import co.enoobong.rave.flutterwave.data.CardPayload
import com.google.gson.JsonParser
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.util.Date
import kotlin.test.assertTrue

/**
 * @author Ibanga Enoobong I
 * @since 3/12/18.
 */
class RaveExtensionsSpec : Spek({
    given("payload objects") {
        val cardPayload = CardPayload(
            "5438898014560229", "812", "08", "20", "NGN", "NG", 1000.0,
            "eno@eno.com",
            "07061234567", "Eno Wa", "Eno", "192.168.1.2", "TEST " + Date(),
            "http://test.test"
        )

        val accountPayload = AccountPayload(
            "1223", "UBA", "USD", "NG", 1000.0, "eno@ay",
            "019191", "Eno w", "eno", "123.45.54", "TESBA", "j"
        )

        on("payload to json string called") {
            val cardPayloadAsJson = cardPayload.toJsonString()
            val accountPayloadAsJson = accountPayload.toJsonString()

            it("should be a json object") {
                val cardPayloadIsJsonObject = JsonParser().parse(cardPayloadAsJson).isJsonObject
                assertTrue(cardPayloadIsJsonObject)
                val accountPayloadIsJsonObject =
                    JsonParser().parse(accountPayloadAsJson).isJsonObject
                assertTrue(accountPayloadIsJsonObject)
            }
        }
    }
})