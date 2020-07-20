package dev.kourosh.baseapp.enums

import dev.kourosh.baseapp.R

enum class MessageType(val backgroundColor: Int,val textColor:Int,val drawable:Int) {
    INFO(R.color.baseAppBrightSkyBlue, R.color.white,R.drawable.snackbar_info),
    WARNING(R.color.baseAppLightGold, R.color.black,R.drawable.snackbar_warn),
    ERROR(R.color.baseAppTomato, R.color.white,R.drawable.snackbar_error),
    SUCCESS(R.color.baseAppShamrockGreen, R.color.white,R.drawable.snackbar_success)
}