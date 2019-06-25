package dev.kourosh.baseapp.enums

import dev.kourosh.baseapp.R

enum class MessageType(val backgroundColor: Int,val textColor:Int) {
    INFO(R.color.bright_sky_blue, R.color.white),
    WARNING(R.color.light_gold, R.color.black),
    ERROR(R.color.tomato, R.color.white),
    SUCCESS(R.color.shamrock_green, R.color.white)
}