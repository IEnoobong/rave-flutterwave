package co.enoobong.rave.flutterwave.service

/**
 * @author Ibanga Enoobong I
 * @since 3/6/18.
 */
interface RaveCallback<in T> {

    fun onSuccess(response: T, responseAsString: String = "")

    fun onError(message: String?, responseAsString: String? = "")
}