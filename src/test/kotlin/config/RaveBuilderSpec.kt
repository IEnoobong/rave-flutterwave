package config

import co.enoobong.rave.flutterwave.data.Environment
import co.enoobong.rave.flutterwave.service.RavePay
import co.enoobong.rave.flutterwave.service.RavePayBuilder
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

/**
 * @author Ibanga Enoobong I
 * @since 3/4/18.
 */

class RaveBuilderSpec : Spek({

    given("a rave pay builder") {
        val secretKey = "FLWSECK-bb971402072265fb156e90a3578fe5e6-X"
        val ravePayBuilder = RavePayBuilder.getInstance()

        on("build called without secret key") {
            it("should throw illegal argument exception") {
                assertFailsWith<IllegalArgumentException> {
                    ravePayBuilder.build()
                }
            }
        }

        on("Properly filled arguments") {
            val ravePay = ravePayBuilder.setSecurityKey(secretKey)
                .setEnvironment(Environment.STAGING)
                .build()
            it("construct RavePay instance") {
                assertSame(RavePay.getInstance(secretKey), ravePay)
            }
        }
    }

})