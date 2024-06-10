package com.phone.randomdialer.data.repository

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.phone.randomdialer.Utils.Constant
import com.phone.randomdialer.data.local.CallDataDao
import com.phone.randomdialer.data.modules.FeedbackRequestData
import com.phone.randomdialer.data.modules.FeedbackResponse
import com.phone.randomdialer.data.remote.ApiService
import com.phone.randomdialer.data.workManager.CallWorkManager
import com.phone.randomdialer.domain.modules.CallData
import com.phone.randomdialer.domain.repository.CallRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CallRepositoryImpl(
    private val apiService: ApiService,
    private val callDataDao: CallDataDao,
    private val context: Context
) : CallRepository {
    override suspend fun fetchDataFromServer() = withContext(Dispatchers.IO) {
        val response = apiService.fetchMobileNumbers()
        val callData = CallData(
            mobileNumbers = response.mobileNumbers,
            showStart = response.settings.showStart == Constant.Visibility.YES.value,
            showStop = response.settings.showStop == Constant.Visibility.YES.value,
            showPopup = response.settings.showPopup == Constant.Visibility.YES.value,
            recallTime = if (response.settings.recallTime.isNotBlank()) response.settings.recallTime.toLong() * 1000 else 5000L,
            popupTime = response.settings.popUpTime?.let { it.toLong() * 1000 } ?: 10000L,
            dialStatus = response.settings.dialStatus
        )
        callDataDao.insert(callData)
    }

    override suspend fun submitFeedback(feedbackRequestData: FeedbackRequestData): FeedbackResponse =
        withContext(Dispatchers.IO) {
            return@withContext apiService.submitCallingFeedback(feedbackRequestData)
        }

    override suspend fun fetchCallData(): Flow<CallData> = withContext(Dispatchers.IO) {
        return@withContext callDataDao.getCallData()
    }

    override suspend fun runPeriodicWork() {
        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniquePeriodicWork(
            CallWorkManager.TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            CallWorkManager.periodicWorkRequest()
        )
    }
}
