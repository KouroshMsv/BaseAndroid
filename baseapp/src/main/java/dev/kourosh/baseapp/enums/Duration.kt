package dev.kourosh.baseapp.enums

import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

enum class Duration {
    LENGTH_SHORT,
    LENGTH_LONG,
    LENGTH_INDEFINITE;
    val toast:Int
    get() = when(this){
        LENGTH_SHORT ->Toast.LENGTH_SHORT
        LENGTH_LONG -> Toast.LENGTH_LONG
        LENGTH_INDEFINITE -> Toast.LENGTH_LONG
    }
    val snackBar:Int
    get() = when(this){
        LENGTH_SHORT ->Snackbar.LENGTH_SHORT
        LENGTH_LONG -> Snackbar.LENGTH_LONG
        LENGTH_INDEFINITE -> Snackbar.LENGTH_INDEFINITE
    }

}