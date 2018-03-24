package co.enoobong.rave.flutterwave.service

import co.enoobong.rave.flutterwave.data.ApiResponse

/**
 * Communicates responses from a server or offline requests. One and only one method will be
 * invoked in response to a given request.
 *
 * @author Ibanga Enoobong I
 * @since 3/6/18.
 */
interface RaveCallback<in T : ApiResponse<*>> {

    /**
     *  Callback for a successful request i.e 200...300 HTTP codes
     *
     *  @param response the parsed JSON response capturing the most essential fields
     *  @param responseAsJsonString the complete JSON response
     */
    fun onSuccess(response: T, responseAsJsonString: String = "")

    /**
     * Callback for an unsuccessful request
     *
     * @param message important message of cause of failure
     * @param responseAsJsonString the complete JSON response
     */
    fun onError(message: String?, responseAsJsonString: String? = "")
}