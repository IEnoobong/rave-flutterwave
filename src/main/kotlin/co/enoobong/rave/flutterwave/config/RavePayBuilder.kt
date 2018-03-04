package co.enoobong.rave.flutterwave.config

import co.enoobong.rave.flutterwave.service.RavePay
import co.enoobong.rave.flutterwave.util.toMd5
import java.util.logging.Logger
import kotlin.properties.Delegates


/**
 * @author Ibanga Enoobong I
 * @since 2/27/18.
 */

internal object RavePayBuilder {

    private val L = Logger.getLogger(RavePayBuilder::class.java.simpleName)
    private const val TARGET = "FLWSECK-"

    var isTest = true
    private var userSecretKey: String by Delegates.notNull()


    @JvmStatic
    fun setEnvironment(testEnvironment: Boolean = true): RavePayBuilder {
        isTest = testEnvironment
        return this
    }

    @JvmStatic
    fun setSecurityKey(secretKey: String): RavePayBuilder {
        val md5Hash = secretKey.toMd5()
        val cleanSecret = secretKey.replace(TARGET, "")
        val hashLength = md5Hash.length
        val encryptionKey =
            cleanSecret.substring(0, 12) + md5Hash.substring(hashLength - 12, hashLength)
        userSecretKey = encryptionKey
        return this
    }

    @JvmStatic
    fun build(): RavePay {
        if (userSecretKey.isEmpty() or userSecretKey.isBlank())
            throw IllegalArgumentException("Security key cannot be empty or blank")
        return RavePay.getInstance(userSecretKey)
    }

    fun getBaseUrl(): String {
        return if (isTest) {
            RaveConstants.STAGING_URL
        } else {
            RaveConstants.LIVE_URL
        }
    }

}