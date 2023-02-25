package com.localfox.partner.app;

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object APIClient {

    var context = MyApplication.applicationContext().getAppContext()
    val cacheSize = 20 * 1024 * 1024L // 10 MB


    fun getClient(baseUrl: String, context: Context): Retrofit {


        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY



        val cache = Cache(context.getCacheDir(), cacheSize)
        MyApplication.applicationContext().client = OkHttpClient.Builder().cache(cache).addInterceptor(provideOfflineCacheInterceptor()).addInterceptor(interceptor)
            .addNetworkInterceptor(provideCacheInterceptor()).build()


        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(MyApplication.applicationContext().client)
            .build()

        return retrofit
    }


    private fun provideCacheInterceptor(): Interceptor? {
        return Interceptor { chain ->
            val response: Response = chain.proceed(chain.request())
            val cacheControl: CacheControl
            cacheControl = if (isConnected()) {
                CacheControl.Builder()
                    .maxAge(0, TimeUnit.SECONDS)
                    .build()
            } else {
               // MyApplication.applicationContext().showInvalidErrorToast()
                CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build()
            }
            response.newBuilder()
                .build()
        }
    }

    private fun provideOfflineCacheInterceptor(): Interceptor? {
        return Interceptor { chain ->
            var request: Request = chain.request()
            if (!isConnected()) {
                val cacheControl = CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build()
                request = request.newBuilder()
                    .cacheControl(cacheControl)
                    .build()
            }
            chain.proceed(request)
        }
    }

    fun isConnected(): Boolean {
        try {
            val e = context.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val activeNetwork = e.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        } catch (e: Exception) {
            Log.w("TAG", e.toString())
        }
        return false
    }

}
