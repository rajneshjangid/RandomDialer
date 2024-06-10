package com.phone.randomdialer.data.workManager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.phone.randomdialer.domain.repository.CallRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class CallWorkManager @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    lateinit var callRepository: CallRepository
    override suspend fun doWork(): Result {
        Log.i(TAG, "doWork: called")
        callRepository.fetchDataFromServer()
        return Result.success()
    }

    companion object {
        const val TAG = "CallWorkManager"
        const val DEFAULT_MIN_INTERVAL = 30L

        fun oneTimeWorkRequest(): OneTimeWorkRequest {
            val constrains = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            return OneTimeWorkRequestBuilder<CallWorkManager>()
                .setConstraints(constrains)
                .addTag("my_work_tag")
                .build()
        }

        fun periodicWorkRequest(): PeriodicWorkRequest {
            val constrains = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            return PeriodicWorkRequestBuilder<CallWorkManager>(
                DEFAULT_MIN_INTERVAL,
                TimeUnit.SECONDS
            ).setConstraints(constrains)
                .build()
        }
    }
}