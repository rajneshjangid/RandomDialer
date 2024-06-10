package com.phone.randomdialer.domain.modules

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.phone.randomdialer.Utils.Constant

@Entity(tableName = "CallData")
data class CallData(
    val mobileNumbers: List<String> = listOf(),
    val dialStatus: String = Constant.DialStatus.ACTIVE.status,
    val showStart: Boolean = true,
    val showStop: Boolean = true,
    val showPopup: Boolean = true,
    val recallTime: Long = 5000L,
    val popupTime: Long = 10000L
) {
    @PrimaryKey
    var id: Long = 0
}
