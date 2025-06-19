package com.example.amazfittoken.model

import com.google.gson.annotations.SerializedName

data class DeviceResponse(
    @SerializedName("items")
    val items: List<Device>?
)

data class Device(
    @SerializedName("macAddress")
    val macAddress: String?,
    @SerializedName("serialNumber")
    val serialNumber: String?,
    @SerializedName("deviceSource")
    val deviceSource: Int?,
    @SerializedName("firmwareVersion")
    val firmwareVersion: String?,
    @SerializedName("hardwareVersion")
    val hardwareVersion: String?,
    @SerializedName("softwareVersion") 
    val softwareVersion: String?,
    @SerializedName("productionSource")
    val productionSource: Int?,
    @SerializedName("additionalInfo")
    val additionalInfoString: String?, // This is a JSON string, not an object
    @SerializedName("deviceType")
    val deviceType: Int?,
    @SerializedName("productionDate")
    val productionDate: Long?,
    @SerializedName("bindingDate")
    val bindingDate: Long?,
    @SerializedName("btKey")
    val btKey: String?,
    @SerializedName("active")
    val active: Boolean?,
    @SerializedName("activeStatus")
    val activeStatus: Int?,
    @SerializedName("displayName")
    val displayName: String?,
    @SerializedName("deviceId")
    val deviceId: String?
) {
    // Parse additionalInfo on demand
    val additionalInfo: AdditionalInfo?
        get() = additionalInfoString?.let {
            try {
                com.google.gson.Gson().fromJson(it, AdditionalInfo::class.java)
            } catch (e: Exception) {
                null
            }
        }
}

data class AdditionalInfo(
    @SerializedName("auth_key")
    val authKey: String?,
    @SerializedName("deviceName")
    val deviceName: String?,
    @SerializedName("sn")
    val serialNumber: String?,
    @SerializedName("productId")
    val productId: String?,
    @SerializedName("productVersion")
    val productVersion: String?,
    @SerializedName("brand type")
    val brandType: Int?
)