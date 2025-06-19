package com.example.amazfittoken.model

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("token_info")
    val tokenInfo: TokenInfo?,
    @SerializedName("access_token")
    val accessToken: String?,
    @SerializedName("token_type")
    val tokenType: String?,
    @SerializedName("expires_in")
    val expiresIn: Int?
)

data class TokenInfo(
    @SerializedName("access_token")
    val accessToken: String?,
    @SerializedName("user_id")
    val userId: String?,
    @SerializedName("country_code")
    val countryCode: String?
)