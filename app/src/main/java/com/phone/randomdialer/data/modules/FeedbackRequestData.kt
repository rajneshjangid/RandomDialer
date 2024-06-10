package com.phone.randomdialer.data.modules

import com.google.gson.annotations.SerializedName

data class FeedbackRequestData(
    @SerializedName("mobileno") val mobileNo: String,
    @SerializedName("demo") val demo: String = "",
    @SerializedName("call_later") val callLater: String = "",
    @SerializedName("no_answer") val noAnswer: String = "",
    @SerializedName("invalidno") val invalidNo: String = "",
    @SerializedName("call_timedate") val callDateTime: String
)