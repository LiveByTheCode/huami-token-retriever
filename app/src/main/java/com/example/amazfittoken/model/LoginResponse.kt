package com.example.amazfittoken.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token_info")
    val tokenInfo: LoginTokenInfo?,
    @SerializedName("regist_info")
    val registInfo: RegistInfo?,
    @SerializedName("thirdparty_info")
    val thirdpartyInfo: ThirdpartyInfo?,
    @SerializedName("result")
    val result: String?,
    @SerializedName("domain")
    val domain: Domain?,
    @SerializedName("domains")
    val domains: List<DomainInfo>?
)

data class LoginTokenInfo(
    @SerializedName("login_token")
    val loginToken: String?,
    @SerializedName("app_token")
    val appToken: String?,
    @SerializedName("user_id")
    val userId: String?,
    @SerializedName("ttl")
    val ttl: Long?,
    @SerializedName("app_ttl")
    val appTtl: Long?
)

data class RegistInfo(
    @SerializedName("is_new_user")
    val isNewUser: Int?,
    @SerializedName("regist_date")
    val registDate: Long?,
    @SerializedName("region")
    val region: String?,
    @SerializedName("country_code")
    val countryCode: String?,
    @SerializedName("countryState")
    val countryState: String?
)

data class ThirdpartyInfo(
    @SerializedName("nickname")
    val nickname: String?,
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("third_id")
    val thirdId: String?,
    @SerializedName("email")
    val email: String?
)

data class Domain(
    @SerializedName("id-dns")
    val idDns: String?
)

data class DomainInfo(
    @SerializedName("cnames")
    val cnames: List<String>?,
    @SerializedName("host")
    val host: String?
)