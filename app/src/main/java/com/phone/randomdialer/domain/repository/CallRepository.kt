package com.phone.randomdialer.domain.repository

import com.phone.randomdialer.data.modules.FeedbackRequestData
import com.phone.randomdialer.data.modules.FeedbackResponse
import com.phone.randomdialer.domain.modules.CallData
import kotlinx.coroutines.flow.Flow

interface CallRepository {
    suspend fun fetchDataFromServer()
    suspend fun submitFeedback(feedbackRequestData: FeedbackRequestData): FeedbackResponse
    suspend fun fetchCallData(): Flow<CallData>
    suspend fun runPeriodicWork()
}
