package com.phone.randomdialer.presentation

data class CallUiState(
    val isCalling: Boolean = false,
    val currentNumber: String = ""
)