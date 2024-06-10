package com.phone.randomdialer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.phone.randomdialer.domain.modules.CallData
import kotlinx.coroutines.flow.Flow

@Dao
interface CallDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(callData: CallData)

    @Query("SELECT * FROM CallData LIMIT 1")
    fun getCallData(): Flow<CallData>
}
