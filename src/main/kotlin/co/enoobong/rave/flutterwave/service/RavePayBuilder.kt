package co.enoobong.rave.flutterwave.service

import co.enoobong.rave.flutterwave.data.Environment
import java.util.logging.Logger


/**
 * @author Ibanga Enoobong I
 * @since 2/27/18.
 */

class RavePayBuilder private constructor() {

    companion object {
        private val L = Logger.getLogger(RavePayBuilder::class.java.simpleName)

        private var INSTANCE: RavePayBuilder? = null

        @JvmStatic
        fun getInstance(): RavePayBuilder {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = RavePayBuilder()
                }
            }
            return INSTANCE!!
        }
    }

    internal var whichEnvironment = Environment.STAGING
    private var userSecretKey = ""


    fun setEnvironment(environment: Environment) = this.also {
        whichEnvironment = environment
    }


    fun setSecurityKey(secretKey: String) = this.also {
        userSecretKey = secretKey
    }


    fun build(): RavePay {
        if (userSecretKey.isEmpty() or userSecretKey.isBlank()) {
            L.severe("Secret Key was empty or blank")
            throw IllegalArgumentException("Secret key cannot be empty or blank")
        }
        return RavePay.getInstance(userSecretKey)
    }

}