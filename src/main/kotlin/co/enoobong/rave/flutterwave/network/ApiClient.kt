package co.enoobong.rave.flutterwave.network

import co.enoobong.rave.flutterwave.config.RaveConstants
import co.enoobong.rave.flutterwave.data.Environment
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

    fun createApiService(environment: Environment): ApiService {
        return createApiClient(environment).create(ApiService::class.java)
    }

    private fun createApiClient(environment: Environment): Retrofit {
        val logging = HttpLoggingInterceptor()
        val isStagingEnvironment = environment == Environment.STAGING

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

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}