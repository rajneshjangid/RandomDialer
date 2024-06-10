package com.phone.randomdialer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.phone.randomdialer.domain.modules.CallData


@Database(entities = [CallData::class], version = 1)
@TypeConverters(ListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun callDao(): CallDataDao
}
