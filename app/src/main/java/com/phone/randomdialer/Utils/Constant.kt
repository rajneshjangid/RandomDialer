package com.phone.randomdialer.Utils

object Constant {
    const val BASE_URL = "https://crm.ipathi.com/api/"
    const val APP_PREF = "app_pref"
    const val APP_DATABASE = "app_database"
    const val WORK_MANAGER_NAME = "app_work_manager"

    enum class Visibility(val value: String) {
        YES("Yes"),
        NO("No")
    }

    enum class DialStatus(val status: String) {
        ACTIVE("Active"),
        INACTIVE("Inactive")
    }

    enum class CallState(val state: String) {
        DEMO("Demo"),
        CALL_LATER("Call Later"),
        NO_ANSWER("No Answer"),
        INVALID_NUMBER("Invalid No.")
    }
}
