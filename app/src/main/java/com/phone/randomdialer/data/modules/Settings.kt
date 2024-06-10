package com.phone.randomdialer.data.modules

import com.google.gson.annotations.SerializedName

data class Settings(
    @SerializedName("dialstatus")
    val dialStatus: String,
    @SerializedName("showstart")
    val showStart: String,
    @SerializedName("showstop")
    val showStop: String,
    @SerializedName("show_popup")
    val showPopup: String,
    @SerializedName("recall_time")
    val recallTime: String,
    @SerializedName("popup_time")
    val popUpTime: String? = null
)