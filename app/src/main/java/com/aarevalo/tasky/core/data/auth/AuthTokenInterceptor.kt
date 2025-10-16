package com.aarevalo.tasky.core.data.auth

import androidx.datastore.core.IOException
import com.aarevalo.tasky.core.data.remote.api.TaskyRefreshTokenApi
import com.aarevalo.tasky.core.data.remote.dto.RefreshTokenRequest
import com.aarevalo.tasky.core.data.remote.dto.RefreshTokenResponse
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import com.aarevalo.tasky.core.domain.user.AuthenticatedUser
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.HttpException

class AuthTokenInterceptor(
    private val sessionStorage: SessionStorage,
    private val refreshTokenApi: TaskyRefreshTokenApi
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        return runBlocking {
            val requestBuilder = chain.request().newBuilder()
            val token = sessionStorage.getSession()?.accessToken
            if (!token.isNullOrBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            val response = chain.proceed(requestBuilder.build())

            if(response.isSuccessful || response.code != 401) {
                return@runBlocking response
            }

            val request = RefreshTokenRequest(
                refreshToken = sessionStorage.getSession()?.refreshToken ?: return@runBlocking response
            )

            val accessTokenResponse: RefreshTokenResponse = try {
                refreshTokenApi.refresh(request)
            } catch(e: IOException) {
                return@runBlocking response
            } catch(e: HttpException) {
                sessionStorage.setSession(null)
                return@runBlocking response
            }

            sessionStorage.setSession(
                AuthenticatedUser(
                    accessToken = accessTokenResponse.accessToken,
                    refreshToken = accessTokenResponse.refreshToken
                )
            )

            response.close()

            chain.proceed(
                chain.request()
                    .newBuilder()
                    .addHeader(
                        "Authorization",
                        "Bearer ${accessTokenResponse.accessToken}"
                    )
                    .build()
            )
        }
    }
}