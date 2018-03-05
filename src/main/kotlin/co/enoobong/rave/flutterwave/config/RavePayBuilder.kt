package co.enoobong.rave.flutterwave.config

import co.enoobong.rave.flutterwave.data.Environment
import co.enoobong.rave.flutterwave.service.RavePay
import co.enoobong.rave.flutterwave.util.toMd5
import org.jetbrains.annotations.TestOnly
import java.util.logging.Logger


/**
 * @author Ibanga Enoobong I
 * @since 2/27/18.
 */

internal object RavePayBuilder {

    private val L = Logger.getLogger(RavePayBuilder::class.java.simpleName)
    private const val TARGET = "FLWSECK-"

    var whichEnvironment = Environment.STAGING
    private var userSecretKey = ""


    @JvmStatic
    fun setEnvironment(environment: Environment): RavePayBuilder {
        whichEnvironment = environment
        return this
    }

    @JvmStatic
    fun setSecurityKey(secretKey: String): RavePayBuilder {
        userSecretKey = encryptSecretKey(secretKey)
        return this
    }

    @TestOnly
    fun getSecurityKey(): String {
        return userSecretKey
    }

    fun encryptSecretKey(secretKey: String): String {
        val md5Hash = secretKey.toMd5()
        val cleanSecret = secretKey.replace(TARGET, "")
        val hashLength = md5Hash.length
        return cleanSecret.substring(0, 12) + md5Hash.substring(hashLength - 12, hashLength)
    }

    @JvmStatic
    fun build(): RavePay {
        if (userSecretKey.isEmpty() or userSecretKey.isBlank()) {
            L.severe("Secret Key was empty or blank")
            throw IllegalArgumentException("Secret key cannot be empty or blank")
        }
        return RavePay.getInstance(userSecretKey)
    }

    fun getBaseUrl(): String {
        return if (whichEnvironment == Environment.STAGING) {
            RaveConstants.STAGING_URL
        } else {
            RaveConstants.LIVE_URL
        }
    }

}