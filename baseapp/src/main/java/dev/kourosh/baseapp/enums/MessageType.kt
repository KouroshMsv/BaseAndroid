package dev.kourosh.baseapp.enums

import dev.kourosh.baseapp.R

enum class MessageType(val backgroundColor: Int,val textColor:Int,val drawable:Int) {
    INFO(R.color.bright_sky_blue, R.color.white,R.drawable.snackbar_info),
    WARNING(R.color.light_gold, R.color.black,R.drawable.snackbar_warn),
    ERROR(R.color.tomato, R.color.white,R.drawable.snackbar_error),
    SUCCESS(R.color.shamrock_green, R.color.white,R.drawable.snackbar_success)
}