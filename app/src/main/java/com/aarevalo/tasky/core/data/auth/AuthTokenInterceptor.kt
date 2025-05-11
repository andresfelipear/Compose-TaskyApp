package com.aarevalo.tasky.core.data.auth

import com.aarevalo.tasky.core.data.remote.api.RefreshTokenApi
import com.aarevalo.tasky.core.data.remote.dto.AccessTokenRequest
import com.aarevalo.tasky.core.data.remote.dto.AccessTokenResponse
import com.aarevalo.tasky.core.domain.preferences.TokenPreferences
import com.aarevalo.tasky.core.domain.preferences.UserPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthTokenInterceptor(
    private val tokenPreferences: TokenPreferences,
    private val userPreferences: UserPreferences,
    private val refreshTokenApi: RefreshTokenApi
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(
            request = chain.request()
            .newBuilder()
                .addHeader("Authorization", "Bearer ${tokenPreferences.readAccessToken()}")
                .build()
        )

        if(response.isSuccessful || response.code != 401){
            return response
        }

        return runBlocking {
            val request = AccessTokenRequest(
                refreshToken = tokenPreferences.readRefreshToken() ?: return@runBlocking response,
                userId = userPreferences.loadUserId() ?: return@runBlocking response
            )

            val accessTokenResponse: AccessTokenResponse = try {
                refreshTokenApi.getAccessToken(request)
            } catch (e: Exception){
                return@runBlocking response
            } catch (e: Exception){
                return@runBlocking response
            }
            tokenPreferences.saveAccessToken(accessTokenResponse.accessToken)

            response.close()
            chain.proceed(
                chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer ${accessTokenResponse.accessToken}")
                    .build()
            )
        }
    }
}