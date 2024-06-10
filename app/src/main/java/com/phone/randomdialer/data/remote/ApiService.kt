package com.phone.randomdialer.data.remote

import com.phone.randomdialer.data.modules.ApiResponse
import com.phone.randomdialer.data.modules.FeedbackRequestData
import com.phone.randomdialer.data.modules.FeedbackResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("fetch_mobile_numbers.php")
    suspend fun fetchMobileNumbers(): ApiResponse

    @POST("insert_calling_feedback.php")
    suspend fun submitCallingFeedback(@Body feedbackRequestData: FeedbackRequestData): FeedbackResponse
}