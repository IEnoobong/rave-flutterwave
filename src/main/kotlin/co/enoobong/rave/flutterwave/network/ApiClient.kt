package co.enoobong.rave.flutterwave.network

import co.enoobong.rave.flutterwave.config.RavePayBuilder
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
object ApiClient {
    private var uniqueService: ApiService? = null

    val apiService: ApiService
        @Synchronized get() {
            if (uniqueService == null) {
                uniqueService = getService()
            }
            return uniqueService!!
        }

    private fun getService(): ApiService {
        return apiClient.create(ApiService::class.java)
    }

    private var uniqueClient: Retrofit? = null

    private val apiClient: Retrofit
        @Synchronized get() {
            if (uniqueClient == null) {
                val logging = HttpLoggingInterceptor()
                logging.level = if (RavePayBuilder.isTest) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE


                val httpClient = OkHttpClient.Builder()
                    .addNetworkInterceptor(logging)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build()

                uniqueClient = Retrofit.Builder()
                    .baseUrl(RavePayBuilder.getBaseUrl())
                    .client(httpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return uniqueClient!!
        }
}