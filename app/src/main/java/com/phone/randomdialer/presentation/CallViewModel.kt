package com.phone.randomdialer.presentation

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phone.randomdialer.Utils.AppPreference
import com.phone.randomdialer.data.workManager.CallWorkManager
import com.phone.randomdialer.domain.repository.CallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    private val repository: CallRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _callDataState = MutableStateFlow(CallDataUiState())
    val callDataState: StateFlow<CallDataUiState> = _callDataState.asStateFlow()

    private val _uiState = MutableStateFlow(CallUiState())
    val uiState: StateFlow<CallUiState> = _uiState.asStateFlow()
    private var currentNumberIndex = -1

    init {
        currentNumberIndex = AppPreference.getCurrentCallIndex(sharedPreferences)
        viewModelScope.launch {
            fetchNumbers()
            repository.fetchDataFromServer()
            repository.runPeriodicWork()
        }
        startApiTimer()
    }

    private fun startApiTimer() {
        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                // Replace this with your API call function
                fetchNumbersFromServer()
                // Delay for 30 minutes
                delay(TimeUnit.MINUTES.toMillis(CallWorkManager.DEFAULT_MIN_INTERVAL))
            }
        }
    }


    fun getDataSize() = callDataState.value.callData.mobileNumbers.size
    private fun getCallData(index: Int) = callDataState.value.callData.mobileNumbers[index]

    fun event(
        callEvent: CallEvent,
        makeCall: ((number: String) -> Unit)? = null
    ) {
        when (callEvent) {
            is CallEvent.FetchNumber -> {
                viewModelScope.launch {
                    fetchNumbersFromServer()
                }
            }

            is CallEvent.StopCalling -> {
                stopCalling()
            }

            is CallEvent.StartCalling -> {
                startCalling {
                    makeCall?.invoke(it)
                }
            }

            is CallEvent.CallFeedback -> {
                viewModelScope.launch {
                    val response = repository.submitFeedback(
                        callEvent.feedbackRequestData
                    )
                    println(response)
                }
            }
        }
    }

    private suspend fun fetchNumbersFromServer() {
        _callDataState.value = _callDataState.value.copy(
            isLoading = true
        )
        repository.fetchDataFromServer()
    }

    private suspend fun fetchNumbers() {
        _callDataState.value = _callDataState.value.copy(
            isLoading = true
        )
        repository.fetchCallData().onEach { result ->
            runCatching {
                _callDataState.value = _callDataState.value.copy(
                    isLoading = false,
                    callData = result
                )
            }.getOrElse {
                _callDataState.value = _callDataState.value.copy(
                    isLoading = false,
                    error = it.message
                )
            }
        }.launchIn(viewModelScope)
    }

    fun startCalling(
        makeCall: (number: String) -> Unit
    ) {
        _uiState.update { it.copy(isCalling = true) }
        callNextNumber(
            makeCall
        )
    }

    fun stopCalling() {
        _uiState.update { it.copy(isCalling = false) }
    }

    private fun callNextNumber(
        makeCall: (number: String) -> Unit
    ) {
        currentNumberIndex++
        if (currentNumberIndex >= getDataSize()) {
            currentNumberIndex = 0
        }
        AppPreference.setCurrentCallIndex(sharedPreferences, currentNumberIndex)
        val number = getCallData(currentNumberIndex)
        makeCall(number)
        _uiState.update { it.copy(currentNumber = number) }
    }
}