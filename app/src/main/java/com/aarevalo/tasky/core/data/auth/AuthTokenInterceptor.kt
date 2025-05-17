package com.aarevalo.tasky.core.data.auth

import androidx.datastore.core.IOException
import com.aarevalo.tasky.core.data.remote.api.TaskyRefreshTokenApi
import com.aarevalo.tasky.core.data.remote.dto.AccessTokenRequest
import com.aarevalo.tasky.core.data.remote.dto.AccessTokenResponse
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
            val response = chain.proceed(
                request = chain.request()
                    .newBuilder()
                    .addHeader(
                        "Authorization",
                        "Bearer ${sessionStorage.getSession()?.accessToken}"
                    )
                    .build()
            )

            if(response.isSuccessful || response.code != 401) {
                return@runBlocking response
            }

            val request = AccessTokenRequest(
                refreshToken = sessionStorage.getSession()?.refreshToken ?: return@runBlocking response,
                userId = sessionStorage.getSession()?.userId ?: return@runBlocking response
            )

            val accessTokenResponse: AccessTokenResponse = try {
                refreshTokenApi.getAccessToken(request)
            } catch(e: IOException) {
                return@runBlocking response
            } catch(e: HttpException) {
                sessionStorage.setSession(null)
                return@runBlocking response
            }

            sessionStorage.setSession(
                AuthenticatedUser(
                    accessToken = accessTokenResponse.accessToken
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