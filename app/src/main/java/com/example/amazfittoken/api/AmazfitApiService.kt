package com.example.amazfittoken.api

import com.example.amazfittoken.model.DeviceResponse
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
    
    @GET("users/{userId}/devices")
    suspend fun getDevices(
        @Path("userId") userId: String,
        @Header("apptoken") appToken: String,
        @Query("enableMultiDevice") enableMultiDevice: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("page") page: Int? = null,
        @Query("pageSize") pageSize: Int? = null
    ): Response<DeviceResponse>
}

object ApiConstants {
    const val TOKENS_BASE_URL = "https://api-user.huami.com/"
    const val LOGIN_BASE_URL = "https://account.huami.com/"
    const val DEVICES_BASE_URL = "https://api-mifit-us2.huami.com/"
    
    // Redirect URI used in OAuth flow - this is what Huami expects
    const val REDIRECT_URI = "https://s3-us-west-2.amazonws.com/hm-registration/successsignin.html"
    
    // Alternative redirect URIs for different regions (if needed in future)
    const val REDIRECT_URI_EU = "https://s3-eu-west-1.amazonws.com/hm-registration/successsignin.html"
    const val REDIRECT_URI_ASIA = "https://s3-ap-southeast-1.amazonws.com/hm-registration/successsignin.html"
}