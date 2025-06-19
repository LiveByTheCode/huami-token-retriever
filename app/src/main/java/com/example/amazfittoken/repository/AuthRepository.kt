package com.example.amazfittoken.repository

import com.example.amazfittoken.api.AmazfitApiService
import com.example.amazfittoken.api.ApiConstants
import com.example.amazfittoken.model.AuthResult
import com.example.amazfittoken.model.Device
import com.example.amazfittoken.model.DeviceResponse
import com.example.amazfittoken.model.LoginResponse
import com.example.amazfittoken.model.TokenResponse
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URI
import java.net.URLDecoder
import java.util.*
import java.util.concurrent.TimeUnit

class AuthRepository {
    
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .followRedirects(false) // Don't follow redirects automatically
        .followSslRedirects(false)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 10; SM-G973F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Mobile Safari/537.36")
                .addHeader("Accept", "application/json")
                .addHeader("Accept-Language", "en-US,en;q=0.9")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()
    
    private val gson = com.google.gson.GsonBuilder()
        .setLenient()
        .create()
    
    private val tokensRetrofit = Retrofit.Builder()
        .baseUrl(ApiConstants.TOKENS_BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    
    private val loginRetrofit = Retrofit.Builder()
        .baseUrl(ApiConstants.LOGIN_BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    
    private val devicesRetrofit = Retrofit.Builder()
        .baseUrl(ApiConstants.DEVICES_BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    
    private val tokensApi = tokensRetrofit.create(AmazfitApiService::class.java)
    private val loginApi = loginRetrofit.create(AmazfitApiService::class.java)
    private val devicesApi = devicesRetrofit.create(AmazfitApiService::class.java)
    
    suspend fun authenticateUser(email: String, password: String): Result<AuthResult> {
        return try {
            // Step 1: Get access token
            val accessTokenResult = getAccessToken(email, password)
            if (accessTokenResult.isFailure) {
                return Result.failure(accessTokenResult.exceptionOrNull()!!)
            }
            
            val accessToken = accessTokenResult.getOrNull()!!
            
            // Step 2: Login with access token to get app token and user ID
            val loginResult = loginWithAccessToken(accessToken)
            if (loginResult.isFailure) {
                return Result.failure(loginResult.exceptionOrNull()!!)
            }
            
            val authResult = loginResult.getOrNull()!!
            Result.success(authResult)
            
        } catch (e: Exception) {
            Result.failure(Exception("Authentication failed: ${e.message}"))
        }
    }
    
    suspend fun getDevices(authResult: AuthResult): Result<List<Device>> {
        return try {
            // Log the request details
            println("Fetching devices for user: ${authResult.userId}")
            println("Using app token: ${authResult.appToken.take(20)}...")
            println("API URL: ${ApiConstants.DEVICES_BASE_URL}users/${authResult.userId}/devices")
            
            // Use enableMultiDevice parameter like the Python script
            val response = devicesApi.getDevices(
                userId = authResult.userId, 
                appToken = authResult.appToken,
                enableMultiDevice = "true"
            )
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                val devices = responseBody?.items ?: emptyList()
                
                // Log the raw response headers to check for pagination info
                println("Response Headers:")
                response.headers().names().forEach { name ->
                    println("$name: ${response.headers()[name]}")
                }
                
                // Log the response for debugging
                println("Devices API Response:")
                println("Total devices found: ${devices.size}")
                devices.forEachIndexed { index, device ->
                    println("Device $index: ${device.deviceId} - ${device.macAddress} - ${device.displayName}")
                }
                
                Result.success(devices)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Failed to get devices: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Devices request error: ${e.message}"))
        }
    }
    
    private suspend fun getAccessToken(email: String, password: String): Result<String> {
        return try {
            val tokenRequest = mapOf(
                "state" to "REDIRECTION",
                "client_id" to "HuaMi",
                "password" to password,
                "redirect_uri" to ApiConstants.REDIRECT_URI,
                "region" to "us-west-2", // TODO: Make this configurable based on user region
                "token" to "access",
                "country_code" to "US" // TODO: Make this configurable based on user region
            )
            
            val response = tokensApi.getTokens(email, tokenRequest)
            
            // The response should be a 30x redirect with access token in Location header
            if (response.code() in 300..399) {
                val location = response.headers()["Location"]
                if (location != null) {
                    val accessToken = extractAccessTokenFromUrl(location)
                    if (accessToken != null) {
                        Result.success(accessToken)
                    } else {
                        Result.failure(Exception("No access token found in redirect URL"))
                    }
                } else {
                    Result.failure(Exception("No redirect location found"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Token request failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Token request error: ${e.message}"))
        }
    }
    
    private suspend fun loginWithAccessToken(accessToken: String): Result<AuthResult> {
        return try {
            val deviceId = generateMacAddress()
            val countryCode = "US"
            
            val loginRequest = mapOf(
                "dn" to "account.huami.com,api-user.huami.com,app-analytics.huami.com,api-watch.huami.com,api-analytics.huami.com,api-mifit.huami.com",
                "app_version" to "5.9.2-play_100355",
                "source" to "com.huami.watch.hmwatchmanager",
                "country_code" to countryCode,
                "device_id" to deviceId,
                "third_name" to "huami",
                "lang" to "en",
                "device_model" to "android_phone",
                "allow_registration" to "false",
                "app_name" to "com.huami.midong",
                "code" to accessToken,
                "grant_type" to "access_token"
            )
            
            val response = loginApi.login(loginRequest)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    // Read the response body correctly (OkHttp handles gzip decompression)
                    val bodyString = responseBody.string()
                    val appToken = extractAppTokenFromRawResponse(bodyString)
                    val userId = extractUserIdFromRawResponse(bodyString)
                    
                    if (appToken != null && userId != null) {
                        Result.success(AuthResult(appToken, userId))
                    } else {
                        Result.failure(Exception("Missing app token or user ID in response"))
                    }
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Login failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Login error: ${e.message}"))
        }
    }
    
    private fun generateDeviceId(): String {
        return UUID.randomUUID().toString().replace("-", "").take(16)
    }
    
    private fun generateMacAddress(): String {
        val random = java.util.Random()
        return "02:00:00:%02x:%02x:%02x".format(
            random.nextInt(256),
            random.nextInt(256),
            random.nextInt(256)
        )
    }
    
    private fun extractAccessTokenFromUrl(url: String): String? {
        return try {
            val uri = java.net.URI(url)
            val query = uri.query ?: return null
            val params = query.split("&").associate {
                val parts = it.split("=", limit = 2)
                if (parts.size == 2) {
                    java.net.URLDecoder.decode(parts[0], "UTF-8") to java.net.URLDecoder.decode(parts[1], "UTF-8")
                } else {
                    parts[0] to ""
                }
            }
            params["access"]
        } catch (e: Exception) {
            null
        }
    }
    
    private fun extractAppTokenFromRawResponse(responseBody: String): String? {
        return try {
            // Look for app_token in the response using regex
            val appTokenRegex = "\"app_token\"\\s*:\\s*\"([^\"]+)\"".toRegex()
            val matchResult = appTokenRegex.find(responseBody)
            matchResult?.groupValues?.get(1)
        } catch (e: Exception) {
            println("Error extracting app token: ${e.message}")
            null
        }
    }
    
    private fun extractUserIdFromRawResponse(responseBody: String): String? {
        return try {
            // Look for user_id in the response using regex
            val userIdRegex = "\"user_id\"\\s*:\\s*\"([^\"]+)\"".toRegex()
            val matchResult = userIdRegex.find(responseBody)
            matchResult?.groupValues?.get(1)
        } catch (e: Exception) {
            println("Error extracting user ID: ${e.message}")
            null
        }
    }
}