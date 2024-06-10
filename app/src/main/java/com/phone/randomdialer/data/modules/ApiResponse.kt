package com.phone.randomdialer.data.modules

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("mobile_numbers")
    val mobileNumbers: List<String>,
    val settings: Settings
)
