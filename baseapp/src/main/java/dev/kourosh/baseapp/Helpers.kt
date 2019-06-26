package dev.kourosh.baseapp

import android.content.Context
import android.provider.Settings

object Helpers {

    fun isTimeAutomatic(c: Context): Boolean {
        return Settings.System.getInt(c.contentResolver,Settings.Global.AUTO_TIME,0) == 1 && Settings.System.getInt(c.contentResolver, Settings.Global.AUTO_TIME_ZONE, 0) == 1
    }
}