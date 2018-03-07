package co.enoobong.rave.flutterwave.network

import co.enoobong.rave.flutterwave.config.RaveConstants
import co.enoobong.rave.flutterwave.data.Environment
import co.enoobong.rave.flutterwave.service.RavePayBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


/**
 * @author Ibanga Enoobong I
 * @since 2/27/18.
 */
internal object ApiClient {
    private var uniqueService: ApiService? = null

    val apiService: ApiService
        get() {
            if (uniqueService == null) {
                synchronized(this) {
                    uniqueService = apiClient.create(ApiService::class.java)
                }
            }
            return uniqueService!!
        }

    private var uniqueClient: Retrofit? = null

    private val apiClient: Retrofit
        get() {
            if (uniqueClient == null) {
                synchronized(this) {
                    val logging = HttpLoggingInterceptor()
                    val isStagingEnvironment =
                        RavePayBuilder.getInstance().whichEnvironment == Environment.STAGING

                    logging.level = if (isStagingEnvironment) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE


                    val httpClient = OkHttpClient.Builder()
                        .addNetworkInterceptor(logging)
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .build()

                    val baseUrl = if (isStagingEnvironment)
                        RaveConstants.STAGING_URL else RaveConstants.LIVE_URL

                    uniqueClient = Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .client(httpClient)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                }
            }
            return uniqueClient!!
        }
}