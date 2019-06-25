package dev.kourosh.baseapp.infrastructure.mvvm.dialog

import androidx.lifecycle.ViewModel
import dev.kourosh.baseapp.SingleLiveEvent
import org.koin.core.KoinComponent

abstract class BaseDialogViewModel : ViewModel(), KoinComponent {
    val errorMessage = SingleLiveEvent<String>()
    fun showMessage(message: String) {
        errorMessage.value = message
    }
}