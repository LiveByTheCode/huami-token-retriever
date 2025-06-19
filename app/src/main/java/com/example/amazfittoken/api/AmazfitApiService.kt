package com.example.amazfittoken.api

import com.example.amazfittoken.model.LoginResponse
import com.example.amazfittoken.model.TokenResponse
import retrofit2.Response
import retrofit2.http.*

interface AmazfitApiService {
    
    @FormUrlEncoded
    @POST("registrations/{email}/tokens")
    suspend fun getTokens(
        @Path("email") email: String,
        @FieldMap tokenRequest: Map<String, String>
    ): Response<TokenResponse>
    
    @FormUrlEncoded
    @POST("v2/client/login")
    suspend fun login(
        @FieldMap loginRequest: Map<String, String>
    ): Response<okhttp3.ResponseBody>
}

object ApiConstants {
    const val TOKENS_BASE_URL = "https://api-user.huami.com/"
    const val LOGIN_BASE_URL = "https://account.huami.com/"
}