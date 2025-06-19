package com.example.amazfittoken.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.amazfittoken.R
import com.example.amazfittoken.model.Device

class DevicesAdapter(
    private val devices: List<Device>,
    private val context: Context
) : RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder>() {

    class DeviceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val deviceNameText: TextView = view.findViewById(R.id.deviceNameText)
        val macAddressText: TextView = view.findViewById(R.id.macAddressText)
        val activeText: TextView = view.findViewById(R.id.activeText)
        val authKeyText: TextView = view.findViewById(R.id.authKeyText)
        val copyKeyButton: Button = view.findViewById(R.id.copyKeyButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]
        
        // Set device name (from displayName, additionalInfo or fallback to device type)
        val additionalInfo = device.additionalInfo
        val deviceName = when {
            !device.displayName.isNullOrBlank() -> device.displayName
            additionalInfo?.deviceName != null -> additionalInfo.deviceName
            else -> getDeviceTypeName(device.deviceType) ?: "Device ${device.deviceId}"
        }
        holder.deviceNameText.text = deviceName
        
        // Set MAC address
        holder.macAddressText.text = device.macAddress ?: "Unknown"
        
        // Set active status (check both active and activeStatus fields)
        val isActive = device.active == true || device.activeStatus == 1
        holder.activeText.text = if (isActive) "Yes" else "No"
        holder.activeText.setTextColor(
            if (isActive) 
                context.getColor(android.R.color.holo_green_dark)
            else 
                context.getColor(android.R.color.holo_red_dark)
        )
        
        // Set auth key - try multiple possible sources
        val authKey = additionalInfo?.authKey 
            ?: device.btKey
            ?: "No auth key available"
            
        val formattedKey = if (authKey != "No auth key available" && !authKey.startsWith("0x")) {
            "0x$authKey"
        } else {
            authKey
        }
        
        holder.authKeyText.text = formattedKey
        
        // Set copy button click listener
        holder.copyKeyButton.setOnClickListener {
            if (authKey != "No auth key available") {
                copyToClipboard(formattedKey)
            } else {
                Toast.makeText(context, "No auth key available for this device", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Disable copy button if no key available
        holder.copyKeyButton.isEnabled = authKey != "No auth key available"
    }

    override fun getItemCount() = devices.size

    private fun copyToClipboard(authKey: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Auth Key", authKey)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Auth key copied to clipboard!", Toast.LENGTH_SHORT).show()
    }
    
    private fun getDeviceTypeName(deviceType: Int?): String? {
        return when (deviceType) {
            1 -> "Mi Band"
            2 -> "Mi Band 1S"
            3 -> "Mi Band 2"
            4 -> "Mi Band 3"
            5 -> "Mi Band 4"
            6 -> "Mi Band 5"
            7 -> "Mi Band 6"
            8 -> "Mi Band 7"
            9 -> "Amazfit Bip"
            10 -> "Amazfit Cor"
            11 -> "Amazfit GTR"
            12 -> "Amazfit GTS"
            13 -> "Amazfit T-Rex"
            14 -> "Amazfit GTR 2"
            15 -> "Amazfit GTS 2"
            16 -> "Amazfit GTR 3"
            17 -> "Amazfit GTS 3"
            else -> "Device Type $deviceType"
        }
    }
}