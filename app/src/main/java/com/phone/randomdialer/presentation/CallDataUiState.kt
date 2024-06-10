package com.phone.randomdialer.presentation

import com.phone.randomdialer.domain.modules.CallData

data class CallDataUiState(
    val callData: CallData = CallData(),
    val isLoading: Boolean = true,
    val error: String? = null
)