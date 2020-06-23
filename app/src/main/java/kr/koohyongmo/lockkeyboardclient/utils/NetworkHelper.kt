package kr.koohyongmo.lockkeyboardclient.utils

import kr.koohyongmo.lockkeyboardclient.keyboard.Constants
import kr.koohyongmo.lockkeyboardclient.keyboard.model.service.RetrofitService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by KooHyongMo on 2020/06/23
 */
object NetworkHelper {

    val instance: RetrofitService
        get() {
            return create()
        }

    fun create(): RetrofitService {
        val builder = OkHttpClient.Builder()
        val logger = HttpLoggingInterceptor()
        logger.apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
        builder.addInterceptor(logger)

        val client = builder.build()
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.SERVER_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(RetrofitService::class.java)
    }
}