package dev.kourosh.baseapp.infrastructure.mvvm.dialog

import androidx.lifecycle.ViewModel
import dev.kourosh.baseapp.SingleLiveEvent

abstract class BaseDialogViewModel : ViewModel() {
    val errorMessage = SingleLiveEvent<String>()
    fun showMessage(message: String) {
        errorMessage.value = message
    }
}