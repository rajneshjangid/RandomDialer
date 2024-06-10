package com.phone.randomdialer.presentation

import com.phone.randomdialer.data.modules.FeedbackRequestData

sealed class CallEvent {
    data object FetchNumber : CallEvent()
    data object StartCalling : CallEvent()
    data object StopCalling : CallEvent()
    data class CallFeedback(val feedbackRequestData: FeedbackRequestData) : CallEvent()
}