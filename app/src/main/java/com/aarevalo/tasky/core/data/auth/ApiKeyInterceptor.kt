package com.aarevalo.tasky.core.data.auth

import com.aarevalo.tasky.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request()
                .newBuilder()
                .addHeader(
                    "x-api-key",
                    BuildConfig.API_KEY
                )
                .build()
        )
    }
}